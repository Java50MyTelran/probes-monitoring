package telran.probes.service;

import telran.probes.dto.Range;

public interface RangeProviderClientService {
Range getRange(long sensorId);
}
