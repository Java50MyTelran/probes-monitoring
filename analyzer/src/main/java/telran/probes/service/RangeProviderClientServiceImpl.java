package telran.probes.service;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.Range;
@Service
@RequiredArgsConstructor
@Slf4j
public class RangeProviderClientServiceImpl implements RangeProviderClientService {
final RestTemplate restTemplate;
final ServiceConfiguration serviceConfiguration;
	@Override
	public Range getRange(long sensorId) {
		// TODO Auto-generated method stub
		return null;
	}
	private Range serviceRequest(long sensorId) {
		Range range = null;
		ResponseEntity<?> responseEntity ;
		try {
			responseEntity = restTemplate.exchange(getUrl(sensorId), HttpMethod.GET, null, Range.class);
			if (responseEntity.getStatusCode().is4xxClientError()) {
				throw new Exception(responseEntity.getBody().toString());
			}
			range = (Range) responseEntity.getBody();
			log.debug("range value {}", range);
		} catch (Exception e) {
			log.error("error at service request: {}", e.getMessage());
			range = new Range(MIN_DEFAULT_VALUE, MAX_DEFAULT_VALUE);
			log.warn("default range value: {}", range);
		} 
		return range;
		
	}
	private String getUrl(long sensorId) {
		String url = String.format("http://%s:%d%s%d",
				serviceConfiguration.getHost(),
				serviceConfiguration.getPort(),
				serviceConfiguration.getPath(),
				sensorId);
		log.debug("url created is {}", url);
		return url;
	}

}
