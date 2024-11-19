package reactive.ui;

import android.graphics.*;

import java.io.*;
import java.nio.charset.*;

import reactive.ui.androidsvg.*;
import reactive.ui.qr.*;
import reactive.ui.qr.Color;
import tee.binding.properties.*;

public class SketchQRcode extends Sketch{
	public NumericProperty<SketchQRcode> size = new NumericProperty<SketchQRcode>(this);
	public NumericProperty<SketchQRcode> left = new NumericProperty<SketchQRcode>(this);
	public NumericProperty<SketchQRcode> top = new NumericProperty<SketchQRcode>(this);
	public NoteProperty<SketchQRcode> text = new NoteProperty<SketchQRcode>(this);

	public SketchQRcode(){

	}

	@Override
	public void draw(Canvas canvas){
		QrCode qrcode = new QrCode();
		//qr.setFontName("Monospaced");
		//qr.setFontSize(16);
		//qr.setModuleWidth(16);
		//qr.setBarHeight(50);
		//qr.setHumanReadableLocation(HumanReadableLocation.BOTTOM);
		qrcode.setContent(this.text.property.value()
				/*"1.Test 123456789 Йцукерт!"
						+ "\n2.Test 123456789 Йцукерт!"
						+ "\n3.Test 123456789 Йцукерт!"
						+ "\n4.Test 123456789 Йцукерт!"
						+ "\n5.Test 123456789 Йцукерт!"
						+ "\n6.Test 123456789 Йцукерт!"
						+ "\n7.Test 123456789 Йцукерт!"
						+ "\n8.Test 123456789 Йцукерт!"
						+ "\n9.Test 123456789 Йцукерт!"

						+ "\n2.Test 123456789 Йцукерт!"
						+ "\n3.Test 123456789 Йцукерт!"
						+ "\n4.Test 123456789 Йцукерт!"
						+ "\n5.Test 123456789 Йцукерт!"
						+ "\n6.Test 123456789 Йцукерт!"
						+ "\n7.Test 123456789 Йцукерт!"
						+ "\n8.Test 123456789 Йцукерт!"
						+ "\n9.Test 123456789 Йцукерт!"

						+ "\n2.Test 123456789 Йцукерт!"
						+ "\n3.Test 123456789 Йцукерт!"
						+ "\n4.Test 123456789 Йцукерт!"
						+ "\n5.Test 123456789 Йцукерт!"
						+ "\n6.Test 123456789 Йцукерт!"
						+ "\n7.Test 123456789 Йцукерт!"
						+ "\n8.Test 123456789 Йцукерт!"
						+ "\n9.Test 123456789 Йцукерт!"*/
		);
		if(this.size.property.value()<=0)this.size.is(1);
		double ratio =  this.size.property.value()/((double)qrcode.getWidth());
		//System.out.println("----------------- size " + this.size.property.value() + " width " + qrcode.getWidth() + " ratio " + ratio);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		SvgRenderer renderer = new SvgRenderer(stream, ratio, reactive.ui.qr.Color.WHITE, Color.BLACK, true);
		try{
			renderer.render(qrcode);
			String content = new String(stream.toByteArray(), StandardCharsets.UTF_8);
			//System.out.println(content);
			SVG svg = SVG.getFromString(content);
			svg.renderToCanvas(canvas);
		}catch(Throwable t){
			t.printStackTrace();
		}
	}

	@Override
	public void unbind(){
		super.unbind();
		text.property.unbind();
		size.property.unbind();
		left.property.unbind();
		top.property.unbind();
	}
}
