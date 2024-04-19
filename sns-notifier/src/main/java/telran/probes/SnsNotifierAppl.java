package telran.probes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;

import jakarta.annotation.PostConstruct;
import telran.probes.dto.DeviationData;

@SpringBootApplication
public class SnsNotifierAppl {
	@Value("${app.aws.sns.topic.base}")
	String topicBase;
	@Value("${app.aws.sns.subject.base}")
	String subjectBase;
	@Value("${app.aws.sns.region}")
	String awsRegion;
	AmazonSNS client ;
	
	public static void main(String[] args) {
		SpringApplication.run(SnsNotifierAppl.class, args);

	}
	@Bean
	Consumer<DeviationData> snsNotifierConsumer() {
		return this::publishingNotification;
	}
	void publishingNotification(DeviationData deviation) {
		long sensorId = deviation.id();
		client.publish(topicBase + sensorId, getMessage(deviation), subjectBase + sensorId);
	}
	private String getMessage(DeviationData deviationData) {
		String text = String.format("sensor %d has value %f with deviation %f at %s",
				deviationData.id(), deviationData.value(), deviationData.deviation(),
				getDateTime(deviationData.timestamp()));
		return text;
	}
private LocalDateTime getDateTime(long timestamp) {
		
		return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}
@PostConstruct
void setSnsClient() {
	client = AmazonSNSClient.builder().withRegion(awsRegion)
			.build();
}

}
