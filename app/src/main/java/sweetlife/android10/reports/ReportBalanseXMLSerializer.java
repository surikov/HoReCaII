package sweetlife.android10.reports;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

public class ReportBalanseXMLSerializer extends ReportsXMLSerializer {

	public ReportBalanseXMLSerializer(ReportInfo info) {
		super(info);

	}
    
	@Override
	protected void SerializeParameters(XmlSerializer serializer) 
			throws IllegalArgumentException, IllegalStateException, IOException {
	
		ReportBalanseInfo reportInfo = (ReportBalanseInfo)mReportInfo;
		
		for( String clientKod : reportInfo.getClientKods()) {

			SerializeArrayNumeric(serializer, KONTRAGENTS, clientKod );
		}
		
		SerializeValueNumeric(serializer, EXPANDED, 
				String.valueOf(reportInfo.getExpandedType()));
		
		SerializeValueBoolean(serializer, ONLY_GROUP_CONTRACTS, 
				reportInfo.isOnlyGroupContracts() ? TRUE_RUS : FALSE_RUS );
	}
}
