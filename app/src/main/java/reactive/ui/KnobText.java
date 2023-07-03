package reactive.ui;

import android.text.*;
import android.app.*;

import tee.binding.properties.*;
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

public class KnobText extends EditText implements Rake {
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	//public NumericProperty<KnobText> date = new NumericProperty<KnobText>(this);
	public NoteProperty<KnobText> text = new NoteProperty<KnobText>(this);//dd.MM.yyyy, yyyy-MM-dd
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	public ItProperty<KnobText, Task> afterTap = new ItProperty<KnobText, Task>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	Paint dot = new Paint();
	boolean initialized = false;
	private boolean lock = false;
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
			KnobText.this.setLayoutParams(params);
			KnobText.this.setWidth(width.property.value().intValue());
			KnobText.this.setHeight(height.property.value().intValue());
			KnobText.this.setMaxWidth(width.property.value().intValue());
			KnobText.this.setMaxHeight(height.property.value().intValue());
			KnobText.this.setMinWidth(width.property.value().intValue());
			KnobText.this.setMinHeight(height.property.value().intValue());
			//System.out.println("params.topMargin: " + params.topMargin+" / "+Decor.this.getLeft()+"x"+Decor.this.getTop()+"/"+Decor.this.getWidth()+"x"+Decor.this.getHeight());
		}
	};

	public KnobText(Context context) {
		super(context);
		init();
	}

	public KnobText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public KnobText(Context context, AttributeSet attrs, int defStyle) {
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
		//this.draw(canvas);
		//this.onDraw(canvas)
		initialized = true;
		dot.setColor(Auxiliary.textColorHint);
		dot.setAntiAlias(true);
		//format.is("yyyy-MM-dd");
		setInputType(InputType.TYPE_NULL);
		setKeyListener(null);
		setFocusable(false);
		setFocusableInTouchMode(false);
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
					//System.out.println(motionEvent);
					if (afterTap.property.value() != null) {
						afterTap.property.value().doTask();
					}
				}
				return true;
			}
		});
		/*date.property.afterChange(new Task() {
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
		});*/
		text.property.afterChange(new Task() {
			@Override
			public void doTask() {
				setText(text.property.value());
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

	/*void resetLabel() {
		if (date.property.value() == 0) {
			setText("");
		}
		else {
			DateFormat to = new SimpleDateFormat(format.property.value());
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(date.property.value().longValue());
			setText(to.format(c.getTime()));
		}
	}*/
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
		afterTap.property.unbind();
		text.property.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
	}
}
