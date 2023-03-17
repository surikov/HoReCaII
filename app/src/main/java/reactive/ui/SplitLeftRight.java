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

public class SplitLeftRight extends SubLayoutless {
	public NumericProperty<SplitLeftRight> split;
	public NumericProperty<SplitLeftRight> position;
	private Rake leftSide;
	private Rake rightSide;
	private boolean initialized = false;
	//private boolean firstOnSizeChanged = true;

	//private boolean lockAdjustSplit=false;
	/*
		public void debug() {
			
			System.out.println("me " + this.getLeft() + "x" + this.getTop() + "/" + this.getWidth() + "x" + this.getHeight());
			System.out.println("bind " + left().property.value() + "x" + top().property.value()+ "/" + width().property.value() + "x" + height().property.value());
		}*/
	@Override
	public void requestLayout() {
		super.requestLayout();
		//System.out.println("requestLayout");
		//reFit.start();
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//super.onSizeChanged(w, h, oldw, oldh);
		//System.out.println("onSizeChanged " + w + "x" + h + " <- " + oldw + "x" + oldh);
		//if (firstOnSizeChanged) {
			//firstOnSizeChanged = false;
			//split.property.value(this.width().property.value()-Layoutless.tapSize*0.5);
		//}
	}
	/*@Override
	protected boolean setFrame(int left, int top, int right, int bottom) {
		boolean r=super.setFrame(left,  top,  right,  bottom);
		return r;
	}
	*/
	public SplitLeftRight(Context context) {
		super(context);
	}
	public SplitLeftRight(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SplitLeftRight(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	/*@Override
	protected void onMeasureX() {
		//
	}*/
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//System.out.println(this.getClass().getCanonicalName() + ".onMeasure: "+ getMeasuredWidth()+"x" + getMeasuredHeight());
		//System.out.println( "spec "+ widthMeasureSpec+"x" + heightMeasureSpec);
	}
	/*@Override
	public void layout(int l, int t, int r, int b) {
		super.layout(l, t, r, b);
	}*/
	public SplitLeftRight leftSide(Rake v) {
		if (leftSide != null) {
			this.removeView(leftSide.view());
		}
		leftSide = v;
		this.addView(leftSide//
				.width().is(split.property)//
				.height().is(height().property)//
				.view()//
				, 0//
		);
		return this;
	}
	public SplitLeftRight rightSide(Rake v) {
		if (rightSide != null) {
			this.removeView(rightSide.view());
		}
		rightSide = v;
		this.addView(rightSide//
				.left().is(split.property)//
				.width().is(width().property.minus(split.property))//
				.height().is(height().property)//
				.view()//
				, 0//
		);
		return this;
	}
	protected void init() {
		super.init();
		if (!initialized) {
			initialized = true;
			this.solid.is(false);
			split = new NumericProperty<SplitLeftRight>(this);
			split.is(50);
			position = new NumericProperty<SplitLeftRight>(this);
			position.is(0);
			Task adjustSplit = new Task() {
				@Override
				public void doTask() {
					if (split.property.value() < 0
					//0.5 * Layoutless.tapSize
					) {
						split.is(
						//0.5 * Layoutless.tapSize
						0);
					}
					if (split.property.value() > width().property.value()
					//- 0.5 * Layoutless.tapSize - 4
					) {
						split.is(width().property.value()
						//- 0.5 * Layoutless.tapSize - 4
						);
					}
				}
			};
			//split.property.afterChange(adjustSplit);
			//split.property.m
			/*this.child(new Decor(this.getContext())//
			.background.is(0x33999999)//
					.left().is(split.property.minus(2))//
					.width().is(5)//
					.height().is(height().property)//
			//
			);
			this.child(new Decor(this.getContext())//
			.background.is(0x99999999)//
					.left().is(split.property.minus(1))//
					.width().is(3)//
					.height().is(height().property)//
			//
			);*/
			this.child(new Decor(this.getContext())//
			.background.is(Auxiliary.textColorPrimary)//
					.left().is(split.property)//
					.width().is(1)//
					.height().is(height().property)//
			//
			);
			this.child(new Decor(this.getContext())//
			.afterTap.is(new Task() {
				@Override
				public void doTask() {
					//System.out.println("tap " + split.property.value() + " / " + width().property.value());
					if (Math.abs(split.property.value() - width().property.value()) < Auxiliary.tapSize * 0.3 + 1) {
						split.is(0.7 * width().property.value());
					}
					else {
						split.is(width().property.value());
					}
				}
			})//
			.dragX.is(split.property.minus(0.5 * Auxiliary.tapSize))//
			.afterDrag.is(adjustSplit)//
			.movableX.is(true)//
			/*
					.sketch(new SketchPlate()//
					.arcX.is(0.5 * (4 + Auxiliary.tapSize))//
					.arcY.is(0.5 * (4 + Auxiliary.tapSize))//
					.background.is(0x66999999)//
					.width.is(4 + Auxiliary.tapSize)//
					.height.is(4 + Auxiliary.tapSize)//
					)//
					.sketch(new SketchPlate()//
					.arcX.is(0.5 * (2 + Auxiliary.tapSize))//
					.arcY.is(0.5 * (2 + Auxiliary.tapSize))//
					.background.is(0x99999999)//
					.left.is(1).top.is(1)//
					.width.is(2 + Auxiliary.tapSize)//
					.height.is(2 + Auxiliary.tapSize)//
					)//
					*/
					.sketch(new SketchPlate()//
					.arcX.is(0.5 * Auxiliary.tapSize)//
					.arcY.is(0.5 * Auxiliary.tapSize)//
					.background.is(Auxiliary.textColorPrimary)//
					.left.is(2).top.is(2)//
					.width.is(Auxiliary.tapSize)//
					.height.is(Auxiliary.tapSize)//
					)//
					.sketch(new SketchPlate()//
					.arcX.is(0.5 * (Auxiliary.tapSize - 2))//
					.arcY.is(0.5 * (Auxiliary.tapSize - 2))//
					.background.is(Auxiliary.colorBackground)//
					.left.is(3).top.is(3)//
					.width.is(Auxiliary.tapSize - 2)//
					.height.is(Auxiliary.tapSize - 2)//
					)//
					/*.sketch(new SketchContour()//
					.width.is(Layoutless.tapSize - 2)//
					.height.is(Layoutless.tapSize - 2)//
					.left.is(2)//
					.top.is(2)//
					.arcX.is(0.5 * (Layoutless.tapSize - 2))//
					.arcY.is(0.5 * (Layoutless.tapSize - 2))//
					.strokeColor.is(Layoutless.themeBlurColor)//
					.strokeWidth.is(1)//
					)//
					*/
					.sketch(new SketchLine()//
							.point(2 + 0.65 * Auxiliary.tapSize, 2 + 0.3 * Auxiliary.tapSize)//
							.point(2 + 0.8 * Auxiliary.tapSize, 2 + 0.5 * Auxiliary.tapSize)//
							.point(2 + 0.65 * Auxiliary.tapSize, 2 + 0.7 * Auxiliary.tapSize)//
					.strokeColor.is(Auxiliary.textColorHint)//
					.strokeWidth.is(1 + 0.08 * Auxiliary.tapSize)//
					)//
					.sketch(new SketchLine()//
							.point(2 + 0.35 * Auxiliary.tapSize, 2 + 0.3 * Auxiliary.tapSize)//
							.point(2 + 0.2 * Auxiliary.tapSize, 2 + 0.5 * Auxiliary.tapSize)//
							.point(2 + 0.35 * Auxiliary.tapSize, 2 + 0.7 * Auxiliary.tapSize)//
					.strokeColor.is(Auxiliary.textColorHint)//
					.strokeWidth.is(1 + 0.08 * Auxiliary.tapSize)//
					)//
					.width().is(Auxiliary.tapSize + 4)//
					.height().is(Auxiliary.tapSize + 4)//
					.top().is(height().property.minus(position.property.plus(1).multiply(Auxiliary.tapSize)).minus(0.5 * Auxiliary.tapSize))//
			);
		}
	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
}
