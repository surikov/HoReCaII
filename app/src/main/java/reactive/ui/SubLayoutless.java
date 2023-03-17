package reactive.ui;

import android.view.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.util.*;
import reactive.ui.*;

import android.view.animation.*;
import tee.binding.properties.*;
import tee.binding.task.*;
import tee.binding.it.*;

import java.io.*;
import java.text.*;

public class SubLayoutless extends Layoutless {
	private boolean initialized = false;
	Task reFit;

	/*
	= new Task() {
		@Override
		public void doTask() {
			int w = width().property.value().intValue();
			int h = height().property.value().intValue();
			System.out.println(this.getClass().getCanonicalName() +" start reFit " + w + "x" + h );
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
			params.leftMargin = left().property.value().intValue();
			params.topMargin = top().property.value().intValue();
			setLayoutParams(params);
			System.out.println(this.getClass().getCanonicalName() + " done reFit " + getLeft() + "x" + getTop() + "/" + getWidth() + "x" + getHeight());
		}
	};*/
	public SubLayoutless(Context context) {
		super(context);
	}
	public SubLayoutless(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SubLayoutless(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	/*@Override
	protected void onMeasureX() {
		//
	}*/
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//System.out.println(this.getClass().getCanonicalName() + ".onMeasure ");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//System.out.println(this.getClass().getCanonicalName() + ".onMeasure done");
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//System.out.println(this.getClass().getCanonicalName() + ".onSizeChanged "+w+"/"+ h+"/"+oldw+"/"+ oldh);
		//super.onSizeChanged(w, h, oldw, oldh);
		//System.out.println(this.getClass().getCanonicalName() + ".onSizeChanged done");
		/*System.out.println("SubLayoutless.onSizeChanged: " + oldw + "x" + oldh + " => " + w + "x" + h + ", measured "//
				+ this.getMeasuredWidth() + "x" + this.getMeasuredHeight()//
				+ " at " + this.getLeft() + ":" + this.getTop()//
		);*/
	}
	@Override
	protected void init() {
		super.init();
		if (!initialized) {
			initialized = true;
			//left = new NumericProperty<SubLayoutless>(this);
			//top = new NumericProperty<SubLayoutless>(this);
			/*reFit = new Task() {
				@Override
				public void doTask() {
					int w = width().property.value().intValue();
					int h = height().property.value().intValue();
					//System.out.println("start reFit " + w + "x" + h );
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
					params.leftMargin = left().property.value().intValue();
					params.topMargin = top().property.value().intValue();
					setLayoutParams(params);
					//System.out.println("done reFit " + getLeft() + "x" + getTop() + "/" + getWidth() + "x" + getHeight());
				}
			};*/
			reFit = new Task() {
				@Override
				public void doTask() {
					int w = width().property.value().intValue();
					int h = height().property.value().intValue();
					if (w <= 0)
						w = 100;
					if (h <= 0)
						h = 100;
					//System.out.println(SubLayoutless.this.getClass().getCanonicalName() +" start reFit " + w + "x" + h +" <- "+ getWidth() + "x" + getHeight());
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
					params.leftMargin = left().property.value().intValue();
					params.topMargin = top().property.value().intValue();
					setLayoutParams(params);
					//System.out.println(SubLayoutless.this.getClass().getCanonicalName() + " done reFit " + getWidth() + "x" + getHeight());
				}
			};
			left().property.afterChange(reFit);
			top().property.afterChange(reFit);
			width().property.afterChange(reFit);
			height().property.afterChange(reFit);
			//System.out.println(this.getClass().getCanonicalName()+".init "+left.property.value());
			//left.is(50);
			//System.out.println(this.getClass().getCanonicalName()+".now "+left.property.value());
		}
	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		/*width().property.unbind();
		height().property.unbind();
		left().property.unbind();
		top().property.unbind();*/
	}
}
