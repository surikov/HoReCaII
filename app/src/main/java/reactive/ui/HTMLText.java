package reactive.ui;

import java.util.Vector;

import reactive.ui.*;

import android.content.*;
import android.graphics.*;

import tee.binding.properties.*;

import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.TextView.BufferType;

import tee.binding.task.*;
import tee.binding.it.*;

import android.text.*;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/*
http://commonsware.com/blog/Android/2010/05/26/html-tags-supported-by-textview.html
<a href="...">
<b>
<big>
<blockquote>
<br>
<cite>
<dfn>
<div align="...">
<em>
<font size="..." color="..." face="...">
<h1>
<h2>
<h3>
<h4>
<h5>
<h6>
<i>
<img src="...">
<p>
<small>
<strike>
<strong>
<sub>
<sup>
<tt>
<u>
*/
public class HTMLText extends TextView implements Rake {
	//private int mode = Layoutless.NONE;
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	public ItProperty<HTMLText, Spanned> html = new ItProperty<HTMLText, Spanned>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	public ItProperty<HTMLText, Task> afterTap = new ItProperty<HTMLText, Task>(this);
	public NumericProperty<HTMLText> background = new NumericProperty<HTMLText>(this);
	Paint paint = new Paint();
	boolean initialized = false;
	private boolean inTableRow = false;
	Task reFit = new Task() {
		@Override
		public void doTask() {
			//if(1==1)return;
			//RelativeLayout.LayoutParams
			//TableRow.LayoutParams
			//System.out.println("reFit: "+inTableRow);
			ViewGroup.MarginLayoutParams params;
			if (inTableRow) {
				params = new
						//RelativeLayout.LayoutParams
						TableRow.LayoutParams(//
						width.property.value().intValue()//
						, height.property.value().intValue());
			} else {
				params = new RelativeLayout.LayoutParams
						//		TableRow.LayoutParams
						(//
								width.property.value().intValue()//
								, height.property.value().intValue());
			}
			params.leftMargin = left.property.value().intValue();
			params.topMargin = top.property.value().intValue();
			HTMLText.this.setLayoutParams(params);
			/*
			RichText.this.setWidth(width.property.value().intValue());
			RichText.this.setHeight(height.property.value().intValue());
			RichText.this.setMinWidth(width.property.value().intValue());
			RichText.this.setMinHeight(height.property.value().intValue());
			RichText.this.setMaxWidth(width.property.value().intValue());
			RichText.this.setMaxHeight(height.property.value().intValue());
			*/
			//System.out.println("reFit: " + width.property.value()+" * "+height.property.value());
			html.property.afterChange(new Task() {
				@Override
				public void doTask() {
					setText(html.property.value(), BufferType.SPANNABLE);
				}
			});
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
	};

	@Override
	public ToggleProperty<Rake> hidden() {
		return hidden;
	}

	Task postInvalidate = new Task() {
		@Override
		public void doTask() {
			postInvalidate();
		}
	};

	public HTMLText(Context context, boolean tableRowMode) {
		super(context);
		this.inTableRow = tableRowMode;
		init();
	}

	public HTMLText(Context context) {
		super(context);
		init();
	}

	public HTMLText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HTMLText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		setMovementMethod(LinkMovementMethod.getInstance());//android:autoLink="web" 
		paint.setColor(0xff000000);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		background.property.afterChange(new Task() {
			@Override
			public void doTask() {
				setBackgroundColor(background.property.value().intValue());
			}
		});
		width.property.afterChange(reFit).value(100);
		height.property.afterChange(reFit).value(100);
		left.property.afterChange(reFit);
		top.property.afterChange(reFit);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
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
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
		background.property.unbind();
	}

	public HTMLText labelStyleSmallNormal() {
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Small);
		return this;
	}

	public HTMLText labelStyleMediumNormal() {
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Medium);
		return this;
	}

	public HTMLText labelStyleLargeNormal() {
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Large);
		return this;
	}

	public HTMLText labelStyleSmallInverse() {
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Small_Inverse);
		return this;
	}

	public HTMLText labelStyleMediumInverse() {
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Medium_Inverse);
		return this;
	}

	public HTMLText labelStyleLargeInverse() {
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Large_Inverse);
		return this;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//System.out.println(event);
		if (afterTap.property.value() != null) {
			if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				afterTap.property.value().start();
			}
			return true;
		}
		return false;
	}
}
