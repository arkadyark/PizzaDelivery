import lejos.hardware.sensor.EV3UltrasonicSensor;

public class HouseCounter implements Interruptor {
	private static final float HOUSE_THRESHOLD = 0.3f;
	
	private int targetHouse;
	private int houseCount = 0;
	private EV3UltrasonicSensor ultrasonic;
	private boolean seeingHouse;

	public HouseCounter(int targetHouse, EV3UltrasonicSensor ultrasonic) {
		this.targetHouse = targetHouse;
		this.houseCount = 0;
		this.ultrasonic = ultrasonic;
		this.seeingHouse = false;
	}
	
	@Override
	public boolean isFinished() {
		float distance = PizzaDeliveryUtils.getDistance(ultrasonic);
		
		if (seeingHouse) {
			if (distance > HOUSE_THRESHOLD) {
				// We've moved past the house, allow ourselves to detect more houses
				seeingHouse = false;
			}
		} else if (distance <= HOUSE_THRESHOLD) {
			// We're next to a house, and weren't before this
			seeingHouse = true;
			houseCount++;
		}
		
		return (houseCount == targetHouse);
	}
}
