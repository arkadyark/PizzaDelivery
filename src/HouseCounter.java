import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/***
 * 
 * Interruptor class that keeps a count of the number of houses that have been seen.
 * Triggers an interrupt when we have arrived at the desired house.
 */

public class HouseCounter implements Interruptor {
	private static final float HOUSE_THRESHOLD = 0.5f;
	
	private int targetHouse;
	private int houseCount = 0;
	private EV3UltrasonicSensor ultrasonic;
	private NXTRegulatedMotor ultrasonicMotor;
	private boolean seeingHouse;

	public HouseCounter(int targetHouse, String deliverySide, EV3UltrasonicSensor ultrasonic, NXTRegulatedMotor ultrasonicMotor) {
		this.targetHouse = targetHouse;
		this.houseCount = 0;
		this.ultrasonic = ultrasonic;
		this.ultrasonicMotor = ultrasonicMotor;
		this.seeingHouse = false;
		
		// Position ultrasonic sensor to see houses on the side we're interested in
		if (deliverySide == "LEFT") {
			ultrasonicMotor.rotateTo(-90);
		} else if (deliverySide == "RIGHT") {
			ultrasonicMotor.rotateTo(90);
		}
	}
	
	@Override
	public boolean isFinished() {
		float distance = PizzaDeliveryUtils.getDistance(ultrasonic);
		PizzaDeliveryUtils.displayMessage("House count: " + Integer.toString(houseCount));
		if (seeingHouse) {
			if (distance > HOUSE_THRESHOLD) {
				// We've moved past the house, allow ourselves to detect more houses
				seeingHouse = false;
			}
		} else if (distance <= HOUSE_THRESHOLD) {
			// We're next to a house, and weren't before this; increment count
			seeingHouse = true;
			houseCount++;
		}
		
		if (houseCount == targetHouse + 1) { // + 1 because of the indicator pile
			// We're at the house, trigger an interrupt
			ultrasonicMotor.rotateTo(0);
			return true;
		} else {
			return false;
		}
	}
}
