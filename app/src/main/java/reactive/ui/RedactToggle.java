package reactive.ui;

import android.text.*;
import tee.binding.properties.*;
import tee.binding.task.Task;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;

public class RedactToggle extends CheckBox implements Rake {
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	public NoteProperty<RedactToggle> labelText = new NoteProperty<RedactToggle>(this);
	public ToggleProperty<RedactToggle> yes = new ToggleProperty<RedactToggle>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
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
			RedactToggle.this.setLayoutParams(params);
			RedactToggle.this.setWidth(width.property.value().intValue());
			RedactToggle.this.setHeight(height.property.value().intValue());
			RedactToggle.this.setMaxWidth(width.property.value().intValue());
			RedactToggle.this.setMaxHeight(height.property.value().intValue());
			RedactToggle.this.setMinWidth(width.property.value().intValue());
			RedactToggle.this.setMinHeight(height.property.value().intValue());
			//System.out.println("params.topMargin: " + params.topMargin+" / "+Decor.this.getLeft()+"x"+Decor.this.getTop()+"/"+Decor.this.getWidth()+"x"+Decor.this.getHeight());
		}
	};

	public RedactToggle(Context context) {
		super(context);
		init();
	}
	public RedactToggle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public RedactToggle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		width.property.afterChange(reFit).value(100);
		height.property.afterChange(reFit).value(100);
		left.property.afterChange(reFit);
		top.property.afterChange(reFit);
		setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!lock) {
					lock = true;
					yes.is(isChecked);
					lock = false;
				}
			}
		});
		labelText.property.afterChange(new Task() {
			@Override
			public void doTask() {
				setText(labelText.property.value());
			}
		});
		yes.property.afterChange(new Task() {
			@Override
			public void doTask() {
				if (!lock) {
					lock = true;
					setChecked(yes.property.value());
					lock = false;
				}
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
		labelText.property.unbind();
		yes.property.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
	}
}
