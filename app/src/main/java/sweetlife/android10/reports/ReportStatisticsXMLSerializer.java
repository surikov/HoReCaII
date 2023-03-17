package sweetlife.android10.reports;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

public class ReportStatisticsXMLSerializer extends ReportsXMLSerializer {

	public ReportStatisticsXMLSerializer(ReportInfo info) {
		super(info);
	}

	@Override
	protected void SerializeParameters(XmlSerializer serializer) 
			throws IllegalArgumentException, IllegalStateException, IOException {

		ReportStatisticsInfo reportInfo = (ReportStatisticsInfo)mReportInfo;

		SerializeValueBoolean(serializer, CLIENTS_ORDERS, TRUE_RUS);

		if(reportInfo.getClientCode() != null && reportInfo.getClientCode().length() != 0) {

			SerializeValueNumeric(serializer, KONTRAGENT, reportInfo.getClientCode());
		}
		SerializeValueDate(serializer, BEGIN_DATE, reportInfo.getBeginOrder());
		SerializeValueDate(serializer, END_DATE, reportInfo.getEndOrder());
	}
}
