/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.aggregatingservices.riv.clinicalprocess.activity.actions.getaggregatedactivities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.clinicalprocess.activity.actions.getactivitiesresponder.v1.GetActivitiesType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.agp.riv.itintegration.engagementindex.v1.EngagementType;
import se.skltp.agp.service.api.QueryObject;
import se.skltp.agp.service.api.RequestListFactory;

public class RequestListFactoryImpl implements RequestListFactory {

    private static final Logger log = LoggerFactory.getLogger(RequestListFactoryImpl.class);

    /**
     * Filtrera svarsposter från i EI (ei-engagement) baserat parametrar i GetActivities requestet (req). Följande villkor måste vara sanna
     * för att en svarspost från EI skall tas med i svaret:
     * 
     * req.careUnitId.size == 0 or req.careUnitId.contains(ei-engagement.logicalAddress)
     * 
     * Svarsposter från EI som passerat filtreringen grupperas på fältet sourceSystem samt postens fält logicalAddress (= PDL-enhet) samlas
     * i listan careUnitId per varje sourceSystem
     * 
     * Ett anrop görs per funnet sourceSystem med följande värden i anropet:
     * 
     * 1. logicalAddress = sourceSystem (systemadressering) 
     * 2. subjectOfCareId = orginal-request.subjectOfCareId
     */
    public List<Object[]> createRequestList(QueryObject qo, FindContentResponseType src) {

        final GetActivitiesType originalRequest = (GetActivitiesType) qo.getExtraArg();
        String reqCareUnit = null;
        if (originalRequest.getSourceSystemId() != null) {
            reqCareUnit = originalRequest.getSourceSystemId().getExtension();
        }

        FindContentResponseType eiResp = (FindContentResponseType) src;
        List<EngagementType> inEngagements = eiResp.getEngagement();

        log.info("Got {} hits in the engagement index", inEngagements.size());

        Map<String, List<String>> sourceSystem_pdlUnitList_map = new HashMap<String, List<String>>();

        for (EngagementType inEng : inEngagements) {
            if (isPartOf(reqCareUnit, inEng.getLogicalAddress())) {
                log.info("Add SS: {} for PDL unit: {}", inEng.getSourceSystem(), inEng.getLogicalAddress());
                addPdlUnitToSourceSystem(sourceSystem_pdlUnitList_map, inEng.getSourceSystem(), inEng.getLogicalAddress());
            }
        }

        // Prepare the result of the transformation as a list of request-payloads,
        // one payload for each unique logical-address (e.g. source system since we are using system addressing),
        // each payload built up as an object-array according to the JAX-WS signature for the method in the service interface
        List<Object[]> reqList = new ArrayList<Object[]>();

        for (Entry<String, List<String>> entry : sourceSystem_pdlUnitList_map.entrySet()) {
            final String sourceSystem = entry.getKey();
            final GetActivitiesType request = originalRequest;
            log.info("Calling source system using logical address {} for subject of care id {}", sourceSystem, originalRequest.getPatientId().getExtension());
            Object[] reqArr = new Object[] { sourceSystem, request };
            reqList.add(reqArr);
        }
        log.debug("Transformed payload: {}", reqList);
        return reqList;
    }

    protected boolean isPartOf(String careUnitId, String careUnit) {
        log.debug("Check careunit {} equals expected {}", careUnitId, careUnit);
        if (StringUtils.isBlank(careUnitId))
            return true;
        return careUnitId.equals(careUnit);
    }

    private void addPdlUnitToSourceSystem(Map<String, List<String>> sourceSystem_pdlUnitList_map, String sourceSystem, String pdlUnitId) {
        List<String> careUnitList = sourceSystem_pdlUnitList_map.get(sourceSystem);
        if (careUnitList == null) {
            careUnitList = new ArrayList<String>();
            sourceSystem_pdlUnitList_map.put(sourceSystem, careUnitList);
        }
        careUnitList.add(pdlUnitId);
    }
}