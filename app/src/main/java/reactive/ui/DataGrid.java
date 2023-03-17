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

public class DataGrid extends SubLayoutless {
	public ToggleProperty<DataGrid> noHead;
	public ToggleProperty<DataGrid> noFoot;
	public static final int maxPageCount = 3;
	public NumericProperty<DataGrid> pageSize;
	public NumericProperty<DataGrid> dataOffset;
	public NumericProperty<DataGrid> headerHeight;
	public NumericProperty<DataGrid> footerHeight;
	public NumericProperty<DataGrid> rowHeight;
	public ToggleProperty<DataGrid> center;
	public int plucked = -1;
	//public int lastClickedRow = -1;

	//public NumericProperty<DataGrid> lastManualScrollY;
	public  Numeric margin;
	public ItProperty<DataGrid, Task> beforeFlip = new ItProperty<DataGrid, Task>(this);
	public ItProperty<DataGrid, Task> afterPluck = new ItProperty<DataGrid, Task>(this);
	public ItProperty<DataGrid, Task> afterTap = new ItProperty<DataGrid, Task>(this);
	public NumericProperty<DataGrid> pluckX;
	public NumericProperty<DataGrid> pluckY;
	//public MotionEvent lastPluckMotionEvent = null;
	public boolean pluckMode = false;
	//public View dispatchMotionEvent=null;
	private boolean lockAppend = false;
	Column[] columnsArray = null;
	ProgressBar progressBar;
	public boolean lockScroll = false;
	public  int currentPage = 0;
	TableLayout tableLayout;
	public GridScroll scrollView;
	Vector<TableRow> rows = new Vector<TableRow>();
	private boolean initialized = false;
	private SubLayoutless header;
	private SubLayoutless footer;

	public DataGrid(Context context) {
		super(context);
	}

	public DataGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DataGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void reset() {
		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).removeAllViews();
		}
		tableLayout.removeAllViews();
		rows.removeAllElements();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	/*
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(noFoot.property.value()){
		canvas.drawRect(new Rect(//under
				0//
				, height().property.value().intValue() - 1-footerHeight.property.value().intValue()//
				, width().property.value().intValue()//
				, height().property.value().intValue()-footerHeight.property.value().intValue() //
				), Auxiliary.paintLine);
		}
	}*/
	public DataGrid columns(Column[] indata) {
		columnsArray = indata;
		if (columnsArray.length < 1) {
			return this;
		}
		int left = 0;
		for (int x = 0; x < columnsArray.length; x++) {
			if (!noHead.property.value()) {
				Rake headerCell = columnsArray[x].header(getContext());
				headerCell.height().is(headerHeight.property.value());
				headerCell.width().is(columnsArray[x].width.property);
				headerCell.left().is(header.shiftX.property.plus(left).plus(margin));
				header.child(headerCell);
			}
			if (!noFoot.property.value()) {
				//System.out.println("foot "+x);
				Rake footerCell = new Decor(getContext()).labelText.is(columnsArray[x].footer.property).labelAlignCenterTop();
				footerCell.height().is(footerHeight.property.value());
				footerCell.width().is(columnsArray[x].width.property);
				footerCell.left().is(footer.shiftX.property.plus(left).plus(margin));
				footer.child(footerCell);
			}
			left = left + columnsArray[x].width.property.value().intValue();
		}
		header.innerWidth.is(left);
		footer.innerWidth.is(left);
		reset();
		flip();
		reFitGrid();
		return this;
	}

	public void clearColumns() {

		for (int i = 0; i < columnsArray.length; i++) {
			columnsArray[i].clear();
		}
	}

	public void dump() {
		System.out.println("dump currentPage " + currentPage);
		System.out.println("scrollView " + scrollView.getScrollY());
		//System.out.println("pageSize " + pageSize.property.value());
		//System.out.println("maxPageCount " + this.maxPageCount);
		//System.out.println("currentPage " + this.currentPage);
		System.out.println("dataOffset " + this.dataOffset.property.value());
		//System.out.println("lastManualScrollY " + lastManualScrollY.property.value());

	}

	public void refresh() {
		flip();
		//System.out.println("after flip grid refresh "+this.dataOffset.property.value());
		scrollView.scrollTo(0, 0);
		//System.out.println("after scrollTo grid refresh "+this.dataOffset.property.value());
	}

	private void flip() {
		//System.err.println("flip currentPage "+currentPage);
		//System.err.println("scrollView "+scrollView.getScrollY());
		currentPage = 0;
		append();
		//currentPage = 0;
		//System.err.println("scrollView "+scrollView.getScrollY());
	}

	public  void append() {
		if (lockAppend) {
			//System.out.println("append locked");
			return;
		}
		if (columnsArray == null) {
			return;
		}
		if (columnsArray.length > 0) {
			lockAppend = true;
			//currentPage++;
			//System.out.println("append "+currentPage+"/"+columnsArray[0].count() );
			scrollView.setOverScrollMode(OVER_SCROLL_NEVER);
			int start = (int) (currentPage * pageSize.property.value());
			for (int y = start; y < columnsArray[0].count() && y < (currentPage + 1) * pageSize.property.value(); y++) {
				TableRow tableRow;
				if (y < rows.size()) {
					tableRow = rows.get(y);
					tableRow.setVisibility(View.VISIBLE);
					for (int x = 0; x < columnsArray.length; x++) {
						this.columnsArray[x].update(y);
					}
				} else {
					tableRow = new TableRow(this.getContext());
					rows.add(tableRow);
					tableLayout.addView(tableRow);
					for (int x = 0; x < columnsArray.length; x++) {
						Rake r = columnsArray[x].item(x, y, getContext());
						r.height().is(rowHeight.property.value());
						r.width().is(columnsArray[x].width.property);
						tableRow.addView(r.view());
					}
				}
			}
			int lastDataRow = columnsArray[0].count();
			int lastPageRow = (int) ((currentPage + 1) * pageSize.property.value());
			int lastFilled = 0;
			if (lastDataRow < lastPageRow) {
				lastFilled = lastDataRow;
			} else {
				lastFilled = lastPageRow;
			}
			int rowSize = rows.size();
			for (int i = lastFilled; i < rowSize; i++) {
				rows.get(i).setVisibility(View.GONE);
			}
			lockAppend = false;
			scrollView.setOverScrollMode(OVER_SCROLL_IF_CONTENT_SCROLLS);
		}
	}

	//	void pressRow(int row) {
	//System.out.println("pressRow " + row);
	//}
	public void tapColumnRow(int row, int column) {
		//lastClickedRow=row;
		for (int x = 0; x < columnsArray.length; x++) {
			columnsArray[x].highlight(row);
		}
		if (column < columnsArray.length) {
			columnsArray[column].afterRowsTap(row);
		}
	}

	public void flipNext() {
		//System.err.println("flipNext");
		//System.out.println("flipNext currentPage "+currentPage+"/"+dataOffset.property.value());
		double off = dataOffset.property.value() + pageSize.property.value() * (maxPageCount - 1);
		dataOffset.is(off);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					clearColumns();
					beforeFlip.property.value().start();
				} catch (Throwable t) {
					//
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void v) {
				//System.err.println("onPostExecute");
				//currentPage = 0;
				flip();
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						//System.err.println("Handler 1");
						double hh = headerHeight.property.value();
						//System.err.println("Handler 2");
						if (noHead.property.value()) {
							hh = 0;
						}
						//System.err.println("Handler 3");
						double fh = footerHeight.property.value();
						if (noFoot.property.value()) {
							fh = 0;
						}
						//System.err.println("Handler 4");
						scrollView.scrollTo(0, (int) (//
								pageSize.property.value() * rowHeight.property.value() - hh - fh//
						));
						//System.err.println("Handler 5");
						lockScroll = false;
						//System.err.println("Handler 6");
						progressBar.setVisibility(View.INVISIBLE);
						//System.err.println("Handler 7");
						//scrollView.scroll(0);
						scrollView.scrollTo(scrollView.getScrollX(), scrollView.getScrollY() + 1);
						//System.err.println("scrollView.scrollTo "+scrollView.getScrollY());
					}
				});
			}
		}.execute();
	}

	public void flipPrev() {
		//System.out.println("flipPrev currentPage "+currentPage+"/"+dataOffset.property.value());
		double off = dataOffset.property.value() - pageSize.property.value() * (maxPageCount - 1);
		if (off < 0) {
			off = 0;
		}
		dataOffset.is(off);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					clearColumns();
					beforeFlip.property.value().start();
				} catch (Throwable t) {
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void v) {
				//currentPage = 0;
				flip();
				currentPage++;
				append();
				currentPage++;
				append();
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						int nn = (int) (2 * pageSize.property.value() * rowHeight.property.value());
						scrollView.scrollTo(0, nn);
						progressBar.setVisibility(View.INVISIBLE);
						lockScroll = false;
					}
				});
			}
		}.execute();
	}

	private void reFitGrid() {
		int left = 0;
		if (center.property.value()) {
			if (columnsArray != null) {
				for (int x = 0; x < columnsArray.length; x++) {
					left = left + columnsArray[x].width.property.value().intValue();
				}
			}
			margin.value((width().property.value() - left) / 2);
			if (margin.value() < 0) {
				margin.value(0);
			}
		} else {
			margin.value(0);
		}
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

	public void exportCurrentDataCSV(Activity activity, String fileNameInDownloadFolder, String encoding) {//encoding=windows-1251,utf-8
		try {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (int i = 0; i < this.columnsArray.length; i++) {
				if (!first) {
					sb.append(",");
				} else {
					first = false;
				}
				sb.append(columnsArray[i].title.property.value());
			}
			sb.append("\n");
			if (columnsArray.length > 0) {
				for (int r = 0; r < columnsArray[0].count(); r++) {
					first = true;
					for (int i = 0; i < this.columnsArray.length; i++) {
						if (!first) {
							sb.append(",");
						} else {
							first = false;
						}
						sb.append(columnsArray[i].export(r));
					}
					sb.append("\n");
				}
			}
			String f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileNameInDownloadFolder;
			File file = new File(f);
			Auxiliary.writeTextToFile(file, sb.toString(), "windows-1251");
			Auxiliary.inform(file.getAbsolutePath(), activity);
			Auxiliary.startFile(activity, android.content.Intent.ACTION_VIEW, "text/plain", file);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
/*
	public void scrollToOldPosition(int yy) {
		//System.out.println("scrollToOldPosition " + yy);
		this.scrollView.scrollTo(0,yy);
	}
*/
	@Override
	protected void init() {
		super.init();
		if (!initialized) {
			initialized = true;
			noHead = new ToggleProperty<DataGrid>(this);
			noFoot = new ToggleProperty<DataGrid>(this);
			noFoot.is(true);
			center = new ToggleProperty<DataGrid>(this);
			//lastManualScrollY = new NumericProperty<DataGrid>(this);

			margin = new Numeric();
			pluckX = new NumericProperty<DataGrid>(this);
			pluckY = new NumericProperty<DataGrid>(this);
			pageSize = new NumericProperty<DataGrid>(this);
			pageSize.is(33);
			dataOffset = new NumericProperty<DataGrid>(this);
			dataOffset.is(0);
			footerHeight = new NumericProperty<DataGrid>(this);
			footerHeight.is(Auxiliary.tapSize);
			footer = new SubLayoutless(this.getContext());
			footer.width().is(width().property);
			footer.height().is(footerHeight.property);
			footer.top().is(height().property.minus(footerHeight.property));
			this.child(footer);
			headerHeight = new NumericProperty<DataGrid>(this);
			headerHeight.is(Auxiliary.tapSize);
			header = new SubLayoutless(this.getContext());
			header.width().is(width().property);
			header.height().is(headerHeight.property);
			this.child(header);
			Decor footLine = new Decor(getContext()).background.is(Auxiliary.colorLine);
			footLine.left().is(header.shiftX.property);
			footLine.top().is(height().property.minus(footerHeight.property).minus(1));
			footLine.width().is(footer.innerWidth.property);
			footLine.height().is(1);
			footLine.hidden().is(noFoot.property);
			this.child(footLine);
			rowHeight = new NumericProperty<DataGrid>(this);
			rowHeight.is(Auxiliary.tapSize);
			tableLayout = new TableLayout(this.getContext());
			scrollView = new GridScroll(this.getContext(),this);

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
			progressBar = new ProgressBar(this.getContext(), null, android.R.attr.progressBarStyleLarge);
			this.addView(progressBar);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(//
					(int) (0.5 * Auxiliary.tapSize)//
					, (int) (0.5 * Auxiliary.tapSize));
			progressBar.setLayoutParams(params);
			progressBar.setVisibility(View.INVISIBLE);
			lockScroll = false;
			new Numeric().bind(header.shiftX.property).afterChange(new Task() {
				@Override
				public void doTask() {
					reFitGrid();
				}
			});
			header.shiftX.property.bind(footer.shiftX.property);
		}
	}
}
