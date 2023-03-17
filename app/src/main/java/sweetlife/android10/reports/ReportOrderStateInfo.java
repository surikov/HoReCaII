package sweetlife.android10.reports;

public class ReportOrderStateInfo extends ReportInfo {

	private Boolean           mOnlyNotCompleted;
	
	public ReportOrderStateInfo( String reportName,
			String beginPeriod,
			String endPeriod,
			String agentKod,
			Boolean onlyNotCompleted) {
		
		super(reportName, beginPeriod, endPeriod, agentKod);

		mOnlyNotCompleted = onlyNotCompleted;
	}

	public Boolean getOnlyNotCompleted() {

		return mOnlyNotCompleted;
	}

	public void setOnlyNotCompleted(Boolean onlyNotCompleted) {

		mOnlyNotCompleted = onlyNotCompleted;
	}
}
