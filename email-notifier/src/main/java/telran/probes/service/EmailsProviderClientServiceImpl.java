package telran.probes.service;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailsProviderClientServiceImpl implements EmailsProviderClientService {
	final RestTemplate restTemplate;
	final ServiceConfiguration serviceConfiguration;
	HashMap<Long, String[]> cache = new HashMap<>();
	@Override
	public String[] getMails(long sensorId) {
		String[] res = cache.get(sensorId);
		if(res == null) {
			log.debug("emails for sensor with id {}  don't exist in cache", sensorId);
			res = serviceRequest(sensorId);
		} else {
			log.debug("emails {} from cache", Arrays.deepToString(res));
		}
		return res;
	}
	private String[] serviceRequest(long sensorId) {
		String[] emails = null;
		ResponseEntity<?> responseEntity;
		try {
			responseEntity = restTemplate.exchange(getUrl(sensorId), HttpMethod.GET, null, String[].class);
			if (responseEntity.getStatusCode().is4xxClientError()) {
				throw new Exception(responseEntity.getBody().toString());
			}
			emails = (String[]) responseEntity.getBody();
			log.debug("emails: {}", Arrays.deepToString(emails));
			cache.put(sensorId, emails);
		} catch (Exception e) {
			log.error("error at service request: {}", e.getMessage());
			emails = new String[] {SERVICE_EMAIL};
			log.warn("default emails: {}", Arrays.deepToString(emails));
		}
		return emails;

	}
	private String getUrl(long sensorId) {
		String url = String.format("http://%s:%d%s%d", serviceConfiguration.getHost(), serviceConfiguration.getPort(),
				serviceConfiguration.getPath(), sensorId);
		log.debug("url created is {}", url);
		return url;
	}
	@Bean
	Consumer<SensorUpdateData> updateEmailsConsumer() {
		return this::updateProcessing;
	}

	void updateProcessing(SensorUpdateData updateData) {
		long sensorId = updateData.id();
		String[] emails = updateData.emails();
		if (cache.containsKey(sensorId) && emails != null) {
			cache.put(sensorId, emails);
		}
	}

}
