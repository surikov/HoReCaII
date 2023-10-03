package reactive.ui;

import android.view.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.util.*;

import reactive.ui.*;

import android.content.res.*;
import android.view.View.MeasureSpec;
import android.view.animation.*;

import tee.binding.properties.*;
import tee.binding.task.*;
import tee.binding.it.*;
import tee.binding.*;
import tee.binding.properties.ToggleProperty;

import android.net.*;

import java.io.*;
import java.text.*;

import android.database.*;
import android.database.sqlite.*;

public class Layoutless extends RelativeLayout implements Rake {
	public final static int NONE = 0;
	public final static int DRAG = 1;
	public final static int ZOOM = 2;
	private int mode = NONE;
	private float lastEventX = 0;
	private float lastEventY = 0;
	private float initialShiftX = 0;
	private float initialShiftY = 0;
	private double initialSpacing;
	private double currentSpacing;
	//private int mw = 320;
	//private int mh = 240;
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	public NumericProperty<Layoutless> innerWidth = new NumericProperty<Layoutless>(this);
	public NumericProperty<Layoutless> innerHeight = new NumericProperty<Layoutless>(this);
	public NumericProperty<Layoutless> shiftX = new NumericProperty<Layoutless>(this);
	public NumericProperty<Layoutless> shiftY = new NumericProperty<Layoutless>(this);
	public NumericProperty<Layoutless> lastShiftX = new NumericProperty<Layoutless>(this);
	public NumericProperty<Layoutless> lastShiftY = new NumericProperty<Layoutless>(this);
	public NumericProperty<Layoutless> zoom = new NumericProperty<Layoutless>(this);
	public NumericProperty<Layoutless> maxZoom = new NumericProperty<Layoutless>(this);
	public NumericProperty<Layoutless> tapX = new NumericProperty<Layoutless>(this);
	public NumericProperty<Layoutless> tapY = new NumericProperty<Layoutless>(this);
	public ToggleProperty<Layoutless> solid = new ToggleProperty<Layoutless>(this);
	public ItProperty<Layoutless, Task> afterTap = new ItProperty<Layoutless, Task>(this);
	public ItProperty<Layoutless, Task> afterShift = new ItProperty<Layoutless, Task>(this);
	public ItProperty<Layoutless, Task> afterZoom = new ItProperty<Layoutless, Task>(this);
	//private static TextView colorTest;
	private boolean initialized = false;
	public Vector<Rake> children = new Vector<Rake>();

	//private boolean measured = false;
	//public Task afterOnMeasure=null;
	protected void init() {
		//System.out.println("init " + initialized);
		if (!initialized) {
			initialized = true;
			solid.is(true);
			Auxiliary.initThemeConstants(this.getContext());
			setFocusable(true);
			setFocusableInTouchMode(true);
			hidden.property.afterChange(new Task() {
				@Override
				public void doTask() {
					if (hidden.property.value()) {
						setVisibility(View.INVISIBLE);
					} else {
						setVisibility(View.VISIBLE);
					}
				}
			});
		}
	}

	@Override
	public ToggleProperty<Rake> hidden() {
		return hidden;
	}

	public Layoutless(Context context) {
		super(context);
		init();
	}

	public Layoutless(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Layoutless(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Layoutless child(Rake v) {
		this.addView(v.view());//,children.size());
		//this.bringChildToFront(v.view());
		//System.out.println("child "+v);
		children.add(v);
		//this.setTra
		//android.support.v4.view.ViewCompat.setElevation(v.view(),children.size());
		return this;
	}

	public Layoutless tallInput(Context context, double row, double left, Note label, Rake content, Numeric contentWidth) {
		this.child(new Decor(context).labelText.is(label)//
				.left().is(this.shiftX.property.plus(left))//
				.top().is(this.shiftY.property.plus(1.5 * row * Auxiliary.tapSize))//
				.width().is(contentWidth)//
				.height().is(0.5 * Auxiliary.tapSize)//
		);
		this.child(content//
				.left().is(this.shiftX.property.plus(left))//
				.top().is(this.shiftY.property.plus((0.5 + 1.5 * row) * Auxiliary.tapSize))//
				.width().is(contentWidth)//
				.height().is(2.8 * Auxiliary.tapSize)//
		);
		return this;
	}

	public Layoutless realInput(Context context, double row, double left, Note label, Rake content, Numeric contentWidth, Toggle hide) {
		this.child(new Decor(context).labelText.is(label).hidden().is(hide)//
				.left().is(this.shiftX.property.plus(left))//
				.top().is(this.shiftY.property.plus(1.5 * row * Auxiliary.tapSize))//
				.width().is(contentWidth)//
				.height().is(0.5 * Auxiliary.tapSize)//
		);
		this.child(content.hidden().is(hide)//
				.left().is(this.shiftX.property.plus(left))//
				.top().is(this.shiftY.property.plus((0.5 + 1.5 * row) * Auxiliary.tapSize))//
				.width().is(contentWidth)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);
		return this;
	}

	public Layoutless input(Context context, double row, double left, Note label, Rake content, Numeric contentWidth, Toggle hide) {
		return realInput(context, row, left, label, content, contentWidth, hide);
	}

	public Layoutless input(Context context, double row, double left, Note label, Rake content, Numeric contentWidth) {
		return realInput(context, row, left, label, content, contentWidth, null);
	}

	public Layoutless input(Context context, double row, double left, Note label, Rake content) {
		return realInput(context, row, left, label, content, new Numeric().value(5 * Auxiliary.tapSize), new Toggle());
	}

	public Layoutless input(Context context, double row, double left, Note label, Rake content, int contentWidth) {
		return realInput(context, row, left, label, content, new Numeric().value(contentWidth), new Toggle());
	}

	public Layoutless input(Context context, double row, double left, String label, Rake content, Toggle hide) {
		return realInput(context, row, left, new Note().value(label), content, new Numeric().value(5 * Auxiliary.tapSize), hide);
	}

	public Layoutless input(Context context, double row, double left, String label, Rake content) {
		return realInput(context, row, left, new Note().value(label), content, new Numeric().value(5 * Auxiliary.tapSize), new Toggle());
	}

	public Layoutless input(Context context, double row, double left, String label, Rake content, int contentWidth) {
		return realInput(context, row, left, new Note().value(label), content, new Numeric().value(contentWidth), new Toggle());
	}

	public Layoutless input(Context context, double row, double left, String label, Rake content, int contentWidth, Toggle hide) {
		return realInput(context, row, left, new Note().value(label), content, new Numeric().value(contentWidth), hide);
	}

	public Layoutless input(Context context, double row, double left, String label, Rake content, Numeric contentWidth) {
		return realInput(context, row, left, new Note().value(label), content, contentWidth, new Toggle());
	}

	public Layoutless field(Context context, double row, Note label, Rake content, Numeric contentWidth) {
		this.child(new Decor(context).labelText.is(label)//
				.labelAlignRightCenter()//
				.left().is(this.shiftX.property)//
				.top().is(this.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * row * Auxiliary.tapSize))//
				.width().is(this.width().property.multiply(0.3))//
				.height().is(0.8 * Auxiliary.tapSize)//
		);
		this.child(content//
				.left().is(this.shiftX.property.plus(this.width().property.multiply(0.3).plus(0.1 * Auxiliary.tapSize)))//
				.top().is(this.shiftY.property.plus(0.2 * Auxiliary.tapSize).plus(0.8 * row * Auxiliary.tapSize))//
				.width().is(contentWidth)//
				.height().is(0.8 * Auxiliary.tapSize)//
		);
		return this;
	}

	public Layoutless field(Context context, double row, Note label, Rake content) {
		return field(context, row, label, content, new Numeric().value(5 * Auxiliary.tapSize));
	}

	public Layoutless field(Context context, double row, Note label, Rake content, int contentWidth) {
		return field(context, row, label, content, new Numeric().value(contentWidth));
	}

	public Layoutless field(Context context, double row, String label, Rake content) {
		return field(context, row, new Note().value(label), content, new Numeric().value(5 * Auxiliary.tapSize));
	}

	public Layoutless field(Context context, double row, String label, Rake content, int contentWidth) {
		return field(context, row, new Note().value(label), content, new Numeric().value(contentWidth));
	}

	public Layoutless field(Context context, double row, String label, Rake content, Numeric contentWidth) {
		return field(context, row, new Note().value(label), content, contentWidth);
	}

	public Rake child(int nn) {
		if (nn < children.size()) {
			return children.get(nn);
		} else {
			return null;
		}
	}

	public int count() {
		return children.size();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		/*System.out.println("Layoutless.onSizeChanged: " + oldw + "x" + oldh + " => " + w + "x" + h + ", measured "//
				+ this.getMeasuredWidth() + "x" + this.getMeasuredHeight()//
				+ " at " + this.getLeft() + ":" + this.getTop()//
		);*/
		//if (w > mw && h > mh) {
		if (w > width.property.value() && h > height.property.value()) {
			width.is(w);

			height.is(h);
		}
		//}
		/*try {
			int n = 0;
			n = 1 / n;
		}
		catch (Throwable t) {
			t.printStackTrace();
		}*/
		//System.out.println("now "+width.property.value()+"x"+height().property.value());
		this.measure(
				View.MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(this.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
		this.layout(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
	}
	/*String maskName(int mask){
		if(mask==android.view.View.MeasureSpec.AT_MOST){
			return "AT_MOST";
		}
		if(mask==android.view.View.MeasureSpec.EXACTLY){
			return "EXACTLY";
		}
		if(mask==android.view.View.MeasureSpec.UNSPECIFIED){
			return "UNSPECIFIED";
		}
		return "?"+mask;
	}*/

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/*int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		System.out.println("onMeasure: " + widthSize + ":" + maskName(MeasureSpec.getMode(widthMeasureSpec)) //
				+ " x " + heightSize + ":" + maskName(MeasureSpec.getMode(heightMeasureSpec))//
				+ ", measured "+this.getMeasuredWidth() + "x" + this.getMeasuredHeight() //
				);*/
		//int exw=MeasureSpec.makeMeasureSpec(widthSize,android.view.View.MeasureSpec.EXACTLY);
		//int exh=MeasureSpec.makeMeasureSpec(heightSize,android.view.View.MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		/*if(widthSize>width.property.value()){
			//width.property.value(widthSize);
		}
		if(heightSize>height.property.value()){
			//height.property.value(heightSize);
		}*/
		//setMeasuredDimension(width.property.value().intValue(), height.property.value().intValue());
		//onMeasureX();
		/*if(afterOnMeasure!=null) {
			afterOnMeasure.start();
		}*/
	}

	/*protected void onMeasureX() {
		System.out.println("onMeasureX: measured "//
				+ this.getMeasuredWidth() + "x" + this.getMeasuredHeight() //
				+ ", properties " //
				+ this.width().property.value() + "x" + this.height().property.value()//
				+" - "+this//
		);
		//if (!measured) {
		//	measured = true;
		//	width.is(getMeasuredWidth());
		//	height.is(getMeasuredHeight());
		//}
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		//if (w > mw && h > mh) {
			width.is(w);
			height.is(h);
			System.out.println("now: properties " //
					+ this.width().property.value() + "x" + this.height().property.value()//
			);
		//}
	}*/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!solid.property.value()) {
			return false;
		}
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
			initialShiftX = shiftX.property.value().floatValue();
			initialShiftY = shiftY.property.value().floatValue();
			lastEventX = event.getX();
			lastEventY = event.getY();
			mode = DRAG;
		} else {
			if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
				if (event.getPointerCount() > 1) {
					if (mode == ZOOM) {
						currentSpacing = spacing(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
					} else {
						initialSpacing = spacing(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
						currentSpacing = initialSpacing;
						mode = ZOOM;
					}
				} else {
					setShift(event.getX(), event.getY());
					lastEventX = event.getX();
					lastEventY = event.getY();
				}
			} else {
				if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
					if (mode == DRAG) {
						finishDrag(event.getX(), event.getY());
					} else {
						if (mode == ZOOM) {
							finishZoom();
						} else {
							//
						}
					}
				} else {
					//
				}
			}
		}
		return true;
	}

	public static double spacing(float x0, float y0, float x1, float y1) {
		float x = x0 - x1;
		float y = y0 - y1;
		return Math.sqrt(x * x + y * y);
	}

	void setShift(float x, float y) {
		double newShiftX = shiftX.property.value() + x - lastEventX;
		double newShiftY = shiftY.property.value() + y - lastEventY;
		shiftX.property.value(newShiftX);
		shiftY.property.value(newShiftY);
	}

	void finishDrag(float x, float y) {
		setShift(x, y);
		if (Math.abs(initialShiftX - shiftX.property.value()) < 1 + 0.1 * Auxiliary.tapSize// 
				&& Math.abs(initialShiftY - shiftY.property.value()) < 1 + 0.1 * Auxiliary.tapSize) {
			finishTap(x, y);
		} else {
			double newShiftX = shiftX.property.value();
			double newShiftY = shiftY.property.value();
			if (innerWidth.property.value() > width.property.value()) {
				if (newShiftX < width.property.value() - innerWidth.property.value()) {
					newShiftX = width.property.value() - innerWidth.property.value();
				}
			} else {
				newShiftX = 0;
			}
			if (innerHeight.property.value() > height.property.value()) {
				if (newShiftY < height.property.value() - innerHeight.property.value()) {
					newShiftY = height.property.value() - innerHeight.property.value();
				}
			} else {
				newShiftY = 0;
			}
			if (newShiftX > 0) {
				newShiftX = 0;
			}
			if (newShiftY > 0) {
				newShiftY = 0;
			}
			if (afterShift.property.value() != null) {
				lastShiftX.property.value(newShiftX);
				lastShiftY.property.value(newShiftY);
				afterShift.property.value().start();
			} else {
				shiftX.property.value(newShiftX);
				shiftY.property.value(newShiftY);
			}
		}
		mode = NONE;
	}

	void finishTap(float x, float y) {
		shiftX.property.value((double) initialShiftX);
		shiftY.property.value((double) initialShiftY);
		tapX.property.value((double) x);
		tapY.property.value((double) y);
		if (afterTap.property.value() != null) {
			afterTap.property.value().start();
		}
	}

	void finishZoom() {
		if (currentSpacing > initialSpacing) {
			if (zoom.property.value() < maxZoom.property.value()) {
				zoom.is(zoom.property.value() + 1);
			}
		} else {
			if (zoom.property.value() > 0) {
				zoom.is(zoom.property.value() - 1);
			}
		}
		shiftX.property.value((double) initialShiftX);
		shiftY.property.value((double) initialShiftY);
		if (afterZoom.property.value() != null) {
			afterZoom.property.value().start();
		}
		mode = NONE;
	}

	@Override
	public NumericProperty<Rake> left() {
		return left;
	}

	@Override
	public NumericProperty<Rake> top() {
		return top;
	}

	@Override
	public NumericProperty<Rake> width() {
		return width;
	}

	@Override
	public NumericProperty<Rake> height() {
		return height;
	}

	@Override
	public View view() {
		return this;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		left.property.unbind();
		top.property.unbind();
		width.property.unbind();
		height.property.unbind();
		innerWidth.property.unbind();
		innerHeight.property.unbind();
		shiftX.property.unbind();
		shiftY.property.unbind();
		zoom.property.unbind();
		maxZoom.property.unbind();
		tapX.property.unbind();
		tapY.property.unbind();
		solid.property.unbind();
		afterTap.property.unbind();
		afterShift.property.unbind();
		afterZoom.property.unbind();
	}
}
