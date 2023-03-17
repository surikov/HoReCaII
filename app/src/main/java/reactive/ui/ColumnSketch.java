package reactive.ui;

import java.util.*;
import tee.binding.properties.*;
import tee.binding.task.*;
import android.content.*;
import android.graphics.*;



public class ColumnSketch extends Column {
	public Vector<Sketch> sketches = new Vector<Sketch>();
	public Vector<Task> tasks = new Vector<Task>();
	public Vector<Decor> cells = new Vector<Decor>();
	public Vector<Integer> backgrounds = new Vector<Integer>();
	protected Rect sz;
	int presell = -1;
	public NumericProperty<ColumnSketch> headerBackground = new NumericProperty<ColumnSketch>(this);
	@Override
	public String export(int row) {
		return "";
	}
	@Override
	public void update(int row) {
		if (row >= 0 && row < cells.size()) {
			Decor cell = cells.get(row);
			if (row > -1 && row < backgrounds.size()) {
				if (backgrounds.get(row) != null) {
					cell.background.is(backgrounds.get(row));
				}
				else {
					cell.background.is(null);
				}
			}
			cell.clear();
			if (row > -1 && row < sketches.size()) {
				cell.sketch(sketches.get(row));
			}
		}
	}
	@Override
	public Rake item(final int column, int row, Context context) {
		Decor cell = new Decor(context, true) {
			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				if (sz == null) {
					sz = new Rect();
				}
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
		if (row > -1 && row < backgrounds.size()) {
			if (backgrounds.get(row) != null) {
				cell.background.is(backgrounds.get(row));
			}
		}
		cell.setPadding(3, 0, 3, 0);
		cell.labelStyleMediumNormal();
		
		if (row > -1 && row < sketches.size()) {
			cell.sketch(sketches.get(row));
		}
		cells.add(cell);
		return cell;
	}
	public ColumnSketch cell(Sketch s, Integer background, Task tap) {
		sketches.add(s);
		tasks.add(tap);
		backgrounds.add(background);
		return this;
	}
	public ColumnSketch cell(Sketch s) {
		return cell(s, null, null);
	}
	public ColumnSketch cell(Sketch s, Task tap) {
		return cell(s, null, tap);
	}
	public ColumnSketch cell(Sketch s, Integer background) {
		return cell(s, background, null);
	}
	@Override
	public int count() {
		return sketches.size();
	}
	public ColumnSketch() {
		this.width.is(150);
	}
	@Override
	public Rake header(Context context) {
		Decor header = new Decor(context) {
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
		sketches.removeAllElements();
		backgrounds.removeAllElements();
		tasks.removeAllElements();
	}
	@Override
	public void afterRowsTap(int row) {
		if (row > -1 && row < tasks.size()) {
			if (tasks.get(row) != null) {
				tasks.get(row).start();
			}
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
