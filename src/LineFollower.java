
public class LineFollower {
	/***
	 * TODO:
	 * - Implement P controller to drive along line (Lab 3) reading from ColorSensor
	 * - Tune P controller/put in tuning from Lab 3/4
	 * - Implement interruption support, poll interruptor periodically
	 */

	
	private Interruptor interruptor;

	public LineFollower(Interruptor interruptor) {
		this.interruptor = interruptor;
	}

	public void driveUntilStopped() {
	}

}
