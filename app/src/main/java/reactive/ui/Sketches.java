package reactive.ui;

import tee.binding.properties.*;
import tee.binding.task.*;
import android.graphics.*;
import android.text.*;
import android.text.Layout.Alignment;
import android.widget.RelativeLayout;
import java.util.*;

public class Sketches extends Sketch {
	public Vector<Sketch> sketches = new Vector<Sketch>();
	
	public Sketches child(Sketch s) {
		sketches.add(s);
		return this;
	}
	@Override
	public void unbind() {
		super.unbind();
		for (int i = 0; i < sketches.size(); i++) {
			sketches.get(i).unbind();
		}
		
	}
	@Override
	public void draw(Canvas canvas) {
		
		
		for (int i = 0; i < sketches.size(); i++) {
			if (sketches.get(i) != null) {
				sketches.get(i).draw(canvas);
			}
		}
	}
	
}
