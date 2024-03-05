package telran.probes.service;

public interface EmailsProviderClientService {
	String SERVICE_EMAIL = "service-sensors@gmail.com";

	String[] getMails(long sensorId);
}
