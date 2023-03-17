package sweetlife.android10.reports;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReportStatisticsInfo extends ReportInfo {
	private String mClientCode;
	private String mBeginOrder;
	private String mEndOrder;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMdd");

	public ReportStatisticsInfo(//
			String reportName//
			, String beginPeriod//
			, String endPeriod//
			, String agentKod//
			, String clientCode//
			, Calendar beginOrder//
			, Calendar endOrder//
	) {
		super(reportName, beginPeriod, endPeriod, agentKod);
		setClientCode(clientCode);
		setBeginOrder(beginOrder);
		setEndOrder(endOrder);
	}
	public String getClientCode() {
		return mClientCode;
	}
	public void setClientCode(String clientCode) {
		mClientCode = clientCode;
	}
	public String getBeginOrder() {
		return mBeginOrder;
	}
	public void setBeginOrder(Calendar beginDate) {
		mBeginOrder = mDateFormat.format(beginDate.getTime()) + "000000";
	}
	public String getEndOrder() {
		return mEndOrder;
	}
	public void setEndOrder(Calendar endDate) {
		mEndOrder = mDateFormat.format(endDate.getTime()) + "235959";
	}
}
