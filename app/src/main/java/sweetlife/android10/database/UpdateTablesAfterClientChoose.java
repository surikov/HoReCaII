package sweetlife.android10.database;

import java.util.Calendar;

import org.apache.http.HttpStatus;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.utils.DatabaseHelper;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.HTTPRequest;
import sweetlife.android10.utils.Hex;
import tee.binding.Bough;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UpdateTablesAfterClientChoose {
	/*public static void UpdateAll(SQLiteDatabase db, String kontragentID, String date) {
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose.UpdateAll");
		UpdateActualNomenclatura(db, date);
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose UPDATE CUR TABLES end UpdateActualNomenclatura " + (new Long(System.currentTimeMillis())).toString());
		UpdateCurKontragenty(db, kontragentID);
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose UPDATE CUR TABLES end UpdateCurKontragenty " + (new Long(System.currentTimeMillis())).toString());
		UpdateCurProdazhi(db, kontragentID);
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose UPDATE CUR TABLES end UpdateCurProdazhi " + (new Long(System.currentTimeMillis())).toString());
		UpdateTempCenyNomenklatury(db, kontragentID, date);
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose UPDATE CUR TABLES end UpdateTempCenyNomenklatury " + (new Long(System.currentTimeMillis())).toString());
		UpdateCurCenyNomenklaturyCR(db);
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose UPDATE CUR TABLES end UpdateCurCenyNomenklaturyCR " + (new Long(System.currentTimeMillis())).toString());
		UpdateCurCenyNomenklaturyMax(db);
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose UPDATE CUR TABLES end UpdateCurCenyNomenklaturyMax " + (new Long(System.currentTimeMillis())).toString());
		UpdateCurTrafiks(db);
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose UPDATE CUR TABLES end UpdateCurTrafiks " + (new Long(System.currentTimeMillis())).toString());
		UpdateCurCenyNomenklaturyHistory(db);
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose UPDATE CUR TABLES end UpdateCurCenyNomenklaturyHistory " + (new Long(System.currentTimeMillis())).toString());
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose UPDATE CUR TABLES end UpdateAll " + (new Long(System.currentTimeMillis())).toString());
		getDolgiPoDocumentam();
	}*/
	public static void getDolgiPoDocumentam() {
		//
		String dolg = "?";
		//dolgMessage
		ApplicationHoreca mAppInstance = ApplicationHoreca.getInstance();
		String clientNum = mAppInstance.getClientInfo().getKod();
		mAppInstance.getClientInfo().dolgMessage = "?";
		String requestString = "" + "<?xml version=\"1.0\" encoding=\"utf-8\"?>"//
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"//
				+ "	<soap:Body>"//
				+ "		<Gat xmlns=\"http://ws.swl/GatDolgi\">"//
				+ "			<Kod>" + clientNum + "</Kod>"//
				+ "		</Gat>"//
				+ "	</soap:Body>"//
				+ "</soap:Envelope>"//
		;
		//System.out.println(requestString);
		HTTPRequest request = new HTTPRequest(Settings.getInstance().getSERVICE_DOLGI_PO_NKLADNIM());
		request.setTimeOut(1000 * 15);
		try {
			int status = request.Execute(requestString);
			if (status != HttpStatus.SC_OK) {
				//System.out.println("status != HttpStatus.SC_OK");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		String responseString = request.getResponse();
		try {
			Bough bough = Bough.parseXML(responseString);
			mAppInstance.getClientInfo().dolgMessage = bough.child("soap:Body").child("m:GatResponse").child("m:return").value.property.value();
			//StringBuilder sb = new StringBuilder();
			//Bough.dumpXML(sb, bough, "");
			//System.out.println("mAppInstance.getClientInfo().dolgMessage " + mAppInstance.getClientInfo().dolgMessage);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static boolean IsKazanStore(SQLiteDatabase db) {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select hex(SkladOtgruzok) from Consts", null);
			if (cursor.moveToFirst()) {
				if (Hex.encodeHex(cursor.getBlob(0)).compareTo(ISklady.KAZAN_ID) == 0) {
					return true;
				}
			}
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return false;
	}
	public static void UpdateActualNomenclatura(SQLiteDatabase db, String date) {
		String storeSqlQueryPart = " and Baza = " + ISklady.HORECA_ID;
		if (IsKazanStore(db) && DateTimeHelper.SQLDateString(Calendar.getInstance().getTime()).compareTo(date) == 0) {
			storeSqlQueryPart = "";
		}
		String sqlDrop = "drop table if exists Temp_ActualNomenklatura";
		String sqlCreate = "create table Temp_ActualNomenklatura(_id integer primary key asc autoincrement, Nomenklatura blob)";
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, "DROP INDEX if exists Temp_ActualNomenklatura_Nomenklatura");
		DatabaseHelper.executeQueryInTranzaction(db, "CREATE INDEX Temp_ActualNomenklatura_Nomenklatura on Temp_ActualNomenklatura (Nomenklatura)");
		String sqlInsert = "insert into Temp_ActualNomenklatura (Nomenklatura) " //
				+ "select Nomenklatura "// 
				+ "from "// 
				+ "( "// 
				+ " select Nomenklatura, max(Period) srez "//
				+ " from  AdresaPoSkladam  "// 
				+ " where Period <= date() "// 
				+ storeSqlQueryPart// 
				+ " group by Nomenklatura "// 
				+ ") "//
		;
		//System.out.println("UpdateActualNomenclatura " + sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
	}
	public static void UpdateCurKontragenty(SQLiteDatabase db, String kontragentID) {
		String sqlDrop = "drop table if exists Cur_Kontragenty";
		String sqlCreate = "create table Cur_Kontragenty(_id integer primary key asc , [Kontragent] blob, Prioritet integer)";
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		String sqlInsert = "insert into Cur_Kontragenty (Kontragent, Prioritet) " + "select _IDRRef, 0 from Kontragenty where _IDRRef = " + kontragentID;
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		sqlInsert = "insert into Cur_Kontragenty (Kontragent, Prioritet) select GolovnoyKontragent, 1 " + "from Kontragenty where  _IDRRef = " + kontragentID + " and GolovnoyKontragent is not null";
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, "create index if not exists IX_Cur_Kontragenty_Kontragent on Cur_Kontragenty (Kontragent)");
	}
	public static void UpdateTempCenyNomenklatury(SQLiteDatabase db, String kontragentID, String date) {
		String sqlDrop = "drop table if exists Temp_Nomenklatura_Tovary";
		String sqlCreate = "create table Temp_Nomenklatura_Tovary " + "(_id integer primary key asc autoincrement, _IDRRef blob, " + "Artikul TEXT, Naimenovanie TEXT, " + "OsnovnoyProizvoditel blob, ProizvoditelNaimenovanie TEXT, " + "Cena numeric, EdinicyIzmereniyaNaimenovanie TEXT, " + "MinNorma numeric, EdinicyIzmereniyaID blob, Roditel blob, " + "Koephphicient numeric, CenaSoSkidkoy numeric null, Nacenka numeric null, "
				+ "Skidka numeric null, VidSkidki blob null,ProcentSkidkiNacenki numeric null, " + "BasePrice numeric, UpperName TEXT, LastPrice numeric )";
		String sqlInsert = "insert into Temp_Nomenklatura_Tovary"//
				+ " ("//
				+ "_IDRRef, Artikul, Naimenovanie"//
				+ ", OsnovnoyProizvoditel, ProizvoditelNaimenovanie, Cena, "//
				+ "EdinicyIzmereniyaNaimenovanie, MinNorma"//
				+ ", EdinicyIzmereniyaID, Roditel, Koephphicient"//
				+ ",CenaSoSkidkoy, Nacenka, "//
				+ "Skidka, VidSkidki, ProcentSkidkiNacenki"//
				+ ", BasePrice, UpperName, LastPrice"//
				+ ") " //
				+ "select"//
				+ " n._IDRRef [_IDRRef], n.Artikul [Artikul], n.Naimenovanie [Naimenovanie], "//
				+ "n.[OsnovnoyProizvoditel] , ifnull(p.[Naimenovanie], '') [ProizvoditelNaimenovanie], tcsn.Cena, "//
				+ "eho.[Naimenovanie] [EdinicyIzmereniyaNaimenovanie], ifnull(vkn.[Kolichestvo], 1) [MinNorma], "//
				+ "ei._IDRRef [EdinicyIzmereniyaID], n.Roditel [Roditel], ei.Koephphicient [Koephphicient], "//
				+ "tcsn.CenaSoSkidkoy [CenaSoSkidkoy], tcsn.Nacenka [Nacenka], "//
				+ "tcsn.Skidka [Skidka], tcsn.VidSkidki [VidSkidki], tcsn.ProcentSkidkiNacenki [ProcentSkidkiNacenki],"//
				+ "ifnull(tcop.Cena,tcsn.Cena) [BasePrice], n.[UpperName], ifnull(pro.[LastPrice], 0) [LastPrice] "// 
				+ "from Temp_ActualNomenklatura an "//
				+ "inner join "//
				+ "( "//
				+ "select tc.*, max(tc.Period) from TekuschayaCenaSkidkaNomenklatury tc "//
				+ "inner join Cur_PolzovatelyVPodrazselenii c on c.Podrazdelenie = tc.Podrazdelenie "//
				+ "where date(tc.Period) <= date('" + date + "') and tc.Kontragent =  " + kontragentID //
				+ "group by tc.Nomenklatura "//
				+ ") tcsn on tcsn.[Nomenklatura] = an.[Nomenklatura] "//
				+ "inner join Nomenklatura n on n.[_IDRRef] = an.[Nomenklatura] "//
				+ "inner join EdinicyIzmereniya ei on n.EdinicaDlyaOtchetov = ei._IDRRef  "//
				+ "inner join EdinicyIzmereniya eho on n.EdinicaKhraneniyaOstatkov = eho._IDRRef  "//
				+ "left outer join VelichinaKvantovNomenklatury vkn on vkn.Nomenklatura = n.[_IDRRef] "//
				+ "left outer join Proizvoditel p on n.[OsnovnoyProizvoditel] = p._IDRRef "//
				+ " join TekuschieCenyOstatkovPartiy tcop on tcop.Nomenklatura = an.[Nomenklatura] "//
				+ "left join CurProdazhi pro on pro.Nomenklatura = an.Nomenklatura "//
				+ "where n.[Usluga] = x'00' and n.[EtoGruppa] = x'01' " //
				+ " group by an.[Nomenklatura]"//
		;
		//System.out.println("UpdateTempCenyNomenklatury " + sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, "CREATE INDEX if not exists IX_Temp_Nomenklatura_Tovary on Temp_Nomenklatura_Tovary (_IDRRef)");
	}
	public static void UpdateCurTrafiks(SQLiteDatabase db) {
		String sqlDrop = "drop table if exists CurTrafiks";
		String sqlCreate = "create table CurTrafiks " //
				+ "(_id integer primary key asc autoincrement, _IDRRef blob, " //
				+ "Artikul TEXT, Naimenovanie TEXT, "//
				+ "OsnovnoyProizvoditel blob, ProizvoditelNaimenovanie TEXT, " //
				+ "EdinicyIzmereniyaNaimenovanie TEXT, "//
				+ "MinNorma numeric, EdinicyIzmereniyaID blob, Roditel blob, "// 
				+ "Koephphicient numeric, UpperName TEXT )";
		String sqlInsert = "insert into CurTrafiks (_IDRRef, Artikul, Naimenovanie, OsnovnoyProizvoditel, ProizvoditelNaimenovanie, "//
				+ "EdinicyIzmereniyaNaimenovanie, MinNorma, EdinicyIzmereniyaID, Roditel, Koephphicient, UpperName) "//
				+ "select n._IDRRef [_IDRRef], n.Artikul [Artikul], n.Naimenovanie [Naimenovanie], "//
				+ "n.[OsnovnoyProizvoditel] , ifnull(p.[Naimenovanie], '') [ProizvoditelNaimenovanie],"//
				+ "eho.[Naimenovanie] [EdinicyIzmereniyaNaimenovanie], ifnull(vkn.[Kolichestvo], 1) [MinNorma], "//
				+ "ei._IDRRef [EdinicyIzmereniyaID], n.Roditel [Roditel], ei.Koephphicient [Koephphicient], n.[UpperName] "// 
				+ "from Nomenklatura n "//
				+ "inner join EdinicyIzmereniya ei on n.EdinicaDlyaOtchetov = ei._IDRRef  " //
				+ "inner join EdinicyIzmereniya eho on n.EdinicaKhraneniyaOstatkov = eho._IDRRef  "//
				+ "left outer join VelichinaKvantovNomenklatury vkn on vkn.Nomenklatura = n.[_IDRRef] "// 
				+ "left outer join Proizvoditel p on n.[OsnovnoyProizvoditel] = p._IDRRef "//
				+ "where n.[Usluga] = x'00' and n.[EtoGruppa] = x'01' and n.[TovarPodZakaz] = x'01' "// 
				+ "group by n._id";
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
	}
	public static void UpdateCurCenyNomenklaturyMax(SQLiteDatabase db) {
		String sqlDrop = "drop table if exists CurNomenklaturaTovaryMax";
		String sqlCreate = "create table CurNomenklaturaTovaryMax " + "(_id integer primary key asc autoincrement, _IDRRef blob, " + "Naimenovanie nvarchar (160), Artikul nvarchar (50), " + "OsnovnoyProizvoditel blob, ProizvoditelNaimenovanie nvarchar(50), " + "Cena numeric, BasePrice numeric, Skidka numeric, CenaSoSkidkoy numeric, " + "VidSkidki blob, EdinicyIzmereniyaNaimenovanie nvarchar(50), " + "MinNorma numeric, Koephphicient numeric, EdinicyIzmereniyaID blob, "
				+ "Roditel blob, [MinCena] numeric, [MaxCena] numeric, UpperName TEXT, LastPrice numeric  )";
		String sqlInsert = "insert into CurNomenklaturaTovaryMax (_IDRRef, Artikul, Naimenovanie, " + "OsnovnoyProizvoditel, " + "ProizvoditelNaimenovanie, Cena, BasePrice, Skidka, CenaSoSkidkoy, VidSkidki, " + "EdinicyIzmereniyaNaimenovanie, MinNorma, Koephphicient, " + "EdinicyIzmereniyaID, Roditel, MinCena, MaxCena, UpperName, LastPrice) " + "select n.[_IDRRef], n.[Artikul], n.[Naimenovanie], n.[OsnovnoyProizvoditel], " + "n.[ProizvoditelNaimenovanie], n.[Cena], n.[BasePrice], "
				+ "n.Skidka, n.CenaSoSkidkoy, n.VidSkidki, n.[EdinicyIzmereniyaNaimenovanie], " + "n.[MinNorma], n.Koephphicient,  n.[EdinicyIzmereniyaID], n.Roditel, " + "ifnull(cr.[MinCena], 0) [MinCena], ifnull(cr.[MaxCena], 0) [MaxCena], " + "n.[UpperName], n.LastPrice " + "from Temp_Nomenklatura_Tovary n " + "inner join Consts c on (((n.Cena - n.BasePrice)*1.0)/n.BasePrice)* 100.0 >= c.VysokonacenochnyyProcent " + "left join CurNomenklaturaTovaryCR cr on cr.[_IDRRef] = n.[_IDRRef]";
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
	}
	/*
	public static void ____UpdateCurCenyNomenklaturyCR(SQLiteDatabase db) {
		String sqlDrop = "drop table if exists CurNomenklaturaTovaryCR";
		String sqlCreate = "create table CurNomenklaturaTovaryCR " + "(_id integer primary key asc autoincrement, _IDRRef blob, " + "Naimenovanie nvarchar (160), Artikul nvarchar (50), " + "OsnovnoyProizvoditel blob, ProizvoditelNaimenovanie nvarchar(50), " + "Cena numeric, BasePrice numeric, Skidka numeric, CenaSoSkidkoy numeric, " + "VidSkidki blob, EdinicyIzmereniyaNaimenovanie nvarchar(50), " + "MinNorma numeric, Koephphicient numeric, EdinicyIzmereniyaID blob, "
				+ "Roditel blob, [MinCena] numeric, [MaxCena] numeric, " + "UpperName TEXT, LastPrice numeric  )";
		String sqlInsert = "insert into CurNomenklaturaTovaryCR ("//
				+ "_IDRRef, Artikul, Naimenovanie, OsnovnoyProizvoditel"//
				+ ", ProizvoditelNaimenovanie, Cena, BasePrice, Skidka"//
				+ ",CenaSoSkidkoy, VidSkidki,EdinicyIzmereniyaNaimenovanie"//
				+ ", MinNorma, Koephphicient, EdinicyIzmereniyaID,Roditel"//
				+ ", MinCena"//
				+ ", MaxCena"//
				+ ", UpperName, LastPrice"//
				+ ") " //
				+ "select"//
				+ " n.[_IDRRef], n.[Artikul], n.[Naimenovanie], n.[OsnovnoyProizvoditel]  "//
				+ ",n.[ProizvoditelNaimenovanie], n.[Cena], n.[BasePrice],n.Skidka"//
				+ ", n.CenaSoSkidkoy, n.VidSkidki ,  n.[EdinicyIzmereniyaNaimenovanie], "//
				+ "n.[MinNorma],  n.Koephphicient,  n.[EdinicyIzmereniyaID], n.Roditel, "//
				+ "n.[Cena] - (n.BasePrice*((mnp.[Nacenka] * 1.0)/100.0)) [MinCena], "//
				+ "n.[Cena]*(1 + ((c.[MaksNacenkaCenyPraysa] * 1.0)/100.0)) [MaxCena], "//
				+ "n.[UpperName], n.LastPrice  " //
				+ "from Temp_Nomenklatura_Tovary n " //
				+ "inner join Consts c "//
				+ "inner join  "//
				+ "( select"//
				+ " mnp.NomenklaturaProizvoditel_2 [Nomenklatura], max(mnp.Nacenka) Nacenka"//
				+ " from MinimalnyeNacenkiProizvoditeley_1 mnp " + "inner join CurPodrazdelenie cp on mnp.Podrazdelenie = cp._IDRRef " + "inner join " + "(select mnp.Podrazdelenie, min(cp.[Prioretet]) Prioretet " + "from MinimalnyeNacenkiProizvoditeley_1 mnp " + "inner join " + "(select Podrazdelenie, min(Period) Period from MinimalnyeNacenkiProizvoditeley_1 " + "where Period <= date() " + "group by Podrazdelenie "
				+ ") mnpg on mnp.[Period] = mnpg.Period "
				+ "inner join CurPodrazdelenie cp on mnp.Podrazdelenie = cp._IDRRef " + "group by mnp.Podrazdelenie) mnpg on mnpg.[Prioretet] = cp.Prioretet " + "group by mnp.NomenklaturaProizvoditel_2 " + ") mnp on n._IDRref = mnp.Nomenklatura "
				+ "where n.VidSkidki <> x'"+sweetlife.android10.supervisor.Cfg.skidkaIdFixirovannaya+"' or n.VidSkidki is null ";
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, "DROP INDEX if exists IX_CurNomenklaturaTovaryCR_IDRRef");
		DatabaseHelper.executeQueryInTranzaction(db, "CREATE INDEX IX_CurNomenklaturaTovaryCR_IDRRef on CurNomenklaturaTovaryCR (_IDRRef)");
	}
	*/
	public static void UpdateCurCenyNomenklaturyHistory(SQLiteDatabase db) {
		String sqlDrop = "drop table if exists CurCenyNomenklaturyHistory";
		String sqlCreate = "create table CurCenyNomenklaturyHistory " + "(_id integer primary key asc autoincrement, _IDRRef blob, " + "Naimenovanie nvarchar (160), Artikul nvarchar (50), " + "OsnovnoyProizvoditel blob, ProizvoditelNaimenovanie nvarchar(50), " + "Cena numeric, BasePrice numeric, Skidka numeric, CenaSoSkidkoy numeric, VidSkidki blob, EdinicyIzmereniyaNaimenovanie nvarchar(50), "
				+ "MinNorma numeric, Koephphicient numeric, EdinicyIzmereniyaID blob, Roditel blob, [MinCena] numeric, [MaxCena] numeric, " + "UpperName TEXT, DataProdazhi date, LastPrice numeric )";
		String sqlInsert = "insert into CurCenyNomenklaturyHistory (_IDRRef, Artikul, Naimenovanie, OsnovnoyProizvoditel, " + "ProizvoditelNaimenovanie, Cena, BasePrice, Skidka, CenaSoSkidkoy, VidSkidki, " + "EdinicyIzmereniyaNaimenovanie, MinNorma, Koephphicient, EdinicyIzmereniyaID, " + "Roditel, MinCena, MaxCena, UpperName, DataProdazhi, LastPrice ) "// 
				+ "select n.[_IDRRef], n.[Artikul], n.[Naimenovanie], " //
				+ "n.[OsnovnoyProizvoditel] , n.[ProizvoditelNaimenovanie], n.[Cena], n.[BasePrice], "//
				+ "n.Skidka, n.CenaSoSkidkoy, n.VidSkidki ,  n.[EdinicyIzmereniyaNaimenovanie], n.[MinNorma], " //
				+ "n.Koephphicient,  n.[EdinicyIzmereniyaID], n.Roditel, "//
				+ "ifnull(ncr.[MinCena], 0) [MinCena], "// 
				+ "ifnull(ncr.[MaxCena], 0) [MaxCena], n.[UpperName], p.DataProdazhi, p.LastPrice  " //
				+ "from Temp_Nomenklatura_Tovary n  "//
				+ "inner join CurProdazhi p on p.Nomenklatura = n._IDRref "// 
				+ "left join CurNomenklaturaTovaryCR ncr on n.[_IDRref] = ncr.[_IDRref] "//
				+ " order by n.[Naimenovanie] ";
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, "DROP INDEX if exists IX_CurCenyNomenklaturyHistory_IDRRef");
		DatabaseHelper.executeQueryInTranzaction(db, "CREATE INDEX IX_CurCenyNomenklaturyHistory_IDRRef on CurCenyNomenklaturyHistory (_IDRRef)");
	}
	public static void UpdateCurProdazhi(SQLiteDatabase db, String kontragentID) {
		Calendar now = Calendar.getInstance();
		Calendar threeMonthAgo = Calendar.getInstance();
		threeMonthAgo.roll(Calendar.MONTH, -3);
		String sqlDrop = "drop table if exists CurProdazhi";
		String sqlCreate = "create table CurProdazhi " + "(_id integer primary key asc autoincrement, Nomenklatura blob, " + "DataProdazhi date , LastPrice numeric)";
		String sqlInsert = "insert into CurProdazhi (Nomenklatura, DataProdazhi, LastPrice) "//
				+ "select n.[Nomenklatura], prod.Period [DataProdazhi], prod.Stoimost/prod.Kolichestvo "// 
				+ "from Temp_ActualNomenklatura n " //
				+ "inner join " + "(     "//
				+ "select max(Period) Period, Nomenklatura, DogovorKontragenta, Stoimost, Kolichestvo " //
				+ "from Prodazhi pg "// 
				+ "where date(Period) <= date('" + DateTimeHelper.SQLDateString(now.getTime()) + "') "// 
				+ "and date(Period) >= date('" + DateTimeHelper.SQLDateString(threeMonthAgo.getTime()) + "') "//
				+ "group by Nomenklatura, DogovorKontragenta, ZakazPokupatelya_0 "// 
				+ ") prod on n.[Nomenklatura] = prod.Nomenklatura "//
				+ "inner join DogovoryKontragentov_strip dk on prod.DogovorKontragenta = dk._IDRref "// 
				+ "where dk.Vladelec = " + kontragentID;
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, "DROP INDEX if exists IX_CurProdazhi_Kontragent");
		DatabaseHelper.executeQueryInTranzaction(db, "CREATE INDEX IX_CurProdazhi_Kontragent on CurProdazhi (Nomenklatura)");
		DatabaseHelper.executeQueryInTranzaction(db, "DROP INDEX if exists IX_CurProdazhi_Nomenklatura");
		DatabaseHelper.executeQueryInTranzaction(db, "CREATE INDEX IX_CurProdazhi_Nomenklatura on CurProdazhi (Nomenklatura)");
	}
}
