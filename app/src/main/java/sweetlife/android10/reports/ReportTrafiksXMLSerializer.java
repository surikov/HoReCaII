package sweetlife.android10.reports;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

public class ReportTrafiksXMLSerializer extends ReportsXMLSerializer {

	public ReportTrafiksXMLSerializer(ReportInfo info) {
		super(info);

	}
    
	@Override
	protected void SerializeParameters(XmlSerializer serializer) 
			throws IllegalArgumentException, IllegalStateException, IOException {
	
		ReportTrafiksInfo reportInfo = (ReportTrafiksInfo)mReportInfo;
		
		SerializeValueBoolean(serializer, ONLY_ONLY_SHIPPED, 
				reportInfo.getOnlyShipped() ? TRUE_RUS : FALSE_RUS );

		SerializeValueBoolean(serializer, ONLY_ONLY_NOT_SHIPPED, 
				reportInfo.getOnlyNotShipped() ? TRUE_RUS : FALSE_RUS );
	}
}
