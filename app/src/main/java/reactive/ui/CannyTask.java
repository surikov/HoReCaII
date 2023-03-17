package reactive.ui;

import java.util.*;
import android.os.*;
import tee.binding.properties.NumericProperty;
import tee.binding.task.*;

public abstract class CannyTask extends Task {
	private double key = 0;
	//private int laziness = 50;
	public NumericProperty<CannyTask> laziness = new NumericProperty<CannyTask>(this);

	public CannyTask() {
		laziness.is(50);
	}
	public void start(int lazy) {
		laziness.is(lazy);
		start();
	}
	public void doBackground() {
	}
	@Override
	public void start() {
		key = Math.random();
		final double started = key;
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(laziness.property.value().intValue());
				}
				catch (Throwable t) {
					//
				}
				if (started == key) {
					doBackground();
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void v) {
				if (started == key) {
					CannyTask.this.doTask();
				}
			}
		}.execute();
	}
}
