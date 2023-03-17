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

public class RedactMultiChoice extends EditText implements Rake {
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	//public NoteProperty<RedactSelection> text = new NoteProperty<RedactSelection>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	Paint dot = new Paint();
	boolean initialized = false;
	//private boolean lock = false;
	private Vector<String> items = new Vector<String>();
	//public Vector<Integer> selection = new Vector<Integer>();
	public They<Integer> selection = new They<Integer>();
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
			RedactMultiChoice.this.setLayoutParams(params);
			RedactMultiChoice.this.setWidth(width.property.value().intValue());
			RedactMultiChoice.this.setHeight(height.property.value().intValue());
			RedactMultiChoice.this.setMaxWidth(width.property.value().intValue());
			RedactMultiChoice.this.setMaxHeight(height.property.value().intValue());
			RedactMultiChoice.this.setMinWidth(width.property.value().intValue());
			RedactMultiChoice.this.setMinHeight(height.property.value().intValue());
			//System.out.println("params.topMargin: " + params.topMargin+" / "+Decor.this.getLeft()+"x"+Decor.this.getTop()+"/"+Decor.this.getWidth()+"x"+Decor.this.getHeight());
		}
	};
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int r = 1 + (int) (Auxiliary.tapSize * 0.05);
		canvas.drawCircle(width.property.value().intValue() - 3 * r, 3 * r, r, dot);
	}
	public RedactMultiChoice(Context context) {
		super(context);
		init();
	}
	public RedactMultiChoice(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public RedactMultiChoice(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	void doSelect() {
		if (items.size() > 0) {
			String[] strings = new String[items.size()];
			for (int i = 0; i < items.size(); i++) {
				strings[i] = items.get(i);
			}
			Auxiliary.pickMultiChoice(this.getContext(), strings, selection);
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
				// your code here....
				//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);  
				if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					//System.out.println("onTouch"+motionEvent);
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
		/*addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if (!lock) {
					lock = true;
					text.property.value(s.toString());
					lock = false;
				}
			}
		});
		text.property.afterChange(new Task() {
			@Override
			public void doTask() {
				
				if (!lock) {
					lock = true;
					setText(text.property.value());
					lock = false;
				}
			}
		});*/
		setGravity(android.view.Gravity.LEFT | android.view.Gravity.TOP);
		setTextAppearance(this.getContext(), android.R.style.TextAppearance_Small);
		setText("", BufferType.SPANNABLE);
		selection.afterChange(new Task() {
			@Override
			public void doTask() {
				//System.out.println("selection "+selection.size());
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
				String s = "";//selection "+selection.size();
				boolean first = true;
				for (int i = 0; i < values.size(); i++) {
					if (first) {
						first = false;
					}
					else {
						s = s + ", ";
					}
					s = s + values.get(i);
				}
				RedactMultiChoice.this.setText(s);
			}
		});
		hidden.property.afterChange(new Task() {
			@Override
			public void doTask() {
				if (hidden.property.value()) {
					setVisibility(View.INVISIBLE);
				}
				else {
					setVisibility(View.VISIBLE);
				}
			}
		});
	}
	@Override
	public ToggleProperty<Rake> hidden() {
		return hidden;
	}
	public RedactMultiChoice item(String s) {
		this.items.add(s);
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
		//text.property.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
	}
}
