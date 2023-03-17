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

public class TintRadialGradient extends Tint {
	public NumericProperty<TintRadialGradient> centerColor = new NumericProperty<TintRadialGradient>(this);
	public NumericProperty<TintRadialGradient> centerX = new NumericProperty<TintRadialGradient>(this);
	public NumericProperty<TintRadialGradient> centerY = new NumericProperty<TintRadialGradient>(this);
	public NumericProperty<TintRadialGradient> edgeColor = new NumericProperty<TintRadialGradient>(this);
	public NumericProperty<TintRadialGradient> radius = new NumericProperty<TintRadialGradient>(this);
//	public NumericProperty<TintRadialGradient> toY = new NumericProperty<TintRadialGradient>(this);

	//public Sketch forUpdate;
	@Override
	public void unbind() {
		centerColor.property.unbind();
		centerX.property.unbind();
		centerY.property.unbind();
		edgeColor.property.unbind();
		radius.property.unbind();
		//toY.property.unbind();
	}

	public Task postInvalidate = new Task() {
		@Override
		public void doTask() {
			setShader(new RadialGradient(//
					centerX.property.value().intValue()//
					, centerY.property.value().intValue()//
					, radius.property.value().intValue()//
					, centerColor.property.value().intValue()//
					, edgeColor.property.value().intValue()//
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

	public TintRadialGradient() {
		super();
		setAntiAlias(true);
		radius.is(1);
		
		centerColor.property.afterChange(postInvalidate);
		centerX.property.afterChange(postInvalidate);
		centerY.property.afterChange(postInvalidate);
		edgeColor.property.afterChange(postInvalidate);
		radius.property.afterChange(postInvalidate);
		//toY.property.afterChange(postInvalidate);
	}
}
