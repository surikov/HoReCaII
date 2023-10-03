package sweetlife.android10.utils;

import sweetlife.android10.gps.GPSInfo;
import sweetlife.android10.log.LogHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.*;

public class DatabaseHelper {

	public static void executeQueryInTranzaction(SQLiteDatabase db, String Query) {
		if (Query != null) {
			db.beginTransactionNonExclusive();
			try {
				db.execSQL(Query);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}
	}

	public static long insertInTranzaction(SQLiteDatabase db, String table, ContentValues values) {
		long returnValue = -1;
		if (table != null && values != null && values.size() != 0) {
			db.beginTransactionNonExclusive();
			try {
				returnValue = db.insert(table, null, values);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}
		return returnValue;
	}

	public static long updateInTranzaction(SQLiteDatabase db, String table, ContentValues values, String whereClause, String[] whereArgs) {
		long returnValue = -1;
		if (table != null && values != null && values.size() != 0) {
			db.beginTransactionNonExclusive();
			try {
				returnValue = db.update(table, values, whereClause, whereArgs);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}
		return returnValue;
	}

	public static void forceUpperCase(SQLiteDatabase mDB) {
		boolean more = true;
		int lp = 0;
		int cntr = 0;
		Cursor c;
		String sql;
		while (more) {
			sql = "select _id,Naimenovanie from Nomenklatura limit 100 offset " + (lp * 100) + ";";
			//System.out.println(sql);
			c = mDB.rawQuery(sql, null);
			more = false;
			lp++;
			while (c.moveToNext()) {
				int id = c.getInt(0);
				String n = c.getString(1);
				if (n != null) {
					more = true;
					ContentValues cv = new ContentValues();
					cv.put("upperName", n.toUpperCase());
					mDB.update("Nomenklatura", cv, "_id=" + id, null);
					mDB.update("Nomenklatura_sorted", cv, "_id=" + id, null);
					cntr++;
				}
			}
			c.close();
			//LogHelper.debug("Nomenklatura.upperName " + (lp * 100));
		}
	}

	public static void adjustDataBase(SQLiteDatabase mDB) {
		LogHelper.debug("adjustDataBase");
		//mDB.execSQL("	analyze;	");
		//if(1==1)return;
		/*
		LogHelper.debug("unuse EdinicyIzmereniya");
		mDB.execSQL("	drop table if exists EdinicyIzmereniya;	");
		LogHelper.debug("done unuse EdinicyIzmereniya");
		*/






		/*
        LogHelper.debug("Nomenklatura.upperName start");
        GPSInfo.lockInsert = true;
        int cntr = 0;
        Cursor c;
        boolean more = true;
        int lp = 0;
        while (more) {
            c = mDB.rawQuery("select _id,Naimenovanie from Nomenklatura where ifnull(upperName,\"\")=\"\" limit 100", null);
            more = false;
            lp++;
            while (c.moveToNext()) {
                //LogHelper.debug("Nomenklatura.upperName " + (lp * 100));
                int id = c.getInt(0);
                String n = c.getString(1);
                if (n != null) {
                    more = true;
                    ContentValues cv = new ContentValues();
                    cv.put("upperName", n.toUpperCase());
                    mDB.update("Nomenklatura", cv, "_id=" + id, null);
                    cntr++;
                }
            }
            c.close();
        }
        */


		mDB.execSQL("delete from skidki where DataOkonchaniya<date();");

		LogHelper.debug("CenyNomenklaturyPoPodrazdeleniu tables");
		mDB.execSQL("create table if not exists CenyNomenklaturyPoPodrazdeleniu ("//
				+ "_id integer primary key asc"//
				+ ",[Registrator] blob null"//
				+ ",[Period] date null"//

				+ ",[Nomenklatura] blob null"//
				+ ",[Podrazdelenie] blob null"//
				+ ",[Cena] numeric null"//
				+ ",[Zapret] blob null"//
				+ ");"//
		);
		LogHelper.debug("recept tables");
		mDB.execSQL("create table if not exists Receptii ("//
				+ "_id integer primary key asc"//
				+ ",[_IDRRef] blob null"//
				+ ",[Naimenovanie] text null"//
				+ ");"//
		);
		mDB.execSQL("create table if not exists ReceptiiIngridienty ("//
				+ "_id integer primary key asc"//
				+ ",[_IDRRef] blob null"//
				+ ",[Ingridient] text null"//
				+ ",[Kluch] text null"//
				+ ");"//
		);
		mDB.execSQL("create table if not exists ReceptiiProducty ("//
				+ "_id integer primary key asc"//
				+ ",[_IDRRef] blob null"//
				+ ",[Product] blob null"//
				+ ",[Kluch] text null"//
				+ ");"//
		);
		mDB.execSQL("create index if not exists IX_Receptii_IDRRef on Receptii(_IDRRef);");
		mDB.execSQL("create index if not exists IX_Receptii_naimenovanie on Receptii(naimenovanie);");
		mDB.execSQL("create index if not exists IX_ReceptiiIngridienty_ingridient on ReceptiiIngridienty(ingridient);");
		mDB.execSQL("create index if not exists IX_ReceptiiIngridienty_IDRRef on ReceptiiIngridienty(_IDRRef);");

System.out.println("create RecommendationAndBasket1221");
		mDB.execSQL("create table if not exists RecommendationAndBasket1221 ("//
				+ "_id integer primary key asc"//
				+ ", Kontragent blob null"//
				+ ", Nomenklatura blob null"//
				+ ", Vid  null"//
				+ ");"//
		);
		mDB.execSQL("create index if not exists IX_RecommendationAndBasket1221_Kontragent on RecommendationAndBasket1221(Kontragent);");
		mDB.execSQL("create index if not exists IX_RecommendationAndBasket1221_Nomenklatura on RecommendationAndBasket1221(Nomenklatura);");
		mDB.execSQL("create index if not exists IX_RecommendationAndBasket1221_Vid on RecommendationAndBasket1221(Vid);");


		mDB.execSQL("create table if not exists Zametki ("//
				+ "_id integer primary key asc"//
				+ ",kontragentKod numeric null"//
				+ ",dateCreate date null"//
				+ ",zametka text null"//text
				+ ");"//
		);

		mDB.execSQL("create table if not exists stars ("//
				+ "_id integer primary key asc"//
				+ ",artikul text null"//
				+ ");"//
		);
		mDB.execSQL("create index if not exists IX_stars_artikul on stars(artikul);");

		//
		try {
			mDB.execSQL("alter table Nomenklatura add column Mark blob;");
		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}



		mDB.execSQL("create table if not exists Brand ("//
				+ "_id integer primary key asc"//
				+ ",_idrref blob null"//
				+ ",Naimenovanie text null"//
				+ ",STM blob null"//
				+ ");"//
		);
		mDB.execSQL("create index if not exists IX_Brand_idrref on Brand(_idrref);");
		mDB.execSQL("create index if not exists IX_Brand_stm on Brand(stm);");

		try {
			mDB.execSQL("alter table Nomenklatura add column Brand blob;");
		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}
		mDB.execSQL("create index if not exists IX_Nomenklatura_Brand on Nomenklatura(Brand);");


		/*try {
			mDB.execSQL("alter table MatricaRowsX add column KontragentImya text;");
			mDB.execSQL("alter table MatricaRowsX add column KommentariiRD text;");
			mDB.execSQL("alter table MatricaRowsX add column TOMGG number;");
			mDB.execSQL("alter table MatricaRowsX add column SKUM1 number;");
			mDB.execSQL("alter table MatricaRowsX add column SKUM2 number;");
			mDB.execSQL("alter table MatricaRowsX add column SKUM3 number;");
			mDB.execSQL("alter table MatricaRowsX add column SKUMGG number;");
			mDB.execSQL("alter table MatricaRowsX add column SKUPlan number;");
			mDB.execSQL("alter table MatricaRowsX add column SKUPotencial number;");
		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}*/

		try {
			mDB.execSQL("alter table AnketaKlienta add column osobennostiRejimaRaboty text;");
		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}

		try {
			mDB.execSQL("alter table nomenklatura add column Tegi text;");
		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}

		mDB.execSQL("create index if not exists IX_nomenklatura_Product on nomenklatura(Product);");
		LogHelper.debug("Skidki adjust");
		String skidkiSQL = "create table if not exists Skidki ("//
				+ "_id integer primary key asc"//
				+ ",[Registrator] blob null"//
				+ ",[Period] date null"//
				+ ",[Kontragent] blob null"//
				+ ",[Nomenklatura] blob null"//
				+ ",[VidSkidki] blob null"//
				+ ",[Polzovatel] blob null"//
				+ ",[Podrazdelenie] blob null"//
				+ ",[Text] text null"//
				+ ",[DataNachala] date null"//
				+ ",[DataOkonchaniya] date null"//
				+ ",[VariantRacheta] blob null"//
				+ ",[Znachenie] numeric null"//
				+ ",[Kolichestvo] numeric null"//
				+ ",[VariantPrimeneniyaPoDate] blob null"//
				+ ");"//
				;
		//executeQueryInTranzaction(mDB, skidkiSQL);
		mDB.execSQL(skidkiSQL);
		executeQueryInTranzaction(mDB, "create index if not exists IX_Skidki_Kontragent on Skidki(Kontragent)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_Skidki_Nomenklatura on Skidki(Nomenklatura)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_Skidki_DataOkonchaniya on Skidki(DataOkonchaniya)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_Skidki_DataNachala on Skidki(DataNachala)");

		// LogHelper.debug("Nomenklatura.upperName: " + cntr);

		adjustExchange(mDB, "ЦеныНоменклатурыСклада", "InformationRegisterRecordSet", "CenyNomenklaturySklada", new FieldsDescription()//
						.field("Period", "Period", FieldsDescription.FIELD_DATE)//
						.field("Recorder", "Registrator", FieldsDescription.FIELD_BLOB)//
						.field("Номенклатура", "Nomenklatura", FieldsDescription.FIELD_BLOB)//
						.field("Цена", "Cena", FieldsDescription.FIELD_NUMERIC)//
				, "create table if not exists CenyNomenklaturySklada ("//
						+ "_id integer primary key asc"//
						+ ",[Period] date null"//
						+ ",[Registrator] blob null"//
						+ ",[Nomenklatura] blob null"//
						+ ",[Cena] numeric null"//
						+ ")"//
		);
		adjustExchange(mDB, "ПоказателиЧекЛиста", "CatalogObject", "PokazateliChekLista", new FieldsDescription()//
						.field("Ref", "_IDRRef", FieldsDescription.FIELD_BLOB)//
						.field("Description", "Description", FieldsDescription.FIELD_TEXT)//
						.field("DeletionMark", "DeletionMark", FieldsDescription.FIELD_TEXT)//
						.field("IsFolder", "IsFolder", FieldsDescription.FIELD_TEXT)//
						.field("Code", "Code", FieldsDescription.FIELD_TEXT)//
						.field("Type", "Type", FieldsDescription.FIELD_TEXT)//
						.field("Parent", "Parent", FieldsDescription.FIELD_BLOB)//
						.field("Общий", "common", FieldsDescription.FIELD_TEXT)//
						.field("ПорядокСортировки", "sortOrder", FieldsDescription.FIELD_NUMERIC)//
				, "create table if not exists PokazateliChekLista ("//
						+ "_id integer primary key asc"//
						+ ",[_IDRRef] blob null"//
						+ ",[Description] text null"//
						+ ",[DeletionMark] text null"//
						+ ",[IsFolder] text null"//
						+ ",[Code] text null"//
						+ ",[Type] text null"//
						+ ",[Parent] blob null"//
						+ ",[common] text null"//
						+ ",[sortOrder] numeric null"//
						+ ")"//
		);
		mDB.execSQL("delete from PokazateliChekLista where DeletionMark=x'01';");
		adjustExchange(mDB, "КатегорииОбъектов", "InformationRegisterRecordSet", "KategoryObjectov", new FieldsDescription()//
						.field("Period", "Period", FieldsDescription.FIELD_DATE)//
						.field("Объект", "Nomenklatura", FieldsDescription.FIELD_BLOB)//
						.field("Категория", "kategorya", FieldsDescription.FIELD_BLOB)//
				, "create table if not exists KategoryObjectov ("//
						+ "_id integer primary key asc"//
						+ ",[Nomenklatura] blob null"//
						+ ",[kategorya] blob null"//A69818A90562E07011E4A5333644C864 Маст лист
						+ ")"//
		);
		adjustExchange(mDB, "ТоварыДляДозаказа", "InformationRegisterRecordSet", "TovaryDlyaDozakaza", new FieldsDescription()//
						.field("Комментарий", "kommentariy", FieldsDescription.FIELD_TEXT)//
						.field("Номенклатура", "nomenklatura", FieldsDescription.FIELD_BLOB)//
						.field("Объект", "object", FieldsDescription.FIELD_BLOB)//
				, "create table if not exists TovaryDlyaDozakaza ("//
						+ "_id integer primary key asc"//
						+ ",[object] blob null"//
						+ ",[nomenklatura] blob null"//
						+ ",[kommentariy] text null"//
						+ ")"//
		);
		adjustExchange(mDB, "ЗапретОтгрузокОтветственного", "InformationRegisterRecordSet", "ZapretOtgruzokOtvetsvennogo", new FieldsDescription()//
						.field("ОбъектЗапрета", "ObjectZapreta", FieldsDescription.FIELD_BLOB)//
						.field("Производитель", "Proizvoditel", FieldsDescription.FIELD_BLOB)//
						.field("Ответственный", "Otvetstvenniy", FieldsDescription.FIELD_BLOB)//
				, "create table if not exists ZapretOtgruzokOtvetsvennogo ("//
						+ "_id integer primary key asc"//
						+ ",[ObjectZapreta] blob null"//
						+ ",[Proizvoditel] blob null"//
						+ ",[Otvetstvenniy] blob null"//
						+ ")"//
		);
		adjustExchange(mDB, "ФиксированныеЦены", "InformationRegisterRecordSet", "FiksirovannyeCeny", new FieldsDescription()//
						.field("Period", "Period", FieldsDescription.FIELD_DATE)//
						.field("Recorder", "Registrator", FieldsDescription.FIELD_BLOB)//
						.field("ПолучательСкидки", "PoluchatelSkidki", FieldsDescription.FIELD_BLOB)//
						.field("Номенклатура", "Nomenklatura", FieldsDescription.FIELD_BLOB)//
						.field("ФиксЦена", "FixCena", FieldsDescription.FIELD_NUMERIC)//
						.field("ДатаОкончания", "DataOkonchaniya", FieldsDescription.FIELD_DATE)//
						.field("Обязательства", "Obyazatelstva", FieldsDescription.FIELD_NUMERIC)//
				, "create table if not exists FiksirovannyeCeny ("//
						+ "_id integer primary key asc"//
						+ ",[Period] date null"//
						+ ",[Registrator] blob null"//
						+ ",[PoluchatelSkidki] blob null"//
						+ ",[Nomenklatura] blob null"//
						+ ",[FixCena] numeric null"//
						+ ",[DataOkonchaniya] date null"//
						+ ",[Obyazatelstva] numeric null"//
						+ ")"//
		);
		adjustExchange(mDB, "СкидкаПартнерКарта", "InformationRegisterRecordSet", "SkidkaPartneraKarta", new FieldsDescription()//
						.field("Period", "Period", FieldsDescription.FIELD_DATE)//
						.field("Recorder", "Registrator", FieldsDescription.FIELD_BLOB)//
						.field("ПолучательСкидки", "PoluchatelSkidki", FieldsDescription.FIELD_BLOB)//
						.field("Подразделение", "Podrazdelenie", FieldsDescription.FIELD_BLOB)//
						.field("ПроцентСкидкиНаценки", "ProcentSkidkiNacenki", FieldsDescription.FIELD_NUMERIC)//
						.field("ДатаОкончания", "DataOkonchaniya", FieldsDescription.FIELD_DATE)//
				, "create table if not exists SkidkaPartneraKarta ("//
						+ "_id integer primary key asc"//
						+ ",[Period] date null"//
						+ ",[Registrator] blob null"//
						+ ",[PoluchatelSkidki] blob null"//
						+ ",[Podrazdelenie] blob null"//
						+ ",[ProcentSkidkiNacenki] numeric null"//
						+ ",[DataOkonchaniya] date null"//
						+ ")"//
		);
		adjustExchange(mDB, "НакопительныеСкидки", "InformationRegisterRecordSet", "NakopitelnyeSkidki", new FieldsDescription()//
						.field("Period", "Period", FieldsDescription.FIELD_DATE)//
						.field("Recorder", "Registrator", FieldsDescription.FIELD_BLOB)//
						.field("ПолучательСкидки", "PoluchatelSkidki", FieldsDescription.FIELD_BLOB)//
						.field("Подразделение", "Podrazdelenie", FieldsDescription.FIELD_BLOB)//
						.field("ПроцентСкидкиНаценки", "ProcentSkidkiNacenki", FieldsDescription.FIELD_NUMERIC)//
						.field("ДатаОкончания", "DataOkonchaniya", FieldsDescription.FIELD_DATE)//
				, "create table if not exists NakopitelnyeSkidki ("//
						+ "_id integer primary key asc"//
						+ ",[Period] date null"//
						+ ",[Registrator] blob null"//
						+ ",[PoluchatelSkidki] blob null"//
						+ ",[Podrazdelenie] blob null"//
						+ ",[ProcentSkidkiNacenki] numeric null"//
						+ ",[DataOkonchaniya] date null"//
						+ ")"//
		);
		adjustExchange(mDB, "НаценкиКонтр", "InformationRegisterRecordSet", "NacenkiKontr", new FieldsDescription()//
						.field("Period", "Period", FieldsDescription.FIELD_DATE)//
						.field("Recorder", "Registrator", FieldsDescription.FIELD_BLOB)//
						.field("ПолучательСкидки", "PoluchatelSkidki", FieldsDescription.FIELD_BLOB)//
						.field("Подразделение", "Podrazdelenie", FieldsDescription.FIELD_BLOB)//
						.field("ПроцентСкидкиНаценки", "ProcentSkidkiNacenki", FieldsDescription.FIELD_NUMERIC)//
						.field("ДатаОкончания", "DataOkonchaniya", FieldsDescription.FIELD_DATE)//
				, "create table if not exists NacenkiKontr ("//
						+ "_id integer primary key asc"//
						+ ",[Period] date null"//
						+ ",[Registrator] blob null"//
						+ ",[PoluchatelSkidki] blob null"//
						+ ",[Podrazdelenie] blob null"//
						+ ",[ProcentSkidkiNacenki] numeric null"//
						+ ",[DataOkonchaniya] date null"//
						+ ")"//
		);
		adjustExchange(mDB, "ЗапретСкидокТов", "InformationRegisterRecordSet", "ZapretSkidokTov", new FieldsDescription()//
						.field("Period", "Period", FieldsDescription.FIELD_DATE)//
						.field("Recorder", "Registrator", FieldsDescription.FIELD_BLOB)//
						.field("Номенклатура", "Nomenklatura", FieldsDescription.FIELD_BLOB)//
						.field("Индивидуальные", "Individualnye", FieldsDescription.FIELD_NVARCHAR)//
						.field("Накопительные", "Nokopitelnye", FieldsDescription.FIELD_NVARCHAR)//
						.field("Партнер", "Partner", FieldsDescription.FIELD_NVARCHAR)//
						.field("Разовые", "Razovie", FieldsDescription.FIELD_NVARCHAR)//
						.field("Наценки", "Nacenki", FieldsDescription.FIELD_NVARCHAR)//
				, "create table if not exists ZapretSkidokTov ("//
						+ "_id integer primary key asc"//
						+ ",[Period] date null"//
						+ ",[Registrator] blob null"//
						+ ",[Nomenklatura] blob null"//
						+ ",[Individualnye] nvarchar(5) null"//
						+ ",[Nokopitelnye] nvarchar(5) null"//
						+ ",[Partner] nvarchar(5) null"//
						+ ",[Razovie] nvarchar(5) null"//
						+ ",[Nacenki] nvarchar(5) null"//
						+ ")"//
		);
		adjustExchange(mDB, "ЗапретСкидокПроизв", "InformationRegisterRecordSet", "ZapretSkidokProizv", new FieldsDescription()//
						.field("Period", "Period", FieldsDescription.FIELD_DATE)//
						.field("Recorder", "Registrator", FieldsDescription.FIELD_BLOB)//
						.field("Производитель", "Proizvoditel", FieldsDescription.FIELD_BLOB)//
						.field("Индивидуальные", "Individualnye", FieldsDescription.FIELD_NVARCHAR)//
						.field("Накопительные", "Nokopitelnye", FieldsDescription.FIELD_NVARCHAR)//
						.field("Партнер", "Partner", FieldsDescription.FIELD_NVARCHAR)//
						.field("Разовые", "Razovie", FieldsDescription.FIELD_NVARCHAR)//
						.field("Наценки", "Nacenki", FieldsDescription.FIELD_NVARCHAR)//
				, "create table if not exists ZapretSkidokProizv ("//
						+ "_id integer primary key asc"//
						+ ",[Period] date null"//
						+ ",[Registrator] blob null"//
						+ ",[Proizvoditel] blob null"//
						+ ",[Individualnye] nvarchar(5) null"//
						+ ",[Nokopitelnye] nvarchar(5) null"//
						+ ",[Partner] nvarchar(5) null"//
						+ ",[Razovie] nvarchar(5) null"//
						+ ",[Nacenki] nvarchar(5) null"//
						+ ")"//
		);
		adjustExchange(mDB, "ЗапретыНаОтгрузку", "InformationRegisterRecordSet", "ZapretyNaOtguzku", new FieldsDescription()//
						.field("Period", "Period", FieldsDescription.FIELD_DATE)//
						.field("Recorder", "Registrator", FieldsDescription.FIELD_BLOB)//
						.field("ОбъектЗапрета", "ObjectZapreta", FieldsDescription.FIELD_BLOB)//
						.field("Номенклатура", "Nomenklatura", FieldsDescription.FIELD_BLOB)//
						.field("Производитель", "Proizvoditel", FieldsDescription.FIELD_BLOB)//
						.field("Запрет", "Zapret", FieldsDescription.FIELD_BLOB)//
				, "create table if not exists ZapretyNaOtguzku ("//
						+ "_id integer primary key asc"//
						+ ",[Period] date null"//
						+ ",[Registrator] blob null"//
						+ ",[ObjectZapreta] blob null"//
						+ ",[Nomenklatura] blob null"//
						+ ",[Proizvoditel] blob null"//
						+ ",[Zapret] blob null"//
						+ ")"//
		);
		adjustExchange(mDB, "Владельцы", "", "PriceVladelcy", new FieldsDescription()//
						.field("Владелец", "vladelec", FieldsDescription.FIELD_BLOB)//
						.field("Ref", "ref", FieldsDescription.FIELD_BLOB)//
				, "create table if not exists PriceVladelcy ("//
						+ "_id integer primary key asc"//
						+ ",[vladelec] blob null"//
						+ ",[ref] blob null"//
						+ ")"//
		);
		//System.out.println("<--------------nomenklaturaVidyKuhon");
		String s1 = "create table if not exists nomenklaturaVidyKuhon ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",_IDRRef blob null"//
				+ ",VidKuhni nchar (99) null"//
				+ ")";
		//System.out.println(s1);
		mDB.execSQL(s1);
		s1 = "create table if not exists MinimalnyeNacenki ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",Nomenklatura blob null"//
				+ ",Podrazdelenie blob null"//
				+ ",Zapret blob null"//
				+ ",Nacenka int null"//
				+ ")";
		mDB.execSQL(s1);
		mDB.execSQL("create index if not exists IX_MinimalnyeNacenki_Nomenklatura on MinimalnyeNacenki(Nomenklatura);");
		mDB.execSQL("create index if not exists IX_MinimalnyeNacenki_Podrazdelenie on MinimalnyeNacenki(Podrazdelenie);");
		mDB.execSQL("create index if not exists IX_MinimalnyeNacenki_Zapret on MinimalnyeNacenki(Zapret);");
		mDB.execSQL("create index if not exists IX_MinimalnyeNacenki_Nacenka on MinimalnyeNacenki(Nacenka);");
		//System.out.println("nomenklaturaTipyTorgTochek");
		s1 = "create table if not exists nomenklaturaTipyTorgTochek ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",_IDRRef blob null"//
				+ ",TipTorgTochki blob null"//
				+ ")";
		//System.out.println(s1);
		mDB.execSQL(s1);
		//System.out.println("done vid-------------->");
		mDB.execSQL("create table if not exists ZayavkaNaDegustaciu ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",[otgruzka] date null"//
				+ ",[status] integer null"//
				+ ",[comment] nchar (99) null"//
				+ ",[kontragent] blob null"//
				+ ")");
		mDB.execSQL("create table if not exists ZayavkaNaDegustaciuNomenklatura ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",[parent] integer null"//
				+ ",[nomenklatura] blob null"//
				+ ",[kolichestvo] numeric null"//
				+ ")");
		//mDB.execSQL("drop table ZayavkaNaSpecifikasia");
		//mDB.execSQL("drop table ZayavkaNaSpecifikasiaNomenklatura");
		mDB.execSQL("create table if not exists ZayavkaNaSpecifikasia ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",[createDate] date null"//
				+ ",[fromDate] date null"//
				+ ",[toDate] date null"//
				+ ",[status] integer null"//
				+ ",[comment] nchar (99) null"//
				+ ",[hrc] nchar (99) null"//
				+ ",[kod] nchar (99) null"//
				+ ")");
		mDB.execSQL("create table if not exists ZayavkaNaSpecifikasiaNomenklatura ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", parent integer"//
				+ ", [Artikul] nvarchar (50) null"//
				+ ", [Cena] numeric null"//
				+ ", [Oborot] numeric null"//
				+ ")");
		//KartaKlienta
		mDB.execSQL("create table if not exists KartaKlientaDok2 ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", [Nazvanie] nvarchar(50) null"//
				+ ", [Number] nvarchar(50) null"//
				+ ", [Kommentarii] nvarchar(50) null"//
				+ ", [UIN] nvarchar(50) null"//
				+ ", [vigrujen] integer null"//
				+ ")");
		mDB.execSQL("create index if not exists IX_KartaKlientaDok2_UIN on KartaKlientaDok2(UIN);");
		//
		mDB.execSQL("create table if not exists KartaKlientaNomenklatura2 ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", [UIN] nvarchar(50) null"//
				+ ", [tovar] blob null"//
				+ ", [vigrujen] integer null"//
				+ ")");
		mDB.execSQL("create index if not exists IX_KartaKlientaNomenklatura2_UIN on KartaKlientaNomenklatura2(UIN);");
		mDB.execSQL("create index if not exists IX_KartaKlientaNomenklatura2_tovar on KartaKlientaNomenklatura2(tovar);");
		//
		mDB.execSQL("create table if not exists KartaKlientaKlient2 ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", [UIN] nvarchar(50) null"//
				+ ", [vladelec] blob null"//
				+ ", [vigrujen] integer null"//
				+ ")");
		mDB.execSQL("create index if not exists IX_KartaKlientaKlient2_UIN on KartaKlientaKlient2(UIN);");
		mDB.execSQL("create index if not exists IX_KartaKlientaKlient2_vladelec on KartaKlientaKlient2(vladelec);");
		//mDB.execSQL("drop table ZayavkaPokupatelyaIskhodyaschaya_Smvz");
		mDB.execSQL("create table if not exists ZayavkaPokupatelyaIskhodyaschaya_Smvz ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", parent blob"//
				+ ", [Komentary] nvarchar (250) null"//
				+ ", [Kind] numeric null"//
				+ ",[dostvkaVozvrNakl] real null"//
				+ ")");
		//
        /*
        mDB.execSQL("create table if not exists ZayavkaNaTovariGeroi ("//
                + "_id integer primary key asc autoincrement"//
                + " ,_IDRRef blob null"
                + " ,DataNachala date null"
                + " ,DataOkonchaniya date null"
                + " ,Podrazdelenie blob null"
                + " ,Nomenklatura blob null"
                + " ,Cena numeric null"
                + ")");
        mDB.execSQL("create index if not exists IX_ZayavkaNaTovariGeroi_IDRRef on ZayavkaNaTovariGeroi(_IDRRef);");
        mDB.execSQL("create index if not exists IX_ZayavkaNaTovariGeroi_DataNachala on ZayavkaNaTovariGeroi(DataNachala);");
        mDB.execSQL("create index if not exists IX_ZayavkaNaTovariGeroi_DataOkonchaniya on ZayavkaNaTovariGeroi(DataOkonchaniya);");
        mDB.execSQL("create index if not exists IX_ZayavkaNaTovariGeroi_Podrazdelenie on ZayavkaNaTovariGeroi(Podrazdelenie);");
        mDB.execSQL("create index if not exists IX_ZayavkaNaTovariGeroi_Nomenklatura on ZayavkaNaTovariGeroi(Nomenklatura);");
        */
		mDB.execSQL("create table if not exists TovariGeroi  ("//
				+ "_id integer primary key asc autoincrement"//
				+ " ,Registrator blob null"
				+ " ,DataNachala date null"
				+ " ,DataOkonchaniya date null"
				+ " ,Podrazdelenie blob null"
				+ " ,Nomenklatura blob null"
				+ " ,Cena numeric null"
				+ ")");
		mDB.execSQL("create index if not exists IX_TovariGeroi_Registrator on TovariGeroi(Registrator);");
		mDB.execSQL("create index if not exists IX_TovariGeroi_DataNachala on TovariGeroi(DataNachala);");
		mDB.execSQL("create index if not exists IX_TovariGeroi_DataOkonchaniya on TovariGeroi(DataOkonchaniya);");
		mDB.execSQL("create index if not exists IX_TovariGeroi_Podrazdelenie on TovariGeroi(Podrazdelenie);");
		mDB.execSQL("create index if not exists IX_TovariGeroi_Nomenklatura on TovariGeroi(Nomenklatura);");

		//mDB.execSQL("drop table if exists Price");
		//mDB.execSQL("delete from _RelationsFieldsHelper where _tableId=(select _id from _RelationsTableHelper where [1cName]='Прайс' limit 1)");
		//mDB.execSQL("delete from _RelationsTableHelper where [1cName]='Прайс'");
		adjustExchange(mDB, "Прайс", "DocumentObject", "Price", new FieldsDescription()//
						.field("Ref", "_IDRRef", FieldsDescription.FIELD_BLOB)//
						.field("Number", "Number", FieldsDescription.FIELD_NVARCHAR)//
						.field("Владелец", "Vladelec", FieldsDescription.FIELD_BLOB)//
						.field("Номенклатура", "Nomenklatura", FieldsDescription.FIELD_BLOB)//
						.field("Трафик", "Trafik", FieldsDescription.FIELD_NVARCHAR)//
				, "create table if not exists Price ("//
						+ "_id integer primary key asc"//
						+ ",[_IDRRef] blob null"//
						+ ",[Number] nvarchar(10) null"//
						+ ",[Vladelec] blob null"//
						+ ",[Nomenklatura] blob null"//
						+ ",[Trafik] nvarchar(5) null"//
						+ ")"//
		);
		adjustExchange(mDB, "КартаКлиента", "DocumentObject", "KartaKlienta", new FieldsDescription()//
						.field("Ref", "_IDRRef", FieldsDescription.FIELD_BLOB)//
						.field("Number", "Number", FieldsDescription.FIELD_NVARCHAR)//
						.field("Контрагент", "kontragent", FieldsDescription.FIELD_BLOB)//
						.field("Номенклатура", "Nomenklatura", FieldsDescription.FIELD_BLOB)//
				, "create table if not exists KartaKlienta ("//
						+ "_id integer primary key asc"//
						+ ",[_IDRRef] blob null"//
						+ ",[Number] nvarchar(10) null"//
						+ ",[kontragent] blob null"//
						+ ",[Nomenklatura] blob null"//
						+ ")"//
		);
		//public static void adjustColumn(SQLiteDatabase mDB, String columnsSpec, String s1cName, String Update1cName, String sqliteName, String tableName, int type) {
		//sweetlife.horeca.utils.DatabaseHelper.adjustColumn(mDB, "blob null", "ИспользованиеЛогистическогоМаршрута", "ИспользованиеЛогистическогоМаршрута", "IspolzovanieLogisticheskogoMarshruta", "Podrazdeleniya", 1);
		adjustColumn(mDB, "text null", "ПоСВ", "ПоСВ", "PoSV", "PokazateliChekLista", 6);
		//
		//mDB.execSQL("drop table if exists Matrica");
		//mDB.execSQL("drop table if exists MatricaRows");
		//mDB.execSQL("drop table if exists MatricaSvod");
		//mDB.execSQL("drop table DannieMercury;");
		mDB.execSQL("create table if not exists DannieMercury ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", hrc text"//
				+ ", comment text"//
				+ ", klient text"//
				+ ", guid text"//
				+ ", saved integer"//
				+ ", file text"//
				+ ")");
		mDB.execSQL("create table if not exists MatricaX ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", utverjden integer"//
				+ ", utverjdenPom integer"//
				+ ", data integer"//
				+ ", nomer nchar(99)"//
				+ ", periodDeystvia integer"//
				+ ", kod nchar(99)"//
				+ ", dataZagruzkiMarshruta integer"//
				+ ", otvetstvenniy nchar(99)"//
				+ ", filled integer null"//
				+ ", dataUpload integer null"//
				+ ", descript nchar(99)"//
				+ ")");
		/*try {
			mDB.execSQL("alter table MatricaX add column nacenka number;");
		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}*/
		mDB.execSQL("create table if not exists MatricaRowsX ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", matrica_id integer"//
				+ ", udalit integer"//
				+ ", kontragent integer"//
				+ ", tipTT nchar(99)"//
				+ ", tipOplaty nchar(99)"//
				+ ", pn integer"//
				+ ", vt integer"//
				+ ", sr integer"//
				+ ", ct integer"//
				+ ", pt integer"//
				+ ", sb integer"//
				+ ", potencialTT integer"//
				+ ", tom1 real"//
				+ ", tom2 real"//
				+ ", tom3 real"//
				+ ", vdm1 real"//
				+ ", vdm2 real"//
				+ ", vdm3 real"//
				+ ", nacenka real"//
				+ ", planTysRub real"//
				+ ", uploaded integer null"//
				+ ", dataUpload  integer null"//
				+ ", descript nchar(99)"//
				+ ")");
		mDB.execSQL("create table if not exists MatricaSvodX ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", matrica_id integer"//
				+ ", data integer"//
				+ ", planUtro real"//
				+ ", planLetuchka real"//
				+ ", planItogo real"//
				+ ", planNarItog real"//
				+ ", uploaded integer null"//
				+ ")");
        /*try {
            mDB.execSQL("alter table Prodazhi add column VidSkidki blob;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
        /*try {
            mDB.execSQL("alter table MinimalnyeNacenki add column MinCena numeric;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
        /*try {
            String sql = "delete from FiksirovannyeCeny where DataOkonchaniya<date('now','-1 day')";
            //System.out.println(sql);
            mDB.execSQL(sql);
            //System.out.println("done " + sql);
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
        /*try {
            mDB.execSQL("alter table MarshrutyAgentov add column OTden integer;");
            mDB.execSQL("alter table MarshrutyAgentov add column OTnar integer;");
            mDB.execSQL("alter table MarshrutyAgentov add column nacenka integer;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
        /*try {
            mDB.execSQL("alter table MatricaRowsX add column Pn1 text;");
            mDB.execSQL("alter table MatricaRowsX add column Vt1 text;");
            mDB.execSQL("alter table MatricaRowsX add column Sr1 text;");
            mDB.execSQL("alter table MatricaRowsX add column Ct1 text;");
            mDB.execSQL("alter table MatricaRowsX add column Pt1 text;");
            mDB.execSQL("alter table MatricaRowsX add column Sb1 text;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
        /*try {
            mDB.execSQL("alter table dopmotivaciya add column periodnach text;");
            mDB.execSQL("alter table dopmotivaciya add column periodkon text;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
        /*try {
            mDB.execSQL("alter table DogovoryKontragentov add column TypeCen blob;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
		//mDB.execSQL("update _RelationsFieldsHelper set sqlFieldType=8 where sqliteName='EtoGruppa' and _tableId=(select _id from _RelationsTableHelper where sqliteName='Podrazdeleniya' limit 1))");
		mDB.execSQL("create table if not exists TypeCen ("//
				+ " _IDRRef blob null"//
				+ ", Naimenovanie nchar(99)"//
				+ ")");
		mDB.execSQL("create table if not exists NacenkaKUchetnoiCene ("//
				+ "Period nchar(99)"//
				+ ",Klient blob"//
				+ ", Procent real"//
				+ ")");
		mDB.execSQL("create table if not exists FiksirovannyeCenyUchet ("//
				+ "Registrator blob" //
				+ " ,Period nchar(99)"//
				+ " ,PoluchatelSkidki blob null"//
				+ " ,Nomenklatura blob null"//
				+ " ,FixCena real"//
				+ " ,DataOkonchaniya nchar(99)"//
				+ " ,Obyazatelstva real"//
				+ ")");
		mDB.execSQL("create table if not exists LimitiList ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", Podrazdelenie nchar(99)"//
				+ ", Data integer"//
				+ ", DataIzm integer"//
				+ ", Otvetstvenniy nchar(99)"//
				+ ", Kommentarii nchar(99)"//
				+ ")");
		mDB.execSQL("create table if not exists LimitiDogovor ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", LimitiList integer"//
				+ ", Gruppa nchar(99)"//
				+ ", TO1 real"//
				+ ", TO2 real"//
				+ ", TOPlan real"//
				+ ", Otsrochka real"//
				+ ", OtsrochkaPlan real"//
				+ ", LimitValue real"//
				+ ", LimitSV real"//
				+ ", LimitPlan real"//
				+ ", KommentariiSv nchar(99)"//
				+ ", KommentariiFin nchar(99)"//
				+ ", Poruchitelstvo real"//
				+ ", LimitPoDogovoru real"//
				+ ", Territiriya nchar(99)"//
				+ ", LimitRachet real"//
				+ ", PlanTO real"//
				+ ", KommentariiTP nchar(99)"//
				+ ")");
		//mDB.execSQL("drop table if exists ZayavkaNaKlienta");
		mDB.execSQL("create table if not exists AnketaKlienta ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",vigruzhen integer"//
				+ ", Podrazdelenie nchar(99)"//
				+ ", UrFizLico integer"//
				+ ", FizOther nchar(99)"//
				+ ", shirota real"//
				+ ", dolgota real"//
				+ ", Naimenovanie nchar(99)"//
				+ ", OsnovnoiKlientTT nchar(99)"//
				+ ", INN nchar(99)"//
				+ ", KPP nchar(99)"//
				+ ", UrAdres nchar(99)"//
				+ ", BIK nchar(99)"//
				+ ", NomerScheta nchar(99)"//
				+ ", PotencialniyKlient nchar(99)"//
				+ ", Viveska nchar(99)"//
				+ ", FaktAdres nchar(99)"//
				+ ", AdresDostavki nchar(99)"//
				+ ", Bolshegruz integer"//
				+ ", KolichestvoMest nchar(99)"//
				+ ", VidKuhni nchar(99)"//
				+ ", TipTT integer"//
				+ ", VremyaRaboti nchar(99)"//
				+ ")");
        /*try {
            mDB.execSQL("alter table AnketaKlienta add column NuzhenPropusk integer;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
		try {
			mDB.execSQL("alter table LimitiList add column plan real;");
			mDB.execSQL("alter table LimitiList add column potencial real;");
		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}
		try {
			mDB.execSQL("alter table Podrazdeleniya add column NeIspolzovatCR blob;");
		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}
		mDB.execSQL("create table if not exists AnketaKlientaContacts ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",anketaId integer"//
				+ ", FIO nchar(99)"//
				+ ", Telefon nchar(99)"//
				+ ", Dolztost nchar(99)"//
				+ ", Rol nchar(99)"//
				+ ")");
		mDB.execSQL("create table if not exists Zapiski ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", nomer nchar(99) null"//
				+ ", data integer"//
				+ ", kontragent blob null"//
				+ ", naimenovanie nchar(99)"//
				+ ", nalbeznal integer"//
				+ ", prichina nchar(99)"//
				+ ", nashklient integer"//
				+ ", tovarooborot nchar(99)"//
				+ ", limity nchar(99)"//
				+ ", otsrochka nchar(99)"//
				+ ", bonusy nchar(99)"//
				+ ", minrazmerzakaza nchar(99)"//
				+ ", poruchitelstvo nchar(99)"//
				+ ", prochee nchar(99)"//
				+ ")");
		mDB.execSQL("create table if not exists ZapiskiFiles ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", zapiska integer"//
				+ ", kind integer"//
				+ ", path nchar(99)"//
				+ ")");
        /*try {
            mDB.execSQL("alter table ZayavkaPokupatelyaIskhodyaschaya_Traphiki add column vs integer;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
		mDB.execSQL("create table if not exists PlanPolevihObucheniy ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",nomer text"//
				+ ",periodDeystvia integer"//
				+ ",otvetstvenniy text"//
				+ ",poyasnenie text"//
				+ ");"//
		);
		mDB.execSQL("create table if not exists PlanPolevihObucheniyStroki ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",planPolevihObucheniy_id integer"//
				+ ",data integer"//
				+ ",podrazdelenie text"//
				+ ",kommentariy text"//
				+ ");"//
		);
		mDB.execSQL("create table if not exists SyncLog ("//
				+ "_id integer primary key asc autoincrement"//
				+ ",startTime text"//
				+ ",endTime text"//
				+ ",description text"//
				+ ");"//
		);
		mDB.execSQL("create table if not exists PokazateliChekListaItem ("//
				+ " _id integer primary key asc"//
				+ " ,doc_id integer null"//
				+ " ,Pokazatel_id integer null"//
				+ " ,Znachenie text null"//
				+ " ,Prim text null"//
				+ " ,Kontragent text null"//
				+ " );"//
		);
		mDB.execSQL("create table if not exists PokazateliChekListaDoc ("//
				+ " _id integer primary key asc"//
				+ " ,Podr text null"//
				+ " ,Otvetstvennii text null"//
				+ " ,Data integer null"//
				+ " ,SSS text null"//
				+ " ,ODR text null"//
				+ " ,NSV text null"//
				+ " ,VSP text null"//
				+ " ,vigruzhen integer null"//
				+ " );"//
		);
		mDB.execSQL("create table if not exists AddKlientDayMarshrut ("//
				+ " _id integer primary key asc"//
				+ " ,territoryKod text null"//
				+ " ,klientKod text null"//
				+ " ,marchrutDate date null"//
				+ " ,editDate date null"//
				+ " ,uploadDate date null"//
				+ " ,ponedelnik integer null"//
				+ " ,vtornik integer null"//
				+ " ,sreda integer null"//
				+ " ,chetverg integer null"//
				+ " ,pyatnisa integer null"//
				+ " ,subbota integer null"//
				+ " );"//
		);
		/*
		create table if not exists PlanPolevihObucheniy (
				_id integer primary key asc autoincrement
				,nomer text
				,periodDeystvia integer
				,otvetstvenniy text
				,poyasnenie text
			);
			create table if not exists PlanPolevihObucheniyStroki (
				_id integer primary key asc autoincrement
				,planPolevihObucheniy_id integer
				,data integer
				,podrazdelenie text
				,kommentariy text
			);
			create index if not exists IX_PlanPolevihObucheniy_periodDeystvia on PlanPolevihObucheniy(periodDeystvia);
			create index if not exists IX_PlanPolevihObucheniy_otvetstvenniy on PlanPolevihObucheniy(otvetstvenniy);

			create index if not exists IX_PlanPolevihObucheniyStroki_planPolevihObucheniy_id on PlanPolevihObucheniyStroki(planPolevihObucheniy_id);
			create index if not exists IX_PlanPolevihObucheniyStroki_podrazdelenie on PlanPolevihObucheniyStroki(podrazdelenie);
			create index if not exists IX_PlanPolevihObucheniyStroki_data on PlanPolevihObucheniyStroki(data);
			*/
        /*try {
            mDB.execSQL("alter table AnketaKlienta add column AdresLoc nchar(199);");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            //t.printStackTrace();
        }*/
        /*try {
            mDB.execSQL("alter table PokazateliChekLista add column PoRT text;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            //t.printStackTrace();
        }*/
		/*try {
			mDB.execSQL("alter table MatricaRowsX add column vrrab nchar(199);");
			mDB.execSQL("alter table MatricaRowsX add column email nchar(199);");
		}
		catch (Throwable t) {
			System.out.println(t.getMessage());
		}
		*/
		/*mDB.execSQL("create table if not exists PoKassamDlyaTPAll ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", vigruzhen integer null"//
				+ ", sozdan integer null"//
				+ ", tp blob null"//
				+ ")");*/
		mDB.execSQL("create table if not exists PoKassamDlyaTP("//
				+ "_id integer primary key asc autoincrement"//
				+ ", vigruzhen integer null"//
				+ ", sozdan integer null"//
				+ ", tp blob null"//
				+ ", Summa real null"//
				+ ", Klient nchar(99)"//
				+ ", Dogovor nchar(99)"//
				+ ", Doverennost nchar(99)"//
				+ ", NomerDoverennosti nchar(99)"//
				+ ", NomerNuk nchar(99)"//
				+ ", DataNuk integer null"//
				+ ")");
		mDB.execSQL("create table if not exists ZayavkaVozmehenie ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", nomer string"//
				+ ", data numeric"//
				+ ", kod integer"//
				+ ", uploaded integer"//
				+ ")");
		//mDB.execSQL("drop table IndividualnieSkidki;");
		mDB.execSQL("create table if not exists IndividualnieSkidki ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", Registrator  blob null"//
				+ ", Period date null"//
				+ ", Kontragent blob null"//
				+ ", Nomenklatura blob null"//
				+ ", Cena numeric null"//
				+ ", DataOkonchaniya date null"//
				+ ")");
        /*
        delete from FlagmanTovar where SegmentKod='000000006' and Articul='48396';
        insert into FlagmanTovar(SegmentKod,Articul) values ('000000006','48396');

        delete from PlanSegmentov where TerritoryKod='уфф99' and SegmentKod='000000027';
        insert into PlanSegmentov(TerritoryKod,SegmentKod,SegmentName,Plan) values ('уфф99','000000027','Икра',3);
        */
		mDB.execSQL("create table if not exists FlagmanTovar ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", SegmentKod  text null"//
				+ ", Articul text null"//
				+ ")");
		mDB.execSQL("create index if not exists IX_FlagmanTovar_SegmentKod on FlagmanTovar(SegmentKod);");
		mDB.execSQL("create index if not exists IX_FlagmanTovar_Articul on FlagmanTovar(Articul);");
		mDB.execSQL("create table if not exists PlanSegmentov ("//
				+ "_id integer primary key asc autoincrement"//
				+ ", TerritoryKod  text null"//
				+ ", SegmentKod text null"//
				+ ", SegmentName text null"//
				+ ", [Plan] text null"//
				+ ")");
		mDB.execSQL("create index if not exists IX_PlanSegmentov_TerritoryKod on PlanSegmentov(TerritoryKod);");
		mDB.execSQL("create index if not exists IX_PlanSegmentov_SegmentKod on PlanSegmentov(SegmentKod);");
		mDB.execSQL("create index if not exists IX_PlanSegmentov_SegmentName on PlanSegmentov(SegmentName);");
		System.out.println("---===AssortimentNaSklade");
		mDB.execSQL("CREATE TABLE if not exists \"AssortimentNaSklade\" ("
				+ " _id integer primary key asc autoincrement"
				+ " ,\"NomenklaturaPostavshhik\"	blob null"
				+ " ,\"KontragentPodrazdelenie\"	blob null"
				+ " ,\"Zapret\"	blob null"
				+ " ,\"Trafic\"	blob null"
				+ " )");
		mDB.execSQL("create index if not exists IX_AssortimentNaSklade_NomenklaturaPostavshhik on AssortimentNaSklade(NomenklaturaPostavshhik);");
		mDB.execSQL("create index if not exists IX_AssortimentNaSklade_KontragentPodrazdelenie on AssortimentNaSklade(KontragentPodrazdelenie);");
		mDB.execSQL("create index if not exists IX_AssortimentNaSklade_Zapret on AssortimentNaSklade(Zapret);");
		mDB.execSQL("create index if not exists IX_AssortimentNaSklade_Trafic on AssortimentNaSklade(Trafic);");

		try {
			mDB.execSQL("alter table MatricaRowsX add column PlanSTM number;");
			mDB.execSQL("alter table MatricaSvodX add column PlanSTM number;");
		}
		catch (Throwable t) {
			System.out.println(t.getMessage());
		}
		//delete from Assortiment where NomenklaturaPostavshhik=X'BFE050505450303011DA6D9397ECB611' and KontragentPodrazdelenie=X'B2E63F309CD622334A63258B97558954';
		//insert into Assortiment(NomenklaturaPostavshhik,KontragentPodrazdelenie,Zapret,Trafic) values (X'BFE050505450303011DA6D9397ECB611',X'B2E63F309CD622334A63258B97558954',X'00',X'00');

		/*try {
			mDB.execSQL("alter table PoKassamDlyaTP add column textSumma nchar(99);");
			
		}
		catch (Throwable t) {
			System.out.println(t.getMessage());
		}*/
       /* try {
            mDB.execSQL("alter table Nomenklatura add column skladEdIzm text;");
            mDB.execSQL("alter table Nomenklatura add column skladEdVes real;");
            mDB.execSQL("alter table Nomenklatura add column skladEdKoef real;");
            mDB.execSQL("alter table Nomenklatura add column otchEdIzm text;");
            mDB.execSQL("alter table Nomenklatura add column otchdEdVes real;");
            mDB.execSQL("alter table Nomenklatura add column otchEdKoef real;");
            mDB.execSQL("alter table Nomenklatura add column kvant real;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
		//System.out.println("dopmotivaciya");
		mDB.execSQL("create table if not exists dopmotivaciya ("//
				+ " _id integer primary key autoincrement not null"//
				+ " ,_IDRRef blob null"//
				+ " ,nomenklatura blob null"//
				+ " ,nazvanie text null"//
				+ " ,period date null"//
				+ " ,Podrazdelenie blob null"//
				+ " );"//
		);
		mDB.execSQL("create index if not exists IX_dopmotivaciya_IDRRef   on dopmotivaciya (_IDRRef);");
		mDB.execSQL("create index if not exists IX_dopmotivaciya_nomenklatura   on dopmotivaciya (nomenklatura);");
		mDB.execSQL("create index if not exists IX_dopmotivaciya_period   on dopmotivaciya (period);");
		mDB.execSQL("create index if not exists IX_dopmotivaciya_Podrazdelenie   on dopmotivaciya (Podrazdelenie);");
		//System.out.println("change prices");
		mDB.execSQL("create table if not exists price_doc ("//
				+ " _IDRRef blob null"//
				+ " ,udalen blob null"//
				+ " ,data date null"//
				+ " ,nomer text null"//
				+ " ,nazvanie text null"//
				+ " ,komentariy text null"//
				+ " );");
		mDB.execSQL("create index if not exists IX_price_doc_UIN on price_doc(_IDRRef);");
		mDB.execSQL("create index if not exists IX_price_doc_udalen on price_doc(udalen);");
		mDB.execSQL("create table if not exists price_owner ("//
				+ " klient_podrazd blob null"//
				+ " ,price_doc blob null"//
				+ " );");
		mDB.execSQL("create index if not exists IX_price_owner_klient_podrazd on price_owner(klient_podrazd);");
		mDB.execSQL("create index if not exists IX_price_owner_price_doc on price_owner(price_doc);");
		mDB.execSQL("create table if not exists price_artikul ("//
				+ " nomenklatura blob null"//
				+ " ,price_doc blob null"//
				+ " ,trafik blob null"//
				+ " );");
		mDB.execSQL("create index if not exists IX_price_artikul_nomenklatura on price_artikul(nomenklatura);");
		mDB.execSQL("create index if not exists IX_price_artikul_price_doc on price_artikul(price_doc);");
		mDB.execSQL("create index if not exists IX_price_artikul_trafik on price_artikul(trafik);");
		//System.out.println("done change prices");
		mDB.execSQL("create table if not exists TipyTorgovihTochek ("//
				+ " _IDRRef blob null"//
				+ " ,Naimenovanie text null"//
				+ " ,DeletionMark blob null"//
				+ " ,Kod text null"//
				+ " );");
		mDB.execSQL("create index if not exists IX_TipyTorgovihTochek_IDRRef on TipyTorgovihTochek(_IDRRef);");


		mDB.execSQL("create table if not exists Sklady ("//
				+ " _IDRRef blob null"//
				+ " ,Naimenovanie text null"//
				+ " ,PometkaUdaleniya blob null"//
				+ " ,Kod text null"//
				+ " );");

		mDB.execSQL("create table if not exists atricle_count ("//
				+ "artikul text"//
				+ ", cnt integer"//
				+ ")");

/*
        try {
            mDB.execSQL("alter table AnketaKlienta add column Komissioner int;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            //t.printStackTrace();
        }*/
        /*try {
            mDB.execSQL("alter table AnketaKlienta add column formatkuhni text;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            //t.printStackTrace();
        }*/
        /*try {
            mDB.execSQL("alter table AnketaKlienta add column predok text;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            //t.printStackTrace();
        }*/
        /*try {
            mDB.execSQL("alter table MarshrutyAgentov add column nedelya integer;");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
		try {
			//System.out.println("clear dopmotivaciya");
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			String sql = "delete from dopmotivaciya where date(periodkon)<date('" + sdf.format(Calendar.getInstance().getTime()) + "')";
			//System.out.println(sql);
			mDB.execSQL(sql);
/*
			String polzovatelID = ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr();
			String dataOtgruzki = DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
			System.out.println(polzovatelID+'/'+dataOtgruzki);
			sweetlife.horeca.database.nomenclature.Request_NomenclatureBase.cacheTop20( polzovatelID,  dataOtgruzki);
			*/
		} catch (Throwable t) {
			System.out.println(t.getMessage());
		}
		//System.out.println("marshrut *********************************");
		// try {
		//   mDB.execSQL("alter table MarshrutyAgentov add column nedelya integer;");
		//FieldsDescription fields=new FieldsDescription().field("Неделя", "nedelya", FieldsDescription.FIELD_NUMERIC);
		//_RelationsTableHelper
			/*String sql="select _id as id from _RelationsTableHelper where sqliteName='MarshrutyAgentov';";
			System.out.println(sql);
			Bough dat=Auxiliary.fromCursor(mDB.rawQuery(sql, null));
			System.out.println(dat.dumpXML());
			long id=(long)Numeric.string2double(dat.child("row").child("id").value.property.value());
			System.out.println(id);
			fields.insert(id, mDB);*/
		// } catch (Throwable t) {
		//System.out.println(t.getMessage());
		//}
        /*try {
            String sql = "select _id as id from _RelationsTableHelper where sqliteName='MarshrutyAgentov';";
            //System.out.println(sql);
            Bough dat = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
            //System.out.println(dat.dumpXML());
            String tableId = dat.child("row").child("id").value.property.value();
            sql = "select _id as id from _RelationsFieldsHelper where sqliteName='nedelya';";
            //System.out.println(sql);
            dat = Auxiliary.fromCursor(mDB.rawQuery(sql, null));
            //System.out.println(dat.dumpXML());
            String fieldId = dat.child("row").child("id").value.property.value();
            //System.out.println(fieldId);
            if (fieldId.trim().length() < 1) {
                sql = "insert into _RelationsFieldsHelper"//
                        + " ([1cName],[Update1cName],[sqliteName],[_tableId],[sqlFieldType])"//
                        + " values ("//
                        + "'Неделя','Неделя','nedelya'," + tableId + ",4"//
                        + ") ";
                //System.out.println(sql);
                mDB.execSQL(sql);
            }
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }*/
		//System.out.println("done *********************************");
		executeQueryInTranzaction(mDB, "create index if not exists IX_SyncLog_endTime  on SyncLog(endTime);");
		executeQueryInTranzaction(mDB, "create index if not exists IX_PlanPolevihObucheniy_periodDeystvia on PlanPolevihObucheniy(periodDeystvia);");
		executeQueryInTranzaction(mDB, "create index if not exists IX_PlanPolevihObucheniy_otvetstvenniy on PlanPolevihObucheniy(otvetstvenniy);");
		executeQueryInTranzaction(mDB, "create index if not exists IX_PlanPolevihObucheniyStroki_planPolevihObucheniy_id on PlanPolevihObucheniyStroki(planPolevihObucheniy_id);");
		executeQueryInTranzaction(mDB, "create index if not exists IX_PlanPolevihObucheniyStroki_podrazdelenie on PlanPolevihObucheniyStroki(podrazdelenie);");
		executeQueryInTranzaction(mDB, "create index if not exists IX_PlanPolevihObucheniyStroki_data on PlanPolevihObucheniyStroki(data);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaNaDegustaciu_status on ZayavkaNaDegustaciu(status);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaNaDegustaciu_otgruzka on ZayavkaNaDegustaciu(otgruzka);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaNaDegustaciu_kontragent on ZayavkaNaDegustaciu(kontragent);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaNaDegustaciuNomenklatura_parent on ZayavkaNaDegustaciuNomenklatura(parent);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaNaDegustaciuNomenklatura_nomenklatura on ZayavkaNaDegustaciuNomenklatura(nomenklatura);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_PriceVladelcy_ref on PriceVladelcy(ref);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_PriceVladelcy_vladelec on PriceVladelcy(vladelec);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_AnketaKlientaContacts_anketaId on AnketaKlientaContacts(anketaId);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_PoKassamDlyaTP_Klient on PoKassamDlyaTP(Klient);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_PoKassamDlyaTP_Klient on PoKassamDlyaTP(sozdan);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_PoKassamDlyaTP_tp on PoKassamDlyaTP(tp);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZapretOtgruzokOtvetsvennogo_ObjectZapreta on ZapretOtgruzokOtvetsvennogo(ObjectZapreta);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZapretOtgruzokOtvetsvennogo_Proizvoditel on ZapretOtgruzokOtvetsvennogo(Proizvoditel);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZapretOtgruzokOtvetsvennogo_Otvetstvenniy on ZapretOtgruzokOtvetsvennogo(Otvetstvenniy);");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_AnketaKlienta_vigruzhen on AnketaKlienta(vigruzhen)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_AnketaKlienta_Naimenovanie on AnketaKlienta(Naimenovanie)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_AnketaKlienta_Podrazdelenie on AnketaKlienta(Podrazdelenie)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaPokupatelyaIskhodyaschaya_Smvz_dostvkaVozvrNakl on ZayavkaPokupatelyaIskhodyaschaya_Smvz(dostvkaVozvrNakl)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaPokupatelyaIskhodyaschaya_Smvz_parent on ZayavkaPokupatelyaIskhodyaschaya_Smvz(parent)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaPokupatelyaIskhodyaschaya_Smvz_kind on ZayavkaPokupatelyaIskhodyaschaya_Smvz(kind)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_LimitiList_Data on LimitiList(Data)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_LimitiList_Podrazdelenie on LimitiList(Podrazdelenie)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_LimitiDogovor_Gruppa on LimitiDogovor(Gruppa)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_LimitiDogovor_LimitiList on LimitiDogovor(LimitiList)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Matrica_periodDeystvia on MatricaX(periodDeystvia)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Matrica_kod on MatricaX(kod)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Matrica_dataZagruzkiMarshruta on MatricaX(dataZagruzkiMarshruta)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_MatricaRows_matrica_id on MatricaRowsX(matrica_id)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_MatricaSvod_matrica_id on MatricaSvodX(matrica_id)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Price_IDRRef on Price(_IDRRef)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Price_Number on Price(Number)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Price_Vladelec on Price(Vladelec)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Price_Nomenklatura on Price(Nomenklatura)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Price_Trafik on Price(Trafik)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Nomenklatura_UpperName on Nomenklatura(UpperName)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Nomenklatura_Naimenovanie on Nomenklatura(Naimenovanie)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Nomenklatura_TovarPodZakaz on Nomenklatura(TovarPodZakaz)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Nomenklatura_Roditel on Nomenklatura(Roditel)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Nomenklatura_EtoGruppa on Nomenklatura(EtoGruppa)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Nomenklatura_artikul on Nomenklatura(artikul)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_CenyNomenklaturySklada_Cena on CenyNomenklaturySklada(Cena)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_CenyNomenklaturySklada_Period on CenyNomenklaturySklada(Period)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_TekuschieCenyOstatkovPartiy_Cena on TekuschieCenyOstatkovPartiy(Cena)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_MinimalnyeNacenkiProizvoditeley_1_Period on MinimalnyeNacenkiProizvoditeley_1(Period)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_PlanovyeNacenkiNaMesyac_Period on PlanovyeNacenkiNaMesyac(Period)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_PlanovyeNacenkiNaMesyac_DataOkonchaniya on PlanovyeNacenkiNaMesyac(DataOkonchaniya)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_PlanovyeNacenkiNaMesyac_Klient on PlanovyeNacenkiNaMesyac(Klient)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_NacenkiKontr_Period on NacenkiKontr(Period)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_NacenkiKontr_ProcentSkidkiNacenki on NacenkiKontr(ProcentSkidkiNacenki)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_NacenkiKontr_PoluchatelSkidki on NacenkiKontr(PoluchatelSkidki)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_NacenkiKontr_DataOkonchaniya on NacenkiKontr(DataOkonchaniya)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZapretSkidokTov_Period on ZapretSkidokTov(Period)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZapretSkidokTov_Nomenklatura on ZapretSkidokTov(Nomenklatura)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZapretSkidokProizv_Period on ZapretSkidokProizv(Period)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZapretSkidokProizv_Proizvoditel on ZapretSkidokProizv(Proizvoditel)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_FiksirovannyeCeny_FixCena on FiksirovannyeCeny(FixCena)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_FiksirovannyeCeny_Period on FiksirovannyeCeny(Period)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_FiksirovannyeCeny_PoluchatelSkidki on FiksirovannyeCeny(PoluchatelSkidki)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_FiksirovannyeCeny_DataOkonchaniya on FiksirovannyeCeny(DataOkonchaniya)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_FiksirovannyeCeny_Nomenklatura on FiksirovannyeCeny(Nomenklatura)");
		//executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_FiksirovannyeCeny_Nomenklatura on FiksirovannyeCeny(Nomenklatura)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_NakopitelnyeSkidki_Period on NakopitelnyeSkidki(Period)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_NakopitelnyeSkidki_ProcentSkidkiNacenki on NakopitelnyeSkidki(ProcentSkidkiNacenki)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_NakopitelnyeSkidki_PoluchatelSkidki on NakopitelnyeSkidki(PoluchatelSkidki)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_AdresaPoSkladam_Sklad on AdresaPoSkladam(Sklad)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Polzovateli_idrref on Polzovateli(_idrref)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Polzovateli_podrazdelenie on Polzovateli(podrazdelenie)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists [IX_Podrazdeleniya_roditel] ON [Podrazdeleniya] ([roditel])");
		//dogovorykontragentov._idrref
		//Prodazhi.dogovorkontragenta
		//Prodazhi.period
		//main.Nomenklatura
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists [IX_Dogovorykontragentov_idrref] ON [dogovorykontragentov] ([_idrref])");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists [IX_Prodazhi_dogovorkontragenta] ON [Prodazhi] ([dogovorkontragenta])");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists [IX_Prodazhi_Nomenklatura] ON [Prodazhi] ([Nomenklatura])");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists [IX_Prodazhi_period] ON [Prodazhi] ([period])");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_DogovoryKontragentov_zakryt on DogovoryKontragentov(zakryt)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_DogovoryKontragentov_GruppaDogovorov on DogovoryKontragentov(GruppaDogovorov)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Kontragenty_Kod on Kontragenty(Kod)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Vizits_Client on Vizits(Client)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_Vizits_BeginDate on Vizits(BeginDate)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaNaSpecifikasia_kod on ZayavkaNaSpecifikasia(kod)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaNaSpecifikasia_toDate on ZayavkaNaSpecifikasia(toDate)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaNaSpecifikasia_fromDate on ZayavkaNaSpecifikasia(fromDate)");
		//ZayavkaNaSpecifikasiaNomenklatura
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaNaSpecifikasiaNomenklatura_artikul on ZayavkaNaSpecifikasiaNomenklatura(artikul)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZayavkaNaSpecifikasiaNomenklatura_parent on ZayavkaNaSpecifikasiaNomenklatura(parent)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZapretyNaOtguzku_nomenklatura on ZapretyNaOtguzku(nomenklatura)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZapretyNaOtguzku_objectzapreta on ZapretyNaOtguzku(objectzapreta)");
		executeQueryInTranzaction(mDB, "CREATE INDEX if not exists IX_ZapretyNaOtguzku_period on ZapretyNaOtguzku(period)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_SkidkaPartneraKarta_ProcentSkidkiNacenki on SkidkaPartneraKarta(ProcentSkidkiNacenki)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_SkidkaPartneraKarta_PoluchatelSkidki on SkidkaPartneraKarta(PoluchatelSkidki)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_SkidkaPartneraKarta_period on SkidkaPartneraKarta(period)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_SkidkaPartneraKarta_DataOkonchaniya on SkidkaPartneraKarta(DataOkonchaniya)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_SkidkaPartneraKarta_Podrazdelenie on SkidkaPartneraKarta(Podrazdelenie)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_GPSPoints_BeginTime on GPSPoints(BeginTime)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_GPSPoints_Upload on GPSPoints(Upload)");
		//
		executeQueryInTranzaction(mDB, "create index if not exists IX_Zapiski_nomer on Zapiski(nomer)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_Zapiski_data on Zapiski(data)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_ZapiskiFiles_zapiska on ZapiskiFiles(zapiska)");
		//
		executeQueryInTranzaction(mDB, "create index if not exists IX_KategoryObjectov_Nomenklatura on KategoryObjectov(Nomenklatura)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_KategoryObjectov_kategorya on KategoryObjectov(kategorya)");
		//
		executeQueryInTranzaction(mDB, "create index if not exists IX_tovaryDlyaDozakaza_nomenklatura on tovaryDlyaDozakaza(nomenklatura)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_tovaryDlyaDozakaza_object on tovaryDlyaDozakaza(object)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_nomenklaturaTipyTorgTochek_IDRRef on nomenklaturaTipyTorgTochek(_IDRRef)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_nomenklaturaTipyTorgTochek_TipTorgTochki on nomenklaturaTipyTorgTochek(TipTorgTochki)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_nomenklaturaVidyKuhon_IDRRef on nomenklaturaVidyKuhon(_IDRRef)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_nomenklaturaVidyKuhon_VidKuhni on nomenklaturaVidyKuhon(VidKuhni)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_dopmotivaciya_periodnach on dopmotivaciya(periodnach)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_dopmotivaciya_periodkon on dopmotivaciya(periodkon)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_IndividualnieSkidki_Registrator on IndividualnieSkidki(Registrator)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_IndividualnieSkidki_Period on IndividualnieSkidki(Period)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_IndividualnieSkidki_Kontragent on IndividualnieSkidki(Kontragent)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_IndividualnieSkidki_Nomenklatura on IndividualnieSkidki(Nomenklatura)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_IndividualnieSkidki_Cena on IndividualnieSkidki(Cena)");
		executeQueryInTranzaction(mDB, "create index if not exists IX_IndividualnieSkidki_DataOkonchaniya on IndividualnieSkidki(DataOkonchaniya)");
		//Period,Kontragent,Nomenklatura,Cena,DataOkonchaniya
		try {
			deleteOldData(mDB);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		//mDB.execSQL("delete from GPSPoints where date(BeginTime)<date('now','-3 days') or Upload>0;");
		GPSInfo.lockInsert = false;
	}

	public static void deleteOldData(SQLiteDatabase mDB) {
		System.out.println("deleteOldData start");
		mDB.execSQL("delete from GPSPoints where date(BeginTime)<date('now','-3 days');");
		//mDB.execSQL("delete from GPSPoints where date(BeginTime)>date('now','+3 days');");
		mDB.execSQL("delete from GPSPoints where date(BeginTime)>date('now','+1 days');");

		mDB.execSQL("delete from vizits where date(BeginTime)<date('now','-20 days');");
		mDB.execSQL("delete from vizits where date(BeginTime)>date('now','+2 days');");

		String dateLimitFunc = "date('now','-3 months')";
		mDB.execSQL("delete from syncLog where date(endTime)<" + dateLimitFunc + ";");
		mDB.execSQL("delete from ZayavkaPokupatelyaIskhodyaschaya_Smvz where parent in (select _idrref from ZayavkaPokupatelyaIskhodyaschaya where date(data)<" + dateLimitFunc + ");");
		mDB.execSQL("delete from ZayavkaPokupatelyaIskhodyaschaya_Tovary where _ZayavkaPokupatelyaIskhodyaschaya_IDRRef in (select _idrref from ZayavkaPokupatelyaIskhodyaschaya where date(data)<" + dateLimitFunc + ");");
		mDB.execSQL("delete from ZayavkaPokupatelyaIskhodyaschaya_Traphiki where _ZayavkaPokupatelyaIskhodyaschaya_IDRRef in (select _idrref from ZayavkaPokupatelyaIskhodyaschaya where date(data)<" + dateLimitFunc + ");");
		mDB.execSQL("delete from ZayavkaPokupatelyaIskhodyaschaya_Uslugi where _ZayavkaPokupatelyaIskhodyaschaya_IDRRef in (select _idrref from ZayavkaPokupatelyaIskhodyaschaya where date(data)<" + dateLimitFunc + ");");
		mDB.execSQL("delete from ZayavkaPokupatelyaIskhodyaschaya where date(data)<" + dateLimitFunc + ";");
		mDB.execSQL("delete from skidki where date(dataokonchaniya)<date('now','-1 days');");
		String historyLimit = "date('now','-5 months')";
		mDB.execSQL("delete from prodazhi where date(period)<" + historyLimit + ";");
		mDB.execSQL("delete from EdinicyIzmereniya where _idrref not in (select EdinicaDlyaOtchetov from nomenklatura) and _idrref not in (select EdinicaKhraneniyaOstatkov from nomenklatura);");
		System.out.println("deleteOldData done");
	}

	public static void adjustColumn(SQLiteDatabase mDB, String columnsSpec, String s1cName, String Update1cName, String sqliteName, String tableName, int type) {
		try {
			//System.out.println("adjustColumn " + sqliteName);
			String sql = "";
			sql = "select count(_id) from _RelationsFieldsHelper where sqliteName='" + sqliteName + "'";
			Cursor tt = mDB.rawQuery(sql, null);
			tt.moveToFirst();
			int cnt = tt.getInt(0);
			if (cnt < 1) {
				//System.out.println(sqliteName + " not found");
				sql = "select _id from _RelationsTableHelper where sqliteName='" + tableName + "'";
				tt = mDB.rawQuery(sql, null);
				tt.moveToFirst();
				int id = tt.getInt(0);
				sql = "insert into _RelationsFieldsHelper"//
						+ " (_tableId,[1cName],[Update1cName],sqliteName, sqlFieldType)"//
						+ " values (" + id + ",'" + s1cName + "','" + Update1cName + "','" + sqliteName + "'," + type + ")";
				//System.out.println(sqliteName + ": " + sql);
				mDB.execSQL(sql);
				sql = "alter table " + tableName + " add column " + sqliteName + " " + columnsSpec;
				//System.out.println(sqliteName + ": " + sql);
				mDB.execSQL(sql);
			} else {
				//System.out.println("skip adjustColumn " + sqliteName);
			}
		} catch (Throwable t) {
			System.out.println("Error: " + t.getMessage());
		}
		//System.out.println("adjustColumn done");
	}

	static void adjustExchange(SQLiteDatabase mDB, String name1C, String updateCategoryName, String sqliteName, FieldsDescription fields, String create) {
		if (1 == 1) {
			//do nothing
		}
		LogHelper.debug("adjustExchange " + sqliteName + ": " + updateCategoryName + "." + name1C + " start");
		//System.out.println(create);
		executeQueryInTranzaction(mDB, create);
		String sql = "select count(_id) from _RelationsTableHelper"//
				+ " where UpdateCategoryName=\"" + updateCategoryName + "\""//
				+ " and [1cName]=\"" + name1C + "\""//
				+ " and sqliteName=\"" + sqliteName + "\"";
		//System.out.println(sql);
		Cursor cursor = mDB.rawQuery(sql, null);
		cursor.moveToNext();
		int cnt = cursor.getInt(0);
		if (cnt < 1) {
			LogHelper.debug("adjustExchange " + sqliteName + ": " + updateCategoryName + "." + name1C + " register table");
			ContentValues values = new ContentValues();
			values.put("UpdateCategoryName", updateCategoryName);
			values.put("[1cName]", name1C);
			values.put("sqliteName", sqliteName);
			values.put("categoryName", "");
			long id = mDB.insert("_RelationsTableHelper", null, values);
			fields.insert(id, mDB);
			LogHelper.debug("adjustExchange " + sqliteName + ": " + name1C + " done");
		} else {
			LogHelper.debug("adjustExchange " + sqliteName + ": " + updateCategoryName + "." + name1C + " skip");
		}
	}
}

class FieldsDescription {

	final static int FIELD_BLOB = 1;
	final static int FIELD_DATE = 2;
	final static int FIELD_NCHAR = 3;
	final static int FIELD_NUMERIC = 4;
	final static int FIELD_NVARCHAR = 5;
	final static int FIELD_TEXT = 6;
	final static int FIELD_TIMESTAMP = 7;
	Vector<String> update1cName = new Vector<String>();
	Vector<String> sqliteName = new Vector<String>();
	Vector<Integer> sqlFieldType = new Vector<Integer>();

	public FieldsDescription field(String update1cName, String sqliteName, int sqlFieldType) {
		this.update1cName.add(update1cName);
		this.sqliteName.add(sqliteName);
		this.sqlFieldType.add(sqlFieldType);
		return this;
	}

	public void insert(long tableID, SQLiteDatabase mDB) {
		for (int i = 0; i < update1cName.size(); i++) {
			LogHelper.debug("insert: " + tableID + ": " + sqliteName.get(i));
			ContentValues values = new ContentValues();
			values.put("_tableID", tableID);
			values.put("Update1cName", update1cName.get(i));
			values.put("[1cName]", "");
			values.put("sqliteName", sqliteName.get(i));
			values.put("sqlFieldType", sqlFieldType.get(i));
			mDB.insert("_RelationsFieldsHelper", null, values);
		}
	}
}
