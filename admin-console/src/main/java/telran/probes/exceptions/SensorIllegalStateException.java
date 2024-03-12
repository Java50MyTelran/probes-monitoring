package telran.probes.exceptions;

@SuppressWarnings("serial")
public class SensorIllegalStateException extends IllegalStateException {
	public SensorIllegalStateException(long sensorId, String collection) {
		super(String.format("sensor with id %d already exists in the collection %s", sensorId, collection));
	}
}
