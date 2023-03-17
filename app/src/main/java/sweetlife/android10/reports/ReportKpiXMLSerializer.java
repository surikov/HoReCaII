package sweetlife.android10.reports;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

public class ReportKpiXMLSerializer extends ReportsXMLSerializer {

	public ReportKpiXMLSerializer(ReportInfo info) {
		super(info);
	}
	
	@Override
	protected void SerializeParameters(XmlSerializer serializer) 
			throws IllegalArgumentException, IllegalStateException, IOException {

		SerializeValueDate(serializer, SHIPPING_DATE_TODAY, ((ReportKpiInfo)mReportInfo).getShippingDate());
	}
}
