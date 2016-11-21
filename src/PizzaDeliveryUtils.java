import lejos.hardware.sensor.EV3UltrasonicSensor;

public class PizzaDeliveryUtils {
	static float getDistance(EV3UltrasonicSensor ultrasonic) {
		int sampleSize = ultrasonic.sampleSize();
		float[] distance = new float[sampleSize];
		ultrasonic.getDistanceMode().fetchSample(distance, 0);
		return distance[0];
	}
}
