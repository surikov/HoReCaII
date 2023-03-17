package reactive.ui;

import reactive.ui.*;

import android.content.*;
import android.graphics.*;
import tee.binding.properties.*;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;
import tee.binding.task.*;
import tee.binding.it.*;
import android.text.*;

public class TintLinearGradient extends Tint {
	public NumericProperty<TintLinearGradient> fromColor = new NumericProperty<TintLinearGradient>(this);
	public NumericProperty<TintLinearGradient> fromX = new NumericProperty<TintLinearGradient>(this);
	public NumericProperty<TintLinearGradient> fromY = new NumericProperty<TintLinearGradient>(this);
	public NumericProperty<TintLinearGradient> toColor = new NumericProperty<TintLinearGradient>(this);
	public NumericProperty<TintLinearGradient> toX = new NumericProperty<TintLinearGradient>(this);
	public NumericProperty<TintLinearGradient> toY = new NumericProperty<TintLinearGradient>(this);

	//public Sketch forUpdate;
	@Override
	public void unbind() {
		fromColor.property.unbind();
		fromX.property.unbind();
		fromY.property.unbind();
		toColor.property.unbind();
		toX.property.unbind();
		toY.property.unbind();
	}

	public Task postInvalidate = new Task() {
		@Override
		public void doTask() {
			setShader(new LinearGradient(//
					fromX.property.value().intValue()//
					, fromY.property.value().intValue()//
					, toX.property.value().intValue()//
					, toY.property.value().intValue()//
					, fromColor.property.value().intValue()//
					, toColor.property.value().intValue()//
					, Shader.TileMode.CLAMP));
			//System.out.println("2");
			if (forUpdate != null) {
				//System.out.println("3");
				if (forUpdate.postInvalidate != null) {
					//setAntiAlias(true);
					forUpdate.postInvalidate.start();
					//System.out.println("1");
				}
			}
		}
	};

	public TintLinearGradient() {
		super();
		setAntiAlias(true);
		fromColor.property.afterChange(postInvalidate);
		fromX.property.afterChange(postInvalidate);
		fromY.property.afterChange(postInvalidate);
		toColor.property.afterChange(postInvalidate);
		toX.property.afterChange(postInvalidate);
		toY.property.afterChange(postInvalidate);
	}
}
