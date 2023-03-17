package sweetlife.android10.database;

import sweetlife.android10.utils.DatabaseHelper;

import android.database.sqlite.SQLiteDatabase;

public class UpdateTempTables {
	public static void update(SQLiteDatabase db, long flags) {
		if (0 != (flags & FLAGS.FLAG_TEMP_ALL)) {
			UpdateAllNomenclatureTables(db);
		}
		if (0 != (flags & FLAGS.FLAG_TEMP_MARSHRUTY_AGENTOV_DAYS)) {
			UpdateMarshrutyAgentovDays(db);
		}
		if (0 != (flags & FLAGS.FLAG_TEMP_MARSHRUTY_AGENTOV_CONTRACTS)) {
			UpdateMarshrutyAgentovContracts(db);
		}
		if (0 != (flags & FLAGS.FLAG_TEMP_LIMITY)) {
			UpdateLimity(db);
		}
		if (0 != (flags & FLAGS.FLAG_TEMP_PODREZDELENIYA)) {
			UpdateCurPodrazseleniya(db);
		}
		/*
		UpdateAllNomenclatureTables(db);
		UpdateMarshrutyAgentovDays(db);
		UpdateMarshrutyAgentovContracts(db);
		UpdateLimity(db);
		UpdateCurPodrazseleniya(db);
		*/
	}
	public static void UpdateAllNomenclatureTables(SQLiteDatabase db) {
		//System.out.println("UpdateAllNomenclatureTables start");
		//sweetlife.horeca.log.LogHelper.debug("UpdateAllNomenclatureTables UPDATE CUR TABLES begin UpdateAllNomenclatureTables " + (new Long(System.currentTimeMillis())).toString());
		UpdateLimity(db);
		//sweetlife.horeca.log.LogHelper.debug("UpdateAllNomenclatureTables UPDATE CUR TABLES end UpdateLimity " + (new Long(System.currentTimeMillis())).toString());
		UpdateMarshrutyAgentovDays(db);
		//sweetlife.horeca.log.LogHelper.debug("UpdateAllNomenclatureTables UPDATE CUR TABLES end UpdateMarshrutyAgentovDays " + (new Long(System.currentTimeMillis())).toString());
		UpdateMarshrutyAgentovContracts(db);
		//sweetlife.horeca.log.LogHelper.debug("UpdateAllNomenclatureTables UPDATE CUR TABLES end UpdateMarshrutyAgentovContracts " + (new Long(System.currentTimeMillis())).toString());
		UpdateCurPodrazseleniya(db);
		//sweetlife.horeca.log.LogHelper.debug("UpdateTablesAfterClientChoose UPDATE CUR TABLES end UpdateCurPodrazseleniya " + (new Long(System.currentTimeMillis())).toString());
		//System.out.println("UpdateAllNomenclatureTables done");
	}
	public static void UpdateMarshrutyAgentovDays(SQLiteDatabase db) {
		DatabaseHelper.executeQueryInTranzaction(db, "DROP INDEX if exists IX_MarshrutyAgentov");
		String sqlDrop = "drop table if exists Cur_MarshrutyAgentov";
		String sqlCreate = "create table Cur_MarshrutyAgentov (_id integer primary key asc ,[Kod] numeric, [Kontragent] blob, [Naimenovanie] nvarchar, [Den1] bit, [Den2] bit, [Den3] bit, [Den4] bit, [Den5] bit, [Den6] bit, " + "[Den7] bit, [Poryadok] numeric null, [GeographicheskayaShirota] numeric, [GeographicheskayaDolgota] numeric)";
		String sqlInsert = "insert into Cur_MarshrutyAgentov("//
				+ "[Kod],[Kontragent], [Naimenovanie], [Den1], [Den2], [Den3], [Den4], [Den5], [Den6], [Poryadok], [GeographicheskayaShirota], [GeographicheskayaDolgota]) "//
				+ "select k.[Kod], k.[_IDRRef], k.[Naimenovanie], "// 
				+ "max(case when ma.[DenNedeli] = 1 then 1 else 0 end) Den1, "//
				+ "max(case when ma.[DenNedeli] = 2 then 1 else 0 end) Den2, "// 
				+ "max(case when ma.[DenNedeli] = 3 then 1 else 0 end) Den3, "//
				+ "max(case when ma.[DenNedeli] = 4 then 1 else 0 end) Den4, "// 
				+ "max(case when ma.[DenNedeli] = 5 then 1 else 0 end) Den5, "//
				+ "max(case when ma.[DenNedeli] = 6 then 1 else 0 end) Den6, "// 
				+ "min(ma.[Poryadok]), k.[GeographicheskayaShirota], k.[GeographicheskayaDolgota] "// 
				+ "from "//
				+ "(select m._id, m.Kontragent [Kontragent], m.[Poryadok] [Poryadok], m.[DenNedeli] [DenNedeli] "// 
				+ "from MarshrutyAgentov m  "//
				+ "left join Cur_PolzovatelyVPodrazselenii cpp on m.Agent = cpp._IDRref "// 
				+ ") ma "// 
				+ "inner join Kontragenty k on k.[_IDRRef] = ma.[Kontragent] "//
				+ "group by ma.[Kontragent]";//, ma.[Poryadok] ";
		//System.out.println("UpdateMarshrutyAgentovDays "+sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, "CREATE INDEX if not exists IX_Cur_MarshrutyAgentov on Cur_MarshrutyAgentov (Kontragent)");
	}
	public static void UpdateMarshrutyAgentovContracts(SQLiteDatabase db) {
		String sqlDrop = "drop table if exists Cur_MarshrutyAgentov_Dogovora";
		String sqlCreate = "create table Cur_MarshrutyAgentov_Dogovora (_id integer primary key asc , [Kontragent] blob, "// 
		+ "[KolichestvoOtkrytykhDogovorov] int, [KolichestvoDogovorov] int)";
		String sqlInsert = "insert into Cur_MarshrutyAgentov_Dogovora([Kontragent], [KolichestvoOtkrytykhDogovorov], [KolichestvoDogovorov]) "//
				+ "\n	select [Kontragent], [KolichestvoOtkrytykhDogovorov], [KolichestvoDogovorov] "// 
				+ "\n		from ( "// 
				+ "\n			select ma.[Kontragent],  "//
				+ "\n		       case when dogKontr.[KolichestvoOtkrytykhDogovorov] is null then 0 else dogKontr.[KolichestvoOtkrytykhDogovorov] end [KolichestvoOtkrytykhDogovorov], "//
				+ "\n		       case when dogKontr.[KolichestvoDogovorov] is null then 0 else dogKontr.[KolichestvoDogovorov] end [KolichestvoDogovorov] "// 
				+ "\n			from MarshrutyAgentov ma "//
				//+ "inner join Cur_PolzovatelyVPodrazselenii cpp on cpp.[_IDRRef] = ma.[Agent] "// 
				+ "\n			inner join Kontragenty k on ma.[Kontragent] = k.[_IDRRef] "//
				+ "\n			left outer join ("//
				+ "\n					select dk.[Vladelec], "// 
				+ "\n						sum(case when dk.PometkaUdaleniya = x'01' or dk.Zakryt = x'01' then 0 else 1 end) [KolichestvoOtkrytykhDogovorov], "//
				+ "\n						count(dk.Zakryt) [KolichestvoDogovorov] "// 
				//+ "\n					from DogovoryKontragentov dk "//
				+ "\n					from DogovoryKontragentov_strip dk "//
				//+ "\n 				join MarshrutyAgentov marshr on dk.[Vladelec]=marshr.[Kontragent] "//
				+ "\n  				group by dk.Vladelec"//
				+ "\n				) dogKontr on ma.[Kontragent] = dogKontr.[Vladelec] "// 
				+ "\n		group by ma.[Kontragent]"//
				+ "\n		) marshrutTp";
		//System.out.println("UpdateMarshrutyAgentovContracts " + sqlInsert);
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, "create index if not exists Cur_MarshrutyAgentov_Dogovora_kontragent on Cur_MarshrutyAgentov_Dogovora(kontragent);");
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
	}
	private static void UpdateLimity(SQLiteDatabase db) {
		String sqlDrop = "drop table if exists Cur_Limity;";
		String sqlCreate = "create table  Cur_Limity (_id integer primary key asc , [SpisokDogovorov] blob, [Limit] numberic, [Otsrochka] numberic) ";
		String sqlInsert = "insert into Cur_Limity([SpisokDogovorov], [Limit], [Otsrochka]) "// 
				+ "select * from (select lf.[SpisokDogovorov], lf.[Limit], lf.[Otsrochka]"//
				+ " from Limity lf inner join  "// 
				+ " ( "// 
				+ "  select _id, SpisokDogovorov, max(Period) "//
				+ "  from Limity "// 
				+ "  where Period <= date() "// 
				+ "  group by SpisokDogovorov " //
				+ "  ) lg on lf._id = lg._id " + " ) l";
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
	}
	private static void UpdateCurPodrazseleniya(SQLiteDatabase db) {
		String sqlDrop = "drop table if exists CurPodrazdelenie";
		String sqlCreate = "create table CurPodrazdelenie(_id integer primary key asc autoincrement, " + "_IDRRef blob, Prioretet integer)";
		DatabaseHelper.executeQueryInTranzaction(db, sqlDrop);
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		sqlCreate = "CREATE INDEX if not exists IX_Cur_Podrazdelenie on CurPodrazdelenie (_IDRRef)";
		DatabaseHelper.executeQueryInTranzaction(db, sqlCreate);
		String sqlInsert = "insert into CurPodrazdelenie " + "(_IDRRef, Prioretet) select Podrazdelenie, 0 from Cur_PolzovatelyVPodrazselenii";
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		sqlInsert = "insert into CurPodrazdelenie (_IDRRef, Prioretet) " //
				+ " select pp.Roditel, cpg.MaxPrioretet + 1 " //
				+ " from CurPodrazdelenie cp "//
				+ " inner join (select Max(Prioretet) MaxPrioretet from CurPodrazdelenie) cpg on cp.[Prioretet] = cpg.MaxPrioretet " + " inner join Podrazdeleniya pp on pp.[_IDRRef] = cp._IDRRef";
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		sqlInsert = "insert into CurPodrazdelenie (_IDRRef, Prioretet) " + "select pp.Roditel, cpg.MaxPrioretet + 1 " + " from CurPodrazdelenie cp " + "inner join (select Max(Prioretet) MaxPrioretet from CurPodrazdelenie) cpg on cp.[Prioretet] = cpg.MaxPrioretet " + "inner join Podrazdeleniya pp on pp.[_IDRRef] = cp._IDRRef";
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		sqlInsert = "insert into CurPodrazdelenie (_IDRRef, Prioretet) " + "select pp.Roditel, cpg.MaxPrioretet + 1 " + " from CurPodrazdelenie cp " + "inner join (select Max(Prioretet) MaxPrioretet from CurPodrazdelenie) cpg on cp.[Prioretet] = cpg.MaxPrioretet " + "inner join Podrazdeleniya pp on pp.[_IDRRef] = cp._IDRRef";
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
		sqlInsert = "insert into CurPodrazdelenie (_IDRRef, Prioretet) " + "select pp.Roditel, cpg.MaxPrioretet + 1 " + " from CurPodrazdelenie cp " + "inner join (select Max(Prioretet) MaxPrioretet from CurPodrazdelenie) cpg on cp.[Prioretet] = cpg.MaxPrioretet " + "inner join Podrazdeleniya pp on pp.[_IDRRef] = cp._IDRRef";
		DatabaseHelper.executeQueryInTranzaction(db, sqlInsert);
	}
}
