package sweetlife.android10.database.nomenclature;

import java.util.Calendar;

import reactive.ui.Auxiliary;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.data.common.Sales;
import sweetlife.android10.database.ISklady;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.ui.Activity_NomenclatureNew;
import sweetlife.android10.update.*;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.DecimalFormatHelper;
import sweetlife.android10.utils.Hex;
import tee.binding.*;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.*;

public abstract class Request_NomenclatureBase implements ITableColumnsNames {
	protected static final int ORDER_ASCEND = 0;
	protected static final int ORDER_DESCEND = 1;
	protected int mOrderDirection = ORDER_DESCEND;
	protected String mOrderField = null;
	public String mStrQuery = null;
	private static String priceVladelec = null;
	private static String currentKontragentID = "";
	final static int ZapretOtgruzokOtvetsvennogoNone = 0;
	final static int ZapretOtgruzokOtvetsvennogoInclude = 1;
	final static int ZapretOtgruzokOtvetsvennogoExclude = 2;
	static String mastListKey = null;//"X'00000000000000000000000000000000'";

	protected Request_NomenclatureBase(boolean degustacia) {
		SetRequestString(degustacia);
	}

	public String getStrQueryCR() {
		return mStrQuery;
	}

	public static String uslovieSkladaPodrazdeleniaNeTraphik(Date dataOtgruzki, String skladPodrazdelenia) {
		//if(1==1)return "";
		boolean letuchka = DateTimeHelper.SQLDateString(dataOtgruzki).equals(DateTimeHelper.SQLDateString(new Date()));
		skladPodrazdelenia = skladPodrazdelenia.toUpperCase();
		//String traphikCondition = " and Traphik=x'01'";
		/*if (!traphik) {
			traphikCondition = " and Traphik=x'00'";
		}*/
		//skladPodrazdelenia = ISklady.HORECA_ID.toUpperCase();
		if (skladPodrazdelenia.equals(ISklady.HORECA_ID.toUpperCase())) {
			return "\n	on (select sklad from AdresaPoSkladam_last horeca where horeca.nomenklatura=n._idrref and horeca.period"//
					+ "\n		=(select max(period) from AdresaPoSkladam_last where nomenklatura=n._idrref"//
					+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'00') "//
					+ "\n		)=" + ISklady.HORECA_sklad_8//
					+ "\n	or (select sklad from AdresaPoSkladam_last horeca where horeca.nomenklatura=n._idrref and horeca.period"//
					+ "\n		=(select max(period) from AdresaPoSkladam_last where nomenklatura=n._idrref"//
					+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'00') "//
					+ "\n		)=" + ISklady.HORECA_sklad_10//
					+ "\n	or (select sklad from AdresaPoSkladam_last horeca where horeca.nomenklatura=n._idrref and horeca.period"//
					+ "\n		=(select max(period) from AdresaPoSkladam_last where nomenklatura=n._idrref"//
					+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'00') "//
					+ "\n		)=" + ISklady.HORECA_sklad_12//
					+ "\n";
		} else {
			if (skladPodrazdelenia.equals(ISklady.KAZAN_ID.toUpperCase())) {
				if (letuchka) {
					return "\n	on (select sklad from AdresaPoSkladam_last kazan where kazan.nomenklatura=n._idrref and kazan.period"//
							+ "\n		=(select max(period) from AdresaPoSkladam_last where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.KAZAN_ID + " and Traphik=x'00') "//
							+ "\n		)=" + ISklady.KAZAN_sklad_14//
							+ "\n";
				} else {
					return "\n	on (select sklad from AdresaPoSkladam_last horeca where horeca.nomenklatura=n._idrref and horeca.period"//
							+ "\n		=(select max(period) from AdresaPoSkladam_last where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'00') "//
							+ "\n		)=" + ISklady.HORECA_sklad_8//
							+ "\n	or (select sklad from AdresaPoSkladam_last horeca where horeca.nomenklatura=n._idrref and horeca.period"//
							+ "\n		=(select max(period) from AdresaPoSkladam_last where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'00') "//
							+ "\n		)=" + ISklady.HORECA_sklad_10//
							+ "\n	or (select sklad from AdresaPoSkladam_last horeca where horeca.nomenklatura=n._idrref and horeca.period"//
							+ "\n		=(select max(period) from AdresaPoSkladam_last where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'00') "//
							+ "\n		)=" + ISklady.HORECA_sklad_12//
							+ "\n	or (select sklad from AdresaPoSkladam_last kazan where kazan.nomenklatura=n._idrref and kazan.period"//
							+ "\n		=(select max(period) from AdresaPoSkladam_last where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.KAZAN_ID + " and Traphik=x'00') "//
							+ "\n		)=" + ISklady.KAZAN_sklad_14//
							+ "\n";
				}
			} else {
				//LogHelper.debug("uslovieSkladaPodrazdeleniaNeTraphik: Unknown skladPodrazdelenia for " + skladPodrazdelenia);
				return "";
			}
		}
	}


	static String uchetnayaCenaKontragentID = null;
	static boolean uchetnayaCenaKontragentTyp = false;

	public static boolean uchetnayaCena(String kontragentID) {
		//System.out.println("uchetnayaCena----------------------------------------------------------------------");
		if (kontragentID.equals(uchetnayaCenaKontragentID)) {
			//System.out.println("cached " + uchetnayaCenaKontragentTyp);
			return uchetnayaCenaKontragentTyp;
		} else {
			uchetnayaCenaKontragentID = kontragentID;
			sweetlife.android10.update.UpdateTask.refreshProdazhi_CR(ApplicationHoreca.getInstance().getDataBase(), uchetnayaCenaKontragentID);
			String sql = "select typ.naimenovanie"//
					+ "\n from Kontragenty kontr"//
					+ "\n join DogovoryKontragentov dog on kontr._IDRRef=dog.Vladelec"//
					+ "\n join TypeCen typ on typ._IDRRef=dog.TypeCen"//
					+ "\n where kontr._idrref=" + uchetnayaCenaKontragentID + ""//
					+ "\n and typ.naimenovanie='Учетная цена'"//
					+ "\n limit 1;";
			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			if (b.children.size() > 0) {
				uchetnayaCenaKontragentTyp = true;
			} else {
				uchetnayaCenaKontragentTyp = false;
			}
			//System.out.println("found " + uchetnayaCenaKontragentTyp);
			return uchetnayaCenaKontragentTyp;
		}
	}

	static String kontragentZapretOtgruzokOtvetsvennogo = null;

	public static int getZapretOtgruzokOtvetsvennogo(String kontragent, String polzovatel) {
		//System.out.println("getZapretOtgruzokOtvetsvennogo " + kontragent + ", " + polzovatel);
		if (polzovatel.trim().length() > 1) {
			if (!kontragent.equals(kontragentZapretOtgruzokOtvetsvennogo)) {
				kontragentZapretOtgruzokOtvetsvennogo = kontragent;
				String refreshZOO = "drop table if exists ZapretOtgruzokOtvetsvennogo_strip;";
				ApplicationHoreca.getInstance().getDataBase().execSQL(refreshZOO);
				refreshZOO = "create table ZapretOtgruzokOtvetsvennogo_strip (_id integer primary key asc,[ObjectZapreta] blob null,[Proizvoditel] blob null,[Otvetstvenniy] blob null);";
				ApplicationHoreca.getInstance().getDataBase().execSQL(refreshZOO);
				refreshZOO = "insert into ZapretOtgruzokOtvetsvennogo_strip"//
						+ " (_id,[ObjectZapreta],[Proizvoditel],[Otvetstvenniy])"//
						+ " select"//
						+ " _id,[ObjectZapreta],[Proizvoditel],[Otvetstvenniy]"//
						+ " from ZapretOtgruzokOtvetsvennogo"//
						+ " where ObjectZapreta=" + kontragent//
						+ ";";
				//System.out.println(refreshZOO);
				ApplicationHoreca.getInstance().getDataBase().execSQL(refreshZOO);
				refreshZOO = "CREATE INDEX IX_ZapretOtgruzokOtvetsvennogo_OtvetstvenniyZoo on ZapretOtgruzokOtvetsvennogo_strip(Otvetstvenniy)";
				ApplicationHoreca.getInstance().getDataBase().execSQL(refreshZOO);
				refreshZOO = "CREATE INDEX IX_ZapretOtgruzokOtvetsvennogo_ProizvoditelZoo on ZapretOtgruzokOtvetsvennogo_strip(Proizvoditel)";
				ApplicationHoreca.getInstance().getDataBase().execSQL(refreshZOO);
			}

			String sql = "select _id"//
					+ "\n	from ZapretOtgruzokOtvetsvennogo_strip"//
					+ "\n	limit 1";
			Cursor c = ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null);
			if (!c.moveToNext()) {
				if (c != null) c.close();
				return ZapretOtgruzokOtvetsvennogoNone;
			} else {
				if (c != null) c.close();

				sql = "select _id"//
						+ "\n	from ZapretOtgruzokOtvetsvennogo_strip"//
						+ "\n	where Otvetstvenniy=" + polzovatel//X'AB7418A90562E07411E34AADBCA4F16C'"
						+ "\n	limit 1";
				c = ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null);
				if (c.moveToNext()) {
					if (c != null) c.close();
					return ZapretOtgruzokOtvetsvennogoInclude;
				} else {
					if (c != null) c.close();
					return ZapretOtgruzokOtvetsvennogoExclude;
				}
			}
		} else {
			//if(c!=null) c.close();
			return ZapretOtgruzokOtvetsvennogoNone;
		}

		//return ZapretOtgruzokOtvetsvennogoNone;
	}

	static String findMustListKey(String polzovatel) {
		if (mastListKey == null) {
			String sql = "select p1.naimenovanie,p2.naimenovanie,p3.naimenovanie"//
					+ "\n		,must1.object as o1,must2.object as o2,must3.object as o3"//
					+ "\n	from Polzovateli p"//
					+ "\n		left join Podrazdeleniya p1 on p.podrazdelenie=p1._idrref"//
					+ "\n		left join Podrazdeleniya p2 on p1.roditel=p2._idrref"//
					+ "\n		left join Podrazdeleniya p3 on p2.roditel=p3._idrref"//
					+ "\n		left join TovaryDlyaDozakaza must1 on must1.object=p1._idrref"//
					+ "\n		left join TovaryDlyaDozakaza must2 on must2.object=p2._idrref"//
					+ "\n		left join TovaryDlyaDozakaza must3 on must3.object=p3._idrref"//
					+ "\n	where p._idrref=" + polzovatel//
					+ "\n limit 1";
			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			String o1 = b.child("row").child("o1").value.property.value();
			String o2 = b.child("row").child("o2").value.property.value();
			String o3 = b.child("row").child("o3").value.property.value();
			//System.out.println(sql);
			//System.out.println(b.dumpXML());
			if (o1.length() > 5) {
				mastListKey = "x'" + o1 + "'";
			} else {
				if (o2.length() > 5) {
					mastListKey = "x'" + o2 + "'";
				} else {
					if (o3.length() > 5) {
						mastListKey = "x'" + o3 + "'";
					} else {
						mastListKey = "X'00000000000000000000000000000000'";
					}
				}
			}
		}
		return mastListKey;
	}

	static String mastListFilter(String polzovatel) {
		//return "cross join KategoryObjectov kobj on kobj.Nomenklatura=n._idrref and kobj.kategorya =x'A69818A90562E07011E4A5333644C864'";
		return "cross join tovaryDlyaDozakaza tovaryDlyaDozakazaFilter on tovaryDlyaDozakazaFilter.nomenklatura=n._idrref and tovaryDlyaDozakazaFilter.object=" + findMustListKey(polzovatel)//
				//+"\n		and ("//
				//+"\n		tovaryDlyaDozakazaFilter.object="//
				//+")"//
				;
	}

	/*public static String firstMonthDay(String d){
		String r=d;//'2020-12-20'
		r=d.substring(0, 7)+"01";
		return r;
	}*/
	static String cachedPolzovatelID = "";
	static String cachedFirstDay = "";

	public static void cacheTop20(String polzovatelID, String firstDay) {
		//System.out.println("cacheTop20 "+polzovatelID+", firstDay"+" - "+cachedPolzovatelID+", "+cachedFirstDay);
		if ((!polzovatelID.equals(cachedPolzovatelID)) || (!firstDay.equals(cachedFirstDay))) {
			cachedPolzovatelID = polzovatelID;
			cachedFirstDay = firstDay;
			String sql = "drop table if exists dopmotivaciya_cache;";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = "create table dopmotivaciya_cache (_id ineger null,nomenklatura blob null);";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = "create index dopmotivaciya_cache_id on dopmotivaciya_cache(_id);";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = "create index dopmotivaciya_cache_nomenklatura on dopmotivaciya_cache(nomenklatura);";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = "insert into dopmotivaciya_cache (_id,nomenklatura)"//
					+ "\n	select dm._id as _id,dm.nomenklatura as nomenklatura"//
					+ "\n	   		from dopmotivaciya dm"//
					+ "\n	   			cross join Polzovateli on Polzovateli._idrref=" + polzovatelID + ""//
					+ "\n	   			cross join Podrazdeleniya p1 on p1._idrref=Polzovateli.podrazdelenie"//
					+ "\n	   			left join Podrazdeleniya p2  on p1.roditel=p2._idrref"//
					+ "\n	   			left join Podrazdeleniya p3  on p2.roditel=p3._idrref"//
					+ "\n	   		where date(dm.periodnach)<=date('" + firstDay + "') and date(dm.periodkon)>=date('" + firstDay + "')"//
					+ "\n	   		and (dm.Podrazdelenie=p1._idrref or dm.Podrazdelenie=p2._idrref or dm.Podrazdelenie=p3._idrref)"//
					+ "\n	   		group by dm.nomenklatura;";
			//System.out.println(sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			//System.out.println("done cacheTop20");
		}
	}


	public static String composeSQL(//
									String dataOtgruzki//2013-01-30
			, String kontragentID//
			, String polzovatelID//
			, String dataNachala//
			, String dataKonca//
			, String poiskovoeSlovo//
			, int tipPoiska//ISearchBy
			, boolean etoTrafik//
			, boolean history//
			, String skladPodrazdelenia//
			, int limit//
			, int offset//
			, boolean isMustList//
			, boolean isTop//


			, boolean DEGUSTACIA_POISK
									//, String receptID
			, String ingredientIdrref, String ingredientKluch
									,boolean no_assortiment
	) {
		return composeSQLall(dataOtgruzki, kontragentID, polzovatelID, dataNachala, dataKonca
				, poiskovoeSlovo, tipPoiska, etoTrafik, history, skladPodrazdelenia, limit//
				, offset, isMustList, isTop, null, null, false, DEGUSTACIA_POISK, ingredientIdrref, ingredientKluch, null,no_assortiment);
	}

	public static String composeSQLall(//
									   String dataOtgruzki//2013-01-30
			, String kontragentID//
			, String polzovatelID//
			, String dataNachala//
			, String dataKonca//
			, String poiskovoeSlovo//
			, int tipPoiska//ISearchBy
			, boolean etoTrafik//
			, boolean history//
			, String skladPodrazdelenia//
			, int limit//
			, int offset//
			, boolean isMustList//
			, boolean isTop//


			, String kuhnya//
			, String tochkaIdrref//
			, boolean individualcena//


			, boolean DEGUSTACIA_POISK//
			, String ingredientIdrref, String ingredientKluch, String flagmanTovarSegmentKod
									   ,boolean no_assortiment
	) {
        /*if (Cfg.useNewSkidkaCalculation) {
            return composeSQLall_NewSkidka(dataOtgruzki, kontragentID, polzovatelID, dataNachala, dataKonca
                    , poiskovoeSlovo, tipPoiska, etoTrafik, history, skladPodrazdelenia, limit//
                    , offset, isMustList, isTop, kuhnya, tochkaIdrref, individualcena, DEGUSTACIA_POISK, ingredientIdrref);
        } else {*/
		return composeSQLall_Old(dataOtgruzki, kontragentID, polzovatelID, dataNachala, dataKonca
				, poiskovoeSlovo, tipPoiska, etoTrafik, history, skladPodrazdelenia, limit//
				, offset, isMustList, isTop, kuhnya, tochkaIdrref, individualcena, DEGUSTACIA_POISK, ingredientIdrref, ingredientKluch
				, flagmanTovarSegmentKod,false,false,false,false,no_assortiment);
		// }
	}

	public static boolean _podrazdeleniya_NeIspolzovatCR = false;
	public static String polzovatelID_podrazdeleniya_NeIspolzovatCR = "?";

	public static boolean podrazdeleniya_NeIspolzovatCR(String polzovatelID) {
		if (polzovatelID_podrazdeleniya_NeIspolzovatCR == polzovatelID) {
			//
		} else {
			polzovatelID_podrazdeleniya_NeIspolzovatCR = polzovatelID;
			String sql = "select p1.naimenovanie as f1, hex(p1.NeIspolzovatCR) as nocr1"
					+ "\n		,p2.naimenovanie as f2, hex(p2.NeIspolzovatCR) as nocr2"
					+ "\n		,p3.naimenovanie as f3, hex(p3.NeIspolzovatCR) as nocr3"
					+ "\n		,p4.naimenovanie as f4, hex(p4.NeIspolzovatCR) as nocr4"
					+ "\n		,p5.naimenovanie as f5, hex(p5.NeIspolzovatCR) as nocr5"
					+ "\n	from Polzovateli"
					+ "\n		join Podrazdeleniya p1 on p1._idrref=Polzovateli.podrazdelenie"
					+ "\n		left join Podrazdeleniya p2  on p1.roditel=p2._idrref"
					+ "\n		left join Podrazdeleniya p3  on p2.roditel=p3._idrref"
					+ "\n		left join Podrazdeleniya p4  on p3.roditel=p4._idrref"
					+ "\n		left join Podrazdeleniya p5  on p4.roditel=p5._idrref"
					+ "\n	where Polzovateli._idrref=" + polzovatelID_podrazdeleniya_NeIspolzovatCR;
			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			_podrazdeleniya_NeIspolzovatCR = false;
			if (
					b.child("row").child("nocr1").value.property.value().equals("01")
							|| b.child("row").child("nocr2").value.property.value().equals("01")
							|| b.child("row").child("nocr3").value.property.value().equals("01")
							|| b.child("row").child("nocr4").value.property.value().equals("01")
							|| b.child("row").child("nocr5").value.property.value().equals("01")

			) {
				_podrazdeleniya_NeIspolzovatCR = true;
			}
		}
		return _podrazdeleniya_NeIspolzovatCR;
	}

	public static String useNewDefaultPriceUserID = "";
	public static boolean useNewDefaultPriceState = false;

	public static boolean useNewDefaultPrice(String polzovatelID) {
		//if(1==1)return true;
		if (!useNewDefaultPriceUserID.equals(polzovatelID)) {
			useNewDefaultPriceUserID = polzovatelID;
			String sql = "select p1.EdinicaPlanirovaniya as f1,p2.EdinicaPlanirovaniya as f2,p3.EdinicaPlanirovaniya as f3,p4.EdinicaPlanirovaniya as f4,p5.EdinicaPlanirovaniya as f5"
					+ "\n from Polzovateli "
					+ "\n   join Podrazdeleniya p1 on p1._idrref=Polzovateli.podrazdelenie"
					+ "\n 	left join Podrazdeleniya p2  on p1.roditel=p2._idrref "//
					+ "\n 	left join Podrazdeleniya p3  on p2.roditel=p3._idrref "//
					+ "\n 	left join Podrazdeleniya p4  on p3.roditel=p4._idrref "//
					+ "\n 	left join Podrazdeleniya p5  on p4.roditel=p5._idrref "//
					+ "\n 	where Polzovateli._idrref=" + polzovatelID;
			Bough b = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
			//System.out.println(sql + ": " + b.dumpXML());
			if (
					b.child("row").child("f1").value.property.value().equals("01")
							|| b.child("row").child("f2").value.property.value().equals("01")
							|| b.child("row").child("f3").value.property.value().equals("01")
							|| b.child("row").child("f4").value.property.value().equals("01")
							|| b.child("row").child("f5").value.property.value().equals("01")

			) {
				useNewDefaultPriceState = true;
				return useNewDefaultPriceState;
			}
			useNewDefaultPriceState = false;
			return useNewDefaultPriceState;
		} else {
			return useNewDefaultPriceState;
		}
	}

	public static String composeSQLall_Old(
			String dataOtgruzki//2013-01-30
			, String kontragentID//
			, String polzovatelID//
			, String dataNachala//
			, String dataKonca//
			, String poiskovoeSlovo//
			, int tipPoiska//ISearchBy
			, boolean etoTrafik//
			, boolean history//
			, String skladPodrazdelenia//
			, int limit//
			, int offset//
			, boolean isMustList//
			, boolean isTop//
			, String kuhnya
			, String tochkaIdrref
			, boolean individualcena
			, boolean DEGUSTACIA_POISK
			, String ingredientIdrref
			, String ingredientKluch
			, String flagmanTovarSegmentKod
			,boolean stmOnly
			,boolean starsOnly
			,boolean recomendaciaOnly
			,boolean korzinaOnly
			,boolean no_assortiment
	) {

		refreshTovariGeroiDay(dataOtgruzki);
		UpdateTask.refreshProdazhi_last(ApplicationHoreca.getInstance().getDataBase(),kontragentID);



		//if (ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().equals("hrc00")) {
		if (Cfg.selectedOrDbHRC().equals("hrc00")) {
			polzovatelID = "x'00'";
		}
		//if (ApplicationHoreca.getInstance().currentHRCmarshrut.length() > 0) {
		if (Cfg.isChangedHRC()) {
			//polzovatelID = "x'" + ApplicationHoreca.getInstance().currentIDmarshrut + "'";
			polzovatelID = "x'" + Cfg.selectedHRC_idrref() + "'";
		}
		int zapretOtgruzokOtvetsvennogo = getZapretOtgruzokOtvetsvennogo(kontragentID, polzovatelID);
		boolean letuchka = dataOtgruzki.equals(DateTimeHelper.SQLDateString(new Date()));
		String defaultSklad = "";
		if (skladPodrazdelenia.trim().toUpperCase().equals(ISklady.HORECA_ID.trim().toUpperCase())) {
			defaultSklad = " and baza=" + ISklady.HORECA_ID;
		} else {
			if (skladPodrazdelenia.trim().toUpperCase().equals(ISklady.KAZAN_ID.trim().toUpperCase())) {
				if (letuchka) {
					defaultSklad = " and baza=" + ISklady.KAZAN_ID;
				} else {
					//
				}
			} else {
				if (skladPodrazdelenia.trim().toUpperCase().equals(ISklady.MOSKVA_ID.trim().toUpperCase())) {
					if (letuchka) {
						defaultSklad = " and baza=" + ISklady.MOSKVA_ID;
					}
				} else {
					//LogHelper.debug("Unknown skladPodrazdelenia " + skladPodrazdelenia);
				}
			}
		}
		String sql = " select n._id "//
				+ "\n 	,n.[_IDRRef] "//
				+ "\n 	,n.[Artikul] ";
		if (DEGUSTACIA_POISK) {
			sql = sql + "\n 	, n.[Naimenovanie] ";
		} else {
			//sql = sql + "\n 	,case curAssortiment.Trafic when x'01' then 'под заказ ' else '' end || n.[Naimenovanie] ";
			sql = sql + "\n					  	,case curAssortiment.Trafic"
					+ "\n			when x'01' then 'под заказ '"
					+ "\n			else ''"
					+ "\n			end"
			+"\n					  	 || case brand.stm"
					+ "\n			when x'01' then 'СТМ: '"
					+ "\n			else ''"
					+ "\n			end"
					+ "\n		|| n.[Naimenovanie]";

		}
		sql = sql + "\n		|| ', НДС '"
				+ "\n		|| case n.stavkands"
				+ "\n			when X'9701531AAE7E29E1418D1FB94BB4DD8D' then '18'"
				+ "\n			when X'8C35D1AA082D09C449482233639CB5DC' then '10'"
				+ "\n			when X'96A72E469DF2A8DF4F5BF008C2577B7D' then '20'"
				+ "\n			else ''"
				+ "\n			end"
				+ "\n		|| '%'"
				/*+ "\n		||	case ifnull(hero1.Cena,0)>0 when 1 then ', Герой' else"
				+ "\n				case ifnull(hero2.Cena,0)>0 when 1 then ', Герой' else"
				+ "\n					case ifnull(hero3.Cena,0)>0 when 1 then ', Герой' else"
				+ "\n						case ifnull(hero4.Cena,0)>0 when 1 then ', Герой' else"
				+ "\n							case ifnull(hero5.Cena,0)>0 when 1 then ', Герой'"
				+ "\n								else ''"
				+ "\n							end"
				+ "\n						end"
				+ "\n					end"
				+ "\n				end"
				+ "\n			end"*/

				+ "\n 		|| case n.mark"
				+ "\n			when x'01' then ', ЧЗ'"
				+ "\n			else ''"
				+ "\n			end"

				+ "\n 		|| case ifnull(top20.nomenklatura,'')"
				+ "\n			when '' then ''"
				+ "\n			else '`'"
				+ "\n			end"
				+ "\n	as Naimenovanie"
		;
		/*sql = sql + "\n 	 || ', НДС ' || case n.stavkands when X'9701531AAE7E29E1418D1FB94BB4DD8D' then '18'"//
				+ "\n 	 when X'8C35D1AA082D09C449482233639CB5DC' then '10'"//
				+ "\n 	 when X'96A72E469DF2A8DF4F5BF008C2577B7D' then '20'"//
				+ "\n 	 else '' end"//
				+ " || '%' "
				+ "\n			 || case ifnull(top20.nomenklatura,'') when '' then '' else '`' end"//
				+ " as Naimenovanie"//
				*/
		sql = sql + "\n 	,n.[OsnovnoyProizvoditel] "//
				+ "\n 	,przv.Naimenovanie as ProizvoditelNaimenovanie "//
		;
		if (uchetnayaCena(kontragentID)) {
			sql = sql + "\n 	,TekuschieCenyOstatkovPartiy.Cena*(100+ifnull(nk1.procent,ifnull(nk2.procent,0)))/100 as Cena ";
		} else {
			sql = sql + "\n 	,(select c.Cena from CenyNomenklaturySklada c where date(c.period)<=date(parameters.dataOtgruzki) and c.nomenklatura=n._idrref order by c.period desc limit 1) as Cena ";
		}
		if (useNewDefaultPrice(polzovatelID)) {
			sql = sql
					+ "\n 	,case when ifnull(newSkidki.price,0)=0 "
					+ "\n 	    then "
					+ "\n 	        case when ( "
					+ "\n 	   				 	(Prodazhi_CR.Stoimost/Prodazhi_CR.kolichestvo >= (1.000+ifnull(n1.nacenka,ifnull(n2.nacenka,ifnull(n3.nacenka,ifnull(n4.nacenka ,ifnull(n5.nacenka ,0))))*0.010))*TekuschieCenyOstatkovPartiy.Cena) "
					+ "\n 	   				 	 and ("
					+ "\n 	   				 	 		ifnull(Prodazhi_CR.VidSkidki,x'00') = x'" + Cfg.skidkaIdOldCenovoyeReagirovanie + "'"
					+ "\n 	   				 	 		or ifnull(Prodazhi_CR.VidSkidki,x'00') = x'" + Cfg.skidkaIdCenovoyeReagirovanie + "'"
					+ "\n 	   				 	 		or ifnull(Prodazhi_CR.VidSkidki,x'00') = x'" + Cfg.skidkaIdAutoReagirovanie + "'"
					+ "\n 	   				 		)"
					+ "\n 	   				 	) "
					+ "\n 	            then round(100*Prodazhi_CR.Stoimost/Prodazhi_CR.kolichestvo)/100.00 "
					+ "\n 	            else 0 "
					+ "\n 	        end"
					+ "\n 	     else "
					+ "\n 	 		newSkidki.price "
					+ "\n        end "
					+ "\n 	 as Skidka "
					+ "\n 	,ifnull(newSkidki.price,0) as CenaSoSkidkoy "//
					+ "\n 	,case when ifnull(newSkidki.comment,'')='' "
					+ "\n 	    then "
					+ "\n 	        case when ( "
					+ "\n 	   				 	(Prodazhi_CR.Stoimost/Prodazhi_CR.kolichestvo >= (1.000+ifnull(n1.nacenka,ifnull(n2.nacenka,ifnull(n3.nacenka,ifnull(n4.nacenka ,ifnull(n5.nacenka ,0))))*0.010))*TekuschieCenyOstatkovPartiy.Cena) "
					+ "\n 	   				 	 and ("
					+ "\n 	   				 	 		ifnull(Prodazhi_CR.VidSkidki,x'00') = x'" + Cfg.skidkaIdOldCenovoyeReagirovanie + "'"
					+ "\n 	   				 	 		or ifnull(Prodazhi_CR.VidSkidki,x'00') = x'" + Cfg.skidkaIdCenovoyeReagirovanie + "'"
					+ "\n 	   				 	 		or ifnull(Prodazhi_CR.VidSkidki,x'00') = x'" + Cfg.skidkaIdAutoReagirovanie + "'"
					+ "\n 	   				 		)"
					+ "\n 	   				 	) "
					+ "\n 	            then 'цр'"
					//+ "\n 	            else '' "
					+ "\n 					else case when ifnull(hero1.Cena,0)>0 or ifnull(hero2.Cena,0)>0 or ifnull(hero3.Cena,0)>0 or ifnull(hero4.Cena,0)>0 or ifnull(hero5.Cena,0)>0"
					+ "\n 						then 'ТГ' else '' end  "
					+ "\n 	        end"
					+ "\n 	     else "
					+ "\n 	 		newSkidki.comment "
					+ "\n        end "
					+ "\n 	 as VidSkidki "
			;
		} else {
			/*sql = sql + "\n 	,ifnull(newSkidki.price,0) as Skidka "
					+ "\n 	,ifnull(newSkidki.price,0) as CenaSoSkidkoy "//
					+ "\n 	,ifnull(newSkidki.comment,'') as VidSkidki "
			;*/
			sql = sql + "\n 	,ifnull(newSkidki.price,0) as Skidka "
					+ "\n 	,ifnull(newSkidki.price,0) as CenaSoSkidkoy "//
					+ "\n 	,case"
					+ "\n 		when ifnull(hero1.Cena,0)>0 or ifnull(hero2.Cena,0)>0 or ifnull(hero3.Cena,0)>0 or ifnull(hero4.Cena,0)>0 or ifnull(hero5.Cena,0)>0"
					+ "\n 		then 'ТГ' else ifnull(newSkidki.comment,'') end as VidSkidki "
			;
		}
		//sql = sql + "\n 	,n.skladEdIzm || ' по ' || n.skladEdVes || 'кг' as [EdinicyIzmereniyaNaimenovanie] ";
		sql = sql + "\n 	,case when n.skladEdIzm='кг' then 'кг' else n.skladEdIzm || ' по ' || n.skladEdVes || 'кг' end as [EdinicyIzmereniyaNaimenovanie] ";
		sql = sql + "\n 	,n.kvant as MinNorma "//
				+ "\n 	,n.otchEdKoef as [Koephphicient] "//
				+ "\n  	,x'00' as [EdinicyIzmereniyaID] "//
				+ "\n  	,n.Roditel as Roditel "//
		;
		if (uchetnayaCena(kontragentID) || podrazdeleniya_NeIspolzovatCR(polzovatelID)) {
			sql = sql + "\n 	,0 as MinCena ";
		} else {
			sql = sql + "\n 	,case when ifnull(newSkidki.price,0)>0 and (newSkidki.comment='Индивидуальная' or newSkidki.comment='Фикс.цена') ";
			sql = sql + "\n 	        then 0 "//

					+ "\n 			when ifnull(hero1.Cena,0)>0 then hero1.Cena"//
					+ "\n 			when ifnull(hero2.Cena,0)>0 then hero2.Cena"//
					+ "\n 			when ifnull(hero3.Cena,0)>0 then hero3.Cena"//
					+ "\n 			when ifnull(hero4.Cena,0)>0 then hero4.Cena"//
					+ "\n 			when ifnull(hero5.Cena,0)>0 then hero5.Cena"//

					+ "\n 			when ifnull(n1.MinCena,0)>0 then n1.MinCena"//
					+ "\n 			when ifnull(n2.MinCena,0)>0 then n2.MinCena"//
					+ "\n 			when ifnull(n3.MinCena,0)>0 then n3.MinCena"//
					+ "\n 			when ifnull(n4.MinCena,0)>0 then n4.MinCena"//
					+ "\n 			when ifnull(n5.MinCena,0)>0 then n5.MinCena"//
					+ "\n 			else round( "//
					+ "\n 				round(1000*(1.000 "//
					+ "\n 						+ifnull(n1.nacenka "//
					+ "\n 						,ifnull(n2.nacenka "//
					+ "\n 						,ifnull(n3.nacenka "//
					+ "\n 						,ifnull(n4.nacenka "//
					+ "\n 						,ifnull(n5.nacenka "//
					+ "\n 						,0))))) "//
					+ "\n 					*0.010)*TekuschieCenyOstatkovPartiy.Cena "//
					+ "\n 					*(case when ifnull(n1.zapret "//
					+ "\n 						,ifnull(n2.zapret "//
					+ "\n 						,ifnull(n3.zapret "//
					+ "\n 						,ifnull(n4.zapret "//
					+ "\n 						,ifnull(n5.zapret "//
					+ "\n 						,x'01')))))=x'00' "//
					+ "\n 						then 1.000 else 0 end) "//
					+ "\n 				    )/1000    ,2 "//
					+ "\n 			    ) "//
					+ "\n 		    end as MinCena "//
			;
		}
		//sql = sql + "\n 	,(select 1.1*c.Cena from CenyNomenklaturySklada c where date(c.period)<=date(parameters.dataOtgruzki) and c.nomenklatura=n._idrref order by c.period desc limit 1) as MaxCena "//
		sql = sql + "\n 	,(select 1.2*c.Cena from CenyNomenklaturySklada c where date(c.period)<=date(parameters.dataOtgruzki) and c.nomenklatura=n._idrref order by c.period desc limit 1) as MaxCena "//
				+ "\n 	,TekuschieCenyOstatkovPartiy.cena as BasePrice "//
				+ "\n 	,round(100*Prodazhi.Stoimost/Prodazhi.kolichestvo)/100.00 as LastPrice "
		;

		if (ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().equals("hrc00")) {
			sql = sql + "\n 	,0 as Nacenka ";
		} else {
			if (uchetnayaCena(kontragentID)) {
				sql = sql + "\n 	,ifnull(nk1.Procent,nk2.Procent) as Nacenka ";
			} else {
				sql = sql + "\n 	,ifnull(nk1.ProcentSkidkiNacenki,nk2.ProcentSkidkiNacenki) as Nacenka ";
			}
		}
		sql = sql + "\n 	,ZapretSkidokTov.Individualnye as ZapretSkidokTovIndividualnye "//
				+ "\n 	,ZapretSkidokTov.Nokopitelnye as ZapretSkidokTovNokopitelnye "//
				+ "\n 	,ZapretSkidokTov.Partner as ZapretSkidokTovPartner "//
				+ "\n 	,ZapretSkidokTov.Razovie as ZapretSkidokTovRazovie "//
				+ "\n 	,ZapretSkidokTov.Nacenki as ZapretSkidokTovNacenki "//
				+ "\n 	,ZapretSkidokProizv.Individualnye as ZapretSkidokProizvIndividualnye "//
				+ "\n 	,ZapretSkidokProizv.Nokopitelnye as ZapretSkidokProizvNokopitelnye "//
				+ "\n 	,ZapretSkidokProizv.Partner as ZapretSkidokProizvPartner "//
				+ "\n 	,ZapretSkidokProizv.Razovie as ZapretSkidokProizvRazovie "//
				+ "\n 	,ZapretSkidokProizv.Nacenki as ZapretSkidokProizvNacenki "//
		;
		sql = sql + "\n 	,0 as FiksirovannyeCeny ";
		sql = sql + "\n 			,0 as SkidkaPartneraKarta ";
		sql = sql + "\n 	,0 as NakopitelnyeSkidki ";
		sql = sql + "\n 	,Prodazhi.period as LastSell "//
				+ "\n	,n.skladEdVes as vesedizm"//
		;
		if (zapretOtgruzokOtvetsvennogo == ZapretOtgruzokOtvetsvennogoInclude) {
			sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
					+ "\n				where Otvetstvenniy=parameters.polzovatel"//
					+ "\n				and proizvoditel=n._idrref"//
					+ "\n				limit 1) as zooSelf"//
			;
			sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
					+ "\n				where Otvetstvenniy=parameters.polzovatel"//
					+ "\n				and proizvoditel=n.roditel"//
					+ "\n				limit 1) as zooParent"//
			;
			sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
					+ "\n				where Otvetstvenniy=parameters.polzovatel"//
					+ "\n				and proizvoditel=parent.roditel"//
					+ "\n				limit 1) as zooParentParent"//
			;
			sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
					+ "\n				where Otvetstvenniy=parameters.polzovatel"//
					+ "\n				and proizvoditel=n.[OsnovnoyProizvoditel]"//
					+ "\n				limit 1) as zooProizvoditel"//
			;
		} else {
			if (zapretOtgruzokOtvetsvennogo == ZapretOtgruzokOtvetsvennogoExclude) {
				sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
						+ "\n				where proizvoditel=n._idrref"//
						+ "\n				limit 1) as zooSelf"//
				;
				sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
						+ "\n				where proizvoditel=n.roditel"//
						+ "\n				limit 1) as zooParent"//
				;
				sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
						+ "\n				where proizvoditel=parent.roditel"//
						+ "\n				limit 1) as zooParentParent"//
				;
				sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
						+ "\n				where proizvoditel=n.[OsnovnoyProizvoditel]"//
						+ "\n				limit 1) as zooProizvoditel"//
				;
			}
		}
		sql = sql + "\n 	,x'00' as skladKazan ";
		sql = sql + "\n 	,x'00' as skladHoreca ";
		sql = sql + "\n 	,x'00' as skladMoskva ";
		sql = sql + "\n						,top20._id as mustListId";
		sql = sql + "\n						,(select  (c.Cena - TekuschieCenyOstatkovPartiy.cena) / TekuschieCenyOstatkovPartiy.cena from CenyNomenklaturySklada c where date(c.period)<=date(parameters.dataOtgruzki) and c.nomenklatura=n._idrref order by c.period desc limit 1) as rPrice";
		sql = sql + "\n  	,n1.minCena as n1minCena ";
		sql = sql + "\n  	,n2.minCena as n2minCena ";
		sql = sql + "\n  	,n3.minCena as n3minCena ";
		sql = sql + "\n  	,n4.minCena as n4minCena ";
		sql = sql + "\n  	,n5.minCena as n5minCena ";
		sql = sql + "\n  	,atricle_count.artikul as artCount ";
		sql = sql + "\n 	,'' || Prodazhi.kolichestvo || '/' || Prodazhi.aktivnost || n.skladEdIzm as lastSellCount ";
		sql = sql + "\n  	,stars.artikul as stars_artikul ";
		sql = sql + "\n  	,newSkidki.datastart as datastart, newSkidki.dataend as dataend";
		//sql = sql + "\n  	,Prodazhi.aktivnost as aktivnostMonth ";
		sql = sql + "\n	from Nomenklatura_sorted n ";
		sql = sql + "\n 	cross join Consts const ";
		if(!no_assortiment){
			sql = sql + "\n 	cross join AssortimentCurrent curAssortiment on curAssortiment.nomenklatura_idrref=n.[_IDRRef]";
		}
		sql = sql + "\n 	cross join (select "//
				+ "\n 			'" + dataOtgruzki + "' as dataOtgruzki "//
				+ "\n 			," + kontragentID + " as kontragent "//
				+ "\n 			," + polzovatelID + " as polzovatel "//
				+ "\n 		) parameters "//
		;
		if (individualcena) {
			sql = sql + "\n 	cross join IndividualnieSkidki iskid on iskid.nomenklatura=n._idrref and iskid.kontragent=parameters.kontragent and parameters.dataOtgruzki >=iskid.period and parameters.dataOtgruzki <=iskid.DataOkonchaniya  ";
		}
		sql = sql + "\n 	cross join Polzovateli on Polzovateli._idrref=parameters.polzovatel "//
				+ "\n 	cross join Podrazdeleniya p1 on p1._idrref=Polzovateli.podrazdelenie "//
		;
		sql = sql + "\n 	cross join kontragenty on kontragenty._idrref=parameters.kontragent ";

		if (flagmanTovarSegmentKod != null) {
			sql = sql + "\n 	cross join FlagmanTovar on  FlagmanTovar.Articul=n.Artikul and FlagmanTovar.SegmentKod='" + flagmanTovarSegmentKod + "'  ";
		}
		if (history) {
			/*
			sql = sql//
					+ "\n 	cross join Prodazhi_last Prodazhi"
					+ "\n 				on Prodazhi.DogovorKontragenta in (select DogovoryKontragentov_strip._IDRref from DogovoryKontragentov_strip where DogovoryKontragentov_strip.vladelec=parameters.kontragent ) "//
					+ "\n 				and Prodazhi.nomenklatura=n.[_IDRRef] ";
			*/
			sql = sql + "\n 	cross join Prodazhi_last Prodazhi on Prodazhi.nomenklatura=n.[_IDRRef] ";
		} else {
			/*
			if (flagmanTovarSegmentKod != null) {
				//Calendar c = Calendar.getInstance();
				//c.set(Calendar.DAY_OF_MONTH, 1);
				//String thismonth = Auxiliary.sqliteDate.format(c.getTime());
				sql = sql//
						+ "\n 	left join Prodazhi_last Prodazhi on Prodazhi.DogovorKontragenta in (select DogovoryKontragentov_strip._IDRref from DogovoryKontragentov_strip where DogovoryKontragentov_strip.vladelec=parameters.kontragent ) "//
						//+ "\n 				and Prodazhi.nomenklatura=n.[_IDRRef] and Prodazhi.period>=date('" + thismonth + "') ";
						+ "\n 				and Prodazhi.nomenklatura=n.[_IDRRef] ";
			} else {
				sql = sql//
						+ "\n 	left join Prodazhi_last Prodazhi on Prodazhi.DogovorKontragenta in (select DogovoryKontragentov_strip._IDRref from DogovoryKontragentov_strip where DogovoryKontragentov_strip.vladelec=parameters.kontragent ) "//
						+ "\n 				and Prodazhi.nomenklatura=n.[_IDRRef] ";
			}
			*/
			sql = sql + "\n 	left join Prodazhi_last Prodazhi on Prodazhi.nomenklatura=n.[_IDRRef] ";
		}
		if(no_assortiment){
			sql = sql + "\n 	left join AssortimentCurrent curAssortiment on curAssortiment.nomenklatura_idrref=n.[_IDRRef]";
		}
		sql = sql + "\n 	left join Prodazhi_CR on Prodazhi_CR.nomenklatura=n.[_IDRRef] ";
		cacheTop20(polzovatelID, dataOtgruzki);
		/*if (flagmanTovarSegmentKod == null) {
			sql = sql + "\n 	left join FlagmanTovar on  FlagmanTovar.Articul=n.Artikul'  ";
		}*/
		sql = sql + "\n  	left join SkidkiLast newSkidki on newSkidki.nomenklatura=n._idrref and date(newSkidki.datastart)<=date('" + dataOtgruzki + "') and date(newSkidki.dataend)>=date('" + dataOtgruzki + "')";
		sql = sql + "\n  	left join dopmotivaciya_cache top20 on top20.nomenklatura=n._idrref";
		sql = sql + "\n 	left join TekuschieCenyOstatkovPartiy_strip TekuschieCenyOstatkovPartiy on TekuschieCenyOstatkovPartiy.nomenklatura=n.[_IDRRef] ";

		sql = sql + "\n 	left join nomenklatura category1 on n.Roditel=category1._idrref";
		sql = sql + "\n 	left join nomenklatura category2 on category1.Roditel=category2._idrref";
		sql = sql + "\n 	left join nomenklatura category3 on category2.Roditel=category3._idrref";
		sql = sql + "\n 	left join nomenklatura category4 on category3.Roditel=category4._idrref";

		sql = sql + "\n 	left join RecommendationAndBasket1221 recKorz on recKorz.Kontragent=parameters.kontragent and recKorz.nomenklatura=n._idrref";

		if (uchetnayaCena(kontragentID)) {
			sql = sql + "\n 	left join NacenkaKUchetnoiCene nk1 on nk1.klient=parameters.kontragent "//
					+ "\n 			and nk1.Period=(select max(Period) from NacenkaKUchetnoiCene "//
					+ "\n 				where klient=parameters.kontragent "//
					+ "\n 					and date(period)<=date(parameters.dataOtgruzki)"// "//
					+ "\n 				) ";
			sql = sql + "\n 	left join (select Procent from NacenkaKUchetnoiCene " //
					+ "\n 			join kontragenty on kontragenty._idrref=" + kontragentID //
					+ "\n 			where klient=kontragenty.GolovnoyKontragent"//
					+ "\n 				and period=("//
					+ "\n 					select max(period)"//
					+ "\n 					from NacenkaKUchetnoiCene"//
					+ "\n 					join kontragenty on kontragenty._idrref=" + kontragentID //
					+ "\n 					where klient=kontragenty.GolovnoyKontragent and date(period)<=date('" + dataOtgruzki + "')"//
					+ "\n 					)"//
					+ "\n 			limit 1"//
					+ "\n 		) nk2";
		} else {
			sql = sql + "\n 	left join NacenkiKontr nk1 on nk1.PoluchatelSkidki=parameters.kontragent "//
					+ "\n 			and nk1.Period=(select max(Period) from NacenkiKontr "//
					+ "\n 				where PoluchatelSkidki=parameters.kontragent "//
					+ "\n 					and date(period)<=date(parameters.dataOtgruzki) and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) and podrazdelenie=p1._idrref"// "//
					+ "\n 				) ";
			sql = sql + "\n 	left join (select ProcentSkidkiNacenki from NacenkiKontr " //
					+ "\n 			join kontragenty on kontragenty._idrref=" + kontragentID //
					+ "\n 			where PoluchatelSkidki=kontragenty.GolovnoyKontragent"//
					+ "\n 				and period=("//
					+ "\n 					select max(period)"//
					+ "\n 					from NacenkiKontr"//
					+ "\n 					join kontragenty on kontragenty._idrref=" + kontragentID //
					+ "\n 					where PoluchatelSkidki=kontragenty.GolovnoyKontragent and date(DataOkonchaniya)>=date('" + dataOtgruzki + "')"//
					+ "\n 					)"//
					+ "\n 			limit 1"//
					+ "\n 		) nk2";
		}
		sql = sql + "\n 	left join ZapretSkidokTov on ZapretSkidokTov.Nomenklatura=n.[_IDRRef] "//
				+ "\n 			and ZapretSkidokTov.Period=(select max(Period) from ZapretSkidokTov "//
				+ "\n 				where Nomenklatura=n.[_IDRRef] "//
				+ "\n 					and date(period)<=date(parameters.dataOtgruzki) "//
				+ "\n 				) "//
				+ "\n 	left join ZapretSkidokProizv on ZapretSkidokProizv.Proizvoditel=n.[OsnovnoyProizvoditel] "//
				+ "\n 				and ZapretSkidokProizv.Period=(select max(Period) from ZapretSkidokProizv "//
				+ "\n 					where Proizvoditel=n.[OsnovnoyProizvoditel] "//
				+ "\n 						and date(period)<=date(parameters.dataOtgruzki) "//
				+ "\n 					) "//
		;
		sql = sql + "\n 	left join Podrazdeleniya p2  on p1.roditel=p2._idrref "//
				+ "\n 	left join Podrazdeleniya p3  on p2.roditel=p3._idrref "//
				+ "\n 	left join Podrazdeleniya p4  on p3.roditel=p4._idrref "//
				+ "\n 	left join Podrazdeleniya p5  on p4.roditel=p5._idrref "//
		;
		sql = sql + "\n 	left join MinimalnyeNacenki n1  on n1.podrazdelenie=p1._idrref and n1.Nomenklatura=n._idrref "//
				+ "\n 	left join MinimalnyeNacenki n2  on n2.podrazdelenie=p2._idrref and n2.Nomenklatura=n._idrref  "//
				+ "\n 	left join MinimalnyeNacenki n3  on n3.podrazdelenie=p3._idrref and n3.Nomenklatura=n._idrref  "//
				+ "\n 	left join MinimalnyeNacenki n4  on n4.podrazdelenie=p4._idrref and n4.Nomenklatura=n._idrref  "//
				+ "\n 	left join MinimalnyeNacenki n5  on (n5.podrazdelenie=X'00000000000000000000000000000000' or n5.podrazdelenie=X'00') and n5.Nomenklatura=n._idrref "//
		;
		sql = sql + "\n		left join TovariGeroiDay hero1 on hero1.podrazdelenie=p1._idrref and hero1.Nomenklatura=n._idrref and date(hero1.DataNachala)<=date(parameters.dataOtgruzki) and date(hero1.DataOkonchaniya)>=date(parameters.dataOtgruzki)"
				+ "\n		left join TovariGeroiDay hero2 on hero2.podrazdelenie=p2._idrref and hero2.Nomenklatura=n._idrref and date(hero2.DataNachala)<=date(parameters.dataOtgruzki) and date(hero2.DataOkonchaniya)>=date(parameters.dataOtgruzki)"
				+ "\n		left join TovariGeroiDay hero3 on hero3.podrazdelenie=p3._idrref and hero3.Nomenklatura=n._idrref and date(hero3.DataNachala)<=date(parameters.dataOtgruzki) and date(hero3.DataOkonchaniya)>=date(parameters.dataOtgruzki)"
				+ "\n		left join TovariGeroiDay hero4 on hero4.podrazdelenie=p4._idrref and hero4.Nomenklatura=n._idrref and date(hero4.DataNachala)<=date(parameters.dataOtgruzki) and date(hero4.DataOkonchaniya)>=date(parameters.dataOtgruzki)"
				+ "\n		left join TovariGeroiDay hero5 on (hero5.podrazdelenie=X'00000000000000000000000000000000' or hero5.podrazdelenie=X'00')=p5._idrref and hero5.Nomenklatura=n._idrref and date(hero5.DataNachala)<=date(parameters.dataOtgruzki) and date(hero5.DataOkonchaniya)>=date(parameters.dataOtgruzki)"
		;
		sql = sql + "\n 	left join Proizvoditel przv on n.[OsnovnoyProizvoditel] = przv._IDRRef ";
		if (uchetnayaCena(kontragentID)) {
			sql = sql + "\n 	left join FiksirovannyeCenyUchet fx1 on fx1.DataOkonchaniya=( "//
					+ "\n 					select max(DataOkonchaniya) from FiksirovannyeCenyUchet "//
					+ "\n 						where PoluchatelSkidki=parameters.kontragent "//
					+ "\n 						and date(period)<=date(parameters.dataOtgruzki) "//
					+ "\n 						and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) "//
					+ "\n 						and Nomenklatura=n.[_IDRRef] "//
					+ "\n 				) and fx1.PoluchatelSkidki=parameters.kontragent "//
					+ "\n 				and fx1.Nomenklatura=n.[_IDRRef] "//
					+ "\n 	left join FiksirovannyeCenyUchet fx2 on fx2.DataOkonchaniya=( "//
					+ "\n 					select max(DataOkonchaniya) from FiksirovannyeCenyUchet "//
					+ "\n 						where PoluchatelSkidki=kontragenty.GolovnoyKontragent "//
					+ "\n 						and date(period)<=date(parameters.dataOtgruzki) "//
					+ "\n 						and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) "//
					+ "\n 						and Nomenklatura=n.[_IDRRef] "//
					+ "\n 				) and fx2.PoluchatelSkidki=kontragenty.GolovnoyKontragent "//
					+ "\n 				and fx2.Nomenklatura=n.[_IDRRef] "//
			;
		} else {
			sql = sql + "\n 	left join FiksirovannyeCeny_actual fx1 on fx1.DataOkonchaniya=( "//
					+ "\n 					select max(DataOkonchaniya) from FiksirovannyeCeny_actual "//
					+ "\n 						where PoluchatelSkidki=parameters.kontragent "//
					+ "\n 						and date(period)<=date(parameters.dataOtgruzki) "//
					+ "\n 						and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) "//
					+ "\n 						and Nomenklatura=n.[_IDRRef] "//
					+ "\n 				) and fx1.PoluchatelSkidki=parameters.kontragent "//
					+ "\n 				and fx1.Nomenklatura=n.[_IDRRef] "//
					+ "\n 	left join FiksirovannyeCeny_actual fx2 on fx2.DataOkonchaniya=( "//
					+ "\n 					select max(DataOkonchaniya) from FiksirovannyeCeny_actual "//
					+ "\n 						where PoluchatelSkidki=kontragenty.GolovnoyKontragent "//
					+ "\n 						and date(period)<=date(parameters.dataOtgruzki) "//
					+ "\n 						and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) "//
					+ "\n 						and Nomenklatura=n.[_IDRRef] "//
					+ "\n 				) and fx2.PoluchatelSkidki=kontragenty.GolovnoyKontragent "//
					+ "\n 				and fx2.Nomenklatura=n.[_IDRRef] "//
			;
		}
		if (zapretOtgruzokOtvetsvennogo != ZapretOtgruzokOtvetsvennogoNone) {
			sql = sql + "\n  		left join Nomenklatura parent on n.roditel=parent._idrref";
		}
		if (ingredientIdrref != null) {
			sql = sql + "\n  		left join ReceptiiProducty reprod on n.product=reprod.product and reprod.Kluch='" + ingredientKluch + "' and reprod._idrref=X'" + ingredientIdrref + "'";
		}
		sql = sql + "\n  		left join atricle_count on atricle_count.artikul=n.artikul";
		sql = sql + "\n  		left join stars on stars.artikul=n.artikul";
		sql = sql + "\n  		left join brand on brand._idrref=n.brand";
		String poisk = "";
		if (poiskovoeSlovo == null) poiskovoeSlovo = "";
		if (poiskovoeSlovo.trim().length() > 0) {
			/*if (tipPoiska == ISearchBy.SEARCH_NAME) {
				poisk = " ( n.[UpperName] like '%" + poiskovoeSlovo.trim().toUpperCase() + "%') ";
			} else {*/
				if (tipPoiska == ISearchBy.SEARCH_NAME) {
					poisk = " ( n.[tegi] like '%" + poiskovoeSlovo.trim().toUpperCase() + "%' or n.[UpperName] like '%" + poiskovoeSlovo.trim().toUpperCase() + "%') ";
				} else {
					if (tipPoiska == ISearchBy.SEARCH_ARTICLE) {
						poisk = " ( n.[Artikul] = '" + poiskovoeSlovo.trim().toUpperCase() + "') ";
					} else {
						if (tipPoiska == ISearchBy.SEARCH_VENDOR) {
							poisk = " ( ProizvoditelNaimenovanie like '%" + poiskovoeSlovo.trim().toUpperCase() + "%') ";
						} else {
							if (tipPoiska == ISearchBy.SEARCH_CHILDREN) {
								//poisk = " ( n.Roditel=" + poiskovoeSlovo.trim().toUpperCase() + ") ";
								poisk = " ( n.Roditel=" + poiskovoeSlovo.trim().toUpperCase()
										+ " or category1.Roditel=" + poiskovoeSlovo.trim().toUpperCase()
										+ " or category2.Roditel=" + poiskovoeSlovo.trim().toUpperCase()
										+ " or category3.Roditel=" + poiskovoeSlovo.trim().toUpperCase()
										+ " or category4.Roditel=" + poiskovoeSlovo.trim().toUpperCase()
										+ ") ";
							} else {
								if (tipPoiska == ISearchBy.SEARCH_IDRREF) {
									poisk = " ( n.[_IDRRef] = " + poiskovoeSlovo.trim().toUpperCase() + ") ";
								} else {
									if (tipPoiska == ISearchBy.SEARCH_CUSTOM) {
										poisk = " ( " + poiskovoeSlovo + ") ";
									} else {
										if (tipPoiska == ISearchBy.SEARCH_HERO) {
										/*poisk = " ( n.[UpperName] like '%" + poiskovoeSlovo.trim().toUpperCase() + "%' and (ifnull(n1.minCena,0)>0"
												+" or ifnull(n2.minCena,0)>0"
												+" or ifnull(n3.minCena,0)>0"
												+" or ifnull(n4.minCena,0)>0"
												+" or ifnull(n5.minCena,0)>0"
												+")) ";
										*/
											poisk = " ( n.[UpperName] like '%" + poiskovoeSlovo.trim().toUpperCase() + "%' and (ifnull(hero1.Cena,0)>0"
													+ " or ifnull(hero2.Cena,0)>0"
													+ " or ifnull(hero3.Cena,0)>0"
													+ " or ifnull(hero4.Cena,0)>0"
													+ " or ifnull(hero5.Cena,0)>0"
													+ ")) ";
										}
									}
								}
							}
						}
					}
				}
			//}
		} else {
			if (tipPoiska == ISearchBy.SEARCH_HERO) {
				/*poisk = " (ifnull(n1.minCena,0)>0"
						+" or ifnull(n2.minCena,0)>0"
						+" or ifnull(n3.minCena,0)>0"
						+" or ifnull(n4.minCena,0)>0"
						+" or ifnull(n5.minCena,0)>0"
						+") ";*/
				poisk = " (ifnull(hero1.Cena,0)>0"
						+ " or ifnull(hero2.Cena,0)>0"
						+ " or ifnull(hero3.Cena,0)>0"
						+ " or ifnull(hero4.Cena,0)>0"
						+ " or ifnull(hero5.Cena,0)>0"
						+ ") ";
			}
		}
		if(no_assortiment){
			sql = sql + "\n where 1=1 ";
		}else{
			if(etoTrafik){
				sql = sql + "\n where  curAssortiment.Trafic=x'01' and curAssortiment.zapret!=x'01' ";
			}else{
				sql = sql + "\n where curAssortiment.zapret!=x'01' ";
			}
		}

		if (poisk.length() > 0) {
			sql = sql + "\n and " + poisk;
		}
		//stmOnly=true;
		if(stmOnly){
			sql = sql + "\n and brand.stm=x'01'" ;
		}
		if (isMustList == true) {
			sql = sql + "\n and top20._id>0";
		} else {
			if (isTop == true) {
				sql = sql + "\n and rPrice>0.2499";
			}
		}
		if (kuhnya != null) {
			sql = sql + "\n and exists (select _idrref from nomenklaturaVidyKuhon kuhnya where kuhnya._idrref=n._idrref and kuhnya.vidkuhni='" + kuhnya + "' limit 1)";
		}
		if (tochkaIdrref != null) {
			sql = sql + "\n and exists (select _idrref from nomenklaturaTipyTorgTochek tchk where tchk._idrref=n._idrref and tchk.TipTorgTochki=X'" + tochkaIdrref + "' limit 1)";
		}
		if (ingredientIdrref != null) {
			sql = sql + "\n and reprod.product is not null";
		}
		//if(Activity_NomenclatureNew.filterByStar.value()){
		//	sql = sql + "\n and stars.artikul=n.artikul";
		//}
		if(starsOnly){
			sql = sql + "\n and stars.artikul=n.artikul";
		}
		if(korzinaOnly){
			sql = sql + "\n and recKorz.vid='Корзина 1221'";
		}
		if(recomendaciaOnly){
			sql = sql + "\n and recKorz.vid='Реком. 1221'";
		}
		/*if(stmOnly) {
			sql=sql+" order by brand.stm desc, n.naimenovanie ";
		}*/
		sql = sql + "\n limit " + limit + " offset " + offset;
		//System.out.println("stmOnly "+stmOnly);
		//System.out.println( "composeSQLall_Old: " + sql);
		return sql;
	}

	static String dataOtgruzkiTovariGeroiDay = "";

	static void refreshTovariGeroiDay(String dataOtgruzki) {
		//System.out.println("refreshTovariGeroiDay "+dataOtgruzki);
		if (dataOtgruzki.equals(dataOtgruzkiTovariGeroiDay)) {
			//
		} else {
			dataOtgruzkiTovariGeroiDay = dataOtgruzki;
			String sql = ""
					+ "\n		create table if not exists TovariGeroiDay  ("
					+ "\n                _id integer primary key asc autoincrement"
					+ "\n                 ,Registrator blob null"
					+ "\n                 ,DataNachala date null"
					+ "\n                 ,DataOkonchaniya date null"
					+ "\n                 ,Podrazdelenie blob null"
					+ "\n                 ,Nomenklatura blob null"
					+ "\n                 ,Cena numeric null"
					+ "\n                );"
					+ "\n				";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = "delete from TovariGeroiDay;";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = ""
					+ "\ninsert into TovariGeroiDay (Registrator,DataNachala,DataOkonchaniya,Podrazdelenie,Nomenklatura,Cena)"
					+ "\n	select hero1.Registrator,hero1.DataNachala,hero1.DataOkonchaniya,hero1.Podrazdelenie,hero1.Nomenklatura,hero1.Cena"
					+ "\n	from TovariGeroi hero1"
					+ "\n	join Podrazdeleniya p on p._idrref=hero1.podrazdelenie"
					+ "\n	join nomenklatura nn on nn._idrref=hero1.nomenklatura"
					+ "\n	where date(hero1.datanachala)<=date('" + dataOtgruzki + "') and date(hero1.DataOkonchaniya)>=date('" + dataOtgruzki + "')"
					+ "\n		and not exists (select hero2._id from TovariGeroi hero2"
					+ "\n			where hero1.nomenklatura=hero2.nomenklatura"
					+ "\n				and hero1.podrazdelenie=hero2.podrazdelenie"
					+ "\n				and date(hero1.datanachala)<date(hero2.datanachala)"
					+ "\n				and date(hero2.datanachala)<=date('" + dataOtgruzki + "')"
					+ "\n				and date(hero2.DataOkonchaniya)>=date('" + dataOtgruzki + "')"
					+ "\n			);";
			//System.out.println("refreshTovariGeroiDay "+sql);
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);

			sql = "create index if not exists IX_TovariGeroiDay_Registrator on TovariGeroiDay(Registrator);";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = "create index if not exists IX_TovariGeroiDay_DataNachala on TovariGeroiDay(DataNachala);";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = "create index if not exists IX_TovariGeroiDay_DataOkonchaniya on TovariGeroiDay(DataOkonchaniya);";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = "create index if not exists IX_TovariGeroiDay_Podrazdelenie on TovariGeroiDay(Podrazdelenie);";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			sql = "create index if not exists IX_TovariGeroiDay_Nomenklatura on TovariGeroiDay(Nomenklatura);";
			ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
		}

	}

	public Cursor Request(SQLiteDatabase db, String orderBy, boolean degustacia) {
		//String sql = mStrQuery + " group by n.[_idrref] " + AddOrderBy(orderBy) + " limit 100;";
		//System.out.println(this.getClass().getCanonicalName() + " Request "//
		//+ "\n" + sql//
		//+ "\n" + DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
		//+ ", " + ApplicationHoreca.getInstance().getClientInfo().getID()//
		//);
		/*System.out.println(this.getClass().getCanonicalName() + " Request:\n" + sql//
				+"\n"+DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
				+"\n"+ApplicationHoreca.getInstance().getClientInfo().getID()//
				);
		*/
		//System.out.println(this.getClass().getCanonicalName() + " Request\n"+
		//Auxiliary.fromCursor(db.rawQuery(mStrQuery, null)).dumpXML());
		Cursor c = db.rawQuery(mStrQuery, null);//
		//, new String[] {//
		//DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
		//, ApplicationHoreca.getInstance().getClientInfo().getID() //
		//});
		//System.out.println(this.getClass().getCanonicalName() + " done request");
		return c;
	}

	protected abstract void SetRequestString(boolean degustacia);

	protected String AddOrderBy(String orderBy) {
		if (orderBy != null && orderBy.length() != 0) {
			if (mOrderField == orderBy) {
				mOrderDirection = 1 - mOrderDirection;
			} else {
				mOrderDirection = ORDER_ASCEND;
			}
			mOrderField = orderBy;
			return " ORDER BY n.[" + orderBy + "] " + (mOrderDirection == ORDER_ASCEND ? "" : "DESC");
		}
		return "";
	}

	public static int get_id(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(ID));
	}

	public static String getIDRRef(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(IDRREF)));
	}

	public static String getArtikul(Cursor cursor) {
		//System.out.println("ARTIKUL "+cursor.getColumnIndex(ARTIKUL)+": "+cursor.getString(cursor.getColumnIndex(ARTIKUL)));
		//return cursor.getString(cursor.getColumnIndex(ARTIKUL));
		String artikul = "?";
		try {
			artikul = cursor.getString(cursor.getColumnIndex(ARTIKUL));

		} catch (Throwable t) {
			t.printStackTrace();
		}
		return artikul;
	}

	public static String getNaimenovanie(Cursor cursor) {
		//System.out.println("NAIMENOVANIE "+cursor.getColumnIndex(NAIMENOVANIE)+": "+cursor.getString(cursor.getColumnIndex(NAIMENOVANIE)));
		return cursor.getString(cursor.getColumnIndex(NAIMENOVANIE));
	}

	public static String getProizvoditelID(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(OSNOVNOY_PROIZVODITEL)));
	}

	public static String getProizvoditelNaimenovanie(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(PROIZVODITEL_NAIMENOVANIE));
	}

	public static String getCena(Cursor cursor) {
		//System.out.println("CENA "+cursor.getColumnIndex(CENA)+": "+cursor.getString(cursor.getColumnIndex(CENA)));
		/*double cena = 0.0;
		try {
			cena = cursor.getDouble(cursor.getColumnIndex(CENA));
		}
		catch (Throwable t) {
			System.out.println("Request_NomenclatureBase getCena: " + t.getMessage());
		}*/
		return DecimalFormatHelper.format(cursor.getDouble(cursor.getColumnIndex(CENA)));
		//return DecimalFormatHelper.format(cena);
	}

	public static String calculateMinCena(double cursorMinCena) {
		String r = "0.0";
		try {
			//double price = cursor.getDouble(cursor.getColumnIndex(MIN_CENA));
			double price = cursorMinCena;
			r = DecimalFormatHelper.format(price);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return r;
	}

	public static String getMinCena(Cursor cursor) {
		String r = "0.0";
		try {
			//double price = cursor.getDouble(cursor.getColumnIndex(MIN_CENA));
			//r = DecimalFormatHelper.format(price);
			r = calculateMinCena(cursor.getDouble(cursor.getColumnIndex(MIN_CENA)));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return r;
	}

	public static String getMaxCena(Cursor cursor) {
		Double price = (Double) cursor.getDouble(cursor.getColumnIndex(MAX_CENA));
		if (price == null || price == 0) {
			return getCena(cursor);
		}
		return DecimalFormatHelper.format(price);
	}

	public static String getEdinicyIzmereniyaNaimenovanie(Cursor cursor) {
		String r = "?";
		try {
			r = cursor.getString(cursor.getColumnIndex(EDINICY_IZMERENIYA_NAIMENOVANIE));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return r;
	}

	public static String getKoephphicient(Cursor cursor) {
		//return ((Double) cursor.getDouble(cursor.getColumnIndex(KOEPHICIENT))).toString();
		String r = "?";
		try {
			r = ((Double) cursor.getDouble(cursor.getColumnIndex(KOEPHICIENT))).toString();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return r;
	}

	public static String getMinNorma(Cursor cursor) {
		String r = "1";
		try {
			r = ((Double) cursor.getDouble(cursor.getColumnIndex(MIN_NORMA))).toString();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return r;
	}

	public static String getEdinicyIzmereniyaID(Cursor cursor) {
		String r = "x'00'";
		try {
			int idx = cursor.getColumnIndex(EDINICY_IZMERENIYA_ID);
			//System.out.println(idx);
			byte[] blb = cursor.getBlob(idx);
			/*if(blb==null){
				return "x'00'";
			}*/
			//System.out.println(blb);
			r = Hex.encodeHex(blb);
			//System.out.println(r);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return r;
	}

	public static String getSkidka(Cursor cursor) {
		try {
			String s = cursor.getString(cursor.getColumnIndex("Skidka"));
			if (("" + s).trim().length() > 0) {
				return s;
			}
		} catch (Throwable t) {
			//System.out.println("Request_NomenclatureBase getSkidka: " + t.getMessage());
		}
		return "" + 0.0;
	}

	public static String getSkidka_old(Cursor cursor) {
		//System.out.println("getSkidka" );
		/*Double sale = (Double) cursor.getDouble(cursor.getColumnIndex(SKIDKA));
		if (sale == null || sale == 0) {
			return null;
		}
		return sale.toString();*/
		//double nacenka = cursor.getDouble(cursor.getColumnIndex("Nacenka"));
		//return "-";
		try {
			String ZapretSkidokTovNokopitelnye = cursor.getString(cursor.getColumnIndex("ZapretSkidokTovNokopitelnye"));
			String ZapretSkidokProizvNacenki = cursor.getString(cursor.getColumnIndex("ZapretSkidokProizvNacenki"));
			double FiksirovannyeCeny = cursor.getDouble(cursor.getColumnIndex("FiksirovannyeCeny"));
			if (FiksirovannyeCeny > 0) {
				return "" + 0.0;
			}
			double SkidkaPartneraKarta = cursor.getDouble(cursor.getColumnIndex("SkidkaPartneraKarta"));
			if (SkidkaPartneraKarta > 0) {
				return "" + 0.0;
			}
			double NakopitelnyeSkidki = cursor.getDouble(cursor.getColumnIndex("NakopitelnyeSkidki"));
			if (!ZapretSkidokTovNokopitelnye.equals("true")) {
				if (NakopitelnyeSkidki > 0) {
					return "" + NakopitelnyeSkidki;
				}
			}
		} catch (Throwable t) {
			//System.out.println("Request_NomenclatureBase getSkidka: " + t.getMessage());
		}
		return "" + 0.0;
	}

	public static double calculateCenaSoSkidkoy(
			double cursorCena
			, double cursorSkidka
			, String cursorVidSkidki
			, double cursorMinCena
			, double cursorNacenka
	) {
		double cena = cursorCena;
		double skidka = cursorSkidka;
		if (skidka > 0) {
			cena = skidka;
		}
		//String vidSkidki = Request_NomenclatureBase.getVidSkidki(cursor);
		String vidSkidki = Request_NomenclatureBase.calculateVidSkidki(cursorNacenka, cursorVidSkidki);
		//double mincen = Double.parseDouble(getMinCena(cursor));
		double mincen = Double.parseDouble(calculateMinCena(cursorMinCena));
		//System.out.println("getCenaSoSkidkoy: " + vidSkidki + ", " + cena + ", " + mincen);
		if (vidSkidki.trim().toUpperCase().equals(Sales.CR_NAME.trim().toUpperCase())) {
			if (cena < mincen) {
				//cena = cursor.getDouble(cursor.getColumnIndex(CENA));
				cena = cursorCena;
				//System.out.println("change getCenaSoSkidkoy: " + vidSkidki + ", " + cena + ", " + mincen);
			}
		}
		return cena;
	}

	public static String getCenaSoSkidkoy(Cursor cursor) {
		/*
		double cena = cursor.getDouble(cursor.getColumnIndex(CENA));
		double skidka = cursor.getDouble(cursor.getColumnIndex(SKIDKA));
		if (skidka > 0) {
			cena = skidka;
		}
		String vidSkidki = Request_NomenclatureBase.getVidSkidki(cursor);
		double mincen = Double.parseDouble(getMinCena(cursor));
		System.out.println("getCenaSoSkidkoy: "+vidSkidki+", "+cena+", "+mincen);
		if (vidSkidki.trim().toUpperCase().equals(Sales.CR_NAME.trim().toUpperCase())) {
			if (cena < mincen) {
				cena = cursor.getDouble(cursor.getColumnIndex(CENA));
				System.out.println("change getCenaSoSkidkoy: "+vidSkidki+", "+cena+", "+mincen);
			}
		}
		return DecimalFormatHelper.format(cena);
		*/
		return DecimalFormatHelper.format(calculateCenaSoSkidkoy(
				cursor.getDouble(cursor.getColumnIndex(CENA))
				, cursor.getDouble(cursor.getColumnIndex(SKIDKA))
				, Request_NomenclatureBase.calculateVidSkidki(cursor.getDouble(cursor.getColumnIndex("Nacenka"))
						, cursor.getString(cursor.getColumnIndex("VidSkidki")))
				, Double.parseDouble(calculateMinCena(cursor.getDouble(cursor.getColumnIndex(MIN_CENA))))
				, cursor.getDouble(cursor.getColumnIndex("Nacenka"))
		));
	}

	public static String getCenaSoSkidkoy_old(Cursor cursor) {
		/*Double priceWithSale = (Double) cursor.getDouble(cursor.getColumnIndex(CENA_SO_SKIDKOY));
		if (priceWithSale == null) {
			return null;
		}
		return DecimalFormatHelper.format(priceWithSale);*/
		double cena = cursor.getDouble(cursor.getColumnIndex(CENA));
		String ZapretSkidokTovNokopitelnye = cursor.getString(cursor.getColumnIndex("ZapretSkidokTovNokopitelnye"));
		String ZapretSkidokProizvNacenki = cursor.getString(cursor.getColumnIndex("ZapretSkidokProizvNacenki"));
		try {
			double Nacenka = cursor.getDouble(cursor.getColumnIndex("Nacenka"));
			//if (Nacenka == 0) {
			//	Nacenka = 1.0;
			//}
			cena = cena * (100 + Nacenka) / 100;
			double FiksirovannyeCeny = cursor.getDouble(cursor.getColumnIndex("FiksirovannyeCeny"));
			if (FiksirovannyeCeny > 0) {
				return DecimalFormatHelper.format(FiksirovannyeCeny);
			}
			double SkidkaPartneraKarta = cursor.getDouble(cursor.getColumnIndex("SkidkaPartneraKarta"));
			if (SkidkaPartneraKarta > 0) {
				return DecimalFormatHelper.format(SkidkaPartneraKarta);
			}
			double NakopitelnyeSkidki = cursor.getDouble(cursor.getColumnIndex("NakopitelnyeSkidki"));
			if (!ZapretSkidokTovNokopitelnye.equals("true")) {
				if (NakopitelnyeSkidki > 0) {
					//cena = cena * Nacenka;
					cena = cena * ((100.0 - NakopitelnyeSkidki) / 100);
					return DecimalFormatHelper.format(cena);
				}
			}
		} catch (Throwable t) {
			//System.out.println("Request_NomenclatureBase getCenaSoSkidkoy: " + t.getMessage());
		}
		return DecimalFormatHelper.format(cena);
	}

	public static String getCenaCR(Cursor cursor) {
		Double priceWithSale = (Double) cursor.getDouble(cursor.getColumnIndex(CENA_SO_SKIDKOY));
		if (priceWithSale == null || priceWithSale == 0) {
			return DecimalFormatHelper.format(cursor.getDouble(cursor.getColumnIndex(CENA)));
		}
		return DecimalFormatHelper.format(priceWithSale);
	}

	public static String calculateVidSkidki(double cursorNacenka, String cursorVidSkidki) {
		String nacenka = calculateWithNacenka(cursorNacenka);
		String skidka = calculateWithSkidki(cursorVidSkidki);
		return skidka + " " + nacenka;
	}

	public static String getVidSkidki(Cursor cursor) {
		//System.out.println("getVidSkidki");
		/*String nacenka = withNacenka(cursor);
		String skidka = withSkidki(cursor);
		return skidka + " " + nacenka;*/
		return calculateVidSkidki(cursor.getDouble(cursor.getColumnIndex("Nacenka")), cursor.getString(cursor.getColumnIndex("VidSkidki")));
	}

	public static boolean isOlderThen2week(Cursor cursor) {
		try {
			String LastSell = cursor.getString(cursor.getColumnIndex("LastSell"));
			if (LastSell == null) {
				return false;
			}
			if (LastSell.length() < 1) {
				return false;
			}
			java.util.Date d = DateTimeHelper.SQLDateToDate(LastSell);
			java.util.Calendar now = Calendar.getInstance();
			now.roll(Calendar.DAY_OF_YEAR, -14);
			//String LastSell = cursor.getString(cursor.getColumnIndex("LastSell"));
			if (d.before(now.getTime())) {
				return true;
				//0xffff6666;
			} else {
				return false;
				//0xffe3e3e3;
			}
			//String LastSell = cursor.getString(cursor.getColumnIndex("LastSell"));
			//return LastSell;
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean nacenkaBolshe25(Cursor cursor) {
		try {
			double CenyNomenklaturySklada = cursor.getDouble(cursor.getColumnIndex("Cena"));
			double TekuschieCenyOstatkovPartiy = cursor.getDouble(cursor.getColumnIndex("BasePrice"));
			int procent = (int) (100.0 * (CenyNomenklaturySklada - TekuschieCenyOstatkovPartiy) / TekuschieCenyOstatkovPartiy);
			//System.out.println("100.0 * (" + CenyNomenklaturySklada + " - " + TekuschieCenyOstatkovPartiy + ") / " + TekuschieCenyOstatkovPartiy + ")=" + procent);
			if (procent >= 25) {
				return true;
			} else {
				return false;
			}
		} catch (Throwable t) {
			return false;
		}
	}

	public static String calculateWithNacenka(double nacenka) {
		if (nacenka > 0) {
			return "+наценка " + nacenka + "%";
		} else {
			return "";
		}
	}

	public static String withNacenka(Cursor cursor) {
		/*double nacenka = cursor.getDouble(cursor.getColumnIndex("Nacenka"));
		if (nacenka > 0) {
			return "+наценка " + nacenka + "%";
		} else {
			return "";
		}*/
		return calculateWithNacenka(cursor.getDouble(cursor.getColumnIndex("Nacenka")));
	}

	public static String calculateWithSkidki(String cursorVidSkidki) {
		String textSkidka = "";
		try {
			textSkidka = "" + cursorVidSkidki;
		} catch (Throwable t) {
			textSkidka = "?";
		}
		return textSkidka;
	}

	public static String withSkidki(Cursor cursor) {
		/*String textSkidka = "";
		try {
			textSkidka = "" + cursor.getString(cursor.getColumnIndex("VidSkidki"));
		} catch (Throwable t) {
			textSkidka = "?";
		}
		return textSkidka;*/
		return calculateWithSkidki(cursor.getString(cursor.getColumnIndex("VidSkidki")));
	}

	public static String withSkidki_old(Cursor cursor) {
		//if (cursor.isNull(cursor.getColumnIndex(VID_SKIDKI))) {
		//	return "";
		//}
		//return Sales.GetSaleName(Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(VID_SKIDKI))));
		try {
			String ZapretSkidokTovNokopitelnye = cursor.getString(cursor.getColumnIndex("ZapretSkidokTovNokopitelnye"));
			String ZapretSkidokProizvNacenki = cursor.getString(cursor.getColumnIndex("ZapretSkidokProizvNacenki"));
			double FiksirovannyeCeny = cursor.getDouble(cursor.getColumnIndex("FiksirovannyeCeny"));
			//System.out.println("FiksirovannyeCeny" + FiksirovannyeCeny + "/" + cursor.getString(cursor.getColumnIndex(ARTIKUL)));
			if (FiksirovannyeCeny > 0) {
				return "Фикс.цена";
			}
			double SkidkaPartneraKarta = cursor.getDouble(cursor.getColumnIndex("SkidkaPartneraKarta"));
			if (SkidkaPartneraKarta > 0) {
				return "Партнёр";
			}
			if (!ZapretSkidokTovNokopitelnye.equals("true")) {
				double NakopitelnyeSkidki = cursor.getDouble(cursor.getColumnIndex("NakopitelnyeSkidki"));
				if (NakopitelnyeSkidki > 0) {
					return "Накоп.";
				}
			}
		} catch (Throwable t) {
			//System.out.println("Request_NomenclatureBase withSkidki: " + t.getMessage());
		}
		return "";
	}

	public static String _getVidSkidkiID(Cursor cursor) {
		if (cursor.isNull(cursor.getColumnIndex(VID_SKIDKI))) {
			return "x'00'";
		}
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(VID_SKIDKI)));
		/*double FiksirovannyeCeny = cursor.getDouble(cursor.getColumnIndex("FiksirovannyeCeny"));
		if (FiksirovannyeCeny > 0) {
			return "Фикс.цена";
		}
		double SkidkaPartneraKarta = cursor.getDouble(cursor.getColumnIndex("SkidkaPartneraKarta"));
		if (SkidkaPartneraKarta > 0) {
			return "Партнёр";
		}
		double NakopitelnyeSkidki = cursor.getDouble(cursor.getColumnIndex("NakopitelnyeSkidki"));
		if (NakopitelnyeSkidki > 0) {
			return "Накоп.";
		}
		return "";*/
	}

	public static double getBasePrice(Cursor cursor) {
		return cursor.getDouble(cursor.getColumnIndex(BASE_PRICE));
	}

	public static double getLastPrice(Cursor cursor) {
		return cursor.getDouble(cursor.getColumnIndex(LAST_PRICE));
	}
}
