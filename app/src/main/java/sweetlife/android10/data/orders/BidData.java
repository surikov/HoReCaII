package sweetlife.android10.data.orders;

import java.util.Calendar;

import sweetlife.android10.consts.ISQLConsts;
import sweetlife.android10.data.common.ExtraChargeInfo;
import sweetlife.android10.utils.DateTimeHelper;

public class BidData implements ISQLConsts {
	private ExtraChargeInfo mExtraChargeInfo;
	private ZayavkaPokupatelya mBid;
	private FoodstuffsData mFoodStuffs;
	private ServicesData mServices;
	private TraficsData mTrafiks;
	private String mClientID;
	private int mLiniyaDostavki = 0;
	private Calendar mChoosedDay;

	public void UpdateOrderExtraChargeInfo(String dataOtgruzki) {
		try {

			//mExtraChargeInfo.Update(mBid.getIDRRef(), mFoodStuffs.getBasePriceAmount(mClientID,dataOtgruzki), mFoodStuffs.getAmount());
			//System.out.println("1");
			String getIDRRef = mBid.getIDRRef();
			//System.out.println("2");
			double getBasePriceAmount = mFoodStuffs.getBasePriceAmount();
			//System.out.println("3");
			double getAmount = mFoodStuffs.getAmount();
			//System.out.println("4");
			double getVozvrat = mFoodStuffs.getVozvrat(mClientID, dataOtgruzki);
			//System.out.println("5");
			mExtraChargeInfo.UpdateInfo(getIDRRef, getBasePriceAmount, getAmount, getVozvrat,mFoodStuffs.getWeight());
			//System.out.println("6");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
/*
	public void AddNomenclature22222(String edinicaIzmereniyaID//
			, double kolichestvo//
			, String nomenklaturaID//
			, double cena//
			, double cenaSoSkidkoy//
			, double minimalnayaCena//
			, double maksimalnayaCena//
			, String artikul//
			, String nomenklaturaNaimenovanie//
			, double koefMest//
			, String edinicaIzmereniyaName//
			, double skidka//
			, String vidSkidki//
			, double minNorma//
			, double sebestoimost//
			, double lastPrice//
								//, String skidkaProcent//
								//, String skidkaNaimenovanie//
	) {
		//System.out.println(this.getClass().getCanonicalName() + ": AddNomenclature");
		if (mFoodStuffs.IsFoodstuffAlreadyInList(nomenklaturaID)) {
			return;
		}
		mFoodStuffs.newFoodstuff(nomenklaturaID//
				, artikul//
				, nomenklaturaNaimenovanie//
				, edinicaIzmereniyaID//
				, edinicaIzmereniyaName//
				, kolichestvo//
				, cena//
				, cenaSoSkidkoy//
				, minimalnayaCena//
				, maksimalnayaCena//
				, skidka//
				, vidSkidki//
				, minNorma//
				, koefMest//
				, sebestoimost//
				, lastPrice//
				//, skidkaProcent//
				//, skidkaNaimenovanie//
		);
		//System.out.println("AddNomenclature "+artikul+": "+minimalnayaCena+"/"+maksimalnayaCena);
		UpdateExtraChargeInfo(DateTimeHelper.SQLDateString(mChoosedDay.getTime()));
	}
*/
	public ExtraChargeInfo getExtraChargeInfo() {
		return mExtraChargeInfo;
	}

	public void setExtraChargeInfo(ExtraChargeInfo extraChargeInfo) {
		mExtraChargeInfo = extraChargeInfo;
	}

	public ZayavkaPokupatelya getBid() {
		return mBid;
	}

	public void setBid(ZayavkaPokupatelya bid) {
		mBid = bid;
	}

	public FoodstuffsData getFoodStuffs() {
		return mFoodStuffs;
	}

	public void setFoodStuffs(FoodstuffsData foodStuffs) {
		mFoodStuffs = foodStuffs;
	}

	public ServicesData getServices() {
		return mServices;
	}

	public void setServices(ServicesData services) {
		mServices = services;
	}

	public String getClientID() {
		return mClientID;
	}

	public void setClientID(String clientID) {
		mClientID = clientID;
	}

	public int getLiniyaDostavki() {
		return mLiniyaDostavki;
	}

	public void setLiniyaDostavki(int liniyaDostavki) {
		mLiniyaDostavki = liniyaDostavki;
	}

	public Calendar getChoosedDay() {
		return mChoosedDay;
	}

	public void setChoosedDay(Calendar choosedDay) {
		mChoosedDay = choosedDay;
	}

	public TraficsData getTrafiks() {
		return mTrafiks;
	}

	public void setTrafiks(TraficsData trafiks) {
		//System.out.println("setTrafiks "+trafiks.getCount());
		mTrafiks = trafiks;
	}
}
