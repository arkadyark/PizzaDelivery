
public class HouseCounter implements Interruptor {

	private int targetHouse;

	public HouseCounter(int targetHouse) {
		this.targetHouse = targetHouse;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
