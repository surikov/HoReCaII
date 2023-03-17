package reactive.ui;

import tee.binding.properties.*;
import android.graphics.*;

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

public class BigGrid extends SubLayoutless {
	private boolean initialized = false;
	int currentShiftX = 0;
	int currentShiftY = 0;
	int cellWidth = 80;
	int cellHeight = 60;
	int deltaX = 0;
	int deltaY = 0;

	public BigGrid(Context context) {
		super(context);
		this.setWillNotDraw(false);
	}
	public BigGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setWillNotDraw(false);
	}
	public BigGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setWillNotDraw(false);
	}
	@Override
	protected void init() {
		super.init();
		if (!initialized) {
			initialized = true;
			System.out.println("init");
			new Numeric().bind(shiftX.property).afterChange(new Task() {
				@Override
				public void doTask() {
					//System.out.println("shiftX" + ": " + shiftX.property.value() + "x" + shiftY.property.value() + " / " + currentShiftX + "x" + currentShiftY);
					BigGrid.this.postInvalidate();
				}
			});
			new Numeric().bind(shiftY.property).afterChange(new Task() {
				@Override
				public void doTask() {
					//System.out.println("shiftY" + ": " + shiftX.property.value() + "x" + shiftY.property.value() + " / " + currentShiftX + "x" + currentShiftY);
					BigGrid.this.postInvalidate();
				}
			});
			this.afterShift.is(new Task() {
				@Override
				public void doTask() {
					//System.out.println("afterShift" + ": " + shiftX.property.value() + "x" + shiftY.property.value() + " / " + currentShiftX + "x" + currentShiftY);
					//System.out.println( shiftX.property.value() + "x" + shiftY.property.value());
					currentShiftX = currentShiftX + shiftX.property.value().intValue();
					currentShiftY = currentShiftY + shiftY.property.value().intValue();
					deltaX = (currentShiftX + shiftX.property.value().intValue()) % cellWidth;
					deltaY = (currentShiftY + shiftY.property.value().intValue()) % cellHeight;
					shiftX.property.value(0);
					shiftY.property.value(0);
				}
			});
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		System.out.println("onDraw" + ": " + shiftX.property.value() + "x" + shiftY.property.value() + " / " + currentShiftX + "x" + currentShiftY);
		//System.out.println("onDraw " + canvas.getWidth() + "/" + canvas.getHeight() + ", " + shiftX.property.value() + "x" + shiftY.property.value());
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(0x66003300);
		//paint.setTextAlign(Align.)
		canvas.drawCircle(100, 100, 20, paint);
		canvas.drawText("testzdfbz fba fbfb sfb afb asfb", 50, 50, paint);
		//int deltaX = (currentShiftX + shiftX.property.value().intValue()) % cellWidth;
		//int deltaY = (currentShiftY + shiftY.property.value().intValue()) % cellHeight;
		int wCount = 12;
		int hCount = 8;
		for (int x = 0; x < wCount; x++) {
			for (int y = 0; y < hCount; y++) {
				int xx = //x * cellWidth+ deltaX;
				deltaX + x * cellWidth + shiftX.property.value().intValue();
				int yy = //y * cellHeight+ deltaY;
				deltaY + y * cellHeight + shiftY.property.value().intValue();
				String lbl = currentShiftX/cellWidth + "x" + currentShiftY/cellHeight;
				canvas.drawRect(//
						xx + 1//
						, yy + 1//
						, xx + cellWidth - 2//
						, yy + cellHeight - 2//
						, paint);
				canvas.drawText(//
						lbl//
						, xx + 1//
						, yy + 1 - paint.ascent()//
						, paint);
			}
		}
		//System.out.println(stX+"/"+stY);
		/*for (int x = 0; x < canvas.getWidth(); x = x + cellWidth) {
			for (int y = 0; y < canvas.getHeight(); y = y + cellHeight) {
				String l = cellWidth * Math.round((x + currentShiftX) / cellWidth)// 
						+ "x"// 
						+ cellHeight * Math.round((y + currentShiftY) / cellHeight);
				canvas.drawText(l//
						//, x + currentShiftX + shiftX.property.value().intValue()//
						//, y + currentShiftY + shiftY.property.value().intValue() - paint.ascent()//
						,x+stX//
						,y+stY- paint.ascent()//
						, paint);
			}
		}*/
	}
	public BigGrid _data() {
		int w = 90;
		int h = 70;
		int wc = 20;
		int hc = 22;
		//this.solid.is(true);
		SubLayoutless s = new SubLayoutless(this.getContext());
		this.child(s.solid.is(false)//
				.left().is(this.shiftX.property)//
				.top().is(this.shiftY.property)//
		);
		for (int x = 0; x < wc; x++) {
			for (int y = 0; y < hc; y++) {
				s.child(new Decor(this.getContext())//
				.labelText.is(x + "x" + y)//
						.left().is(x * w)//
						.top().is(y * h)//
						.width().is(w)//
						.height().is(h)//
				);
			}
		}
		this.innerWidth.is(wc * w);
		this.innerHeight.is(hc * h);
		s.width().is(wc * w);
		s.height().is(hc * h);
		return this;
	}
}
