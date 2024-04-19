package telran.probes.service;

import java.net.URI;
import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
import telran.probes.dto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RangeProviderClientServiceImpl implements RangeProviderClientService {
	final RestTemplate restTemplate;
	final ServiceConfiguration serviceConfiguration;
	HashMap<Long, Range> cache = new HashMap<>();
	

	@Override
	public Range getRange(long sensorId) {
		Range res = cache.get(sensorId);
		if (res == null) {
			log.debug("range for sensor with id {}  doesn't exist in cache", sensorId);
			res = serviceRequest(sensorId);
		} else {
			log.debug("range {} from cache", res);
		}
		return res;
	}

	private Range serviceRequest(long sensorId) {
		Range range = null;
		ResponseEntity<?> responseEntity;
		try {
			responseEntity = restTemplate.exchange(getUrl(sensorId), HttpMethod.GET, null, Range.class);
			if (responseEntity.getStatusCode().is4xxClientError()) {
				throw new Exception(responseEntity.getBody().toString());
			}
			range = (Range) responseEntity.getBody();
			log.debug("range value {}", range);
			cache.put(sensorId, range);
		} catch (Exception e) {
			log.error("error at service request: {}", e.getMessage());
		
			log.warn("default range value: {}", range);
		}
		return range;

	}

	private String getUrl(long sensorId) {
		String url = String.format("http://%s:%d%s/%d", serviceConfiguration.getHost(), serviceConfiguration.getPort(),
				serviceConfiguration.getPath(), sensorId);
		log.debug("url created is {}", url);
		return url;
	}

	@Bean
	Consumer<SensorUpdateData> updateRangeConsumer() {
		return this::updateProcessing;
	}

	void updateProcessing(SensorUpdateData updateData) {
		long sensorId = updateData.id();
		Range range = updateData.range();
		if (cache.containsKey(sensorId) && range != null) {
			cache.put(sensorId, range);
		}
	}


}
