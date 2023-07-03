package reactive.ui;

import reactive.ui.*;

import android.content.*;
import android.graphics.*;

import tee.binding.properties.*;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;

import tee.binding.task.*;
import tee.binding.it.*;

import android.text.*;

public class Tint extends Paint {
	public Sketch forUpdate;

	public void unbind() {
	}

	public Task postInvalidate = new Task() {
		@Override
		public void doTask() {
			if (forUpdate != null) {
				forUpdate.postInvalidate.start();
			}
		}
	};
}
