package reactive.ui;

import tee.binding.properties.*;
import android.view.*;
import android.app.Activity;
import android.content.*;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.*;
import android.util.*;
import android.widget.*;

import java.io.File;
import java.util.*;
import tee.binding.task.*;
import tee.binding.it.*;

public class SQLiteGrid extends SubLayoutless {
	public ToggleProperty<SQLiteGrid> noHead;
	public ToggleProperty<SQLiteGrid> noFoot;
	public NumericProperty<SQLiteGrid> headerHeight;
	public NumericProperty<SQLiteGrid> footerHeight;
	public NumericProperty<SQLiteGrid> rowHeight;
	//public ToggleProperty<ExtendedGrid> alignCenter;
	private boolean initialized = false;
	private SubLayoutless header;
	private SubLayoutless footer;
	private SQLiteGridColumn[] columns;
	private TableLayout tableLayout;
	private ScrollView scrollView;
	private Numeric margin;

	public SQLiteGrid(Context context) {
		super(context);
	}
	public SQLiteGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SQLiteGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	protected void init() {
		super.init();
		if (!initialized) {
			initialized = true;
			margin = new Numeric();
			//alignCenter = new ToggleProperty<ExtendedGrid>(this);
			noHead = new ToggleProperty<SQLiteGrid>(this);
			noHead.is(false);
			noFoot = new ToggleProperty<SQLiteGrid>(this);
			noFoot.is(true);
			headerHeight = new NumericProperty<SQLiteGrid>(this);
			headerHeight.is(Auxiliary.tapSize);
			header = new SubLayoutless(this.getContext());
			header.width().is(width().property);
			header.height().is(headerHeight.property);
			this.child(header);
			footerHeight = new NumericProperty<SQLiteGrid>(this);
			footerHeight.is(Auxiliary.tapSize);
			footer = new SubLayoutless(this.getContext());
			footer.width().is(width().property);
			footer.height().is(headerHeight.property);
			footer.top().is(height().property.minus(footerHeight.property));
			this.child(footer);
			rowHeight = new NumericProperty<SQLiteGrid>(this);
			rowHeight.is(Auxiliary.tapSize);
			tableLayout = new TableLayout(this.getContext());
			
			scrollView = new ScrollView(this.getContext()) {
				@Override
				public boolean onTouchEvent(MotionEvent event) {
					System.out.println("onTouchEvent " + event);
					return super.onTouchEvent(event);
				}
				@Override
				protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
					System.out.println("onScrollChanged " + left+", "+top+" / "+oldLeft+", "+oldTop);
					super.onScrollChanged(left, top, oldLeft, oldTop);
				}
			};
			scrollView.addView(tableLayout);
			this.addView(scrollView);
			new Numeric().bind(width().property).afterChange(new Task() {
				@Override
				public void doTask() {
					reFitGrid();
				}
			});
			new Numeric().bind(height().property).afterChange(new Task() {
				@Override
				public void doTask() {
					reFitGrid();
				}
			});
			new Numeric().bind(header.shiftX.property).afterChange(new Task() {
				@Override
				public void doTask() {
					reFitGrid();
				}
			});
			header.shiftX.property.bind(footer.shiftX.property);
		}
	}
	public SQLiteGrid columns(SQLiteGridColumn[] indata) {
		columns = indata;
		if (columns.length < 1) {
			return this;
		}
		int left = 0;
		for (int x = 0; x < columns.length; x++) {
			if (!noHead.property.value()) {
				Rake headerCell = columns[x].header(getContext());
				headerCell.height().is(headerHeight.property.value());
				headerCell.width().is(columns[x].width.property);
				headerCell.left().is(header.shiftX.property.plus(left).plus(margin));
				header.child(headerCell);
			}
			if (!noFoot.property.value()) {
				Rake footerCell = columns[x].footer(getContext());
				footerCell.height().is(footerHeight.property.value());
				footerCell.width().is(columns[x].width.property);
				footerCell.left().is(footer.shiftX.property.plus(left).plus(margin));
				footer.child(footerCell);
			}
			left = left + columns[x].width.property.value().intValue();
		}
		header.innerWidth.is(left);
		footer.innerWidth.is(left);
		return this;
	}
	private void reFitGrid() {
		int left = 0;
		//if (alignCenter.property.value()) {
		if (columns != null) {
			for (int x = 0; x < columns.length; x++) {
				left = left + columns[x].width.property.value().intValue();
			}
		}
		margin.value((width().property.value() - left) / 2);
		if (margin.value() < 0) {
			margin.value(0);
		}
		/*}
		else {
			margin.value(0);
		}*/
		double hh = headerHeight.property.value();
		if (noHead.property.value()) {
			hh = 0;
		}
		double fh = footerHeight.property.value();
		if (noFoot.property.value()) {
			fh = 0;
		}
		if (scrollView != null) {
			int scrw = width().property.value().intValue();
			int scrh = (int) (height().property.value() - hh - fh);
			int scrl = 0;
			int scrt = (int) hh;
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(scrw, scrh);
			params.topMargin = scrt;
			params.leftMargin = scrl;
			scrollView.setLayoutParams(params);
		}
		if (tableLayout != null) {
			FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) tableLayout.getLayoutParams();
			p.leftMargin = (int) (header.shiftX.property.value() + margin.value());
			tableLayout.setLayoutParams(p);
		}
	}
}
