package sweetlife.android10.utils;

import java.text.DecimalFormat;

public class DecimalFormatHelper {

	private static DecimalFormat mDecimalFormat;
	private static DecimalFormat mDecimalFormat3;
	
	static {

		mDecimalFormat = new DecimalFormat("0.00");
		mDecimalFormat3 = new DecimalFormat("0.000");
	}
	
	public static String format( double value ) {
		
		return mDecimalFormat.format(value).replace(",", ".");
	}
public static String format3( double value ) {
		
		return mDecimalFormat3.format(value).replace(",", ".");
	}
}
