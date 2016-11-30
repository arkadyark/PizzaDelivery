/***
 * 
 * Base class implementing interruptions of driving. 
 * Drivers will drive until they either reach their target, or are interrupted by an interruptor 
 */

public interface Interruptor {
	public boolean isFinished();
}
