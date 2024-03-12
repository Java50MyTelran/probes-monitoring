package telran.probes.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class ProbesConfiguration {
	@Value("${app.sensors.deviation.percent: 40}")
	int deviationPercent;
	@Value("${app.sensors.deviation.factor: 0.3}")
	float deviationFactor;
	@Value("${app.sensors.negative.deviation.percent: 50}")
	int negativeDeviationPercent;
}
