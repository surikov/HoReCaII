package reactive.ui;

import android.text.*;

import tee.binding.properties.*;
import tee.binding.task.Task;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import java.util.*;

public class RedactFilteredSingleChoice extends EditText implements Rake {
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	public NumericProperty<RedactFilteredSingleChoice> selection = new NumericProperty<RedactFilteredSingleChoice>(this);
	//public NoteProperty<RedactFilteredSingleChoice> textLabel = new NoteProperty<RedactFilteredSingleChoice>(this);
	public NoteProperty<RedactFilteredSingleChoice> filter = new NoteProperty<RedactFilteredSingleChoice>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	Paint dot = new Paint();
	DataGrid grid;
	ColumnText lines;
	boolean initialized = false;
	String[] stringsRows = null;
	/*Task rowtap=new Task(){
		public void doTask(){
			System.out.println("selection "+selection.property.value());
		}
	};*/
	//private boolean lock = false;
	public Vector<String> items = new Vector<String>();
	Task reFit = new Task() {
		@Override
		public void doTask() {
			//System.out.
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(//
					width.property.value().intValue()//
					, height.property.value().intValue());
			params.leftMargin = left.property.value().intValue();
			params.topMargin = top.property.value().intValue();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.alignWithParent = true;
			RedactFilteredSingleChoice.this.setLayoutParams(params);
			RedactFilteredSingleChoice.this.setWidth(width.property.value().intValue());
			RedactFilteredSingleChoice.this.setHeight(height.property.value().intValue());
			RedactFilteredSingleChoice.this.setMaxWidth(width.property.value().intValue());
			RedactFilteredSingleChoice.this.setMaxHeight(height.property.value().intValue());
			RedactFilteredSingleChoice.this.setMinWidth(width.property.value().intValue());
			RedactFilteredSingleChoice.this.setMinHeight(height.property.value().intValue());
			//System.out.println("params.topMargin: " + params.topMargin+" / "+Decor.this.getLeft()+"x"+Decor.this.getTop()+"/"+Decor.this.getWidth()+"x"+Decor.this.getHeight());
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int r = 1 + (int) (Auxiliary.tapSize * 0.05);
		canvas.drawCircle(width.property.value().intValue() - 3 * r, 3 * r, r, dot);
	}

	public RedactFilteredSingleChoice(Context context) {
		super(context);
		init();
	}

	public RedactFilteredSingleChoice(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RedactFilteredSingleChoice(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void pick(String[] strings) {
		stringsRows = strings;
		grid = new DataGrid(RedactFilteredSingleChoice.this.getContext());
		lines = new ColumnText();
		Auxiliary.pick(RedactFilteredSingleChoice.this.getContext(), "", new SubLayoutless(RedactFilteredSingleChoice.this.getContext())//
						.child(new RedactText(RedactFilteredSingleChoice.this.getContext()).text.is(filter.property)
								.left().is(Auxiliary.tapSize * 0.5)
								.top().is(Auxiliary.tapSize * 0.3)
								.width().is(Auxiliary.tapSize * 10)
								.height().is(Auxiliary.tapSize * 0.7))//
						.child(grid.noHead.is(true).columns(new Column[]{
								lines.title.is("data").width.is(Auxiliary.tapSize * 15 - Auxiliary.tapSize)
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
		if (stringsRows != null) {
			System.out.println("filter " + filter.property.value());
			grid.clearColumns();
			for (int i = 0; i < stringsRows.length; i++) {
				if (stringsRows[i].toUpperCase().indexOf(filter.property.value().trim().toUpperCase()) > -1) {
					final int nn = i;
					Task rowtap = new Task() {
						public void doTask() {
							selection.property.value(nn);
							//System.out.println("selection " + nn + "/" + selection.property.value());
						}
					};
					//rowtap
					lines.cell(stringsRows[i], rowtap);
				}
			}
			grid.refresh();
		}
	}

	void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		dot.setColor(Auxiliary.textColorHint);
		//dot.setColor(Auxiliary.textColorLink);
		dot.setAntiAlias(true);
		setInputType(InputType.TYPE_NULL);
		setKeyListener(null);
		setFocusable(false);
		setFocusableInTouchMode(false);
		//format.is("yyyy-MM-dd");
		width.property.afterChange(reFit).value(100);
		height.property.afterChange(reFit).value(100);
		left.property.afterChange(reFit);
		top.property.afterChange(reFit);
		//setOnClickListener(new View.OnClickListener() {
		setOnTouchListener(new View.OnTouchListener() {
			@Override
			//public void onClick(View v) {
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					if (items.size() > 0) {
						String[] strings = new String[items.size()];
						for (int i = 0; i < items.size(); i++) {
							strings[i] = items.get(i);
						}
						//Auxiliary.pickSingleChoice(RedactFilteredSingleChoice.this.getContext(), strings, selection.property);
						pick(strings);
					}
				}
				return true;
			}
		});
		selection.property.afterChange(new Task() {
			@Override
			public void doTask() {
				resetLabel();
			}
		});
		filter.property.afterChange(new Task() {
			@Override
			public void doTask() {

				refreshList();
			}
		});
		//selection.is(-1);
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

	public RedactFilteredSingleChoice item(String s) {
		this.items.add(s);
		resetLabel();
		return this;
	}

	void resetLabel() {
		String s = "";
		int n = selection.property.value().intValue();
		if (n >= 0 && n < items.size()) {
			s = items.get(n);
		}
		setText(s);
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
		//textLabel.property.unbind();
		selection.property.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
	}
}
