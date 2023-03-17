package reactive.ui;

import tee.binding.it.Numeric;
import tee.binding.properties.*;
import tee.binding.task.*;
import android.graphics.*;
import android.text.StaticLayout;
import android.text.Layout.Alignment;

import java.util.*;

public class SketchLine extends Sketch {
	public NumericProperty<SketchLine> strokeColor = new NumericProperty<SketchLine>(this);
	public NumericProperty<SketchLine> strokeWidth = new NumericProperty<SketchLine>(this);
	/*public NumericProperty<SketchLine> startX = new NumericProperty<SketchLine>(this);
	public NumericProperty<SketchLine> startY = new NumericProperty<SketchLine>(this);
	public NumericProperty<SketchLine> endX = new NumericProperty<SketchLine>(this);
	public NumericProperty<SketchLine> endY = new NumericProperty<SketchLine>(this);*/
	private Paint paint = new Paint();
	private Path path = new Path();
	private Vector<Numeric> xs = new Vector<Numeric>();
	private Vector<Numeric> ys = new Vector<Numeric>();
	//private boolean first = true;
	//private Vector<Integer>xx=new Vector<Integer>();
	//private Vector<Integer>yy=new Vector<Integer>();
	/*
	Bitmap bm = null;
	Rect src = null;
	Rect dest = null;
	Paint bmPaint = new Paint();
	boolean valid = false;
	boolean released = false;
*/
	@Override
	public void unbind() {
		super.unbind();
		strokeColor.property.unbind();
		strokeWidth.property.unbind();
		for (int i = 0; i < xs.size(); i++) {
			xs.get(i).unbind();
		}
		xs.removeAllElements();
		for (int i = 0; i < ys.size(); i++) {
			ys.get(i).unbind();
		}
		ys.removeAllElements();
		/*if (bm != null) {
			bm.recycle();
			bm = null;
		}*/
	}
	void resetPath() {
		path = new Path();
		if (xs.size() > 0) {
			path.moveTo(xs.get(0).value().floatValue(), ys.get(0).value().floatValue());
			for (int i = 0; i < xs.size(); i++) {
				path.lineTo(xs.get(i).value().floatValue(), ys.get(i).value().floatValue());
			}
		}
		//valid = false;
	}
	public SketchLine point(double x, double y) {
		return point(new Numeric().value(x), new Numeric().value(y));
	}
	public SketchLine point(Numeric x, Numeric y) {
		xs.add(new Numeric().bind(x).afterChange(new Task() {
			@Override
			public void doTask() {
				resetPath();
				postInvalidate.start();
			}
		}, true));
		ys.add(new Numeric().bind(y).afterChange(new Task() {
			@Override
			public void doTask() {
				resetPath();
				postInvalidate.start();
			}
		}, true));
		resetPath();
		
		postInvalidate.start();
		/*
		if (first) {
		path.moveTo((float) x, (float) y);
		first = false;
		//System.out.println("moveTo "+x+"x"+y);
		}
		else {
		path.lineTo((float) x, (float) y);
		postInvalidate.start();
		//System.out.println("lineTo "+x+"x"+y);
		}*/
		//xx.add(x);
		//yy.add(y);
		
		return this;
	}
	public SketchLine() {
		// paint.setAntiAlias(true);
		paint.setAntiAlias(true);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStyle(Paint.Style.STROKE);
		strokeColor.property.afterChange(new Task() {
			@Override
			public void doTask() {
				//valid = false;
				paint.setColor(strokeColor.property.value().intValue());
				postInvalidate.start();
			}
		});
		strokeWidth.property.afterChange(new Task() {
			@Override
			public void doTask() {
				//valid = false;
				paint.setStrokeWidth(strokeWidth.property.value().floatValue());
				postInvalidate.start();
			}
		});
		strokeWidth.is(3);
		strokeColor.is(0xffff0000);
		/*startX.property.afterChange(postInvalidate);
		startY.property.afterChange(postInvalidate);
		endX.property.afterChange(postInvalidate);
		endY.property.afterChange(postInvalidate);*/
	}
	@Override
	public void draw(Canvas canvas) {
		/*canvas.drawLine(//
				startX.property.value().floatValue()//
				, startY.property.value().floatValue()//
				, endX.property.value().floatValue()//
				, endY.property.value().floatValue()//
				, paint//
		);*/
		/*resetBM();
		if (bm != null) {
			//canvas.drawPath(path, paint);
			canvas.drawBitmap(bm, src, dest, bmPaint);
		}*/
		canvas.drawPath(path, paint);
		
	}
	/*void resetBM() {
		if (valid) {
			return;
		}
		valid = true;
		int w = width.property.value().intValue();
		int h = height.property.value().intValue();
		int l = left.property.value().intValue();
		int t = top.property.value().intValue();
		if (w > 0 && h > 0) {
			bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
			src = new Rect(0, 0, w, h);
			dest = new Rect(l, t, l + w, t + h);
			Canvas canvas = new Canvas(bm);
			canvas.drawPath(path, paint);
			
			Paint p=new Paint();
			p.setColor(0xff00ffff);
			canvas.drawRoundRect(new RectF(0, 0,  8,  8)//
			, 4//
			, 4//
			, p);
			
			System.out.println("recache");
			//StaticLayout staticLayout = new StaticLayout(text.property.value(), paint, w, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
			//staticLayout.draw(canvas);
		}
	}*/
}
