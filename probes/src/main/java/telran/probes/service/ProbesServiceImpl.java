package telran.probes.service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.configuration.ProbesConfiguration;
import telran.probes.dto.*;
import telran.probes.model.SensorRangeDoc;
import telran.probes.repo.SensorRangesRepo;
@Service
@RequiredArgsConstructor
@Slf4j
public class ProbesServiceImpl implements ProbesService {
final SensorRangesRepo sensorsRangeRepo;
final ProbesConfiguration probesConfiguration;
Map<Long, Range> rangesMap;
long[] sensorIds;
	@Override
	public ProbeData getRandomProbeData() {
		long id = getRandomId();
		Range range = rangesMap.get(id);
		
		return new ProbeData(id, getRandomInt(1, 100) < probesConfiguration.getDeviationPercent() ? 
				getRandomDeviation(range) : getRandomNormalValue(range), System.currentTimeMillis());
	}
private double getRandomNormalValue(Range range) {
		
		return ThreadLocalRandom.current().nextDouble(range.minValue(), range.maxValue());
	}
	private double getRandomDeviation(Range range) {
		
		return getRandomInt(1,100) < probesConfiguration.getNegativeDeviationPercent() ?
				getLessMin(range.minValue()) : getGreaterMax(range.maxValue());
	}
	private double getGreaterMax(double maxValue) {
		
		double res =  maxValue + Math.abs(maxValue * probesConfiguration.getDeviationFactor());
		log.debug("positive deviation - maxValue: {}, new value: {}", maxValue, res);
		return res;
	}
	private double getLessMin(double minValue) {
		
		double res =  minValue - Math.abs(minValue * probesConfiguration.getDeviationFactor());
		log.debug("negative deviation - minValue: {}, new value: {}", minValue, res);
		return res;
	}
	@PostConstruct
	void fillRangesMap() {
		List<SensorRangeDoc> documents = sensorsRangeRepo.findAll();
		if (documents.isEmpty()) {
			log.warn("unit test data implied");
			documents = List.of(new SensorRangeDoc(123, new Range(10, 100)),
					new SensorRangeDoc(124, new Range(-10, 10)),new SensorRangeDoc(125, new Range(150, 300)) );
		}
		rangesMap = documents.stream()
				.collect(Collectors.toMap(d -> d.getSensorId(), d -> d.getRange()));
		//for integration test of sending logs/alarms to CloudWatch
		rangesMap.put(1000l, new Range(100, 200));
		log.trace("map of ranges is {}", rangesMap);
		sensorIds = rangesMap.keySet().stream().mapToLong(id -> id).toArray();
		log.trace("sensor ids are {}", sensorIds);
		
	}
	private long getRandomId() {
		int index = getRandomInt(0, sensorIds.length);
		return sensorIds[index];
	}
private int getRandomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

}
