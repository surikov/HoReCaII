package sweetlife.android10.database.nomenclature;

import java.util.Date;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.database.ISklady;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_Trafiks implements ISearchBy, ITableColumnsNames {
	public static Cursor RequestNomenlatureGroupsWithoutParent(SQLiteDatabase db) {
		//System.out.println("RequestNomenlatureGroupsWithoutParent.Request");
		return RequestNomenlatureGroupsWithParent(db, "x'00'");
	}
	public static Cursor RequestNomenlatureGroupsWithParent(SQLiteDatabase db, String parent) {
		//
		String sqlString = "select _id, _IDRRef, Naimenovanie from Nomenklatura "//
				+ "where EtoGruppa=x'01' and Roditel = " + parent// 
				+ " order by Naimenovanie "//
		;
		//System.out.println("Request_Trafiks.RequestNomenlatureGroupsWithParent.Request " + sqlString);
		Cursor c = db.rawQuery(sqlString, null);
		return c;
	}
	public static String uslovieSkladaPodrazdeleniaDlyaTraphik(Date dataOtgruzki, String skladPodrazdelenia) {
		boolean letuchka = DateTimeHelper.SQLDateString(dataOtgruzki).equals(DateTimeHelper.SQLDateString(new Date()));
		skladPodrazdelenia = skladPodrazdelenia.toUpperCase();
		//String traphikCondition = " and Traphik=x'01'";
		/*if (!traphik) {
			traphikCondition = " and Traphik=x'00'";
		}*/
		//skladPodrazdelenia = ISklady.HORECA_ID.toUpperCase();
		if (skladPodrazdelenia.equals(ISklady.HORECA_ID.toUpperCase())) {
			return "\n	on (select sklad from AdresaPoSkladam horeca where horeca.nomenklatura=n._idrref and horeca.period"//
					+ "\n		=(select max(period) from adresaposkladam where nomenklatura=n._idrref"//
					+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'01') "//
					+ "\n		)=" + ISklady.HORECA_sklad_8//
					+ "\n	or (select sklad from AdresaPoSkladam horeca where horeca.nomenklatura=n._idrref and horeca.period"//
					+ "\n		=(select max(period) from adresaposkladam where nomenklatura=n._idrref"//
					+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'01') "//
					+ "\n		)=" + ISklady.HORECA_sklad_10//
					+ "\n	or (select sklad from AdresaPoSkladam horeca where horeca.nomenklatura=n._idrref and horeca.period"//
					+ "\n		=(select max(period) from adresaposkladam where nomenklatura=n._idrref"//
					+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'01') "//
					+ "\n		)=" + ISklady.HORECA_sklad_12//
					+ "\n";
		}
		else {
			if (skladPodrazdelenia.equals(ISklady.KAZAN_ID.toUpperCase())) {
				if (letuchka) {
					return "\n	on (select sklad from AdresaPoSkladam kazan where kazan.nomenklatura=n._idrref and kazan.period"//
							+ "\n		=(select max(period) from adresaposkladam where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.KAZAN_ID + " and Traphik=x'01') "//
							+ "\n		)=" + ISklady.KAZAN_sklad_14//
							+ "\n";
				}
				else {
					return "\n	on ((select sklad from AdresaPoSkladam horeca where horeca.nomenklatura=n._idrref and horeca.period"//
							+ "\n		=(select max(period) from adresaposkladam where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'01') "//
							+ "\n		)=" + ISklady.HORECA_sklad_8//
							+ "\n	or (select sklad from AdresaPoSkladam horeca where horeca.nomenklatura=n._idrref and horeca.period"//
							+ "\n		=(select max(period) from adresaposkladam where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'01') "//
							+ "\n		)=" + ISklady.HORECA_sklad_10//
							+ "\n	or (select sklad from AdresaPoSkladam horeca where horeca.nomenklatura=n._idrref and horeca.period"//
							+ "\n		=(select max(period) from adresaposkladam where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.HORECA_ID + " and Traphik=x'01') "//
							+ "\n		)=" + ISklady.HORECA_sklad_12//
							+ "\n	) and ( ifnull((select sklad from AdresaPoSkladam kazan where kazan.nomenklatura=n._idrref and kazan.period"//
							+ "\n		=(select max(period) from adresaposkladam where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.KAZAN_ID + ") "//
							+ "\n		)," + ISklady.EMPTY_sklad + ")=" + ISklady.EMPTY_sklad//
							+ "\n	or ifnull((select sklad from AdresaPoSkladam kazan where kazan.nomenklatura=n._idrref and kazan.period"//
							+ "\n		=(select max(period) from adresaposkladam where nomenklatura=n._idrref"//
							+ "\n			and baza=" + ISklady.KAZAN_ID + " and Traphik=x'01') "//
							+ "\n		),'')=" + ISklady.KAZAN_sklad_14//
							+ "\n )";
				}
			}
			else {
				return "";
			}
		}
	}
	public static Cursor RequestNomenclatureByParent(SQLiteDatabase db, String parent,boolean degustacia) {
		/*
		String sqlString = "select n._id, n.[_IDRRef], n.[Artikul], n.[Naimenovanie], " //
				+ "n.[OsnovnoyProizvoditel] , n.[ProizvoditelNaimenovanie],"//
				+ "n.[EdinicyIzmereniyaNaimenovanie], n.[MinNorma], "// 
				+ "n.[EdinicyIzmereniyaID], n.Roditel [Roditel], "// 
				+ "n.[Koephphicient], n.[UpperName] "//
				+ "from CurTrafiks n " //
				+ "where Roditel = " + parent// 
				+ " order by Naimenovanie ";
		*/
		String clientID = "0";
		try {
			clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
		}
		catch (Throwable t) {
			LogHelper.debug("Request_Trafiks.RequestNomenclatureByParent: " + t.getMessage());
		}
		String data = DateTimeHelper.SQLDateString(new java.util.Date());
		String sqlString = "	select					\n" //
				+ "	n._id					\n" //
				+ "	,n.[_IDRRef]					\n" //
				+ "	,n.[Artikul]					\n" //
				+ "	,n.[Naimenovanie]					\n" //
				+ "	,n.[OsnovnoyProizvoditel]					\n" //
				+ " ,ifnull((select Naimenovanie from Proizvoditel \n"
				+ " 		where n.[OsnovnoyProizvoditel] = Proizvoditel ._IDRRef limit 1 \n"
				+ " 		),1) as [ProizvoditelNaimenovanie] \n"
				+ "	,eho.[Naimenovanie] as [EdinicyIzmereniyaNaimenovanie]					\n" //
				+ " ,ifnull((select max(VelichinaKvantovNomenklatury.Kolichestvo) from VelichinaKvantovNomenklatury \n"
				+ "	 		where VelichinaKvantovNomenklatury.Nomenklatura = n.[_IDRRef] \n"
				+ "			),1) as [MinNorma] \n"
				+ "	,ei._IDRRef as [EdinicyIzmereniyaID]					\n" //
				+ ", n.Roditel [Roditel] \n"
				+ "	,ei.Koephphicient as [Koephphicient]					\n" //
				+ " ,n.[UpperName] \n"//
				+ "	,(select max(Cena) from CenyNomenklaturySklada 					\n" //
				+ "			  where CenyNomenklaturySklada.nomenklatura=n.[_IDRRef] 			\n" //
				+ "			  and Period=(select max(Period) from CenyNomenklaturySklada 			\n" //
				+ "				where nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))		\n" //
				+ "		as [Cena]	"//
				+ "	,0 as Skidka					\n" //
				+ "	,0 as CenaSoSkidkoy					\n" //
				+ "	,x'00' as VidSkidki					\n" //
				+ "	,n.Roditel as Roditel					\n" //
				+ "\n ,(select (1.0+(select ifnull(nacenka1,ifnull(nacenka2,ifnull(nacenka3,ifnull(nacenka4,nacenka5))))"
				+ "\n 	from (select (select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p1._idrref and NomenklaturaProizvoditel_2=n._idrref"
				+ "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p1._idrref and NomenklaturaProizvoditel_2=n._idrref)"
				+ "\n ) as nacenka1"
				+ "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p2._idrref and NomenklaturaProizvoditel_2=n._idrref"
				+ "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p2._idrref and NomenklaturaProizvoditel_2=n._idrref)"
				+ "\n ) as nacenka2"
				+ "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p3._idrref and NomenklaturaProizvoditel_2=n._idrref"
				+ "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p3._idrref and NomenklaturaProizvoditel_2=n._idrref)"
				+ "\n ) as nacenka3"
				+ "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p4._idrref and NomenklaturaProizvoditel_2=n._idrref"
				+ "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p3._idrref and NomenklaturaProizvoditel_2=n._idrref)"
				+ "\n ) as nacenka4"
				+ "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=X'00000000000000000000000000000000' and NomenklaturaProizvoditel_2=n._idrref"
				+ "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=X'00000000000000000000000000000000' and NomenklaturaProizvoditel_2=n._idrref)"
				+ "\n ) as nacenka5"
				+ "\n 		from Podrazdeleniya p1"
				+ "\n 		left join Podrazdeleniya p2 on p1.roditel=p2._idrref"
				+ "\n 		left join Podrazdeleniya p3 on p2.roditel=p3._idrref"
				+ "\n 		left join Podrazdeleniya p4 on p3.roditel=p4._idrref"
				+ "\n 		join Polzovateli on p1._idrref=Polzovateli.podrazdelenie and Polzovateli._idrref= parameters.polzovatel"
				+ "\n 		)"
				+ "\n 	)/100.0)*max(TekuschieCenyOstatkovPartiy.Cena) from TekuschieCenyOstatkovPartiy"
				+ "\n  			where TekuschieCenyOstatkovPartiy.nomenklatura=n.[_IDRRef]"
				+ "\n ) as [MinCena]\n"
				/*
				+ "	,(select (1.0+nacenka/100.0)*max(TekuschieCenyOstatkovPartiy.Cena) from MinimalnyeNacenkiProizvoditeley_1					\n" //
				+ "				,TekuschieCenyOstatkovPartiy		\n" //
				+ "			where NomenklaturaProizvoditel_2=n.[_IDRRef]			\n" //
				+ "			and TekuschieCenyOstatkovPartiy.nomenklatura=n.[_IDRRef]			\n" //
				+ "			and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 			\n" //
				+ "				where NomenklaturaProizvoditel_2=n.[_IDRRef]))		\n" //
				+ "		as [MinCena]				\n" //
				*/
				+ "	,(select (1.0+c.[MaksNacenkaCenyPraysa]/100.0)*max(Cena) from CenyNomenklaturySklada 					\n" //
				+ "			where CenyNomenklaturySklada.nomenklatura=n.[_IDRRef] 			\n" //
				+ "			and Period=(select max(Period) from CenyNomenklaturySklada			\n" //
				+ "				where nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))		\n" //
				+ "		as [MaxCena]				\n" //
				+ "	,(select max(Cena) from TekuschieCenyOstatkovPartiy 					\n" //
				+ "			  where TekuschieCenyOstatkovPartiy.nomenklatura=n.[_IDRRef]) 			\n" //
				+ "		as [BasePrice]				\n" //
				+ "	,(select max(0.0+Stoimost/kolichestvo) from Prodazhi 					\n" //
				+ "			join DogovoryKontragentov_strip on Prodazhi .DogovorKontragenta = DogovoryKontragentov_strip._IDRref			\n" //
				+ "			where Prodazhi.nomenklatura=n.[_IDRRef] and period=(			\n" //
				+ "				select max(period) from Prodazhi 		\n" //
				+ "					join DogovoryKontragentov_strip on Prodazhi .DogovorKontragenta = DogovoryKontragentov_strip._IDRref	\n" //
				+ "					where Prodazhi.nomenklatura=n.[_IDRRef] and DogovoryKontragentov_strip .vladelec=parameters.kontragent	\n" //
				+ "				)		\n" //
				+ "			) 			\n" //
				+ "		as [LastPrice]  				\n" //
				+ " ,(select max(ProcentSkidkiNacenki) from NacenkiKontr"// 
				+ "			where PoluchatelSkidki=parameters.kontragent"//
				+ "			and Period=(select max(Period) from NacenkiKontr"// 
				+ "				where PoluchatelSkidki=parameters.kontragent and date(period)<=date(parameters.dataOtgruzki)))"//		
				+ "		as [Nacenka]\n"//	
				+ "	 ,(select Individualnye from ZapretSkidokTov where Nomenklatura=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokTov		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokTovIndividualnye]			\n"//
				+ "	 ,(select Nokopitelnye from ZapretSkidokTov where Nomenklatura=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokTov		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokTovNokopitelnye ]			\n"//
				+ "	 ,(select Partner from ZapretSkidokTov where Nomenklatura=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokTov		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokTovPartner ]			\n"//
				+ "	 ,(select Razovie from ZapretSkidokTov where Nomenklatura=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokTov		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokTovRazovie ]			\n"//
				+ "	 ,(select Nacenki from ZapretSkidokTov where Nomenklatura=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokTov		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokTovNacenki ]			\n"//
				+ "	 ,(select Individualnye from ZapretSkidokProizv where Proizvoditel=n.[OsnovnoyProizvoditel]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokProizv 		\n"//
				+ "					where Proizvoditel=n.[OsnovnoyProizvoditel] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokProizvIndividualnye]			\n"//
				+ "	 ,(select Nokopitelnye  from ZapretSkidokProizv where Proizvoditel=n.[OsnovnoyProizvoditel]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokProizv 		\n"//
				+ "					where Proizvoditel=n.[OsnovnoyProizvoditel] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokProizvNokopitelnye]			\n"//
				+ "	 ,(select Partner  from ZapretSkidokProizv where Proizvoditel=n.[OsnovnoyProizvoditel]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokProizv 		\n"//
				+ "					where Proizvoditel=n.[OsnovnoyProizvoditel] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokProizvPartner ]			\n"//
				+ "	 ,(select Razovie  from ZapretSkidokProizv where Proizvoditel=n.[OsnovnoyProizvoditel]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokProizv 		\n"//
				+ "					where Proizvoditel=n.[OsnovnoyProizvoditel] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokProizvRazovie ]			\n"//
				+ "	 ,(select Nacenki from ZapretSkidokProizv where Proizvoditel=n.[OsnovnoyProizvoditel]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokProizv 		\n"//
				+ "					where Proizvoditel=n.[OsnovnoyProizvoditel] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokProizvNacenki ]			\n"//
				+ "	 ,(select FixCena from FiksirovannyeCeny where Nomenklatura=n.[_IDRRef] and PoluchatelSkidki=parameters.kontragent					\n"//
				+ "				and Period=(select max(Period) from FiksirovannyeCeny		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and PoluchatelSkidki=parameters.kontragent and date(period)<=date(parameters.dataOtgruzki) and date(dataokonchaniya)>=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [FiksirovannyeCeny]			\n"//
				+ "	 ,(select ProcentSkidkiNacenki from SkidkaPartneraKarta where PoluchatelSkidki=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from SkidkaPartneraKarta		\n"//
				+ "					where PoluchatelSkidki=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki) and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) ))	\n"//
				+ "			as [SkidkaPartneraKarta]			\n"//
				+ "	 ,(select ProcentSkidkiNacenki from NakopitelnyeSkidki where PoluchatelSkidki=parameters.kontragent					\n"//
				+ "				and Period=(select max(Period) from NakopitelnyeSkidki		\n"//
				+ "					where PoluchatelSkidki=parameters.kontragent and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [NakopitelnyeSkidki]			\n"//
				+ "\n 				,(select max(period) from Prodazhi"
				+ "\n 					join DogovoryKontragentov_strip on Prodazhi .DogovorKontragenta = DogovoryKontragentov_strip._IDRref"
				+ "\n 					where Prodazhi.nomenklatura=n.[_IDRRef] and DogovoryKontragentov_strip .vladelec=parameters.kontragent" + "\n 				) as [LastSell] "
				+ "\n "
				+ "	from Nomenklatura n					\n" //
				+ "\n join AdresaPoSkladam skl on skl.nomenklatura=n._idrref "//
				+ "\n and skl.sklad<>X'00000000000000000000000000000000' "//
				+ "\n and skl.period=(select max(period) from AdresaPoSkladam where AdresaPoSkladam.nomenklatura=n._idrref) "//				
				+ "\n " + "	join EdinicyIzmereniya eho on n.EdinicaKhraneniyaOstatkov = eho._IDRRef					\n" //
				+ "	join EdinicyIzmereniya ei on n.EdinicaDlyaOtchetov = ei._IDRRef					\n" //
				+ "	join Consts c \n" //
				+ uslovieSkladaPodrazdeleniaDlyaTraphik(//
						ApplicationHoreca.getInstance().getShippingDate().getTime()//
						, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya())// 
				+ "\n	join (select '"// 
				+ DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime()) //
				+ "' as dataOtgruzki,"//
				+ clientID//
				+ " as kontragent,"//
				+ ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				+ " as polzovatel"//
				+ ") parameters					\n" //
				//+ " join Prodazhi on Prodazhi.Nomenklatura = n._IDRref \n"//
				//+ " join DogovoryKontragentov dk on Prodazhi.DogovorKontragenta = dk._IDRref" //
				//+ " and date(Prodazhi.period) <= date('" + data + "') and date(Prodazhi.period) >= date('" + data + "') " + " and dk.vladelec=kontragent \n"
				+ " where  Roditel = " + parent// 
				+ " group by n._id "//
				+ " order by n.Naimenovanie " //
		;
		sqlString = Request_NomenclatureBase.composeSQL(//
				DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
				, ApplicationHoreca.getInstance().getClientInfo().getID()//
				, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				, ""//
				, ""//
				, parent//
				, ISearchBy.SEARCH_CHILDREN//
				, true//
				, false//
				, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya(), 200, 0,false,false,degustacia,null,null,false);
		//System.out.println("Request_Trafiks.RequestNomenclatureByParent.Request " );
		Cursor c = db.rawQuery(sqlString, null);
		return c;
	}
	public static Cursor RequestNomenclatureBySearchString(SQLiteDatabase db, String searchString, int searchBy,boolean degustacia) {
		/*String sqlString = "select n._id, n.[_IDRRef], n.[Artikul], n.[Naimenovanie], " //
				+ "n.[OsnovnoyProizvoditel] , n.[ProizvoditelNaimenovanie],"//
				+ "n.[EdinicyIzmereniyaNaimenovanie], n.[MinNorma], " //
				+ "n.[EdinicyIzmereniyaID], n.Roditel [Roditel], " + "n.[Koephphicient], n.[UpperName] "//
				+ "from CurTrafiks n ";
		*/
		String clientID = "0";
		try {
			clientID = ApplicationHoreca.getInstance().getClientInfo().getID();
		}
		catch (Throwable t) {
			LogHelper.debug("Request_Trafiks.RequestNomenclatureByParent: " + t.getMessage());
		}
		String data = DateTimeHelper.SQLDateString(new java.util.Date());
		String sqlString = "	select					\n" //
				+ "	n._id					\n" //
				+ "	,n.[_IDRRef]					\n" //
				+ "	,n.[Artikul]					\n" //
				+ "	,n.[Naimenovanie]					\n" //
				+ "	,n.[OsnovnoyProizvoditel]					\n" //
				+ " ,ifnull((select Naimenovanie from Proizvoditel \n"
				+ " 		where n.[OsnovnoyProizvoditel] = Proizvoditel ._IDRRef limit 1 \n"
				+ " 		),1) as [ProizvoditelNaimenovanie] \n"
				+ "	,eho.[Naimenovanie] as [EdinicyIzmereniyaNaimenovanie]					\n" //
				+ " ,ifnull((select max(VelichinaKvantovNomenklatury.Kolichestvo) from VelichinaKvantovNomenklatury \n"
				+ "	 		where VelichinaKvantovNomenklatury.Nomenklatura = n.[_IDRRef] \n"
				+ "			),1) as [MinNorma] \n"
				+ "	,ei._IDRRef as [EdinicyIzmereniyaID]					\n" //
				+ ", n.Roditel [Roditel] \n"
				+ "	,ei.Koephphicient as [Koephphicient]					\n" //
				+ " ,n.[UpperName] \n"//
				+ "	,(select max(Cena) from CenyNomenklaturySklada 					\n" //
				+ "			  where CenyNomenklaturySklada.nomenklatura=n.[_IDRRef] 			\n" //
				+ "			  and Period=(select max(Period) from CenyNomenklaturySklada 			\n" //
				+ "				where nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))		\n" //
				+ "		as [Cena]	"//
				+ "	,0 as Skidka					\n" //
				+ "	,0 as CenaSoSkidkoy					\n" //
				+ "	,x'00' as VidSkidki					\n" //
				+ "	,n.Roditel as Roditel					\n" //
				+ "\n ,(select (1.0+(select ifnull(nacenka1,ifnull(nacenka2,ifnull(nacenka3,ifnull(nacenka4,nacenka5))))"
				+ "\n 	from (select (select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p1._idrref and NomenklaturaProizvoditel_2=n._idrref"
				+ "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p1._idrref and NomenklaturaProizvoditel_2=n._idrref)"
				+ "\n ) as nacenka1"
				+ "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p2._idrref and NomenklaturaProizvoditel_2=n._idrref"
				+ "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p2._idrref and NomenklaturaProizvoditel_2=n._idrref)"
				+ "\n ) as nacenka2"
				+ "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p3._idrref and NomenklaturaProizvoditel_2=n._idrref"
				+ "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p3._idrref and NomenklaturaProizvoditel_2=n._idrref)"
				+ "\n ) as nacenka3"
				+ "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p4._idrref and NomenklaturaProizvoditel_2=n._idrref"
				+ "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p3._idrref and NomenklaturaProizvoditel_2=n._idrref)"
				+ "\n ) as nacenka4"
				+ "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=X'00000000000000000000000000000000' and NomenklaturaProizvoditel_2=n._idrref"
				+ "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=X'00000000000000000000000000000000' and NomenklaturaProizvoditel_2=n._idrref)"
				+ "\n ) as nacenka5"
				+ "\n 		from Podrazdeleniya p1"
				+ "\n 		left join Podrazdeleniya p2 on p1.roditel=p2._idrref"
				+ "\n 		left join Podrazdeleniya p3 on p2.roditel=p3._idrref"
				+ "\n 		left join Podrazdeleniya p4 on p3.roditel=p4._idrref"
				+ "\n 		join Polzovateli on p1._idrref=Polzovateli.podrazdelenie and Polzovateli._idrref= parameters.polzovatel"
				+ "\n 		)"
				+ "\n 	)/100.0)*max(TekuschieCenyOstatkovPartiy.Cena) from TekuschieCenyOstatkovPartiy"
				+ "\n  			where TekuschieCenyOstatkovPartiy.nomenklatura=n.[_IDRRef]"
				+ "\n ) as [MinCena]\n"
				/*
				+ "	,(select (1.0+nacenka/100.0)*max(TekuschieCenyOstatkovPartiy.Cena) from MinimalnyeNacenkiProizvoditeley_1					\n" //
				+ "				,TekuschieCenyOstatkovPartiy		\n" //
				+ "			where NomenklaturaProizvoditel_2=n.[_IDRRef]			\n" //
				+ "			and TekuschieCenyOstatkovPartiy.nomenklatura=n.[_IDRRef]			\n" //
				+ "			and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 			\n" //
				+ "				where NomenklaturaProizvoditel_2=n.[_IDRRef]))		\n" //
				+ "		as [MinCena]				\n" //
				*/
				+ "	,(select (1.0+c.[MaksNacenkaCenyPraysa]/100.0)*max(Cena) from CenyNomenklaturySklada 					\n" //
				+ "			where CenyNomenklaturySklada.nomenklatura=n.[_IDRRef] 			\n" //
				+ "			and Period=(select max(Period) from CenyNomenklaturySklada			\n" //
				+ "				where nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))		\n" //
				+ "		as [MaxCena]				\n" //
				+ "	,(select max(Cena) from TekuschieCenyOstatkovPartiy 					\n" //
				+ "			  where TekuschieCenyOstatkovPartiy.nomenklatura=n.[_IDRRef]) 			\n" //
				+ "		as [BasePrice]				\n" //
				+ "	,(select max(0.0+Stoimost/kolichestvo) from Prodazhi 					\n" //
				+ "			join DogovoryKontragentov_strip on Prodazhi .DogovorKontragenta = DogovoryKontragentov_strip._IDRref			\n" //
				+ "			where Prodazhi.nomenklatura=n.[_IDRRef] and period=(			\n" //
				+ "				select max(period) from Prodazhi 		\n" //
				+ "					join DogovoryKontragentov_strip on Prodazhi .DogovorKontragenta = DogovoryKontragentov_strip._IDRref	\n" //
				+ "					where Prodazhi.nomenklatura=n.[_IDRRef] and DogovoryKontragentov_strip .vladelec=parameters.kontragent	\n" //
				+ "				)		\n" //
				+ "			) 			\n" //
				+ "		as [LastPrice]  				\n" //
				+ " ,(select max(ProcentSkidkiNacenki) from NacenkiKontr"// 
				+ "			where PoluchatelSkidki=parameters.kontragent"//
				+ "			and Period=(select max(Period) from NacenkiKontr"// 
				+ "				where PoluchatelSkidki=parameters.kontragent and date(period)<=date(parameters.dataOtgruzki)))"//		
				+ "		as [Nacenka]\n"//	
				+ "	 ,(select Individualnye from ZapretSkidokTov where Nomenklatura=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokTov		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokTovIndividualnye]			\n"//
				+ "	 ,(select Nokopitelnye from ZapretSkidokTov where Nomenklatura=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokTov		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokTovNokopitelnye ]			\n"//
				+ "	 ,(select Partner from ZapretSkidokTov where Nomenklatura=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokTov		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokTovPartner ]			\n"//
				+ "	 ,(select Razovie from ZapretSkidokTov where Nomenklatura=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokTov		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokTovRazovie ]			\n"//
				+ "	 ,(select Nacenki from ZapretSkidokTov where Nomenklatura=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokTov		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokTovNacenki ]			\n"//
				+ "	 ,(select Individualnye from ZapretSkidokProizv where Proizvoditel=n.[OsnovnoyProizvoditel]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokProizv 		\n"//
				+ "					where Proizvoditel=n.[OsnovnoyProizvoditel] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokProizvIndividualnye]			\n"//
				+ "	 ,(select Nokopitelnye  from ZapretSkidokProizv where Proizvoditel=n.[OsnovnoyProizvoditel]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokProizv 		\n"//
				+ "					where Proizvoditel=n.[OsnovnoyProizvoditel] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokProizvNokopitelnye]			\n"//
				+ "	 ,(select Partner  from ZapretSkidokProizv where Proizvoditel=n.[OsnovnoyProizvoditel]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokProizv 		\n"//
				+ "					where Proizvoditel=n.[OsnovnoyProizvoditel] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokProizvPartner ]			\n"//
				+ "	 ,(select Razovie  from ZapretSkidokProizv where Proizvoditel=n.[OsnovnoyProizvoditel]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokProizv 		\n"//
				+ "					where Proizvoditel=n.[OsnovnoyProizvoditel] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokProizvRazovie ]			\n"//
				+ "	 ,(select Nacenki from ZapretSkidokProizv where Proizvoditel=n.[OsnovnoyProizvoditel]					\n"//
				+ "				and Period=(select max(Period) from ZapretSkidokProizv 		\n"//
				+ "					where Proizvoditel=n.[OsnovnoyProizvoditel] and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [ZapretSkidokProizvNacenki ]			\n"//
				+ "	 ,(select FixCena from FiksirovannyeCeny where Nomenklatura=n.[_IDRRef] and PoluchatelSkidki=parameters.kontragent					\n"//
				+ "				and Period=(select max(Period) from FiksirovannyeCeny		\n"//
				+ "					where Nomenklatura=n.[_IDRRef] and PoluchatelSkidki=parameters.kontragent and date(period)<=date(parameters.dataOtgruzki) and date(dataokonchaniya)>=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [FiksirovannyeCeny]			\n"//
				+ "	 ,(select ProcentSkidkiNacenki from SkidkaPartneraKarta where PoluchatelSkidki=n.[_IDRRef]					\n"//
				+ "				and Period=(select max(Period) from SkidkaPartneraKarta		\n"//
				+ "					where PoluchatelSkidki=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki) and date(DataOkonchaniya)>=date(parameters.dataOtgruzki) ))	\n"//
				+ "			as [SkidkaPartneraKarta]			\n"//
				+ "	 ,(select ProcentSkidkiNacenki from NakopitelnyeSkidki where PoluchatelSkidki=parameters.kontragent					\n"//
				+ "				and Period=(select max(Period) from NakopitelnyeSkidki		\n"//
				+ "					where PoluchatelSkidki=parameters.kontragent and date(period)<=date(parameters.dataOtgruzki)))	\n"//
				+ "			as [NakopitelnyeSkidki]			\n"//
				+ "\n 				,(select max(period) from Prodazhi"
				+ "\n 					join DogovoryKontragentov_strip on Prodazhi .DogovorKontragenta = DogovoryKontragentov_strip._IDRref"
				+ "\n 					where Prodazhi.nomenklatura=n.[_IDRRef] and DogovoryKontragentov_strip .vladelec=parameters.kontragent"
				+ "\n 				) as [LastSell] "
				+ "\n "
				+ "	from Nomenklatura n					\n" //
				+ "\n join AdresaPoSkladam skl on skl.nomenklatura=n._idrref "//
				+ "\n and skl.sklad<>X'00000000000000000000000000000000' "//
				+ "\n and skl.period=(select max(period) from AdresaPoSkladam where AdresaPoSkladam.nomenklatura=n._idrref) "//				
				+ "\n "
				+ "	join EdinicyIzmereniya eho on n.EdinicaKhraneniyaOstatkov = eho._IDRRef					\n" //
				+ "	join EdinicyIzmereniya ei on n.EdinicaDlyaOtchetov = ei._IDRRef					\n" //
				+ "	join Consts c \n" //
				+ uslovieSkladaPodrazdeleniaDlyaTraphik(ApplicationHoreca.getInstance().getShippingDate().getTime(),
						ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya()) + "\n	join (select '"// 
				+ DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime()) //
				+ "' as dataOtgruzki,"//
				+ clientID//
				+ " as kontragent,"//
				+ ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				+ " as polzovatel"//
				+ ") parameters					\n" //
				//+ " join Prodazhi on Prodazhi.Nomenklatura = n._IDRref \n"//
				//+ " join DogovoryKontragentov dk on Prodazhi.DogovorKontragenta = dk._IDRref" //
				//+ " and date(Prodazhi.period) <= date('" + data + "') and date(Prodazhi.period) >= date('" + data + "') " + " and dk.vladelec=kontragent \n"
		//+" where  Roditel = " + parent// 
		//+" group by n._id "//
		//+" order by n.Naimenovanie " //
		;
		if (searchBy == SEARCH_ARTICLE) {
			sqlString = sqlString + " where ( n.[Artikul] = '" + searchString + "' )";
		}
		else {
			if (searchBy == SEARCH_NAME) {
				sqlString = sqlString + " where ( n.[UpperName] like '%" + searchString + "%')";
			}
			else {
				if (searchBy == SEARCH_IDRREF) {
					sqlString = sqlString + " where ( n.[_IDRRef] = " + searchString + " )";
				}
				else {
					if (searchBy == SEARCH_VENDOR) {
						sqlString = sqlString + " where ( n.[ProizvoditelNaimenovanie] like '" + searchString + "%')";
					}
				}
			}
		}
		sqlString = sqlString + " group by n._id "//
				+ " order by n.Naimenovanie ";
		sqlString = Request_NomenclatureBase.composeSQL(//
				DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
				, ApplicationHoreca.getInstance().getClientInfo().getID()//
				, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				, ""//
				, ""//
				, searchString//
				, searchBy//
				, true//
				, false//
				, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya(), 200, 0,false,false,degustacia,null,null,false);
		//System.out.println("Request_Trafiks.RequestNomenclatureBySearchString.Request ");
		Cursor c = db.rawQuery(sqlString, null);
		//System.out.println("RequestNomenclatureBySearchString.Request done");
		return c;
	}
	public static int get_id(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(ID));
	}
	public static String getIDRRef(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(IDRREF)));
	}
	public static String getArtikul(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(ARTIKUL));
	}
	public static String getNaimenovanie(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(NAIMENOVANIE));
	}
	public static String getProizvoditelID(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(OSNOVNOY_PROIZVODITEL)));
	}
	public static String getProizvoditelNaimenovanie(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(PROIZVODITEL_NAIMENOVANIE));
	}
	public static String getEdinicyIzmereniyaNaimenovanie(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(EDINICY_IZMERENIYA_NAIMENOVANIE));
	}
	public static double getKoephphicient(Cursor cursor) {
		return cursor.getDouble(cursor.getColumnIndex(KOEPHICIENT));
	}
	public static double getMinNorma(Cursor cursor) {
		return cursor.getDouble(cursor.getColumnIndex(MIN_NORMA));
	}
	public static String getEdinicyIzmereniyaID(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex(EDINICY_IZMERENIYA_ID)));
	}
}
