package sweetlife.android10.reports;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReportKpiInfo extends ReportInfo {

	private String mShippingDate;
	
	public ReportKpiInfo(String reportName, 
			String beginPeriod,
			String endPeriod, 
			String agentKod,
			Calendar shippingDate) {
		
		super(reportName, beginPeriod, endPeriod, agentKod);
		
		setShippingDate(shippingDate);
	}

	public String getShippingDate() {
		
		return mShippingDate;
	}

	public void setShippingDate(Calendar shippingDate) {

		mShippingDate = new SimpleDateFormat("yyyyMMdd").format(shippingDate.getTime())+ "235959";
	}

}
