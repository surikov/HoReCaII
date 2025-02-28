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
	public void doTask1(String p1){
		//
	}
	public void doTask2(String p1, String p2){
		//
	}
	public void doTask3(String p1, String p2, String p3){
		//
	}

	/**
	 *
	 */
	public abstract void doTask();
}
