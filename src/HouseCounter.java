
public class HouseCounter implements Interruptor {
	/***
	 * TODO:
	 * - Implement reading from the ultrasonic sensor to detect if next to a house
	 * - Implement counting of houses (avoid double-counting)
	 * - Implement isFinished() interrupting when we reach the target house
	 */
	
	private int targetHouse;

	public HouseCounter(int targetHouse) {
		this.targetHouse = targetHouse;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
