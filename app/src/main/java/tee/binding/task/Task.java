package tee.binding.task;

/**
 *
 * @author User
 */
public abstract class Task{

	/**
	 *
	 */
	private boolean lock = false;

	public void start(){
		if(!lock){
			lock = true;
			doTask();
			lock = false;
		}
	}

	public void doTask2(String p1, String p2){
		//
	}

	/**
	 *
	 */
	public abstract void doTask();
}
