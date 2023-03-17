package reactive.ui;

import android.content.*;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.text.Html;

import java.util.*;

import tee.binding.it.Numeric;
import tee.binding.properties.NumericProperty;
import tee.binding.task.*;

public class ColumnDescription extends Column {
	public Vector<String> descriptions = new Vector<String>();
	//cell.html.is(Html.fromHtml("<p>" + strings.get(row) + "<br/><small>" + descriptions.get(row) + "</small></p>"));
	public Vector<String> strings = new Vector<String>();
	public Vector<Task> tasks = new Vector<Task>();
	public Vector<HTMLText> cells = new Vector<HTMLText>();
	public Vector<Integer> backgrounds = new Vector<Integer>();
	//protected Paint linePaint = new Paint();
	protected Rect sz;
	int presell = -1;
	public NumericProperty<ColumnDescription> headerBackground = new NumericProperty<ColumnDescription>(this);

	@Override
	public String export(int row) {
		String s = "";
		if (row > -1 && row < strings.size()) {
			if (strings.get(row) != null) {
				s = s + strings.get(row);
			}
		}
		s=s+" / ";
		if (row > -1 && row < descriptions.size()) {
			if (descriptions.get(row) != null) {
				s = s + descriptions.get(row);
			}
		}
		return s;
	}
	@Override
	public void update(int row) {
		if (row >= 0 && row < cells.size()) {
			HTMLText cell = cells.get(row);
			if (row > -1 && row < backgrounds.size()) {
				if (backgrounds.get(row) != null) {
					cell.background.is(backgrounds.get(row));
				}
				else {
					cell.background.is(null);
				}
			}
			if (row > -1 && row < strings.size()) {
				cell.html.is(Html.fromHtml("<p>" + strings.get(row) + "<br/><small>" + descriptions.get(row) + "</small></p>"));
			}
			else {
				cell.html.is(Html.fromHtml("<p> <br/><small> </small></p>"));
			}
		}
	}
	@Override
	public Rake item(final int column, int row, Context context) {
		//linePaint.setColor((int) (Auxiliary.colorLine));
		HTMLText cell = new HTMLText(context, true) {
			//
			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				if (sz == null) {
					sz = new Rect();
				}
				//linePaint.setStrokeWidth(11);
				//linePaint.setColor(0xff6600ff);
				if (column > 0) {
					if (!noVerticalBorder.property.value()) {
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
		if (row > -1 && row < backgrounds.size()) {
			if (backgrounds.get(row) != null) {
				cell.background.is(backgrounds.get(row));
			}
		}
		cell.setPadding(3, 0, 3, 0);
		//cell.labelStyleMediumNormal();
		//cell.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		cell.labelStyleMediumNormal();
		//cell.labelStyleLargeNormal();
		//cell.background.is(0x99ff00ff);
		//cell.left().is(111);
		if (row > -1 && row < strings.size()) {
			cell.html.is(Html.fromHtml("<p>" + strings.get(row) + "<br/><small>" + descriptions.get(row) + "</small></p>"));
			//cell.labelText.is(strings.get(row));
		}
		
		cells.add(cell);
		return cell;
	}
	public ColumnDescription cell(String s, Integer background, Task tap, String description) {
		strings.add(s);
		tasks.add(tap);
		backgrounds.add(background);
		descriptions.add(description);
		return this;
	}
	public ColumnDescription cell(String s) {
		return cell(s, null, null, null);
	}
	public ColumnDescription cell(String s, String description) {
		return cell(s, null, null, description);
	}
	public ColumnDescription cell(String s, Task tap) {
		return cell(s, null, tap, null);
	}
	public ColumnDescription cell(String s, Task tap, String description) {
		return cell(s, null, tap, description);
	}
	public ColumnDescription cell(String s, Integer background) {
		return cell(s, background, null, null);
	}
	public ColumnDescription cell(String s, Integer background, String description) {
		return cell(s, background, null, description);
	}
	@Override
	public int count() {
		return strings.size();
	}
	public ColumnDescription() {
		this.width.is(150);
		//linePaint.setColor(0x33ff0000);
		//linePaint.setColor(Auxiliary.colorLine);
		//linePaint.setAntiAlias(true);
		//linePaint.setFilterBitmap(true);
		//linePaint.setDither(true);
		//linePaint.setStrokeWidth(0);
		//linePaint.setst
		//linePaint.setStyle(Style.STROKE);
	}
	@Override
	public Rake header(Context context) {
		//Knob k = new Knob(context).labelText.is(title.property.value());
		//linePaint.setColor((int) (Auxiliary.colorLine));
		Decor header = new Decor(context) {
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
		header.setPadding(3, 0, 3, 2);
		header.labelStyleSmallNormal();
		header.labelAlignCenterBottom();
		header.labelText.is(title.property.value());
		return header;
	}
	@Override
	public void clear() {
		if (presell >= 0 && presell < cells.size()) {
			if (presell >= 0 && presell < backgrounds.size()) {
				if (backgrounds.get(presell) != null) {
					cells.get(presell).background.is(backgrounds.get(presell));
				}
				else {
					cells.get(presell).background.is(0);
				}
			}
		}
		strings.removeAllElements();
		backgrounds.removeAllElements();
		tasks.removeAllElements();
		descriptions.removeAllElements();
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
	public void highlight(int row) {
		if (presell >= 0 && presell < cells.size()) {
			if (presell >= 0 && presell < backgrounds.size()) {
				if (backgrounds.get(presell) != null) {
					cells.get(presell).background.is(backgrounds.get(presell));
				}
				else {
					cells.get(presell).background.is(0);
				}
			}
		}
		if (row >= 0 && row < cells.size()) {
			presell = row;
			cells.get(row).background.is(Auxiliary.colorSelection);
		}
	}
}
