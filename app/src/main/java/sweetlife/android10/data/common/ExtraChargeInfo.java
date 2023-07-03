package sweetlife.android10.data.common;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.DecimalFormatHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ExtraChargeInfo implements ITableColumnsNames {
	private String mClientID;
	private SQLiteDatabase mDB;
	private double mOrderFactPercent = 0;
	private double mClientPlanPercent = 0;
	public double planPodrazdeleniaNaMesiac = 0;
	public double nacenkaFactPodrzdelenia = 0;

	public ExtraChargeInfo(SQLiteDatabase db, String clientID) {
		mDB = db;
		mClientID = clientID;
		UpdateClientPlan();
	}

	public boolean IsExtraChargeInfoAvailable() {
		if (mClientPlanPercent != 0) {
			return true;
		}
		return false;
	}

	public void Update(String editOrderID, double basePriceAmount, double amount, double vozvrat) {
		//System.out.println(this.getClass().getCanonicalName()+": Update: "+editOrderID+"/"+basePriceAmount+"/"+amount);
		UpdateFactCalculatePart(basePriceAmount, amount, vozvrat);
	}

	public void UpdateFactCalculatePart(double basePriceAmount, double amount, double vozvrat) {
		UpdateOrderFactCalculatePart(basePriceAmount, amount, vozvrat);
	}

	private void UpdateOrderFactCalculatePart(double basePriceAmount, double amount, double vozvrat) {
		if (basePriceAmount != 0) {

			double summaSVozvr = amount - vozvrat;
			//System.out.println("summaSVozvr " + amount + "-"+vozvrat+"="+summaSVozvr);
			//System.out.println( amount + "-"+vozvrat+"="+(amount-vozvrat));
			mOrderFactPercent = (summaSVozvr - basePriceAmount) / basePriceAmount * 100.0;
			//System.out.println( "("+summaSVozvr + " - "+basePriceAmount+") / "+basePriceAmount+" * 100 = "+mOrderFactPercent);
			//System.out.println("new " + mOrderFactPercent + "%");
			//System.out.println("old " + ((amount - basePriceAmount) / basePriceAmount * 100) + "%");
		} else {
			mOrderFactPercent = 0;
		}
		//System.out.println(this.getClass().getCanonicalName()+": mOrderFactPercent: "+mOrderFactPercent);
	}

	private void UpdateClientPlan() {
		/*
		String sqlStr = "select p.Nacenka from PlanovyeNacenkiNaMesyac p "// 
				+ "inner join " //
				+ "(" //
				+ " select Klient, max(date(Period)) [Period] from PlanovyeNacenkiNaMesyac "//
				+ " where date() >= date(Period) and date() <= date(DataOkonchaniya) "// 
				+ " group by Klient "//
				+ ") maxp on maxp.Klient = p.Klient and date(maxp.Period) == date(p.Period) "// 
				+ "where p.Klient = "// 
				+ mClientID;
		*/
		mClientPlanPercent = 0;
		//System.out.println("skip mClientPlanPercent");
		/*
		String sqlStr = "select p.Nacenka from PlanovyeNacenkiNaMesyac p "// 
				+ "\n inner join " //
				+ "\n (" //
				+ "\n  select Klient, max(date(Period)) [Period] from PlanovyeNacenkiNaMesyac "//
				+ "\n  where date() >= date(Period) and date() <= date(DataOkonchaniya) and Klient = " + mClientID// 
				+ "\n  group by Klient "//
				+ "\n ) maxp on maxp.Klient = p.Klient and date(maxp.Period) = date(p.Period)"// 
				+ "\n where p.Klient = " + mClientID//
				+ "\n  limit 1;";
		//System.out.println(sqlStr);
		*/
		Cursor cursor = null;
		/*
		try {
			//System.out.println("mClientPlanPercent start");
			cursor = mDB.rawQuery(sqlStr, null);
			if (cursor.moveToFirst()) {//&& cursor.getDouble(cursor.getColumnIndex(NACENKA)) != 0 ) {
				mClientPlanPercent = cursor.getDouble(cursor.getColumnIndex(NACENKA));
			}
			else {
				LogHelper.debug("mClientPlanPercent not found");
			}
			//System.out.println("mClientPlanPercent done");
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		*/

		//planPodrazdeleniaNaMesiac = 0;
		/*
		sqlStr = "select sum(ValovyyDokhod)/(sum(Tovarooborot)-sum(ValovyyDokhod)) from PlanovyeNacenkiNaMesyac join"//
				+ "\n\t ("//
				+ "\n\t select Klient, max(date(Period)) [Period]"// 
				+ "\n\t from PlanovyeNacenkiNaMesyac"//  
				+ "\n\t join MarshrutyAgentov on MarshrutyAgentov.kontragent=PlanovyeNacenkiNaMesyac.Klient"// 
				+ "\n\t where date() >=date(PlanovyeNacenkiNaMesyac.Period) and date() <= date(PlanovyeNacenkiNaMesyac.DataOkonchaniya)"//  
				+ "\n\t group by PlanovyeNacenkiNaMesyac.Klient "//
				+ "\n\t ) mx"//
				+ "\n\t on mx.Klient=PlanovyeNacenkiNaMesyac .klient and mx.period=PlanovyeNacenkiNaMesyac  .period";
		*/
		planPodrazdeleniaNaMesiac = 0;
		//System.out.println("skip planPodrazdeleniaNaMesiac");
		/*
		sqlStr = "select sum(ValovyyDokhod)/(sum(Tovarooborot)-sum(ValovyyDokhod)) as rez"//
				+ "\n		from PlanovyeNacenkiNaMesyac "//
				+ "\n		where klient | PlanovyeNacenkiNaMesyac.period in ("//
				+ "\n			select Klient | max(date(Period)) as kp"//
				+ "\n				from PlanovyeNacenkiNaMesyac"//
				+ "\n				where date() >=date(PlanovyeNacenkiNaMesyac.Period) "//
				+ "\n					and date() <= date(PlanovyeNacenkiNaMesyac.DataOkonchaniya)"//
				+ "\n					and Klient in ("//
				+ "\n						select kontragent from MarshrutyAgentov"//
				+ "\n						)"//
				+ "\n						group by PlanovyeNacenkiNaMesyac.Klient "//
				+ "\n			) ";
		try {
			//System.out.println("planPodrazdeleniaNaMesiac start");
			cursor = mDB.rawQuery(sqlStr, null);
			if (cursor.moveToFirst()) {
				planPodrazdeleniaNaMesiac = cursor.getDouble(0);
				cursor.close();
			}
			else {
				LogHelper.debug("planPodrazdeleniaNaMesiac not found");
			}
			//System.out.println("planPodrazdeleniaNaMesiac done");
		}
		catch (Throwable t) {
			LogHelper.debug("planPodrazdeleniaNaMesiac: " + t.getMessage());
		}
		*/
		/*
		sqlStr = "select (sum(SummaProdazhi)-sum(Sebestoimost))/sum(SummaProdazhi) from DlyaRaschetaNacenkiVNetbuke join"//
				+ "\n\t ("//
				+ "\n\t select max(DlyaRaschetaNacenkiVNetbuke.period) as period,DlyaRaschetaNacenkiVNetbuke.kontragent as kontragent"//
				+ "\n\t from DlyaRaschetaNacenkiVNetbuke "//
				+ "\n\t join MarshrutyAgentov on MarshrutyAgentov.kontragent=DlyaRaschetaNacenkiVNetbuke.kontragent"// 
				+ "\n\t where DlyaRaschetaNacenkiVNetbuke.period<=date()"//
				+ "\n\t group by DlyaRaschetaNacenkiVNetbuke.kontragent "//
				+ "\n\t ) mx on mx.kontragent=DlyaRaschetaNacenkiVNetbuke.kontragent and DlyaRaschetaNacenkiVNetbuke.period=mx.period";
		*/
		String
				sqlStr = "select (sum(SummaProdazhi)-sum(Sebestoimost))/sum(SummaProdazhi) from DlyaRaschetaNacenkiVNetbuke join"//
				+ "\n\t ("//
				+ "\n\t select max(DlyaRaschetaNacenkiVNetbuke.period) as period,DlyaRaschetaNacenkiVNetbuke.kontragent as kontragent"//
				+ "\n\t from DlyaRaschetaNacenkiVNetbuke "//
				+ "\n\t join MarshrutyAgentov on MarshrutyAgentov.kontragent=DlyaRaschetaNacenkiVNetbuke.kontragent"// 
				+ "\n\t where DlyaRaschetaNacenkiVNetbuke.period<=date()"//
				+ "\n\t group by DlyaRaschetaNacenkiVNetbuke.kontragent "//
				+ "\n\t ) mx on mx.kontragent=DlyaRaschetaNacenkiVNetbuke.kontragent and DlyaRaschetaNacenkiVNetbuke.period=mx.period" // 
				+ " limit 1";
		try {
			//System.out.println("nacenkaFactPodrzdelenia start");
			cursor = mDB.rawQuery(sqlStr, null);
			if (cursor.moveToFirst()) {
				nacenkaFactPodrzdelenia = cursor.getDouble(0);
				cursor.close();
			} else {
				LogHelper.debug("nacenkaFactPodrzdelenia not found");
			}
			//System.out.println("nacenkaFactPodrzdelenia done");
		} catch (Throwable t) {
			LogHelper.debug("nacenkaFactPodrzdelenia: " + t.getMessage());
		}
		//LogHelper.debug("mClientPlanPercent for " + mClientID + " is " + mClientPlanPercent);
	}

	public String getOrderFactPersent() {
		/*if (!IsExtraChargeInfoAvailable()) {
			return null;
		}*/
		return DecimalFormatHelper.format(mOrderFactPercent) + "%";
	}

	public String getClientPlanPersent() {
		if (!IsExtraChargeInfoAvailable() || mClientPlanPercent == 0) {
			return null;
		}
		return DecimalFormatHelper.format(mClientPlanPercent) + "%";
	}
}
