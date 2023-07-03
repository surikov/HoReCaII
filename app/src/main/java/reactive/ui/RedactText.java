package reactive.ui;

import android.text.*;

import tee.binding.properties.*;
import tee.binding.task.Task;

import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class RedactText extends EditText implements Rake {
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	public NoteProperty<RedactText> text = new NoteProperty<RedactText>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	public ToggleProperty<RedactText> singleLine = new ToggleProperty<RedactText>(this);
	public ToggleProperty<RedactText> password = new ToggleProperty<RedactText>(this);
	public ToggleProperty<RedactText> selectAllOnFocus = new ToggleProperty<RedactText>(this);
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
			RedactText.this.setLayoutParams(params);
			RedactText.this.setWidth(width.property.value().intValue());
			RedactText.this.setHeight(height.property.value().intValue());
			RedactText.this.setMaxWidth(width.property.value().intValue());
			RedactText.this.setMaxHeight(height.property.value().intValue());
			RedactText.this.setMinWidth(width.property.value().intValue());
			RedactText.this.setMinHeight(height.property.value().intValue());
			//System.out.println("params.topMargin: " + params.topMargin+" / "+Decor.this.getLeft()+"x"+Decor.this.getTop()+"/"+Decor.this.getWidth()+"x"+Decor.this.getHeight());
		}
	};

	public RedactText(Context context) {
		super(context);
		init();
	}

	public RedactText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RedactText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void reSetInputMethod() {
		if (password.property.value()) {
			this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else {
			this.setInputType(InputType.TYPE_CLASS_TEXT);
		}
	}

	void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		//this.setSingleLine(true);


		width.property.afterChange(reFit).value(100);
		height.property.afterChange(reFit).value(100);
		left.property.afterChange(reFit);
		top.property.afterChange(reFit);
		//setSingleLine(singleLine.property.value());
		this.setMaxLines(singleLine.property.value() ? 1 : 10);
		reSetInputMethod();
		this.setSelectAllOnFocus(selectAllOnFocus.property.value());
		addTextChangedListener(new TextWatcher() {
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
		});
		singleLine.property.afterChange(new Task() {
			@Override
			public void doTask() {
				//setSingleLine(singleLine.property.value());
				setMaxLines(singleLine.property.value() ? 1 : 10);
			}
		});

		password.property.afterChange(new Task() {
			@Override
			public void doTask() {
				reSetInputMethod();
			}
		});
		selectAllOnFocus.property.afterChange(new Task() {
			@Override
			public void doTask() {
				setSelectAllOnFocus(selectAllOnFocus.property.value());
			}
		});
		this.setGravity(Gravity.TOP);
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
		//System.out.println("<-----------------------------------");
		//this.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
		//this.setTransformationMethod(PasswordTransformationMethod.getInstance());
		//this.setSelection(this.getText().length());
		//System.out.println("----------------------------------->");
	}

	@Override
	public ToggleProperty<Rake> hidden() {
		return hidden;
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
		text.property.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
	}
}
