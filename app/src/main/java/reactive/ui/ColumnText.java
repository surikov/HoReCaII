package reactive.ui;

import android.content.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.widget.TextView.BufferType;

import java.util.*;

import tee.binding.it.*;
import tee.binding.properties.*;
import tee.binding.task.*;

public class ColumnText extends Column {
	public Vector<String> strings = new Vector<String>();
	public Vector<Task> tasks = new Vector<Task>();
	public Vector<Decor> cells = new Vector<Decor>();
	public Vector<Integer> backgrounds = new Vector<Integer>();
	//protected Paint linePaint = new Paint();
	protected Rect sz;
	int presell = -1;
	public NumericProperty<ColumnText> headerBackground = new NumericProperty<ColumnText>(this);
	public ToggleProperty<ColumnText> center = new ToggleProperty<ColumnText>(this);

	@Override
	public String export(int row) {
		if (row > -1 && row < strings.size()) {

			return strings.get(row);
		}
		return "";
	}

	@Override
	public void update(int row) {
		if (row >= 0 && row < cells.size()) {
			Decor cell = cells.get(row);
			if (row > -1 && row < backgrounds.size()) {
				if (backgrounds.get(row) != null) {
					cell.background.is(backgrounds.get(row));
				} else {
					cell.background.is(null);
				}
			}
			if (row > -1 && row < strings.size()) {
				cell.labelText.is(strings.get(row));
			} else {
				cell.labelText.is("");
			}
		}
	}

	@Override
	public Rake item(final int column, int row, Context context) {
		//linePaint.setColor((int) (Auxiliary.colorLine));
		Decor cell = new Decor(context, true) {
			//
			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				if (sz == null) {
					sz = new Rect();
				}
				//linePaint.setStrokeWidth(11);
				//linePaint.setColor(0xff6600ff);
				if (!noVerticalBorder.property.value()) {
					if (column > 0) {
						sz.left = 0;
						sz.top = 0;
						sz.right = 1;
						sz.bottom = height().property.value().intValue();
						canvas.drawRect(sz, Auxiliary.paintLine);//left
					}
				}
				if (!noHorizontalBorder.property.value()) {
					sz.left = 0;
					sz.top = height().property.value().intValue() - 1;
					sz.right = width().property.value().intValue();
					sz.bottom = height().property.value().intValue();
					canvas.drawRect(sz, Auxiliary.paintLine);//under
				}
			}
		};
		if(this.center.property.value()){
			cell.labelAlignCenterTop();
		}
		if (row > -1 && row < backgrounds.size()) {
			if (backgrounds.get(row) != null) {
				cell.background.is(backgrounds.get(row));
			}
		}
		cell.setPadding(3, 0, 3, 0);
		cell.labelStyleMediumNormal();
		//cell.labelAlignLeftCenter();
		//this.setupCell(cell);
		if (row > -1 && row < strings.size()) {
			cell.labelText.is(strings.get(row));
		}
		cells.add(cell);
		return cell;
	}

	public ColumnText cell(String s, Integer background, Task tap) {
		strings.add(s);
		tasks.add(tap);
		backgrounds.add(background);
		return this;
	}

	public ColumnText cell(String s) {
		//System.out.println(s);
		return cell(s, null, null);
	}

	public ColumnText cell(String s, Task tap) {
		return cell(s, null, tap);
	}

	public ColumnText cell(String s, Integer background) {
		return cell(s, background, null);
	}

	@Override
	public int count() {
		return strings.size();
	}

	public ColumnText() {
		this.width.is(150);
		//this.labelStyleMediumNormal();
		//linePaint.setColor(0x33666666);
		/*int c1 = 0xffff0000;
		int transp=c1 & 0xff000000;
		transp=transp/32;
		transp=transp & 0xff000000;
		transp=0x33000000;
		int pure=c1 & 0x00ffffff;
		c1=transp+pure;*/
		//linePaint.setAntiAlias(true);
		//linePaint.setFilterBitmap(true);
		//linePaint.setDither(true);
		//linePaint.setColor(Auxiliary.colorLine);
		//linePaint.setStrokeWidth(0);
		//linePaint.setst
		//linePaint.setStyle(Style.STROKE);
	}

	@Override
	public Rake header(Context context) {
		//Knob k = new Knob(context).labelText.is(title.property.value());
		//linePaint.setColor((int) (Auxiliary.colorLine));
		Decor titleDecor = new Decor(context) {
			//
			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				canvas.drawRect(new Rect(//under
						0//
						, height().property.value().intValue() - 1//
						, width().property.value().intValue()//
						, height().property.value().intValue() //
				), Auxiliary.paintLine);
			}
		};
		titleDecor.setPadding(3, 0, 3, 2);
		titleDecor.labelStyleSmallNormal();
		titleDecor.labelAlignCenterBottom();
		titleDecor.labelText.is(title.property);
		//System.out.println("column title "+title.property.value());
		return titleDecor;
	}

	@Override
	public void clear() {
		if (presell >= 0 && presell < cells.size()) {
			if (presell >= 0 && presell < backgrounds.size()) {
				if (backgrounds.get(presell) != null) {
					cells.get(presell).background.is(backgrounds.get(presell));
				} else {
					cells.get(presell).background.is(0);
				}
			}
		}
		strings.removeAllElements();
		backgrounds.removeAllElements();
		tasks.removeAllElements();
		//cells.removeAllElements();
	}

	@Override
	public void afterRowsTap(int row) {
		if (row > -1 && row < tasks.size()) {
			if (tasks.get(row) != null) {
				tasks.get(row).start();
			}
			//System.out.println("label "+strings.get(row));
		}
	}
	@Override
	public void showHighlight(int row) {
		if (row >= 0 && row < cells.size()) {
			//System.out.println("3");
			presell = row;
			cells.get(row).background.is(Auxiliary.colorSelection);
		}
	}
	@Override
	public void highlight(int row) {
		//System.out.println("text "+this.title.property.value()+" highlight "+row);
		if (presell >= 0 && presell < cells.size()) {
			if (presell >= 0 && presell < backgrounds.size()) {
				if (backgrounds.get(presell) != null) {
					//System.out.println("1");
					cells.get(presell).background.is(backgrounds.get(presell));
				} else {
					//System.out.println("2");
					cells.get(presell).background.is(0);
				}
			}
		}
		this.showHighlight(row);
		/*if (row >= 0 && row < cells.size()) {
			//System.out.println("3");
			presell = row;
			cells.get(row).background.is(Auxiliary.colorSelection);
		}*/
		//System.out.println("text "+this.title.property.value()+" highlight "+row+"/"+cells.get(row).background.property.value());
	}
}
