package sweetlife.android10.reports;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

public class ReportPredzTrafiksXMLSerializer extends ReportsXMLSerializer {

	public ReportPredzTrafiksXMLSerializer(ReportInfo info) {
		super(info);

	}
    
	@Override
	protected void SerializeParameters(XmlSerializer serializer) 
			throws IllegalArgumentException, IllegalStateException, IOException {
	
		ReportPredzTrafiksInfo reportInfo = (ReportPredzTrafiksInfo)mReportInfo;
		
		
	}
}
