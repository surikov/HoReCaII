package sweetlife.android10.reports;

public interface IReportConsts {
	//	String TAG_GET_REPORT              = "getReport";
	//	String TAG_NAME                    = "�?мя";
	//	String TAG_BEGIN_PERIOD            = "НачалоПериода";
	//	String TAG_END_PERIOD              = "КонецПериода";
	//	String TAG_USER_CODE               = "КодПользователя";
	//	String TAG_PARAM_CLIENT_CODE       = "ПараметрыКодыКонтрагента";
	//	String TAG_PARAM_EXPANDED          = "ПараметрыРазвернутый";
	//	String TAG_PARAM_CONTRACT_GROUP    = "ПараметрыТолькоДоговораГруппы";
	String TAG_DATA = "m:Data";
	//	String ATTR_GET_REPORT             = "http://ws.swl/fileHRC";
	int TYPE_EXPANDED = 1;
	int TYPE_COMBINED = 3;
	int TYPE_COLLAPSED = 4;
	int TYPE_EXPANDED_BY_CONTACTS = 8;
	int TYPE_COLLAPSED_BY_CONTACTS = 7;
	String REPORT_FILE_PREFFIX = "Report";
	String REPORT_FILE_EXTENSION = ".htm";
	String EXTRA_REPORT_NAME = "report_name";
	String EXTRA_REPORT_TYPE = "report_type";
	String EXTRA_REPORT_FILE_PATH = "report_path";
	int ERROR_REQUEST_DATA = 101;
	int ERROR_PARSE_DATA = 102;
	String REPORT_TYPE_BALANSE = "Взаиморасчеты";
	String REPORT_TYPE_STATISTICS = "СтатистикеЗаказовHRC";
	String REPORT_TYPE_ORDER_STATES = "СтатусыЗаказов";
	String REPORT_TYPE_DISTRIBUTION = "Дистрибуция";
	String REPORT_TYPE_TRAFIKS = "ТрафикиПоТП";
	String REPORT_TYPE_PREDZTRAFIKS = "ОтчетПоТрафикамHRC";
	String REPORT_TYPE_DISPOSALS = "СтатусыРаспоряжений";
	String REPORT_TYPE_FIXED_PRICES = "ЗаявкиНаФиксированныеЦены";
	String REPORT_TYPE_DRIVERS_DELIVERY = "ДоставкаПоВодителям";
	String REPORT_TYPE_KPI = "Показатели";
}
