package sweetlife.android10.reports;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

public class ReportOrderStateXMLSerializer extends ReportsXMLSerializer {

	public ReportOrderStateXMLSerializer(ReportInfo info) {
		super(info);

	}
    
	@Override
	protected void SerializeParameters(XmlSerializer serializer) 
			throws IllegalArgumentException, IllegalStateException, IOException {

		SerializeValueBoolean(serializer, ONLY_ONLY_NOT_COMPLETE, 
				((ReportOrderStateInfo)mReportInfo).getOnlyNotCompleted() ? TRUE_RUS : FALSE_RUS );
	}
}
