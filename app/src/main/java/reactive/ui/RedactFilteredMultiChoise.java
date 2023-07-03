package reactive.ui;

import java.util.Collections;
import java.util.Vector;

import android.text.*;
import android.text.method.*;

import tee.binding.They;
import tee.binding.properties.*;
import tee.binding.task.Task;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import android.widget.TextView.BufferType;


public class RedactFilteredMultiChoise extends EditText implements Rake {

	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	public NoteProperty<RedactFilteredMultiChoise> filter = new NoteProperty<RedactFilteredMultiChoise>(this);
	Paint dot = new Paint();
	DataGrid grid = null;
	ColumnText lines;
	//IDRow[] stringsRows = null;
	boolean initialized = false;
	public Vector<CheckRow> rows = new Vector<CheckRow>();
	//public They<Integer> selection = new They<Integer>();
	Task reFit = new Task() {
		@Override
		public void doTask() {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(//
					width.property.value().intValue()//
					, height.property.value().intValue());
			params.leftMargin = left.property.value().intValue();
			params.topMargin = top.property.value().intValue();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.alignWithParent = true;
			RedactFilteredMultiChoise.this.setLayoutParams(params);
			RedactFilteredMultiChoise.this.setWidth(width.property.value().intValue());
			RedactFilteredMultiChoise.this.setHeight(height.property.value().intValue());
			RedactFilteredMultiChoise.this.setMaxWidth(width.property.value().intValue());
			RedactFilteredMultiChoise.this.setMaxHeight(height.property.value().intValue());
			RedactFilteredMultiChoise.this.setMinWidth(width.property.value().intValue());
			RedactFilteredMultiChoise.this.setMinHeight(height.property.value().intValue());
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int r = 1 + (int) (Auxiliary.tapSize * 0.05);
		canvas.drawCircle(width.property.value().intValue() - 3 * r, 3 * r, r, dot);
	}

	public RedactFilteredMultiChoise(Context context) {
		super(context);
		init();
	}

	public RedactFilteredMultiChoise(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RedactFilteredMultiChoise(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void pick() {//IDRow[] strings) {
		//stringsRows = strings;
		grid = new DataGrid(RedactFilteredMultiChoise.this.getContext());
		lines = new ColumnText();
		Auxiliary.pick(RedactFilteredMultiChoise.this.getContext(), "", new SubLayoutless(RedactFilteredMultiChoise.this.getContext())//
						.child(new RedactText(RedactFilteredMultiChoise.this.getContext()).text.is(filter.property)
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 0.3)
								.width().is(Auxiliary.tapSize * 10)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(grid.noHead.is(true).columns(new Column[]{lines.title.is("data")
								.width.is(Auxiliary.tapSize * 15 - Auxiliary.tapSize)
						})
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 1.0)
								.width().is(Auxiliary.tapSize * 10)
								.height().is(Auxiliary.tapSize * 8))//
						.width().is(Auxiliary.tapSize * 11)//
						.height().is(Auxiliary.tapSize * 9)//
				, null, null, null, null, null, null);
		refreshList();
	}

	void refreshList() {
		if (grid != null) {
			System.out.println("filter " + filter.property.value());
			grid.clearColumns();
			//String ss = "";
			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).txt.toUpperCase().indexOf(filter.property.value().trim().toUpperCase()) > -1) {
					if (rows.get(i).checked) {
						final int nn = i;
						Task rowtap = new Task() {
							public void doTask() {
								rows.get(nn).checked = false;
								refreshList();
							}
						};
						lines.cell("✔ " + rows.get(i).txt, rowtap);
						//if (ss.length() > 0) ss = ss + ", ";
						//ss = ss + items.get(i).row;
					}
				}
			}
			//setText(ss);
			for (int i = 0; i < rows.size(); i++) {
				if (rows.get(i).txt.toUpperCase().indexOf(filter.property.value().trim().toUpperCase()) > -1) {
					if (!rows.get(i).checked) {
						final int nn = i;
						Task rowtap = new Task() {
							public void doTask() {
								rows.get(nn).checked = true;
								refreshList();
							}
						};
						lines.cell("   " + rows.get(i).txt, rowtap);
					}
				}
			}
			grid.refresh();


		}
		setTextLabel();
	}

	void setTextLabel() {
		String ss = "";
		for (int i = 0; i < this.rows.size(); i++) {
			if (rows.get(i).checked) {
				if (ss.length() > 0) ss = ss + ", ";
				ss = ss + rows.get(i).txt;
			}
		}
		setText(ss);
	}

	void doSelect() {
		if (rows.size() > 0) {
			/*IDRow[] strings = new IDRow[items.size()];
			for (int i = 0; i < items.size(); i++) {
				strings[i] = items.get(i);
			}*/
			//Auxiliary.pickMultiChoice(this.getContext(), strings, selection);
			pick();//strings);
		}
	}

	void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		dot.setColor(Auxiliary.textColorHint);
		dot.setAntiAlias(true);
		setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					doSelect();
				}
				return true;
			}
		});
		setKeyListener(null);
		this.setFocusable(false);
		this.setFocusableInTouchMode(false);
		width.property.afterChange(reFit).value(100);
		height.property.afterChange(reFit).value(100);
		left.property.afterChange(reFit);
		top.property.afterChange(reFit);
		setGravity(android.view.Gravity.LEFT | android.view.Gravity.TOP);
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Small);
		setText("", BufferType.SPANNABLE);
		/*selection.afterChange(new Task() {
			@Override
			public void doTask() {
				Vector<String> values = new Vector<String>();
				for (int i = 0; i < selection.size(); i++) {
					int n = selection.at(i).intValue();
					String v = "?";
					if (n >= 0 && n < items.size()) {
						v = items.get(n);
					}
					values.add(v);
				}
				Collections.sort(values);
				String s = "";
				boolean first = true;
				for (int i = 0; i < values.size(); i++) {
					if (first) {
						first = false;
					} else {
						s = s + ", ";
					}
					s = s + values.get(i);
				}
				RedactFilteredMultiChoise.this.setText(s);
			}
		});*/
		filter.property.afterChange(new Task() {
			@Override
			public void doTask() {
				refreshList();
			}
		});
		hidden.property.afterChange(new Task() {
			@Override
			public void doTask() {
				if (hidden.property.value()) {
					setVisibility(View.INVISIBLE);
				} else {
					setVisibility(View.VISIBLE);
				}
			}
		});

	}

	@Override
	public ToggleProperty<Rake> hidden() {
		return hidden;
	}

	/*
		public RedactFilteredMultiChoise item(boolean checked, String id, String s) {
			//String nn=""+this.items.size();
			IDRow item = new IDRow(checked, id, s);
			this.items.add(item);
			//setTextLabel();
			return this;
		}
		*/
	public RedactFilteredMultiChoise items(Vector<CheckRow> items) {
		this.rows = items;
		this.refreshList();
		this.setTextLabel();
		return this;
	}

	@Override
	public NumericProperty<Rake> left() {
		return left;
	}

	@Override
	public NumericProperty<Rake> top() {
		return top;
	}

	@Override
	public NumericProperty<Rake> width() {
		return width;
	}

	@Override
	public NumericProperty<Rake> height() {
		return height;
	}

	@Override
	public View view() {
		return this;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
	}
}
