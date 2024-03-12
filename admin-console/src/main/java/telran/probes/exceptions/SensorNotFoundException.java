package telran.probes.exceptions;

import telran.exceptions.NotFoundException;

@SuppressWarnings("serial")
public class SensorNotFoundException extends NotFoundException {

	public SensorNotFoundException(long sensorId, String collectionName) {
		super(String.format("sensor %d not found in collection %s", sensorId, collectionName));
		
	}

}
