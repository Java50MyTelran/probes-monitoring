package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.client.RestTemplate;

import telran.probes.dto.SensorUpdateData;
import telran.probes.service.EmailsProviderClientService;
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmaiNotifierServiceTest {
	private static final long SENSOR_ID = 123;
	private static final String[] EMAILS = {"mail1.gmail.com", "mail2.gmail.com"};
	private static final String URL = "http://localhost:8585/emails/sensor/";
	private static final String SENSOR_NOT_FOUND_MESSAGE ="sensor not found";
	private static final long SENSOR_ID_NOT_FOUND = 124;
	private static final String[] EMAILS_DEFAULT = {EmailsProviderClientService.SERVICE_EMAIL};
	private static final long SENSOR_ID_UNAVAILABLE = 170;
	private static final String[] EMAILS_UPDATED = {"mail3.gmail.com"};
	@Autowired
	InputDestination producer;
	@Autowired
	EmailsProviderClientService providerService;
	@MockBean
	RestTemplate restTemplate;
	private String updateBindingName = "updateEmailsConsumer-in-0";
	@Test
	@Order(1)
	void normalFlowNoCache() {
		ResponseEntity<String[]> responseEntity = new ResponseEntity<>(EMAILS,HttpStatus.OK);
		when(restTemplate.exchange(getUrl(SENSOR_ID), HttpMethod.GET, null, String[].class))
		.thenReturn(responseEntity );
		assertArrayEquals(EMAILS, providerService.getMails(SENSOR_ID));
	}
	@SuppressWarnings("unchecked")
	@Order(2)
	@Test
	void normalFlowWithCache() {
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
		.thenAnswer(new Answer<ResponseEntity<?>>() {
			@Override
			public ResponseEntity<?> answer(InvocationOnMock invocation) throws Throwable {
				fail("method exchange should not be called");
				return null;
			}
		});
		assertEquals(EMAILS, providerService.getMails(SENSOR_ID));
	}
	@Test
	@Order(3)
	void sensorNotFoundTest() {
		ResponseEntity<String> responseEntity = new ResponseEntity<>(SENSOR_NOT_FOUND_MESSAGE,HttpStatus.NOT_FOUND);
		when(restTemplate.exchange(getUrl(SENSOR_ID_NOT_FOUND), HttpMethod.GET, null, String.class))
		.thenReturn(responseEntity );
		assertArrayEquals(EMAILS_DEFAULT, providerService.getMails(SENSOR_ID_NOT_FOUND));
		
	}
	@Test
	@Order(4)
	void defaultNotInCache() {
		ResponseEntity<String[]> responseEntity = new ResponseEntity<>(EMAILS,HttpStatus.OK);
		when(restTemplate.exchange(getUrl(SENSOR_ID_NOT_FOUND), HttpMethod.GET, null, String[].class))
		.thenReturn(responseEntity );
		assertArrayEquals(EMAILS, providerService.getMails(SENSOR_ID_NOT_FOUND));
	}
	@SuppressWarnings("unchecked")
	@Test
	void remoteWebServiceUnavailable() {
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
		.thenThrow(new RuntimeException("Service is unavailable"));
		assertArrayEquals(EMAILS_DEFAULT, providerService.getMails(SENSOR_ID_UNAVAILABLE));
	}
	@Test
	void updateRangeSensorInMap() throws InterruptedException {
		
		producer.send
		(new GenericMessage<SensorUpdateData>(new SensorUpdateData(SENSOR_ID,
				null, EMAILS_UPDATED)), updateBindingName);
		Thread.sleep(100);
		assertArrayEquals(EMAILS_UPDATED, providerService.getMails(SENSOR_ID));
	}
	private String getUrl(long sensorId) {
		return URL + sensorId;
	}

}
