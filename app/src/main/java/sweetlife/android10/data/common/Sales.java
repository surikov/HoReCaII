package sweetlife.android10.data.common;

public class Sales {

	public    static final String NAKOPIT_NAME      = "Накопит";
	public    static final String INDIVID_NAME      = "Индивид";
	public    static final String FIX_PRICE_NAME    = "Фикс цена";
	public    static final String PARTNER_NAME      = "Партнер";		
	public    static final String CR_NAME           = "ЦР";
	public    static final String hero_NAME           = "ТГ";
	public    static final String CR_hero_NAME           = "Т.Г.";
	public    static final String DEFAULT_NAME      = "";
	
	public    static final int    NAKOPIT_CODE      = 1;
	public    static final int    INDIVID_CODE      = 2;
	public    static final int    FIX_PRICE_CODE    = 5;
	public    static final int    PARTNER_CODE      = 0;		
	public    static final int    CR_CODE           = 7;
	public    static final int    DEFAULT_CODE      = 100;
/*
	public    static final String NAKOPIT_ID        = "x'913AD1938AF7144E4322AC76B890CFCE'";
	public    static final String INDIVID_ID        = "x'9B6E6AB7E8559CEA4D3787EE115C6103'";
	public    static final String FIX_PRICE_ID      = "x'BDA5601EB37A75774F39EA16AD78895C'";
	public    static final String PARTNER_ID        = "x'914F887F023F24874F33033AC1CACCEB'";		
	public    static final String CR_ID             = "x'99D730123902D40541B2F8954FE1E089'";
	*/
	public    static final String DEFAULT_ID        = "x'00'";
	/*
	public static String _GetSaleName( String id ) {

		//if( id.compareToIgnoreCase(PARTNER_ID) == 0 ) {
		if( id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaId_x_Gazeta) == 0 ) {
			return PARTNER_NAME;
		}
		//else if( id.compareToIgnoreCase(NAKOPIT_ID) == 0 ) {
		else if( id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdNakopitelnaya) == 0 ) {
			return NAKOPIT_NAME;
		} 
		//else if( id.compareToIgnoreCase(INDIVID_ID) == 0 ) {
		else if( id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdIndividualnaya) == 0 ) {
			return INDIVID_NAME;
		} 
		//else if( id.compareToIgnoreCase(FIX_PRICE_ID) == 0 ) {
		else if( id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdFixirovannaya) == 0 ) {
			return FIX_PRICE_NAME;
		} 
		//else if( id.compareToIgnoreCase(CR_ID) == 0 ) {
		else if( id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdOldCenovoyeReagirovanie) == 0
				|| id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdCenovoyeReagirovanie) == 0
				|| id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdAutoReagirovanie) == 0
		) {
			return CR_NAME;
		} 
		return DEFAULT_NAME;
	}
	*/
	/*public static int GetSaleCode( String id ) {
		
		//if( id.compareToIgnoreCase(PARTNER_ID) == 0 ) {
		if( id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaId_x_Gazeta) == 0 ) {
			return PARTNER_CODE;
		}
		//else if( id.compareToIgnoreCase(NAKOPIT_ID) == 0 ) {
		else if( id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdNakopitelnaya) == 0 ) {
			return NAKOPIT_CODE;
		} 
		//else if( id.compareToIgnoreCase(INDIVID_ID) == 0 ) {
		else if( id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdIndividualnaya) == 0 ) {
			return INDIVID_CODE;
		} 
		//else if( id.compareToIgnoreCase(FIX_PRICE_ID) == 0 ) {
		else if( id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdFixirovannaya) == 0 ) {
			return FIX_PRICE_CODE;
		} 
		else if( //id.compareToIgnoreCase(CR_ID) == 0
				id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdOldCenovoyeReagirovanie) == 0
						|| id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdCenovoyeReagirovanie) == 0
						|| id.compareToIgnoreCase(sweetlife.horeca.supervisor.Cfg.skidkaIdAutoReagirovanie) == 0
		) {

			return CR_CODE;
		} 
		
		return DEFAULT_CODE;
	}*/
}
