package sweetlife.android10.reports;

import java.util.ArrayList;

public class ReportBalanseInfo extends ReportInfo {

	private ArrayList<String> mClientKods;
	private Integer           mExpandedType;
	private Boolean           mOnlyGroupContracts;
	
	public ReportBalanseInfo( String reportName,
			String beginPeriod,
			String endPeriod,
			String agentKod,
			ArrayList<String> clientKods,
			Integer expandedType,
			Boolean onlyGroupContracts) {
		
		super(reportName, beginPeriod, endPeriod, agentKod);
		
		mClientKods = clientKods;
		mExpandedType = expandedType;
		mOnlyGroupContracts = onlyGroupContracts;
		
	}
	
	public ArrayList<String> getClientKods() {

		return mClientKods;
	}
	public void setClientKods(ArrayList<String> clientKods) {

		mClientKods = clientKods;
	}
	public Integer getExpandedType() {

		return mExpandedType;
	}
	public void setExpandedType(int expandedType) {

		mExpandedType = expandedType;
	}
	public Boolean isOnlyGroupContracts() {

		return mOnlyGroupContracts;
	}
	public void setOnlyGroupContracts(boolean onlyGroupContracts) {

		mOnlyGroupContracts = onlyGroupContracts;
	}
}
