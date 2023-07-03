package tee.binding.task;

import tee.binding.it.It;

import java.util.*;

/**
 *
 * @author User
 */
public abstract class LazyTask extends Task {
	private Timer timer = null;
	private int laziness = 50;
	private boolean lock = false;

	/**
	 *
	 */
	public LazyTask() {
	}

	/**
	 *
	 * @param it
	 * @return
	 */
	public LazyTask laziness(int it) {
		laziness = it;
		return this;
	}

	/**
	 *
	 * @return
	 */
	public int laziness() {
		return laziness;
	}

	private void clear() {
		timer.cancel();
		timer.purge();
		timer = null;
	}

	/**
	 *
	 */
	@Override
	public void start() {
		if (timer != null) {
			clear();
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				clear();
				if (lock) {
					start();
				} else {
					lock = true;
					doTask();
					lock = false;
				}
			}
		}, laziness);
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		//System.out.println("\nLazyTask\n");
		final It<Integer> lazy = new It<Integer>().value(-1);
		lazy.afterChange(new LazyTask() {
			@Override
			public void doTask() {
				//System.out.println("[lazy]: someone changed value to " + lazy.value());
			}
		});
		final It<Integer> quick = new It<Integer>().value(-2);
		quick.afterChange(new Task() {
			@Override
			public void doTask() {
				//System.out.println("[quick]: someone changed value to " + quick.value());
			}
		});
		lazy.bind(quick);
		for (int i = 0; i < 5; i++) {
			//System.out.println("/i: " + i);
			lazy.value(i);
		}
	}
}
