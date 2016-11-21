import lejos.hardware.sensor.EV3UltrasonicSensor;

public class ObstacleDetector implements Interruptor {
	private static final float OBSTACLE_THRESHOLD = 0.2f;
	
	private EV3UltrasonicSensor ultrasonic;

	ObstacleDetector(EV3UltrasonicSensor ultrasonic) {
		this.ultrasonic = ultrasonic;
	}
	
	@Override
	public boolean isFinished() {
		float distance = PizzaDeliveryUtils.getDistance(ultrasonic);
		return distance < OBSTACLE_THRESHOLD;
	}
}
