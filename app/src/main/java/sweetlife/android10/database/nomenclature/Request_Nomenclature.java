package sweetlife.android10.database.nomenclature;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.utils.DateTimeHelper;

public class Request_Nomenclature extends Request_NomenclatureBase {
	public Request_Nomenclature(boolean degustacia){
		super( degustacia);
	}
	@Override
	
	protected void SetRequestString(boolean degustacia) {
		mStrQuery = composeSQL(//
				DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
				, ApplicationHoreca.getInstance().getClientInfo().getID()//
				, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				, ""//
				, ""//
				, ""//
				, 1//
				, false//
				, true//
				, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya(),200,0,false,false,degustacia,null,null);
	}
	protected void SetRequestString_() {
		/*
				mStrQuery = "select n._id, n.[_IDRRef], n.[Artikul], n.[Naimenovanie], n.[OsnovnoyProizvoditel], " +
						"n.[ProizvoditelNaimenovanie], n.[Cena], "+ 
						"n.Skidka, n.CenaSoSkidkoy, n.VidSkidki,  " +
						"n.[EdinicyIzmereniyaNaimenovanie], n.[MinNorma], " +
						"n.Koephphicient,  n.[EdinicyIzmereniyaID], n.Roditel, "+       
						"ifnull(ncr.[MinCena], 0) [MinCena], ifnull(ncr.[MaxCena], 0) [MaxCena], " +
						"n.[BasePrice], n.[LastPrice]  "+
						"from Temp_Nomenklatura_Tovary n  "+
						"left join CurNomenklaturaTovaryCR ncr on n.[_IDRref] = ncr.[_IDRref]";
				*/
		mStrQuery = "	select					\n" //
				+ "	n._id					\n" //
				+ "	,n.[_IDRRef]					\n" //
				+ "	,n.[Artikul]					\n" //
				+ "	,n.[Naimenovanie]					\n" //
				+ "	,n.[OsnovnoyProizvoditel]					\n" //
				//+ "	,ifnull(p.[Naimenovanie], '') as ProizvoditelNaimenovanie					\n" //
				+ " ,ifnull((select Naimenovanie from Proizvoditel \n"
				+ " 		where n.[OsnovnoyProizvoditel] = Proizvoditel ._IDRRef limit 1 \n"
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
				+ " ,ifnull((select max(VelichinaKvantovNomenklatury.Kolichestvo) from VelichinaKvantovNomenklatury \n"
				+ "	 		where VelichinaKvantovNomenklatury.Nomenklatura = n.[_IDRRef] \n"
				+ "			),1) as [MinNorma] \n"
				+ "	,ei.Koephphicient as [Koephphicient]					\n" //
				+ "	,ei._IDRRef as [EdinicyIzmereniyaID]					\n" //
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
				+ "			join DogovoryKontragentov on Prodazhi .DogovorKontragenta = DogovoryKontragentov._IDRref			\n" //
				+ "			where Prodazhi.nomenklatura=n.[_IDRRef] and period=(			\n" //
				+ "				select max(period) from Prodazhi 		\n" //
				+ "					join DogovoryKontragentov on Prodazhi .DogovorKontragenta = DogovoryKontragentov._IDRref	\n" //
				+ "					where Prodazhi.nomenklatura=n.[_IDRRef] and DogovoryKontragentov .vladelec=parameters.kontragent	\n" //
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
				
				
+"\n 				,(select max(period) from Prodazhi" 		
+"\n 					join DogovoryKontragentov on Prodazhi .DogovorKontragenta = DogovoryKontragentov._IDRref"	
+"\n 					where Prodazhi.nomenklatura=n.[_IDRRef] and DogovoryKontragentov .vladelec=parameters.kontragent"	
+"\n 				) as [LastSell] "
+"\n "
				
				
				+ "	from Nomenklatura_sorted n					\n" //

				+"\n join AdresaPoSkladam_last skl on skl.nomenklatura=n._idrref "//
				//+"\n and skl.sklad<>X'00000000000000000000000000000000' "//
				//+"\n and skl.period=(select max(period) from AdresaPoSkladam where AdresaPoSkladam.nomenklatura=n._idrref) "//				
				+"\n "
				
				+ "	left join Proizvoditel p on n.[OsnovnoyProizvoditel] = p._IDRRef					\n" //
				+ "	join EdinicyIzmereniya_strip eho on n.EdinicaKhraneniyaOstatkov = eho._IDRRef					\n" //
				//+ "	left join VelichinaKvantovNomenklatury vkn on vkn.Nomenklatura = n.[_IDRRef]					\n" //
				+ "	join EdinicyIzmereniya_strip ei on n.EdinicaDlyaOtchetov = ei._IDRRef					\n" //
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
				+ uslovieSkladaPodrazdeleniaNeTraphik(ApplicationHoreca.getInstance().getShippingDate().getTime(), ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya())
				+ "\n	join (select '"// 
				+ DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime()) //
				+ "' as dataOtgruzki,"//
				+ ApplicationHoreca.getInstance().getClientInfo().getID()//
				+ " as kontragent,"//
				+ ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				+ " as polzovatel"//
				+") parameters					\n" //
				//+ "	join CenyNomenklaturySklada on CenyNomenklaturySklada .nomenklatura=n.[_IDRRef] 					\n" //
		;
	}
}
