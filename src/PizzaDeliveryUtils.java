import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class PizzaDeliveryUtils {
	public static double DEG_TO_DEG = (9+14.5)/(2*5.5);
	public static double DIST_TO_DEG = 360/(Math.PI*5.5);
	public static int SPEED = 200;
	
	static float getDistance(EV3UltrasonicSensor ultrasonic) {
		int sampleSize = ultrasonic.sampleSize();
		float[] distance = new float[sampleSize];
		ultrasonic.getDistanceMode().fetchSample(distance, 0);
		return distance[0];
	}
	
	static float getAngle(EV3GyroSensor gyro) {
		int sampleSize = gyro.sampleSize();
		float[] angle = new float[sampleSize];
		gyro.getAngleMode().fetchSample(angle, 0);
		return angle[0];
	}
	
	static float getReflectedLight(EV3ColorSensor color) {
		int sampleSize = color.sampleSize();
		float[] redsample = new float[sampleSize];
		color.getRedMode().fetchSample(redsample, 0);
		return redsample[0];
	}
}
