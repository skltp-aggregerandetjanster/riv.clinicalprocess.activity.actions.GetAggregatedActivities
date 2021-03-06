package se.skltp.aggregatingservices.riv.clinicalprocess.activity.actions.getaggregatedactivities;

import lombok.extern.log4j.Log4j2;
import riv.clinicalprocess.activity.actions.enums.v1.AddressPartTypeEnum;
import riv.clinicalprocess.activity.actions.enums.v1.PostalAddressUseEnum;
import riv.clinicalprocess.activity.actions.enums.v1.TelTypeEnum;
import riv.clinicalprocess.activity.actions.enums.v1.TimeStampTypeFormatEnum;
import riv.clinicalprocess.activity.actions.getactivitiesresponder.v1.GetActivitiesResponseType;
import riv.clinicalprocess.activity.actions.getactivitiesresponder.v1.GetActivitiesType;
import riv.clinicalprocess.activity.actions.v1.ActivityGroupType;
import riv.clinicalprocess.activity.actions.v1.ActivityType;
import riv.clinicalprocess.activity.actions.v1.AdditionalParticipantType;
import riv.clinicalprocess.activity.actions.v1.AddressPartType;
import riv.clinicalprocess.activity.actions.v1.AddressType;
import riv.clinicalprocess.activity.actions.v1.CVType;
import riv.clinicalprocess.activity.actions.v1.CareGiverType;
import riv.clinicalprocess.activity.actions.v1.CareUnitType;
import riv.clinicalprocess.activity.actions.v1.DeviceType;
import riv.clinicalprocess.activity.actions.v1.IIType;
import riv.clinicalprocess.activity.actions.v1.LegalAuthenticatorType;
import riv.clinicalprocess.activity.actions.v1.LocationType;
import riv.clinicalprocess.activity.actions.v1.OrganisationType;
import riv.clinicalprocess.activity.actions.v1.PartialTimePeriodType;
import riv.clinicalprocess.activity.actions.v1.PartialTimeStampType;
import riv.clinicalprocess.activity.actions.v1.PatientType;
import riv.clinicalprocess.activity.actions.v1.PerformerRoleType;
import riv.clinicalprocess.activity.actions.v1.PersonType;
import riv.clinicalprocess.activity.actions.v1.ReferredInformationType;
import riv.clinicalprocess.activity.actions.v1.RelationType;
import riv.clinicalprocess.activity.actions.v1.SourceSystemType;
import riv.clinicalprocess.activity.actions.v1.TelType;
import riv.clinicalprocess.activity.actions.v1.TimePeriodType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.cxf.message.MessageContentsList;
import org.springframework.stereotype.Service;
import se.skltp.aggregatingservices.data.TestDataGenerator;

@Log4j2
@Service
public class ServiceTestDataGenerator extends TestDataGenerator {

	private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
	
	@Override
	public String getPatientId(MessageContentsList messageContentsList) {
		GetActivitiesType request = (GetActivitiesType) messageContentsList.get(1);
		String patientId = request.getPatientId().getExtension();
		return patientId;
	}

	@Override
	public Object createResponse(Object... responseItems) {
		log.info("Creating a response with {} items", responseItems.length);
		GetActivitiesResponseType response = new GetActivitiesResponseType();
		for (int i = 0; i < responseItems.length; i++) {
            response.getActivityGroup().add((ActivityGroupType) responseItems[i]);
        }

		log.info("response.toString:" + response.toString());

		return response;
	}

	@Override
	public Object createResponseItem(String logicalAddress, String registeredResidentId, String businessObjectId, String time) {
		log.debug("Created ResponseItem for logical-address {}, registeredResidentId {} and businessObjectId {}",
				new Object[]{logicalAddress, registeredResidentId, businessObjectId});

        ActivityGroupType activityGroup = new ActivityGroupType();

        activityGroup.getActivity().add(new ActivityType());
        activityGroup.getActivity().get(0).setApprovedForPatient(true);
        activityGroup.getActivity().get(0)
                .setDescription("Kompletterande fritextbeskrivning av aktiviteten där aktiviteten inte fullt ut kan beskrivas med kodbeteckningen.");
        activityGroup.getActivity().get(0).setId(iiType("1.2.752.129.2.1.2.1", logicalAddress + ":" + new Date().getTime()));
        activityGroup.getActivity().get(0).setTargetSite(cvType("Fysisk", "1.2.752.116.2.1"));
        activityGroup.getActivity().get(0).getRelation().add(new RelationType());
        activityGroup.getActivity().get(0).getRelation().get(0).setCode(cvType("kod", "kodsystem"));
        activityGroup.getActivity().get(0).getRelation().get(0).setReferredInformation(new ReferredInformationType());
        activityGroup.getActivity().get(0).getRelation().get(0).getReferredInformation().setType("abcabcabc");
        activityGroup.getActivity().get(0).setCode(cvType("kod", "kodsystem"));
        activityGroup.getActivity().get(0).setMethod(cvType("kod", "kodsystem"));
        activityGroup.getActivity().get(0).setRegistrationTime("20100530");
        activityGroup.getActivity().get(0).setStatus(cvType("kod", "kodsystem"));
        activityGroup.getActivity().get(0).setTargetSite(cvType("kod", "kodsystem"));
        activityGroup.getActivity().get(0).setTime(new PartialTimePeriodType());
        activityGroup.getActivity().get(0).getTime().setStart(new PartialTimeStampType());
        activityGroup.getActivity().get(0).getTime().setEnd(new PartialTimeStampType());

        activityGroup.getAdditionalParticipant().add(new AdditionalParticipantType());
        activityGroup.getAdditionalParticipant().get(0).setDevice(deviceType());
        activityGroup.getAdditionalParticipant().get(0).setId(iiType("rot", "ext"));
        activityGroup.getAdditionalParticipant().get(0).setLocation(location(logicalAddress));
        activityGroup.getAdditionalParticipant().get(0).setOrganisation(new OrganisationType());
        activityGroup.getAdditionalParticipant().get(0).getOrganisation().setName("organisation name");
        activityGroup.getAdditionalParticipant().get(0).setPerson(new PersonType());
        activityGroup.getAdditionalParticipant().get(0).getPerson().setName("additional participant person name");
        activityGroup.getAdditionalParticipant().get(0).setRole(cvType("kod", "kodsystem"));
        activityGroup.getAdditionalParticipant().get(0).setTime(timePeriod());

        activityGroup.getDevice().add(deviceType());
        activityGroup.setCareProcessId("ABC123");

        activityGroup.setLegalAuthenticator(new LegalAuthenticatorType());
        activityGroup.getLegalAuthenticator().setId(iiType("rot", "ext"));
        activityGroup.getLegalAuthenticator().setName("legal authenticator's name");
        activityGroup.getLegalAuthenticator().setTime(new PartialTimeStampType());
        activityGroup.getLegalAuthenticator().getTime().setFormat(TimeStampTypeFormatEnum.YYYYMMD_DHHMM);
        activityGroup.getLegalAuthenticator().getTime().setValue("201012241201");

        activityGroup.setLocation(location(logicalAddress));
        activityGroup.setPatient(patient(registeredResidentId));
        activityGroup.setPerformerRole(performer());
        activityGroup.setSourceSystem(sourceSystemType(logicalAddress));

        return activityGroup;
	}

	public Object createRequest(String patientId, String sourceSystemHSAId){

		GetActivitiesType request = new GetActivitiesType();

		request.setPatientId(new IIType());
		request.getPatientId().setExtension(patientId);
		request.setSourceSystemId(new IIType());
		request.getSourceSystemId().setExtension(sourceSystemHSAId);

		return request;
	}
	
    private TimePeriodType timePeriod() {
        final TimePeriodType type = new TimePeriodType();
        type.setEnd(df.format(LocalDateTime.now()));
        type.setStart(df.format(LocalDateTime.now()));
        return type;
    }

    private SourceSystemType sourceSystemType(final String logicalAddresss) {
        final SourceSystemType type = new SourceSystemType();
        type.setId(iiType("1.2.757.129.2.1.4.1", logicalAddresss));
        return type;
    }

    private PerformerRoleType performer() {
        final PerformerRoleType perf = new PerformerRoleType();
        perf.setCareUnit(careUnit());
        perf.setCode(cvType("HL7 RoleCode", "2.16.840.1.113883.5.111"));
        perf.setId(iiType("1.2.752.129.2.1.4.1", "hsaid"));
        return perf;
    }

    private CareUnitType careUnit() {
        final CareUnitType careUnitType = new CareUnitType();
        careUnitType.setId(iiType("1.2.752.129.2.1.4.1", "hsaId"));
        careUnitType.setName("Svedala sjukhus");
        careUnitType.setCareGiver(careGiver());
        return careUnitType;
    }

    private CareGiverType careGiver() {
        final CareGiverType type = new CareGiverType();
        type.setId(iiType("1.2.752.129.2.1.4.1", "hsaid"));
        type.setName("Test Testsson");
        return type;
    }

    private PatientType patient(final String registeredResidentId) {
        final PatientType type = new PatientType();
        if (registeredResidentId != null) {
            type.setDateOfBirth("19" + registeredResidentId.substring(0, 6));
        }
        type.setGender(cvType("9", "1.2.752.129.2.2.1.1"));
        type.setId(iiType("1.2.752.129.2.1.3.1", registeredResidentId));
        return type;
    }

    private LocationType location(final String hsaId) {
        final LocationType loc = new LocationType();
        loc.setId(iiType("1.2.752.129.2.1.4.1", hsaId));
        loc.setName("TestName");
        loc.getAddress().add(address());
        loc.getElectronicAddress().add(new TelType());
        loc.getElectronicAddress().get(0).setUse(TelTypeEnum.VOICE);
        loc.getElectronicAddress().get(0).setValue("0123456789");
        return loc;
    }

    private AddressType address() {
        final AddressType address = new AddressType();
        address.setUse(PostalAddressUseEnum.H);
        address.getPart().add(new AddressPartType());
        address.getPart().get(0).setType(AddressPartTypeEnum.STA);
        address.getPart().get(0).setValue("Malmö");
        return address;
    }

    private DeviceType deviceType() {
        final DeviceType device = new DeviceType();
        device.setId(iiType("1.2.160", "1234"));
        return device;
    }

    private IIType iiType(final String root, final String extension) {
        final IIType ii = new IIType();
        ii.setExtension(extension);
        ii.setRoot(root);
        return ii;
    }

    private CVType cvType(final String code, final String codeSystem) {
        final CVType cv = new CVType();
        cv.setCode(code);
        cv.setCodeSystem(codeSystem);
        return cv;
    }
}
