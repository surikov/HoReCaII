package reactive.ui;

import tee.binding.properties.*;

import android.content.*;
import android.graphics.Canvas;
import android.graphics.Rect;

public class SQLiteGridColumn {
	public NumericProperty<SQLiteGridColumn> width = new NumericProperty<SQLiteGridColumn>(this);
	public ToggleProperty<SQLiteGridColumn> noVerticalBorder = new ToggleProperty<SQLiteGridColumn>(this);
	public ToggleProperty<SQLiteGridColumn> noHorizontalBorder = new ToggleProperty<SQLiteGridColumn>(this);
	public NoteProperty<SQLiteGridColumn> headerText = new NoteProperty<SQLiteGridColumn>(this);
	public NoteProperty<SQLiteGridColumn> footerText = new NoteProperty<SQLiteGridColumn>(this);

	public void tap(int row) {
	}

	public Rake header(Context context) {
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
		header.labelText.is(headerText.property.value());
		return header;
	}

	public Rake footer(Context context) {
		Decor footer = new Decor(context) {
			//
			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				canvas.drawRect(new Rect(//under
						0//
						, 0//
						, width().property.value().intValue()//
						, 1 //
				), Auxiliary.paintLine);
			}
		};
		footer.setPadding(3, 0, 3, 2);
		footer.labelStyleSmallNormal();
		footer.labelAlignCenterTop();
		footer.labelText.is(footerText.property.value());
		return footer;
	}
}
