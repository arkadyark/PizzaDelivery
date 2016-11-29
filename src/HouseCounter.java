import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class HouseCounter implements Interruptor {
	private static final float HOUSE_THRESHOLD = 0.3f;
	
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
		
		if (deliverySide == "LEFT") {
			ultrasonicMotor.rotateTo(-90);
		} else if (deliverySide == "RIGHT") {
			ultrasonicMotor.rotateTo(90);
		}
	}
	
	@Override
	public boolean isFinished() {
		float distance = PizzaDeliveryUtils.getDistance(ultrasonic);
		
		String nextToHouseString = seeingHouse ? "next to a house\n" : "not next to a house\n";
		String houseCountString = "Seen houses " + Integer.toString(houseCount) + "/" + Integer.toString(targetHouse);
		PizzaDeliveryUtils.displayStatus("N/A", nextToHouseString + houseCountString);
		
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
		
		if (houseCount == targetHouse) {
			ultrasonicMotor.rotateTo(90);
			return true;
		} else {
			return false;
		}
	}
}
