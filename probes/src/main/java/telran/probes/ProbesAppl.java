package telran.probes;

import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.configuration.ProbesConfiguration;
import telran.probes.dto.ProbeData;
import telran.probes.service.ProbesService;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class ProbesAppl {
	private static final long TIMEOUT = 10000;
	final ProbesService probesService;
	final ProbesConfiguration probesConfiguration;

	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext ctx = SpringApplication.run(ProbesAppl.class, args);
		Thread.sleep(TIMEOUT);
		ctx.close();

	}

	@Bean
	Supplier<ProbeData> probesSupplier() {
		return this::probeGeneration;
	}

	ProbeData probeGeneration() {
		return getProbeData();

	}

	private ProbeData getProbeData() {
		String bindingName = "probesSupplier-out-0"; //see Bean probesSupplier in this file
		 ProbeData probeData = probesService.getRandomProbeData();
		 log.debug("probe data: {} has been sent to {}", probeData, bindingName);
		 return probeData;
	}

}
