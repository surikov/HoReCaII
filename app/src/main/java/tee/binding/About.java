package tee.binding;

import tee.binding.it.*;
import tee.binding.properties.*;
import tee.binding.it.Toggle;
import tee.binding.it.Note;
import tee.binding.it.It;

/**
 * 
 * @author User
 */
public class About {
    /**
     * 
     * @return
     */
    public static String getVersion() {
	return "1.6.8";
    }
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
	System.out.println("Examples of using");
        String dumb="";
        Numeric zoom = new Numeric();
        NoteProperty<String> title = new NoteProperty<String>(dumb);
        title.is(new Note().value("Zoom: [").append(zoom.multiply(100).plus(100).asNote()).append("%]"));
        System.out.println(title.property.value());
        zoom.value(1);
        System.out.println(title.property.value());
        /*
	It.main(args);
	Calculation.main(args);
	Numeric.main(args);
	Fit.main(args);
	Note.main(args);
	Toggle.main(args);
	Fork.main(args);*/
//	Column.main(args);
	System.out.println(getVersion());
    }
}
