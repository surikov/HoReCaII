package sweetlife.android10.reports;

import sweetlife.android10.log.LogHelper;

public class ReportInfo {

	private String            mReportName;
	private String            mBeginPeriod;
	private String            mEndPeriod;
	private String            mAgentKod;

	public ReportInfo( String reportName,
			String beginPeriod,
			String endPeriod,
			String agentKod) {
		LogHelper.debug(this.getClass().getCanonicalName()+" construct");
		mReportName = reportName;
		mBeginPeriod = beginPeriod;
		mEndPeriod = endPeriod;
		mAgentKod = agentKod;
	}

	public String getReportName() {

		return mReportName;
	}
	public void setReportName(String reportName) {

		mReportName = reportName;
	}
	public String getBeginPeriod() {

		return mBeginPeriod;
	}
	public void setBeginPeriod(String beginPeriod) {

		mBeginPeriod = beginPeriod;
	}
	public String getEndPeriod() {

		return mEndPeriod;
	}
	public void setEndPeriod(String endPeriod) {

		mEndPeriod = endPeriod;
	}
	public String getAgentKod() {

		return mAgentKod;
	}
	public void setAgentKod(String agentKod) {

		mAgentKod = agentKod;
	}
}
