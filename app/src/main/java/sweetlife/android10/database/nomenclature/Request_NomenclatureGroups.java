package sweetlife.android10.database.nomenclature;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.utils.DateTimeHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_NomenclatureGroups extends Request_NomenclatureBase {
	public Request_NomenclatureGroups(boolean degustacia) {
		super(degustacia);
	}

	public static Cursor categoriesStat(SQLiteDatabase db) {
		String sql = "select categoryFolder._id, categoryFolder._IDRRef, categoryFolder.Naimenovanie"
				+ "\n 		,count(folder2._id) as cnt2"
				+ "\n 		,0 as r2"
				+ "\n		,count(folder2._id) as subcount"
				+ "\n 	from Nomenklatura_sorted categoryFolder"
				+ "\n		left join nomenklatura folder1 on categoryFolder._IDRRef=folder1.Roditel"
				+ "\n		left join nomenklatura folder2 on folder1._IDRRef=folder2.Roditel"
				+ "\n 	where categoryFolder.roditel=x'00' and categoryFolder.etogruppa=x'01'"
				+ "\n	group by categoryFolder._id"
				+ "\n	having cnt2>0"
				+ "\n 	order by categoryFolder.naimenovanie;";
		System.out.println("root folders Request_NomenclatureGroups.RequestNomenlatureGroupsWithoutParent " + sql);
		Cursor c = db.rawQuery(sql, null);
		return c;
	}

	public static Cursor RequestNomenlatureGroupsWithoutParentOld(SQLiteDatabase db) {
		//return RequestNomenlatureGroupsWithParent(db, "x'00000000000000000000000000000000'");
		String kontragent = ApplicationHoreca.getInstance().getClientInfo().getID();
		//String polzovatel = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
		//String polzovatel = "x'" + ApplicationHoreca.getInstance().currentIDmarshrut + "'";
		String polzovatel = "x'" + Cfg.selectedHRC_idrref() + "'";
		//String price = "x'" + getPriceVladelecID(kontragent) + "'";
		int zapretOtgruzokOtvetsvennogo = getZapretOtgruzokOtvetsvennogo(kontragent, polzovatel);
		//System.out.println("RequestNomenlatureGroupsWithoutParent kontrgent " + kontragent + ", polzovatel " + polzovatel + ", price " + price + ", zapretOtgruzokOtvetsvennogo " + zapretOtgruzokOtvetsvennogo);
		//int zapretOtgruzokOtvetsvennogo = Request_NomenclatureBase.getZapretOtgruzokOtvetsvennogo(kontragent, polzovatel);
		//zapretOtgruzokOtvetsvennogo=ZapretOtgruzokOtvetsvennogoExclude;
		//String sql = "select p._id, p._IDRRef, p.Naimenovanie";
		/*if (zapretOtgruzokOtvetsvennogo == ZapretOtgruzokOtvetsvennogoInclude) {
			sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo"//
					+ "\n				where Otvetstvenniy=" + polzovatel// 
					+ "\n				and ObjectZapreta=" + kontragent//
					+ "\n				and proizvoditel=n._idrref"//
					+ "\n				limit 1) as zooSelf"//
			;
			sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo"//
					+ "\n				where Otvetstvenniy=" + polzovatel// 
					+ "\n				and ObjectZapreta=" + kontragent//
					+ "\n				and proizvoditel=f._idrref"//
					+ "\n				limit 1) as zooParent"//
			;
			sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo"//
					+ "\n				where Otvetstvenniy=" + polzovatel// 
					+ "\n				and ObjectZapreta=" + kontragent//
					+ "\n				and proizvoditel=p._idrref"//
					+ "\n				limit 1) as zooParentParent"//
			;
			sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo"//
					+ "\n				where Otvetstvenniy=" + polzovatel//
					+ "\n				and ObjectZapreta=" + kontragent//
					+ "\n				and proizvoditel=n.[OsnovnoyProizvoditel]"//
					+ "\n				limit 1) as zooProizvoditel"//
			;
		}
		else {
			if (zapretOtgruzokOtvetsvennogo == ZapretOtgruzokOtvetsvennogoExclude) {
				sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo"//
						+ "\n				where ObjectZapreta=" + kontragent//
						+ "\n				and proizvoditel=n._idrref"//
						+ "\n				limit 1) as zooSelf"//
				;
				sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo"//
						+ "\n				where ObjectZapreta=" + kontragent//
						+ "\n				and proizvoditel=f._idrref"//
						+ "\n				limit 1) as zooParent"//
				;
				sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo"//
						+ "\n				where ObjectZapreta=" + kontragent//
						+ "\n				and proizvoditel=p._idrref"//
						+ "\n				limit 1) as zooParentParent"//
				;
				sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo"//
						+ "\n				where ObjectZapreta=" + kontragent//
						+ "\n				and proizvoditel=n.[OsnovnoyProizvoditel]"//
						+ "\n				limit 1) as zooProizvoditel"//
				;
			}
			else {
				sql = sql + "\n ,1 as zooSelf,1 as zooParent,1 as zooParentParent,1 as zooProizvoditel";
			}
		}*/
		/*
		sql = sql + "\n\t from Nomenklatura_sorted p";
		sql = sql + "\n\t left join Nomenklatura_sorted  f on f.roditel=p._idrref"//
				+ "\n\t left join Nomenklatura_sorted  n on n.roditel=f._idrref"//
				//+ "\n\t left join Nomenklatura_sorted  n on n.roditel=f._idrref"//
				//+ "\n\t	left join Proizvoditel przv on n.[OsnovnoyProizvoditel] = przv._IDRRef "
		;
		sql = sql + "\n\t where p.EtoGruppa=x'01'"//
				+ "\n\t\t and (p.Roditel = x'00000000000000000000000000000000' or p.Roditel = x'00')" //
				+ "\n\t\t and p.PometkaUdaleniya<>x'01'";
		//sql = sql + "\n\t\t and (zooSelf=1 or zooParent=1 or zooParentParent=1 or zooProizvoditel=1)";
		sql = sql + "\n\t group by p._IDRRef"//
				//+ "\n\t having count(n._idrref)>0"//
				+ "\n\t order by p.Naimenovanie ";//
		*/
		/*String sql = //"select f1._idrref as _idrref,f1.kod as kod,f1.artikul as artikul ,f1.etogruppa as etogruppa,f1.naimenovanie as naimenovanie"//
				"select f1._id, f1._IDRRef, f1.Naimenovanie || ': ' || (count(c1._idrref)+count(c2._idrref)) as Naimenovanie"//
				+ "\n		,count(c1._idrref) as cnt1"//
				+ "\n		,count(c2._idrref) as cnt2"//
				+ "\n	from Nomenklatura_sorted f1"//
				+ "\n		left join Nomenklatura_sorted c1 on c1.roditel=f1._idrref and c1.etogruppa=x'00' and c1.usluga=x'00'"//
				+ "\n		left join Nomenklatura_sorted sf2 on sf2.roditel=f1._idrref and sf2.etogruppa=x'01'"//
				+ "\n		left join Nomenklatura_sorted c2 on c2.roditel=sf2._idrref and c2.etogruppa=x'00' and c2.usluga=x'00'"//
				+ "\n	where f1.roditel=x'00'"// 
				+ "\n		and f1.etogruppa=x'01'";*/
		String sql = //"select f1._idrref as _idrref,f1.kod as kod,f1.artikul as artikul ,f1.etogruppa as etogruppa,f1.naimenovanie as naimenovanie"//
				"select f1._id, f1._IDRRef, f1.Naimenovanie || ': ' || count(c2._idrref) as Naimenovanie"//
						+ "\n		,count(c2._idrref) as cnt2"//
						//+ "\n		,max(price2.price_doc) as r2"//
						+ "\n		,max(AssortimentCurrent._id) as r2"//
				;
		/*
		+ "\n			,(select 1 from zapretotgruzokotvetsvennogo"//
		+ "\n 				where ObjectZapreta="+kontragent//
		+ "\n 				and proizvoditel=c2._idrref"//
		+ "\n 				limit 1) as zooSelf"//
		+ "\n   		,(select 1 from zapretotgruzokotvetsvennogo"//
		+ "\n 				where ObjectZapreta="+kontragent//
		+ "\n 				and proizvoditel=c2.roditel"//
		+ "\n 				limit 1) as zooParent"//
		+ "\n   		,(select 1 from zapretotgruzokotvetsvennogo"//
		+ "\n 				where ObjectZapreta="+kontragent//
		+ "\n 				and proizvoditel=sf2.roditel"//
		+ "\n 				limit 1) as zooParentParent"//
		+ "\n   		,(select 1 from zapretotgruzokotvetsvennogo"//
		+ "\n 				where ObjectZapreta="+kontragent//
		+ "\n 				and proizvoditel=c2.[OsnovnoyProizvoditel]"//
		+ "\n 				limit 1) as zooProizvoditel"//
		*/
		if (zapretOtgruzokOtvetsvennogo == ZapretOtgruzokOtvetsvennogoInclude) {
			sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
					+ "\n				where Otvetstvenniy=" + polzovatel//
					+ "\n				and proizvoditel=c2._idrref"//
					+ "\n				limit 1) as zooSelf"//
			;
			sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
					+ "\n				where Otvetstvenniy=" + polzovatel//
					+ "\n				and proizvoditel=c2.roditel"//
					+ "\n				limit 1) as zooParent"//
			;
			sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
					+ "\n				where Otvetstvenniy=" + polzovatel//
					+ "\n				and proizvoditel=sf2.roditel"//
					+ "\n				limit 1) as zooParentParent"//
			;
			sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
					+ "\n				where Otvetstvenniy=" + polzovatel//
					+ "\n				and proizvoditel=c2.[OsnovnoyProizvoditel]"//
					+ "\n				limit 1) as zooProizvoditel"//
			;
			//sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo where proizvoditel=n._idrref limit 1) as zoo1";
			//sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo where proizvoditel=parent._idrref limit 1) as zoo2";
			//sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo where proizvoditel=parentparent._idrref limit 1) as zoo3";
		} else {
			if (zapretOtgruzokOtvetsvennogo == ZapretOtgruzokOtvetsvennogoExclude) {
				sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
						+ "\n				where proizvoditel=c2._idrref"//
						+ "\n				limit 1) as zooSelf"//
				;
				sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
						+ "\n				where proizvoditel=c2.roditel"//
						+ "\n				limit 1) as zooParent"//
				;
				sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
						+ "\n				where proizvoditel=sf2.roditel"//
						+ "\n				limit 1) as zooParentParent"//
				;
				sql = sql + "\n  		,(select 1 from ZapretOtgruzokOtvetsvennogo_strip"//
						+ "\n				where proizvoditel=c2.[OsnovnoyProizvoditel]"//
						+ "\n				limit 1) as zooProizvoditel"//
				;
				//sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo where proizvoditel=n._idrref limit 1) as zoo1";
				//sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo where proizvoditel=parent._idrref limit 1) as zoo2";
				//sql = sql + "\n  		,(select 1 from zapretotgruzokotvetsvennogo where proizvoditel=parentparent._idrref limit 1) as zoo3";
			}
		}
		sql = sql + "\n	from Nomenklatura_sorted f1"//
				+ "\n		left join Nomenklatura_sorted sf2 on sf2.roditel=f1._idrref and sf2.etogruppa=x'01'"//
				+ "\n		left join Nomenklatura_sorted c2 on c2.roditel=sf2._idrref and c2.etogruppa=x'00' and c2.usluga=x'00'"//
				//+ "\n		left join price_artikul price2 on price2.nomenklatura=c2.[_IDRRef] and price2.price_doc=" + price + ""//
				+ "\n		left join AssortimentCurrent on AssortimentCurrent.nomenklatura_idrref=c2.[_IDRRef]"//
		;
		//sql = sql + "\n 	cross join AssortimentCurrent curAssortiment on curAssortiment.nomenklatura_idrref=c2.[_IDRRef]";
		sql = sql
				+ "\n	where f1.roditel=x'00' and f1.etogruppa=x'01'";
		if (zapretOtgruzokOtvetsvennogo == ZapretOtgruzokOtvetsvennogoInclude) {
			sql = sql + "\n		and (ifnull(zooSelf,0)=1 or ifnull(zooParent,0)=1 or ifnull(zooParentParent,0)=1 or ifnull(zooProizvoditel,0)=1)";
		} else {
			if (zapretOtgruzokOtvetsvennogo == ZapretOtgruzokOtvetsvennogoExclude) {
				sql = sql + "\n		and (not (ifnull(zooSelf,0)=1 or ifnull(zooParent,0)=1 or ifnull(zooParentParent,0)=1 or ifnull(zooProizvoditel,0)=1))";
			} else {
				//
			}
		}
		sql = sql + "\n	group by f1._idrref"//
				+ "\n	having cnt2>0 and r2>0"//
				+ "\n	order by f1.naimenovanie;";
		System.out.println("root folders Request_NomenclatureGroups.RequestNomenlatureGroupsWithoutParent " + sql);
		Cursor c = db.rawQuery(sql, null);
		//System.out.println("done root");
		return c;
	}

	public static Cursor RequestNomenlatureGroupsWithParent(SQLiteDatabase db, String parent) {
		String sqlString = "select n._id, n._IDRRef, n.Naimenovanie from Nomenklatura n" //
				+ " left join Nomenklatura  ch on ch.roditel=n._idrref"//
				+ " where n.EtoGruppa=x'01' and n.Roditel = " //
				+ parent //
				+ " and n.PometkaUdaleniya<>x'01'" //
				+ " group by n._IDRRef"//
				+ " having count(ch._idrref)>0"//
				+ " order by n.Naimenovanie ";//
		//System.out.println("////// RequestNomenlatureGroupsWithParent " + parent + "\n" + sqlString);
		return db.rawQuery(sqlString, null);
	}

	public static Cursor RequestNomenclatureByParentOld(SQLiteDatabase db, String parent, boolean degustacia) {
		/*String sqlString = "select n._id, n.[_IDRRef], n.[Artikul], n.[Naimenovanie], n.[OsnovnoyProizvoditel], " +
				"n.[ProizvoditelNaimenovanie], n.[Cena], "+
				"n.Skidka, n.CenaSoSkidkoy, n.VidSkidki,  " +
				"n.[EdinicyIzmereniyaNaimenovanie], n.[MinNorma], " +
				"n.Koephphicient,  n.[EdinicyIzmereniyaID], "+
				"ifnull(ncr.[MinCena], 0) [MinCena], ifnull(ncr.[MaxCena], 0) [MaxCena], " +
				"n.[BasePrice], n.[LastPrice]  "+
				"from Temp_Nomenklatura_Tovary n  "+
				"left join CurNomenklaturaTovaryCR ncr on n.[_IDRref] = ncr.[_IDRref] " +
				"where n.Roditel = " + parent +
				" order by n.Naimenovanie ";*/
		String sqlString = "	select					\n" //
				+ "	n._id					\n" //
				+ "	,n.[_IDRRef]					\n" //
				+ "	,n.[Artikul]					\n" //
				+ "	,n.[Naimenovanie]					\n" //
				+ "	,n.[OsnovnoyProizvoditel]					\n" //
				//+ "	,ifnull(p.[Naimenovanie], '') as ProizvoditelNaimenovanie					\n" //
				+ " ,ifnull((select Naimenovanie from Proizvoditel \n" + " 		where n.[OsnovnoyProizvoditel] = Proizvoditel ._IDRRef limit 1 \n"
				+ " 		),1) as [ProizvoditelNaimenovanie] \n"
				+ "	,(select max(Cena) from CenyNomenklaturySklada 					\n" //
				+ "			  where CenyNomenklaturySklada.nomenklatura=n.[_IDRRef] 			\n" //
				+ "			  and Period=(select max(Period) from CenyNomenklaturySklada 			\n" //
				+ "				where nomenklatura=n.[_IDRRef] and date(period)<=date(parameters.dataOtgruzki)))		\n" //
				+ "		as [Cena]	"//
				+ "	,0 as Skidka					\n" //
				+ "	,0 as CenaSoSkidkoy					\n" //
				+ "	,x'00' as VidSkidki					\n" //
				+ "	,eho.[Naimenovanie] as [EdinicyIzmereniyaNaimenovanie]					\n" //
				//+ "	,ifnull(vkn.[Kolichestvo], 1) as [MinNorma]					\n" //
				+ " ,ifnull((select max(VelichinaKvantovNomenklatury.Kolichestvo) from VelichinaKvantovNomenklatury \n" + "	 		where VelichinaKvantovNomenklatury.Nomenklatura = n.[_IDRRef] \n" + "			),1) as [MinNorma] \n"
				+ "	,ei.Koephphicient as [Koephphicient]					\n" //
				+ "	,ei._IDRRef as [EdinicyIzmereniyaID]					\n" //
				+ "	,n.Roditel as Roditel					\n" //
				+ "\n ,(select (1.0+(select ifnull(nacenka1,ifnull(nacenka2,ifnull(nacenka3,ifnull(nacenka4,nacenka5))))" + "\n 	from (select (select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p1._idrref and NomenklaturaProizvoditel_2=n._idrref" + "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p1._idrref and NomenklaturaProizvoditel_2=n._idrref)" + "\n ) as nacenka1" + "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p2._idrref and NomenklaturaProizvoditel_2=n._idrref" + "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p2._idrref and NomenklaturaProizvoditel_2=n._idrref)" + "\n ) as nacenka2" + "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p3._idrref and NomenklaturaProizvoditel_2=n._idrref" + "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p3._idrref and NomenklaturaProizvoditel_2=n._idrref)" + "\n ) as nacenka3" + "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p4._idrref and NomenklaturaProizvoditel_2=n._idrref" + "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=p3._idrref and NomenklaturaProizvoditel_2=n._idrref)" + "\n ) as nacenka4" + "\n 		,(select nacenka from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=X'00000000000000000000000000000000' and NomenklaturaProizvoditel_2=n._idrref" + "\n and period=(select max(period) from MinimalnyeNacenkiProizvoditeley_1 where podrazdelenie=X'00000000000000000000000000000000' and NomenklaturaProizvoditel_2=n._idrref)" + "\n ) as nacenka5" + "\n 		from Podrazdeleniya p1" + "\n 		left join Podrazdeleniya p2 on p1.roditel=p2._idrref" + "\n 		left join Podrazdeleniya p3 on p2.roditel=p3._idrref"
				+ "\n 		left join Podrazdeleniya p4 on p3.roditel=p4._idrref"
				+ "\n 		join Polzovateli on p1._idrref=Polzovateli.podrazdelenie and Polzovateli._idrref= parameters.polzovatel" + "\n 		)" + "\n 	)/100.0)*max(TekuschieCenyOstatkovPartiy.Cena) from TekuschieCenyOstatkovPartiy" + "\n  			where TekuschieCenyOstatkovPartiy.nomenklatura=n.[_IDRRef]" + "\n ) as [MinCena]\n"
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
				+ "		as [Nacenka]"//
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
				+ "\n 				,(select max(period) from Prodazhi" + "\n 					join DogovoryKontragentov_strip on Prodazhi .DogovorKontragenta = DogovoryKontragentov_strip._IDRref" + "\n 					where Prodazhi.nomenklatura=n.[_IDRRef] and DogovoryKontragentov_strip .vladelec=parameters.kontragent" + "\n 				) as [LastSell] " + "\n " + "	from Nomenklatura n					\n" //
				+ "\n join AdresaPoSkladam skl on skl.nomenklatura=n._idrref "//
				+ "\n and skl.sklad<>X'00000000000000000000000000000000' "//
				+ "\n and skl.period=(select max(period) from AdresaPoSkladam where AdresaPoSkladam.nomenklatura=n._idrref) "//
				+ "\n "
				//+ "	left join Proizvoditel p on n.[OsnovnoyProizvoditel] = p._IDRRef					\n" //
				+ "	join EdinicyIzmereniya eho on n.EdinicaKhraneniyaOstatkov = eho._IDRRef					\n" //
				//+ "	left join VelichinaKvantovNomenklatury vkn on vkn.Nomenklatura = n.[_IDRRef]					\n" //
				+ "	join EdinicyIzmereniya ei on n.EdinicaDlyaOtchetov = ei._IDRRef					\n" //
				//+ "	join AdresaPoSkladam on AdresaPoSkladam.nomenklatura=n.[_IDRRef] and AdresaPoSkladam.sklad<>X'00000000000000000000000000000000'\n" //
				/*+ " join (select max(period) as skladPeriod,nomenklatura as skladNomenklatura \n"//
				+ " 	from AdresaPoSkladam join nomenklatura n2 on n2._idrref=skladNomenklatura \n"//
				+ " 	group by skladNomenklatura,baza \n"//
				+ " 	) sklad on sklad.skladNomenklatura=n._idrref \n"//
				+ " join AdresaPoSkladam on sklad.skladPeriod=AdresaPoSkladam.period \n"//
				+ " 	and n._idrref=AdresaPoSkladam.nomenklatura \n" //
				+ " 	and AdresaPoSkladam.sklad<>X'00000000000000000000000000000000' \n"*/
				/*+ "\n\t left join AdresaPoSkladam kazanAddress on kazanAddress.period =(select max(period) from AdresaPoSkladam"//
				+ "\n\t			where nomenklatura=n._idrref and baza="//
				+ ISklady.KAZAN_ID
				+ ")"//
				+ "\n\t				and n._idrref=kazanAddress.nomenklatura and kazanAddress.baza=" //
				+ ISklady.KAZAN_ID
				+ ""//
				+ "\n\t left join AdresaPoSkladam horecaAddress on horecaAddress.period =(select max(period) from AdresaPoSkladam"//
				+ "\n\t 		where nomenklatura=n._idrref and baza=" //
				+ ISklady.HORECA_ID
				+ ")"//
				+ "\n\t 			and n._idrref=horecaAddress.nomenklatura and horecaAddress.baza=" //
				+ ISklady.HORECA_ID
				+ "\n"*/
				/*
				+ "\n	join AdresaPoSkladam on AdresaPoSkladam.period =(select max(period) from AdresaPoSkladam" //
				+ "\n								where nomenklatura=n._idrref)"//
				+ "\n  	and n._idrref=AdresaPoSkladam.nomenklatura" //
				+ "\n  	and AdresaPoSkladam.sklad<>X'00000000000000000000000000000000' \n"//
				*/
				+ "	join Consts c \n" //
				+ uslovieSkladaPodrazdeleniaNeTraphik(ApplicationHoreca.getInstance().getShippingDate().getTime(), ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya()) + "\n	join (select '"//
				+ DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime()) //
				+ "' as dataOtgruzki,"//
				+ ApplicationHoreca.getInstance().getClientInfo().getID()//
				+ " as kontragent,"//
				+ ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				+ " as polzovatel"//
				+ ") parameters					\n" //
				//+ "	join CenyNomenklaturySklada on CenyNomenklaturySklada .nomenklatura=n.[_IDRRef] 					\n" //
				+ " where n.Roditel = " + parent
				//+ "and n.TovarPodZakaz=x'00' \n" //
				+ " group by n._IDRRef"//
				+ " order by n.Naimenovanie \n";
		sqlString = composeSQL(//
				DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
				, ApplicationHoreca.getInstance().getClientInfo().getID()//
				, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				, ""//
				, ""//
				, parent//
				, ISearchBy.SEARCH_CHILDREN//
				, false//
				, false//
				, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya(), 200, 0, false, false, degustacia, null, null);
		//System.out.println(" RequestNomenclatureByParent " + parent+"\n"+sqlString);
		Cursor c = db.rawQuery(sqlString, null);
		//System.out.println(" RequestNomenclatureByParent done ");
		return c;
	}
	public static Cursor wholeSubFolderContent(SQLiteDatabase db, String parent, boolean degustacia) {
		String sqlString = composeSQL(//
				DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
				, ApplicationHoreca.getInstance().getClientInfo().getID()//
				, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				, ""//
				, ""//
				, parent//
				, ISearchBy.SEARCH_CHILDREN//
				, false//
				, false//
				, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya(), 200, 0, false, false, degustacia, null, null);
		System.out.println(" RequestNomenclatureByParent " + parent+"\n"+sqlString);
		Cursor c = db.rawQuery(sqlString, null);
		return c;
	}
	@Override
	protected void SetRequestString(boolean degustacia) {
	}
}
