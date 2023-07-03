package reactive.ui;

import tee.binding.properties.NumericProperty;
import tee.binding.task.Task;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class SketchContour extends Sketch {
	public NumericProperty<SketchContour> width = new NumericProperty<SketchContour>(this);
	public NumericProperty<SketchContour> height = new NumericProperty<SketchContour>(this);
	public NumericProperty<SketchContour> left = new NumericProperty<SketchContour>(this);
	public NumericProperty<SketchContour> top = new NumericProperty<SketchContour>(this);

	public NumericProperty<SketchContour> strokeColor = new NumericProperty<SketchContour>(this);
	public NumericProperty<SketchContour> strokeWidth = new NumericProperty<SketchContour>(this);
	public NumericProperty<SketchContour> arcX = new NumericProperty<SketchContour>(this);
	public NumericProperty<SketchContour> arcY = new NumericProperty<SketchContour>(this);
	private Paint paint = new Paint();

	@Override
	public void unbind() {
		/*width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();*/
		super.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
		strokeColor.property.unbind();
		strokeWidth.property.unbind();
		arcX.property.unbind();
		arcY.property.unbind();
	}

	//Paint paint = new Paint();
	public SketchContour() {
		//paint.property.value(new Paint());
		paint.setAntiAlias(true);
		//paint.setAntiAlias(true);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStyle(Paint.Style.STROKE);
		width.is(100);
		height.is(100);
		left.is(0);
		top.is(0);
		//background.is(0xff990000);
		//paint.property.value().setColor(background.property.value().intValue());
		width.property.afterChange(postInvalidate);
		height.property.afterChange(postInvalidate);
		top.property.afterChange(postInvalidate);
		//background.property.afterChange(postInvalidate);
		strokeColor.property.afterChange(new Task() {
			@Override
			public void doTask() {
				// TODO Auto-generated method stub
				paint.setColor(strokeColor.property.value().intValue());
				postInvalidate.start();
			}
		});
		strokeWidth.property.afterChange(new Task() {
			@Override
			public void doTask() {
				// TODO Auto-generated method stub
				paint.setStrokeWidth(strokeWidth.property.value().floatValue());
				postInvalidate.start();
			}
		});
		arcX.property.afterChange(postInvalidate);
		arcY.property.afterChange(postInvalidate);
		//paint.property.afterChange(postInvalidate);
	}

	public void draw(Canvas canvas) {
		//System.out.println("draw "+paint.property.value().getColor());
		int w = width.property.value().intValue();
		int h = height.property.value().intValue();
		int x = left.property.value().intValue();
		int y = top.property.value().intValue();
		//canvas.drawRect(new RectF(x, y, x+w, y+h), paint.property.value());
		//canvas.dr
		/*paint.setStrokeWidth(strokeWidth.property.value().floatValue());
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(background.property.value().intValue());
		canvas.drawRoundRect(new RectF(x, y, x + w, y + h)//
				, arcX.property.value().floatValue()//
				, arcY.property.value().floatValue()//
				, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(strokeColor.property.value().intValue());*/
		canvas.drawRoundRect(new RectF(x, y, x + w, y + h)//
				, arcX.property.value().floatValue()//
				, arcY.property.value().floatValue()//
				, paint);
	}

	;
}
