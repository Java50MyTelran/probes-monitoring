package telran.probes;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import telran.probes.dto.ProbeData;
import telran.probes.service.RangeProviderClientService;

@SpringBootApplication
@RequiredArgsConstructor
public class AnalyzerAppl {
	String producerBindingName = "analyzerProducer-out-0";
	final RangeProviderClientService clientService;
	final StreamBridge streamBridge;
	public static void main(String[] args) {
		SpringApplication.run(AnalyzerAppl.class, args);

	}
	@Bean
	Consumer<ProbeData> analyzerConsumer() {
		return probeData -> probeDataAnalyzing(probeData);
	}
	private void probeDataAnalyzing(ProbeData probeData) {
		// TODO 
		// in the case probeData value doesn't fall into a range received from RangeProviderClientService
		// create a proper deviation and  streamBridge.send(producerBindingName, deviation);
		
	}

}
