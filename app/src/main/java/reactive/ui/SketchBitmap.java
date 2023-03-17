package reactive.ui;

import android.graphics.*;
import tee.binding.properties.*;
import tee.binding.task.*;

public class SketchBitmap extends Sketch {
	public ItProperty<SketchBitmap, Bitmap> bitmap = new ItProperty<SketchBitmap, Bitmap>(this);
	Paint paint = new Paint();
	public NumericProperty<SketchBitmap> width = new NumericProperty<SketchBitmap>(this);
	public NumericProperty<SketchBitmap> height = new NumericProperty<SketchBitmap>(this);
	public NumericProperty<SketchBitmap> left = new NumericProperty<SketchBitmap>(this);
	public NumericProperty<SketchBitmap> top = new NumericProperty<SketchBitmap>(this);
	public SketchBitmap() {
		paint.setColor(0xff000000);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
	}
	@Override
	public void draw(Canvas canvas) {
		if (bitmap.property.value() != null) {
			int w = bitmap.property.value().getWidth();
			int h = bitmap.property.value().getHeight();
			float l = (float) (this.left.property.value() + (this.width.property.value() - w) / 2);
			float t = (float) (this.top.property.value() + (this.height.property.value() - w) / 2);
			//System.out.println(l+"x"+t+"/"+
			//bitmap.property.value().getWidth());
			canvas.drawBitmap(bitmap.property.value(), l, t, paint);
		}
	}
	@Override
	public void unbind() {
		super.unbind();
		bitmap.property.unbind();
		width.property.unbind();
		height.property.unbind();
		left.property.unbind();
		top.property.unbind();
		/*if (bitmap.property.value() != null) {
			bitmap.property.value().recycle();
		}*/
	}
}
