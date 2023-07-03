package reactive.ui;

import java.util.Date;
import java.util.Vector;

import tee.binding.properties.*;
import tee.binding.task.Task;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.text.*;

public class ColumnNumeric extends Column {
	private DecimalFormat formater = new DecimalFormat();
	public Vector<Double> numbers = new Vector<Double>();
	public Vector<Task> tasks = new Vector<Task>();
	public Vector<Decor> cells = new Vector<Decor>();
	public Vector<Integer> backgrounds = new Vector<Integer>();
	//protected Paint linePaint = new Paint();
	protected Rect sz;
	int presell = -1;
	public NumericProperty<ColumnNumeric> headerBackground = new NumericProperty<ColumnNumeric>(this);
	public NoteProperty<ColumnNumeric> format = new NoteProperty<ColumnNumeric>(this);//http://docs.oracle.com/javase/1.4.2/docs/api/java/text/DecimalFormat.html

	@Override
	public String export(int row) {
		if (row > -1 && row < numbers.size()) {

			return formater.format(numbers.get(row));
		}
		return "";
	}

	String getValue(double d) {
		if (d == 0) {
			return "";
		} else {

			if (format.property.value().length() > 1) {
				//System.out.println(t+": "+(int)t+": "+d+": "+formater.format(d));
				return formater.format(d);
			} else {
				return "" + d;
			}
		}
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
			if (row > -1 && row < numbers.size()) {
				cell.labelText.is(getValue(numbers.get(row)));
				//cell.labelText.is(formater.format(numbers.get(row)));
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
		//cell.labelStyleMediumNormal();
		cell.labelStyleMediumNormal();
		//cell.labelAlignCenterCenter();
		//this.setupCellStyleAlign(cell);
		if (row > -1 && row < numbers.size()) {
			//cell.labelText.is(formater.format(numbers.get(row)));
			cell.labelText.is(getValue(numbers.get(row)));
		}
		cells.add(cell);
		return cell;
	}

	public ColumnNumeric cell(double s, Integer background, Task tap) {
		numbers.add(s);
		tasks.add(tap);
		backgrounds.add(background);
		//this.labelStyleMediumNormal();
		return this;
	}

	public ColumnNumeric cell(double s) {
		return cell(s, null, null);
	}

	public ColumnNumeric cell(double s, Task tap) {
		return cell(s, null, tap);
	}

	public ColumnNumeric cell(double s, Integer background) {
		return cell(s, background, null);
	}

	@Override
	public int count() {
		return numbers.size();
	}

	public ColumnNumeric() {
		this.width.is(150);
		//linePaint.setAntiAlias(true);
		//linePaint.setFilterBitmap(true);
		//linePaint.setDither(true);
		format.property.afterChange(new Task() {
			@Override
			public void doTask() {
				try {
					formater = new DecimalFormat(format.property.value());
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
		//this.labelStyleMediumNormal();
	}

	@Override
	public Rake header(Context context) {
		//linePaint.setColor((int) (Auxiliary.colorLine));
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
				} else {
					cells.get(presell).background.is(0);
				}
			}
		}
		numbers.removeAllElements();
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
				} else {
					cells.get(presell).background.is(0);
				}
			}
		}
		this.showHighlight(row);
	}
	@Override
	public void showHighlight(int row) {
		if (row >= 0 && row < cells.size()) {
			//System.out.println("3");
			presell = row;
			cells.get(row).background.is(Auxiliary.colorSelection);
		}
	}
}
