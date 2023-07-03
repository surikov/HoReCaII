package reactive.ui;

import tee.binding.properties.*;

import android.graphics.*;

import android.view.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.util.*;

import reactive.ui.*;

import android.view.animation.*;

import tee.binding.properties.*;
import tee.binding.task.*;
import tee.binding.it.*;

import java.io.*;
import java.text.*;

public class KnobImage extends ImageButton implements Rake {
	//public NoteProperty<KnobImage> labelText = new NoteProperty<KnobImage>(this);
	private ToggleProperty<Rake> hidden = new ToggleProperty<Rake>(this);
	private NumericProperty<Rake> width = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> height = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> left = new NumericProperty<Rake>(this);
	private NumericProperty<Rake> top = new NumericProperty<Rake>(this);
	public ItProperty<KnobImage, Task> tap = new ItProperty<KnobImage, Task>(this);
	public ItProperty<KnobImage, Bitmap> bitmap = new ItProperty<KnobImage, Bitmap>(this);//Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rocket),200,100,true);
	boolean initialized = false;
	//Context context;
	Task reFit = new Task() {
		@Override
		public void doTask() {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(//
					width.property.value().intValue()//
					, height.property.value().intValue());
			params.leftMargin = left.property.value().intValue();
			params.topMargin = top.property.value().intValue();
			KnobImage.this.setLayoutParams(params);
		}
	};

	public KnobImage(Context context) {
		super(context);
		init();
	}

	public KnobImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public KnobImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		//this.context = c;
		bitmap.property.afterChange(new Task() {
			@Override
			public void doTask() {
				//setText(labelText.property.value());
				KnobImage.this.setImageBitmap(bitmap.property.value());
			}
		});
		width.property.afterChange(reFit).value(100);
		height.property.afterChange(reFit).value(100);
		left.property.afterChange(reFit);
		top.property.afterChange(reFit);
		setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tap.property.value() != null) {
					tap.property.value().doTask();
				}
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
		bitmap.property.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
	}
}
