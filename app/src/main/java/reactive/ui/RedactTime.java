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

public class RedactTime extends EditText implements Rake {
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	public NumericProperty<RedactTime> time = new NumericProperty<RedactTime>(this);
	public NoteProperty<RedactTime> format = new NoteProperty<RedactTime>(this);//dd.MM.yyyy, yyyy-MM-dd
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	Paint dot = new Paint();
	boolean initialized = false;
	private boolean lock = false;
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
			RedactTime.this.setLayoutParams(params);
			RedactTime.this.setWidth(width.property.value().intValue());
			RedactTime.this.setHeight(height.property.value().intValue());
			RedactTime.this.setMaxWidth(width.property.value().intValue());
			RedactTime.this.setMaxHeight(height.property.value().intValue());
			RedactTime.this.setMinWidth(width.property.value().intValue());
			RedactTime.this.setMinHeight(height.property.value().intValue());
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int r = 1 + (int) (Auxiliary.tapSize * 0.05);
		canvas.drawCircle(width.property.value().intValue() - 3 * r, 3 * r, r, dot);
	}

	public RedactTime(Context context) {
		super(context);
		init();
	}

	public RedactTime(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RedactTime(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		dot.setColor(Auxiliary.textColorHint);
		dot.setAntiAlias(true);
		format.is("kk:mm");
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
					c.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
					c.setTimeInMillis(time.property.value().longValue());
					if (time.property.value() == 0) {
						c.setTimeInMillis(new Date().getTime());
					}
					//c.set(Calendar.YEAR, 0);
					//c.set(Calendar.MONTH, 0);
					//c.set(Calendar.DAY_OF_MONTH, 1);
					//c.set(Calendar.HOUR, 0);
					//c.set(Calendar.MINUTE, 0);
					//c.set(Calendar.SECOND, 0);
					//c.set(Calendar.MILLISECOND, 0);
					//System.out.println("show "+c.get(Calendar.HOUR));
					new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							Calendar lc = Calendar.getInstance();
							lc.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
							lc.setTimeInMillis(time.property.value().longValue());
							//lc.set(Calendar.YEAR, 0);
							//lc.set(Calendar.MONTH, 0);
							//lc.set(Calendar.DAY_OF_MONTH, 1);
							lc.set(Calendar.HOUR_OF_DAY, hourOfDay);
							lc.set(Calendar.MINUTE, minute);
							//lc.set(Calendar.SECOND, 0);
							//lc.set(Calendar.MILLISECOND, 0);
							time.property.value((double) lc.getTimeInMillis());
						}
					}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
				}
				return true;
			}
		});
		time.property.afterChange(new Task() {
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
		if (time.property.value() == 0) {
			setText("");
		} else {
			DateFormat to = new SimpleDateFormat(format.property.value());
			to.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));

			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(time.property.value().longValue());
			setText(to.format(c.getTime()));
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
		time.property.unbind();
		format.property.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
	}
}
