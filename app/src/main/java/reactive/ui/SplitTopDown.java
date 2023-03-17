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

public class SplitTopDown extends SubLayoutless {
	public NumericProperty<SplitTopDown> split;
	public NumericProperty<SplitTopDown> position;
	private Rake topSide;
	private Rake downSide;
	private boolean initialized = false;
	//private boolean firstOnSizeChanged = true;

	@Override
	public void requestLayout() {
		super.requestLayout();
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//super.onSizeChanged(w, h, oldw, oldh);
		//if (firstOnSizeChanged) {
		//	firstOnSizeChanged = false;
		//}
	}
	public SplitTopDown(Context context) {
		super(context);
	}
	public SplitTopDown(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SplitTopDown(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	/*@Override
	protected void onMeasureX() {
		//
	}*/
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	public SplitTopDown topSide(Rake v) {
		if (topSide != null) {
			this.removeView(topSide.view());
		}
		topSide = v;
		this.addView(topSide//
				.width().is(width().property)//
				.height().is(split.property)//
				.view()//
				, 0//
		);
		return this;
	}
	public SplitTopDown downSide(Rake v) {
		if (downSide != null) {
			this.removeView(downSide.view());
		}
		downSide = v;
		this.addView(downSide//
				.top().is(split.property)//
				.width().is(width().property)//
				.height().is(height().property.minus(split.property))//
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
			split = new NumericProperty<SplitTopDown>(this);
			split.is(50);
			position = new NumericProperty<SplitTopDown>(this);
			position.is(0);
			Task adjustSplit = new Task() {
				@Override
				public void doTask() {
					if (split.property.value() < 0) {
						split.is(0);
					}
					if (split.property.value() > height().property.value()) {
						split.is(height().property.value());
					}
				}
			};
			/*this.child(new Decor(this.getContext()).background.is(0x33999999)//
					.top().is(split.property.minus(2))//
					.width().is(width().property)//
					.height().is(5)//
			);
			this.child(new Decor(this.getContext()).background.is(0x99999999)//
					.top().is(split.property.minus(1))//
					.width().is(width().property)//
					.height().is(3)//
			);*/
			this.child(new Decor(this.getContext()).background.is(Auxiliary.textColorPrimary)//
					.top().is(split.property)//
					.width().is(width().property)//
					.height().is(1)//
			);
			this.child(new Decor(this.getContext())//
			.afterTap.is(new Task() {
				@Override
				public void doTask() {
					if (Math.abs(split.property.value() - height().property.value()) < Auxiliary.tapSize * 0.3 + 1) {
						split.is(0.7*height().property.value());
					}
					else {
						split.is(height().property.value());
					}
				}
			})//
			.dragY.is(split.property.minus(0.5 * Auxiliary.tapSize))//
			.afterDrag.is(adjustSplit)//
			.movableY.is(true)//
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
					.sketch(new SketchLine()//
							.point( 2 + 0.3 * Auxiliary.tapSize,2 + 0.65 * Auxiliary.tapSize)//
							.point( 2 + 0.5 * Auxiliary.tapSize,2 + 0.8 * Auxiliary.tapSize)//
							.point( 2 + 0.7 * Auxiliary.tapSize,2 + 0.65 * Auxiliary.tapSize)//
					.strokeColor.is(Auxiliary.textColorHint)//
					.strokeWidth.is(1 + 0.08 * Auxiliary.tapSize)//
					)//
					
					.sketch(new SketchLine()//
							.point( 2 + 0.3 * Auxiliary.tapSize,2 + 0.35 * Auxiliary.tapSize)//
							.point( 2 + 0.5 * Auxiliary.tapSize,2 + 0.2 * Auxiliary.tapSize)//
							.point( 2 + 0.7 * Auxiliary.tapSize,2 + 0.35 * Auxiliary.tapSize)//
					.strokeColor.is(Auxiliary.textColorHint)//
					.strokeWidth.is(1 + 0.08 * Auxiliary.tapSize)//
					)//
					
					.width().is(Auxiliary.tapSize + 4)//
					.height().is(Auxiliary.tapSize + 4)//
					.left().is(position.property.plus(1).multiply(Auxiliary.tapSize).minus(0.5 * Auxiliary.tapSize))//
			);
		}
	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
}
