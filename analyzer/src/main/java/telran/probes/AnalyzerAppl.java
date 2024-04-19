package telran.probes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;
import telran.probes.dto.*;

import telran.probes.service.RangeProviderClientService;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class AnalyzerAppl {
	@Value("${app.analyzer.producer.binding.name}")
	String producerBindingName;
	final RangeProviderClientService clientService;
	final StreamBridge streamBridge;
	@Value("${app.aws.cloudwatch.logs.group.name}")
	String logGroupName;
	@Value("${app.aws.cloudwatch.logs.stream.name}")
	String streamName;
	CloudWatchLogsClient logsClient;
	DescribeLogStreamsRequest logStreamRequest;
	DescribeLogStreamsResponse describeLogStreamsResponse;
	String sequenceToken;
	public static void main(String[] args) {
		SpringApplication.run(AnalyzerAppl.class, args);

	}
	@Bean
	Consumer<ProbeData> analyzerConsumer() {
		return probeData -> probeDataAnalyzing(probeData);
	}
	private void probeDataAnalyzing(ProbeData probeData) {
		// in the case probeData value doesn't fall into a range received from RangeProviderClientService
		// create a proper deviation and  streamBridge.send(producerBindingName, deviation);
		log.trace("received probe: {}", probeData);
		long sensorId = probeData.id();
		Range range = clientService.getRange(sensorId);
		if(range == null) {
			
			sendLogEvent(probeData);
			log.debug("data about sensor {} has been sent to CloudWatch group {} stream {}",
					sensorId, logGroupName, streamName);
		} else {
			processRange(probeData, sensorId, range);
		}
		
		
	}
	private void processRange(ProbeData probeData, long sensorId, Range range) {
		double value = probeData.value();
		
		double border = Double.NaN;
		if (value < range.minValue()) {
			border = range.minValue();
		} else if(value > range.maxValue()) {
			border = range.maxValue();
		}
		if (!Double.isNaN(border)) {
			double deviation = value - border;
			log.debug("deviation: {}", deviation);
			DeviationData dataDeviation =
					new DeviationData(sensorId, deviation, value, System.currentTimeMillis());
			streamBridge.send(producerBindingName, dataDeviation);
			log.debug("deviation data {} sent to {}", dataDeviation, producerBindingName);
			
		}
	}
	private void sendLogEvent(ProbeData probeData) {
		InputLogEvent inputLogEvent = InputLogEvent.builder().message(getMessage(probeData))
				.timestamp(System.currentTimeMillis()).build();
		PutLogEventsRequest putLogEventsRequest = PutLogEventsRequest.builder()
				.logEvents(Arrays.asList(inputLogEvent)).logGroupName(logGroupName).logStreamName(streamName)
				.sequenceToken(sequenceToken).build();
		logsClient.putLogEvents(putLogEventsRequest);
		
	}
	private String getMessage(ProbeData probeData) {
		
		return String.format("ERROR: sensor %d has value %f at %s but range has not been provided",
				probeData.id(), probeData.value(), getDateTime(probeData.timestamp()));
	}
	private LocalDateTime getDateTime(long timestamp) {
		
		return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}
	@PostConstruct
	void setCloudWatchLogs() {
		logsClient = CloudWatchLogsClient.builder().region(Region.US_EAST_1).build();
		 logStreamRequest = DescribeLogStreamsRequest.builder().logGroupName(logGroupName)
				.logStreamNamePrefix(streamName).build();
		// Assume that a single stream is returned since a specific stream name was
		// specified in the previous request.
		 describeLogStreamsResponse = logsClient.describeLogStreams(logStreamRequest);
		sequenceToken = describeLogStreamsResponse.logStreams().get(0).uploadSequenceToken();
	}

}
