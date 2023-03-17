package reactive.ui;

import tee.binding.properties.*;
import tee.binding.task.*;
import android.graphics.*;
import android.text.*;
import android.text.Layout.Alignment;
import android.widget.RelativeLayout;

import java.util.*;

public class SketchText extends Sketch {
	/*public NumericProperty<SketchText> width = new NumericProperty<SketchText>(this);
	public NumericProperty<SketchText> height = new NumericProperty<SketchText>(this);
	public NumericProperty<SketchText> left = new NumericProperty<SketchText>(this);
	public NumericProperty<SketchText> top = new NumericProperty<SketchText>(this);*/
	public NumericProperty<SketchText> width = new NumericProperty<SketchText>(this);
	public NumericProperty<SketchText> height = new NumericProperty<SketchText>(this);
	public NumericProperty<SketchText> left = new NumericProperty<SketchText>(this);
	public NumericProperty<SketchText> top = new NumericProperty<SketchText>(this);
	public NumericProperty<SketchText> color = new NumericProperty<SketchText>(this);
	public NoteProperty<SketchText> text = new NoteProperty<SketchText>(this);
	public ItProperty<SketchText, Typeface> typeface = new ItProperty<SketchText, Typeface>(this); // .face.is(Typeface.createFromAsset(me.getAssets(), "fonts/PoiretOne-Regular.ttf"))
	public NumericProperty<SketchText> size = new NumericProperty<SketchText>(this);
	private TextPaint paint = new TextPaint();
	StaticLayout staticLayout;
	//Bitmap bm = null;
	Rect src = null;
	Rect dest = null;
	Paint bmPaint = new Paint();
	//boolean valid = false;
	//boolean released = false;
	//private StaticLayout staticLayout;

	Task reFit = new Task() {
		@Override
		public void doTask() {
			//resetLayout();
			//valid = false;
			staticLayout = new StaticLayout(text.property.value(), paint, width.property.value().intValue(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

			postInvalidate.start();
		}
	};

	public SketchText() {
		color.is(Auxiliary.textColorPrimary);
		paint.setColor(color.property.value().intValue());
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		size.is(14);
		//paint.setTextSize(17);
		//paint.setColor(color.property.value().intValue());
		//aint.sett
		text.is("?");
		text.property.afterChange(new Task() {
			@Override
			public void doTask() {
				//resetLayout();
				//valid = false;
				reFit.start();
				postInvalidate.start();
			}
		});
		paint.setDither(true);
		color.property.afterChange(new Task() {
			@Override
			public void doTask() {
				paint.setColor(color.property.value().intValue());
				//resetLayout();
				//valid = false;
				reFit.start();
				postInvalidate.start();
			}
		});
		typeface.property.afterChange(new Task() {
			@Override
			public void doTask() {
				paint.setTypeface(typeface.property.value());
				//resetLayout();
				//valid = false;
				reFit.start();
				postInvalidate.start();
			}
		});
		size.property.afterChange(new Task() {
			@Override
			public void doTask() {
				paint.setTextSize(size.property.value().floatValue());
				//resetLayout();
				//valid = false;
				postInvalidate.start();
			}
		});
		width.property.afterChange(reFit).value(100);
		height.property.afterChange(reFit).value(100);
		left.property.afterChange(reFit);
		top.property.afterChange(reFit);
		//System.out.println(paint.getColor());
	}

	@Override
	public void unbind() {
		super.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
		color.property.unbind();
		text.property.unbind();
		typeface.property.unbind();
		size.property.unbind();
		/*if (bm != null) {
			bm.recycle();
			bm = null;
		}
		released = true;*/
		//System.out.println("released");
		/*width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();*/
	}/*
		void resetLayout() {
		if (valid) {
			return;
		}
		valid = true;
		int w = width.property.value().intValue();
		int h = height.property.value().intValue();
		int l = left.property.value().intValue();
		int t = top.property.value().intValue();
		if (w > 0 && h > 0) {
			bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
			src = new Rect(0, 0, w, h);
			dest = new Rect(l, t, l + w, t + h);
			Canvas canvas = new Canvas(bm);
			StaticLayout staticLayout = new StaticLayout(text.property.value(), paint, w, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
			staticLayout.draw(canvas);
		}
		}*/

	@Override
	public void draw(Canvas canvas) {
		int w = width.property.value().intValue();
		int h = height.property.value().intValue();

		if (w > 0 && h > 0 && staticLayout != null) {
			canvas.save();
			int textX = left.property.value().intValue();
			int textY = top.property.value().intValue();
			canvas.translate(textX, textY);
			staticLayout.draw(canvas);
			canvas.restore();
		}
		//resetLayout();
		//System.out.println("draw");
		//float x=Math.abs(paint.getFontMetrics().ascent) + Math.abs(paint.getFontMetrics().descent);		
		//canvas.drawText(text.property.value(), 0, x, paint);
		//TextPaint mTextPaint=new TextPaint();
		//canvas.save();
		// calculate x and y position where your text will be placed
		//int textX = left.property.value().intValue();
		//int textY = top.property.value().intValue();
		//canvas.translate(textX, textY);
		//boolean is_ok = canvas.clipRect(0, 0, width.property.value().intValue(), height.property.value().intValue(), Region.Op.REPLACE);
		//staticLayout.draw(canvas);
		//canvas.restore();
		/*if (bm != null) {
			canvas.drawBitmap(bm, src, dest, bmPaint);
		}*/
	}
}
