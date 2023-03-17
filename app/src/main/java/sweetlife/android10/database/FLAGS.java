package sweetlife.android10.database;

public class FLAGS 
{
	public static final long FLAG_ALL 											= 1<<0; // 0x0001
	public static final long FLAG_VELICHINA_KVANTOV_NOMENKLATURY 				= 1<<1; // 0x0002
	public static final long FLAG_VIZIT_TP 										= 1<<2; // 0x0004
	public static final long FLAG_DOGOVORY_KONTRAGENTOV 						= 1<<3; // 0x0008
	public static final long FLAG_DOSTUPNAYA_SUMMA_ZAKAZA						= 1<<4; // 0x0016
	public static final long FLAG_DNI_NEDELI				 					= 1<<5; // 0x0032
	public static final long FLAG_EDINICY_IZMERENIYA							= 1<<6; // 0x0064
	public static final long FLAG_GRUPPY_DOGOVOROV	 							= 1<<7; // 0x0128
	public static final long FLAG_GPS 											= 1<<8; // 0x0256
	public static final long FLAG_GPS_DOKUMENTY				 					= 1<<9; // 0x0512
	public static final long FLAG_KONTRAGENTY									= 1<<10; // 0x1024
	public static final long FLAG_LIMITY			 							= 1<<11; // 0x2048
	public static final long FLAG_LINII_DOSTAVKI_SO_SKLADOV						= 1<<12; // 0x4096
	public static final long FLAG_LINII_DOSTAVKI				 				= 1<<13; // 0x8192
	public static final long FLAG_MARSHRUTY_AGENTOV								= 1<<14; // 0x16384
	public static final long FLAG_MINIMALNYE_NACENKI_PROIZVODITELEY_1			= 1<<15; // 0x32768
	public static final long FLAG_MINIMALNYE_NACENKI_PROIZVODITELEY_0			= 1<<16; // 0x65536
	public static final long FLAG_MINIMALNYE_NACENKI_PROIZVODITELEY_TOVARY		= 1<<17; // 0x131072
	public static final long FLAG_NASTROYKI_POLZOVATELEY_0						= 1<<18; // 0x262144
	public static final long FLAG_NASTROYKI_POLZOVATELEY_1						= 1<<19; // 0x524288
	public static final long FLAG_NOMENKLATURA									= 1<<20; // 0x1048576
	public static final long FLAG_PODRAZDELENIYA			 					= 1<<21; // 0x2097152
	public static final long FLAG_POINT_GPS										= 1<<22; // 0x4194304
	public static final long FLAG_PRODAZHI			 							= 1<<23; // 0x8388608
	public static final long FLAG_PROIZVODITEL									= 1<<24; // 0x16777216
	public static final long FLAG_POLZOVATELI				 					= 1<<25; // 0x33554432
	public static final long FLAG_STATUS_ZAKAZA_POKUPATELYA_0					= 1<<26; // 0x67108864
	public static final long FLAG_STATUS_ZAKAZA_POKUPATELYA_1					= 1<<27; // 0x134217728
	public static final long FLAG_SKLADY										= 1<<28; // 0x268435456
	public static final long FLAG_STATUSY_AKTUALNOSTI_TOVAROV					= 1<<29; // 0x536870912
	public static final long FLAG_SMU_NETBUKI									= 1<<30; // 0x1073741824
	public static final long FLAG_TOP_TOVARY_PO_OSNOVNYM_PROIZVODITELYAM		= 1<<31; // 0x2147483648
	public static final long FLAG_TIPY_OPLATY									= 1<<32; // 0x4294967296
	public static final long FLAG_TIPY_CEN_NOMENKLATURY							= 1<<33; // 0x8589934592
	public static final long FLAG_TIPY_CEN_NOMENKLATURY_KONTRAGENTOV			= 1<<34; // 0x17179869184
	public static final long FLAG_ZAPRETY_NA_OTGRUZKU							= 1<<35; // 0x34359738368
	public static final long FLAG_ZAKAZ_POKUPATELYA								= 1<<36; // 0x68719476736
	public static final long FLAG_ZAKAZ_POKUPATELYA_TOVARY						= 1<<37; // 0x137438953472
	public static final long FLAG_ZAKAZ_POKUPATELYA_USLUGI						= 1<<38; // 0x274877906944
	public static final long FLAG_ZAYAVKA_POKUPATELYA_ISKHODYASCHAYA			= 1<<39; // 0x549755813888
	public static final long FLAG_ZAYAVKA_POKUPATELYA_ISKHODYASCHAYA_TOVARY		= 1<<40; // 0x1099511627776
	public static final long FLAG_ZAYAVKA_POKUPATELYA_ISKHODYASCHAYA_USLUGI		= 1<<41; // 0x2199023255552
	public static final long FLAG_ZAPRET_OTGRUZOK_OTVETSTVENNOGO				= 1<<42; // 0x4398046511104
	public static final long FLAG_CENY_NOMENKLATURY_KONTRAGENTOV	 			= 1<<43; // 0x8796093022208
	public static final long FLAG_CENY_NOMENKLATURY_SKLADA						= 1<<44; // 0x17592186044416
	public static final long FLAG_YUR_PHIZ_LICO									= 1<<45; // 0x35184372088832
	
	//all
	public static final long FLAG_TEMP_ALL 									= 1<<0; // 0x0001
	//MarshrutyAgentov Kontragenty
	public static final long FLAG_TEMP_MARSHRUTY_AGENTOV_DAYS 				= 1<<1; // 0x0002
	//MarshrutyAgentov Kontragenty DogovoryKontragentov
	public static final long FLAG_TEMP_MARSHRUTY_AGENTOV_CONTRACTS 			= 1<<2; // 0x0004
	//Limity
	public static final long FLAG_TEMP_LIMITY 								= 1<<3; // 0x0008
	//всегда при обновлении дельты или при первом старте
	public static final long FLAG_TEMP_PODREZDELENIYA       				= 1<<4; // 0x0016
}
