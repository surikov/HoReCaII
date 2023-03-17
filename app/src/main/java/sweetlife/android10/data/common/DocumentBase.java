package sweetlife.android10.data.common;

import java.util.Calendar;
import java.util.Date;

import sweetlife.android10.utils.DateTimeHelper;


public abstract class DocumentBase {
	
	protected Integer mDocumentNumber = 0;
	protected Integer mDocumentNumberLenght = 10;	
	protected String  mDocumentNumberPrefix = "AND";	
	
	protected String generateDocumentNumber() {
		
		mDocumentNumber++;
		
		StringBuilder resultNumber = new StringBuilder( mDocumentNumberPrefix + mDocumentNumber.toString());
		
		while( resultNumber.length() < mDocumentNumberLenght ) {
		
			resultNumber.insert(mDocumentNumberPrefix.length(), '0');
		}
		
		return resultNumber.toString();
	}
	
	public static Date nextWorkingDate(Calendar chosedDay) {
		
		Calendar rigthNow = DateTimeHelper.getOnlyDateInfo( chosedDay );

		switch( rigthNow.get( Calendar.DAY_OF_WEEK ) ) {

		case Calendar.SATURDAY:

			rigthNow.roll(Calendar.DAY_OF_YEAR, 2 );
			break;
		default:
			
			rigthNow.roll(Calendar.DAY_OF_YEAR, 1 );
			break;
		}

		return rigthNow.getTime();
	}	
}
