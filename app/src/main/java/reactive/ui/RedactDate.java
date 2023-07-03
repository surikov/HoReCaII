package reactive.ui;

import android.text.*;
import android.app.*;

import tee.binding.properties.*;
import tee.binding.task.Task;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RedactDate extends EditText implements Rake {

	public NumericProperty<RedactDate> date = new NumericProperty<RedactDate>(this);
	public NoteProperty<RedactDate> format = new NoteProperty<RedactDate>(this);//dd.MM.yyyy, yyyy-MM-dd
	Paint dot = new Paint();
	boolean initialized = false;
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
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
			RedactDate.this.setLayoutParams(params);
			RedactDate.this.setWidth(width.property.value().intValue());
			RedactDate.this.setHeight(height.property.value().intValue());
			RedactDate.this.setMaxWidth(width.property.value().intValue());
			RedactDate.this.setMaxHeight(height.property.value().intValue());
			RedactDate.this.setMinWidth(width.property.value().intValue());
			RedactDate.this.setMinHeight(height.property.value().intValue());
		}
	};
	private boolean lock = false;

	public RedactDate(Context context) {
		super(context);
		init();
	}

	public RedactDate(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RedactDate(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int r = 1 + (int) (Auxiliary.tapSize * 0.05);
		canvas.drawCircle(width.property.value().intValue() - 3 * r, 3 * r, r, dot);
	}

	void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		dot.setColor(Auxiliary.textColorHint);
		dot.setAntiAlias(true);
		format.is("yyyy-MM-dd");
		setInputType(InputType.TYPE_NULL);
		setKeyListener(null);
		setFocusable(false);
		setFocusableInTouchMode(false);
		width.property.afterChange(reFit).value(100);
		height.property.afterChange(reFit).value(100);
		left.property.afterChange(reFit);
		top.property.afterChange(reFit);
		setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					Calendar c = Calendar.getInstance();
					//c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
					c.setTimeInMillis(date.property.value().longValue());
					if (date.property.value() == 0) {
						c.setTimeInMillis(new Date().getTime());
					}
					/*c.set(Calendar.HOUR, 0);
					c.set(Calendar.MINUTE, 0);
					c.set(Calendar.SECOND, 0);
					c.set(Calendar.MILLISECOND, 0);*/
					//System.out.println("start from " + c.get(Calendar.YEAR)+"/"+ c.get(Calendar.MONTH)+"/" +c.get(Calendar.DAY_OF_MONTH));
					//System.out.println("calendar " + c);
					c.clear(Calendar.ZONE_OFFSET);
					//System.out.println("now calendar " + c);
					//System.out.println("date " + c.getTime());
					new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
							//System.out.println("onDateSet "+year+"/"+ monthOfYear+"/"+ dayOfMonth);
							Calendar newCalendar = Calendar.getInstance();
							//newCalendar.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
							newCalendar.setTimeInMillis(date.property.value().longValue());
							newCalendar.set(Calendar.YEAR, year);
							newCalendar.set(Calendar.MONTH, monthOfYear);
							newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
							//newCalendar.set(Calendar.HOUR, 0);
							//newCalendar.set(Calendar.MINUTE, 0);
							//newCalendar.set(Calendar.SECOND, 0);
							//newCalendar.set(Calendar.MILLISECOND, 0);
							//System.out.println("set "+newCalendar);
							date.property.value((double) newCalendar.getTimeInMillis());
						}
					}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
				}
				return true;
			}
		});
		date.property.afterChange(new Task() {
			@Override
			public void doTask() {
				resetLabel();
			}
		});
		format.property.afterChange(new Task() {
			@Override
			public void doTask() {
				resetLabel();
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

	void resetLabel() {
		if (date.property.value() == 0) {
			setText("");
		} else {
			DateFormat to = new SimpleDateFormat(format.property.value());
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(date.property.value().longValue());
			String v = to.format(c.getTime());
			setText(v);
			//System.out.println("resetLabel "+c);
			//System.out.println("date "+c.getTime());
			//System.out.println("text "+v);
		}
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
		date.property.unbind();
		format.property.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
	}
}
