package reactive.ui;
//https://github.com/woo-j/OkapiBarcode
import reactive.ui.qr.*;
import java.io.*;
import java.nio.charset.*;
import reactive.ui.androidsvg.*;
import android.graphics.Canvas;
public class QRGenerator{
	void test(Canvas canvas){
		QrCode qr=new QrCode();
		//qr.setFontName("Monospaced");
		//qr.setFontSize(16);
		qr.setModuleWidth(120);
		qr.setBarHeight(50);
		qr.setHumanReadableLocation(HumanReadableLocation.BOTTOM);
		qr.setContent("Test 123456789 Йцукерт!");

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		SvgRenderer renderer = new SvgRenderer(stream, 1, Color.WHITE, Color.BLACK, true);
		try{
			renderer.render(qr);

			String content = new String(stream.toByteArray(), StandardCharsets.UTF_8);
			SVG svg = SVG.getFromString(content);
			svg.renderToCanvas(canvas);
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
}
