package se.skltp.aggregatingservices.riv.clinicalprocess.activity.actions.getaggregatedactivity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.mockito.Mockito;

import riv.clinicalprocess.activity.actions.getactivityresponder.v1.GetActivityType;
import riv.clinicalprocess.activity.actions.v1.IIType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentResponseType;
import se.skltp.agp.riv.itintegration.engagementindex.v1.EngagementType;
import se.skltp.agp.service.api.QueryObject;
import se.skltp.agp.service.api.RequestListFactory;

public class RequestListFactoryTest {

	private RequestListFactoryImpl testObject = new RequestListFactoryImpl();
	private static final GetActivityType validRequest = new GetActivityType();
	private static final GetActivityType invalidRequest = new GetActivityType();
	
	private static final String SUBJECT_OF_CARE = UUID.randomUUID().toString();
	private static final String SOURCE_SYSTEM_HSAID = UUID.randomUUID().toString();
	private static final String OTHER_SOURCE_SYSTEM_HSAID = UUID.randomUUID().toString();
	
	@BeforeClass
	public static void init() {
		final IIType person = new IIType();
		person.setExtension(SUBJECT_OF_CARE);

		final IIType sourceSystem = new IIType();
		sourceSystem.setExtension(SOURCE_SYSTEM_HSAID);
		
		final IIType otherSourceSystem = new IIType();
		otherSourceSystem.setExtension(OTHER_SOURCE_SYSTEM_HSAID);
		
		validRequest.setPatientId(person);
		validRequest.setSourceSystemId(sourceSystem);
		invalidRequest.setPatientId(person);
		invalidRequest.setSourceSystemId(otherSourceSystem);
	}
	
	@Test
	public void testCreateRequestList() {
		QueryObject validQo = Mockito.mock(QueryObject.class);
		Mockito.when(validQo.getExtraArg()).thenReturn(validRequest);
		QueryObject invalidQo = Mockito.mock(QueryObject.class);
		Mockito.when(invalidQo.getExtraArg()).thenReturn(invalidRequest);
		
		final FindContentResponseType findContent = new FindContentResponseType();
		final EngagementType eng = new EngagementType();
		eng.setLogicalAddress(SOURCE_SYSTEM_HSAID);
		eng.setSourceSystem(SOURCE_SYSTEM_HSAID);
		eng.setRegisteredResidentIdentification(SUBJECT_OF_CARE);
		findContent.getEngagement().add(eng);
		
		List<Object[]> validRequestList = testObject.createRequestList(validQo, findContent);
		List<Object[]> invalidRequestList = testObject.createRequestList(invalidQo, findContent);
		
		assertFalse(validRequestList.isEmpty());
		assertTrue(invalidRequestList.isEmpty());
	}
		
	@Test
	public void isPartOf(){
		List<String> careUnitIdList = Arrays.asList("UNIT1","UNIT2");
		assertTrue(new RequestListFactoryImpl().isPartOf(careUnitIdList, "UNIT2"));
		assertTrue(new RequestListFactoryImpl().isPartOf(careUnitIdList, "UNIT1"));
		
		careUnitIdList = new ArrayList<String>();
		assertTrue(new RequestListFactoryImpl().isPartOf(careUnitIdList, "UNIT1"));
		
		careUnitIdList = null;
		assertTrue(new RequestListFactoryImpl().isPartOf(careUnitIdList, "UNIT1"));
	}
	
	@Test
	public void isNotPartOf(){
		List<String> careUnitIdList = Arrays.asList("UNIT1","UNIT2");
		assertFalse(new RequestListFactoryImpl().isPartOf(careUnitIdList, "UNIT3"));
		assertFalse(new RequestListFactoryImpl().isPartOf(careUnitIdList, null));
	}
	
	@Test
	public void testIsPartOf() {
		assertTrue(new RequestListFactoryImpl().isPartOf("TEST", "TEST"));
		assertFalse(new RequestListFactoryImpl().isPartOf("TEST_1", "TEST_2"));
	}
	
}
