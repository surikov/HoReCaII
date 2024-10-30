package sweetlife.android10.database;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.Hex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_FoodStuffList implements ITableColumnsNames {
	public static Cursor Request(SQLiteDatabase db, String bidID) {
		String defaultSklad = "";
		String skladPodrazdelenia = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
		boolean letuchka = false;//dataOtgruzki.equals(DateTimeHelper.SQLDateString(new Date()));
		if (skladPodrazdelenia.trim().toUpperCase().equals(ISklady.HORECA_ID.trim().toUpperCase())) {
			defaultSklad = " and baza=" + ISklady.HORECA_ID;
		}
		else {
			if (skladPodrazdelenia.trim().toUpperCase().equals(ISklady.KAZAN_ID.trim().toUpperCase())) {
				if (letuchka) {
					defaultSklad = " and baza=" + ISklady.KAZAN_ID;
				}
			}
		}
		String sqlString = "select zpt._id, zpt.[NomerStroki], zpt.[EdinicaIzmereniya], zpt.[Kolichestvo]"// 3
				+ "\n		,zpt.[Nomenklatura], zpt.[Summa], zpt.[Cena], zpt.[CenaSoSkidkoy],zpt.[MinimalnayaCena] "//8 
				+ "\n		,zpt.[MaksimalnayaCena], n.[Artikul]"//10
				+ "\n		, n.[Naimenovanie] "// 11
				+ "\n				 || ' (склад '"//
				+ "\n				 || case (select sklad from AdresaPoSkladam_last where nomenklatura=n._idrref " + defaultSklad + " order by sklad  limit 1)"//
				+ "\n			 when " + ISklady.HORECA_sklad_8 + " then 8"//
				+ "\n			 when " + ISklady.HORECA_sklad_10 + " then 10"//
				+ "\n			 when " + ISklady.HORECA_sklad_12 + " then 12"//
				+ "\n			 when " + ISklady.KAZAN_sklad_14 + " then 14"//
				+ "\n				 else '?' end" //
				+ "\n			 || ')' || case ifnull(ko._id,'') when '' then '' else '`' end"//
				
				+ "\n		 as [NomenklaturaNaimenovanie]"//
				//+ "\n		,ifnull(vkn.[Kolichestvo], 1) [MinNorma], eho.[Naimenovanie] [EdinicaIzmereniyaNaimenovanie] "// 
				+ "\n		,ifnull(n.kvant, 1) [MinNorma], n.skladEdIzm [EdinicaIzmereniyaNaimenovanie] "//
				//+ "\n		,zpt.[Skidka], zpt.[VidSkidki], ei.[Koephphicient], zpt.[Sebestoimost]"// 
				+ "\n		,zpt.[Skidka], zpt.[VidSkidki], n.otchEdKoef as Koephphicient, zpt.[Sebestoimost]"//
				//+ "\n		,ifnull(p.[Naimenovanie], '') [ProizvoditelNaimenovanie], ifnull(pro.[LastPrice], 0) [LastPrice]  "
				//+ "\n		,ifnull(p.[Naimenovanie], '') [ProizvoditelNaimenovanie], ifnull(pro.Stoimost/pro.kolichestvo, 0) [LastPrice]  "
				+ "\n		,ifnull(p.[Naimenovanie], '') [ProizvoditelNaimenovanie]"




				//+", ifnull(pro.Stoimost/pro.kolichestvo, 0) [LastPrice]  "
				+", ifnull(zpt._KeyField, 0) [LastPrice]  "

//+", 123.45 [LastPrice]  "


								   /*lastprice new calc
				+ "	,(select max(0.0+Stoimost/kolichestvo) from Prodazhi 					\n" //
				+ "			join DogovoryKontragentov on Prodazhi .DogovorKontragenta = DogovoryKontragentov._IDRref			\n" //
				+ "			where Prodazhi.nomenklatura=n.[_IDRRef] and period=(			\n" //
				+ "				select max(period) from Prodazhi 		\n" //
				+ "					join DogovoryKontragentov on Prodazhi .DogovorKontragenta = DogovoryKontragentov._IDRref	\n" //
				+ "					where Prodazhi.nomenklatura=n.[_IDRRef] and DogovoryKontragentov .vladelec=parameters.kontragent	\n" //
				+ "				)		\n" //
				+ "			) 			\n" //
				+ "		as [LastPrice]  "
*/


				+", ifnull(zpt.nacenka, 0) [nacenka]  "

				//Prodazhi.Stoimost/Prodazhi.kolichestvo
				//+ "aux.byHand as byHand "// 
				+ "\n	from [ZayavkaPokupatelyaIskhodyaschaya_Tovary] zpt "// 
				//+ "inner join [Nomenklatura] n on zpt.[Nomenklatura] = n._IDRRef "//
				+ "\n	inner join [Nomenklatura] n on zpt.[Nomenklatura] = n._IDRRef "// 
				//+ "\n	inner join EdinicyIzmereniya_strip eho on n.EdinicaKhraneniyaOstatkov = eho._IDRRef  "//
				//+ "\n	inner join EdinicyIzmereniya eho on n.EdinicaKhraneniyaOstatkov = eho._IDRRef  "//
				//+ "\n	left join VelichinaKvantovNomenklatury vkn on vkn.Nomenklatura = n.[_IDRRef]" //
				//+ "\n	inner join EdinicyIzmereniya ei on n.[EdinicaDlyaOtchetov] = ei._IDRRef "// 
				//+ "\n	inner join EdinicyIzmereniya_strip ei on n.[EdinicaDlyaOtchetov] = ei._IDRRef "//
				+ "\n	left outer join Proizvoditel p on n.[OsnovnoyProizvoditel] = p._IDRRef " //
				//+ "\n	left join CurProdazhi pro on pro.Nomenklatura = n._IDRRef "//


				+ "\n	left join Prodazhi_last pro on pro.Nomenklatura = n._IDRRef "//


				//EdinicyIzmereniya_strip
				+ "\n	left join KategoryObjectov ko on ko.Nomenklatura=n._idrref and ko.kategorya =x'A69818A90562E07011E4A5333644C864' "//
				+ "\n	where zpt.[_ZayavkaPokupatelyaIskhodyaschaya_IDRRef] = " + bidID//
				+ "\n	group by zpt._id, zpt.Nomenklatura"//
				+ "\n	order by n.Naimenovanie"//
		;
		//System.out.println("Request_FoodStuffList.Request " + sqlString);
		Cursor cursorFoodStuffs = db.rawQuery(sqlString, null);
		if (cursorFoodStuffs.moveToFirst()) {

			return cursorFoodStuffs;
		}
		cursorFoodStuffs.close();
		return null;
	}
	public static int get_id(Cursor cursor) {
		return cursor.getInt(0);
	}
	public static int getNomerStroki(Cursor cursor) {
		return cursor.getInt(1);
	}
	public static String getEdinicaIzmereniyaID(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(2));
	}
	public static double getKolichestvo(Cursor cursor) {
		return cursor.getDouble(3);
	}
	public static String getNomenklatura(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(4));
	}
	public static double getSumma(Cursor cursor) {
		return cursor.getDouble(5);
	}
	public static double getCena(Cursor cursor) {
		return cursor.getDouble(6);
	}
	public static double getCenaSoSkidkoy(Cursor cursor) {
		return cursor.getDouble(7);
	}
	public static double getMinimalnayaCena(Cursor cursor) {
		return cursor.getDouble(8);
	}
	public static double getMaksimalnayaCena(Cursor cursor) {
		return cursor.getDouble(9);
	}
	public static String getArtikul(Cursor cursor) {
		return cursor.getString(10);
	}
	public static String getNomenklaturaNaimenovanie(Cursor cursor, SQLiteDatabase db) {
		//System.out.println("getNomenklaturaNaimenovanie for " + getArtikul(cursor));
		String s = cursor.getString(11);
		return s;
	}
	public static String getNomenklaturaNaimenovanie__(Cursor cursor, SQLiteDatabase db) {
		//System.out.println("getNomenklaturaNaimenovanie for " + getArtikul(cursor));
		String s = cursor.getString(11) + " (склад ?)";
		try {
			/*
			String sqlString = Request_NomenclatureBase.composeSQL(//
					DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
					, ApplicationHoreca.getInstance().getClientInfo().getID()//
					, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
					, ""//
					, ""//
					, getArtikul(cursor)//
					, ISearchBy.SEARCH_ARTICLE//
					, false//
					, false//
					, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya()//
					, 200, 0);
			s = Auxiliary.fromCursor(db.rawQuery(sqlString, null)).child("row").child("Naimenovanie").value.property.value();
			*/
			String defaultSklad = "";
			String skladPodrazdelenia = ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya();
			boolean letuchka = false;//dataOtgruzki.equals(DateTimeHelper.SQLDateString(new Date()));
			if (skladPodrazdelenia.trim().toUpperCase().equals(ISklady.HORECA_ID.trim().toUpperCase())) {
				defaultSklad = " and baza=" + ISklady.HORECA_ID;
			}
			else {
				if (skladPodrazdelenia.trim().toUpperCase().equals(ISklady.KAZAN_ID.trim().toUpperCase())) {
					if (letuchka) {
						defaultSklad = " and baza=" + ISklady.KAZAN_ID;
					}
				}
			}
			String sql = " select n.[Naimenovanie] "//
					+ " || ' [склад '"//
					+ " || case (select sklad from AdresaPoSkladam_last where nomenklatura=n._idrref " + defaultSklad + " order by sklad  limit 1)"//
					+ " when " + ISklady.HORECA_sklad_8 + " then 8"//
					+ " when " + ISklady.HORECA_sklad_10 + " then 10"//
					+ " when " + ISklady.HORECA_sklad_12 + " then 12"//
					+ " when " + ISklady.KAZAN_sklad_14 + " then 14"//
					+ " else '?' end" //
					+ " || ']'"//
					+ " as Naimenovanie"//
					+ " from nomenklatura n"//
			;
			Cursor c = db.rawQuery(sql, null);
			if (c.moveToNext()) {
				s = c.getString(0);
			}
			c.close();
			//s = "test (склад 8)";
			//return cursor.getString(11);\return s;
		}
		catch (Throwable t) {
			t.printStackTrace();
			LogHelper.debug("Request_FoodStuffList.getNomenklaturaNaimenovanie " + t.getMessage());
		}
		//System.out.println("getNomenklaturaNaimenovanie is " + s);
		return s;
	}
	public static double getMinKolichestvo(Cursor cursor) {
		return cursor.getDouble(12);
	}
	public static String getEdinicaIzmereniya(Cursor cursor) {
		return cursor.getString(13);
	}
	public static double getSkidka(Cursor cursor) {
		return cursor.getDouble(14);
	}
	public static String getVidSkidki(Cursor cursor) {
		//return Request_NomenclatureBase.getVidSkidki(cursor);
		try {
			return Hex.encodeHex(cursor.getBlob(15));
		}
		catch (Throwable t) {
			return "";
		}
		//return "";
	}
	public static double getKoefficientMest(Cursor cursor) {
		return cursor.getDouble(16);
	}
	public static double getSebestoimost(Cursor cursor) {
		return cursor.getDouble(17);
	}
	public static String getProizvoditelID(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(18));
	}
	public static double getLastPrice(Cursor cursor) {
		return cursor.getDouble(cursor.getColumnIndex(LAST_PRICE));
	}
	/*public static boolean getByHand(Cursor cursor) {
		if (cursor.getDouble(cursor.getColumnIndex("byHand")) > 0) {
			return true;
		}
		else {
			return false;
		}
		//return false;
	}*/
}
