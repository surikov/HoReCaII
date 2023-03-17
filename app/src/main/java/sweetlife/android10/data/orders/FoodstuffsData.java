package sweetlife.android10.data.orders;

import reactive.ui.Auxiliary;
import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.data.common.NomenclatureBasedDocument;
import sweetlife.android10.data.common.NomenclatureBasedDocumentItems;
import sweetlife.android10.data.common.NomenclatureBasedItem;
import sweetlife.android10.database.Request_FoodStuffList;
import sweetlife.android10.supervisor.Cfg;
import sweetlife.android10.utils.DateTimeHelper;
import tee.binding.Bough;
import tee.binding.it.Numeric;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FoodstuffsData extends NomenclatureBasedDocumentItems {
	SQLiteDatabase db;
Double currentNacenka=null;
	public FoodstuffsData(SQLiteDatabase db, NomenclatureBasedDocument zayavka) {
		super(db, zayavka);
		this.db = db;
		//System.out.println(this.getClass().getName()+" read from db start");
		Cursor cursor = Request_FoodStuffList.Request(db, mZayavka.getIDRRef());
		if (cursor != null && cursor.moveToFirst()) {
			ReadFromDataBase(cursor, db);
			//cursor.close();
			if(cursor!=null){
				cursor.close();
			}
		}
		if(cursor!=null){
			cursor.close();
		}
		//System.out.println(this.getClass().getName()+" read from db end");
	}
	@Override
	protected void ReadFromDataBase(Cursor cursor, SQLiteDatabase db) {
		ZayavkaPokupatelya_Foodstaff foodstaff;
		int nomerStroki;
		do {
			nomerStroki = Request_FoodStuffList.getNomerStroki(cursor);
			if (mNomenclatureNumber < nomerStroki) {
				mNomenclatureNumber = nomerStroki;
			}
			foodstaff = new ZayavkaPokupatelya_Foodstaff(//
					Request_FoodStuffList.get_id(cursor)//
					, nomerStroki//
					, Request_FoodStuffList.getNomenklatura(cursor)//
					, Request_FoodStuffList.getArtikul(cursor)//
					, Request_FoodStuffList.getNomenklaturaNaimenovanie(cursor, db)//
					, mZayavka.getIDRRef()//
					, Request_FoodStuffList.getEdinicaIzmereniyaID(cursor)//
					, Request_FoodStuffList.getEdinicaIzmereniya(cursor)//
					, Request_FoodStuffList.getKolichestvo(cursor)//
					, Request_FoodStuffList.getSumma(cursor)//
					, Request_FoodStuffList.getCena(cursor)//
					, Request_FoodStuffList.getCenaSoSkidkoy(cursor)//
					, Request_FoodStuffList.getMinimalnayaCena(cursor)//
					, Request_FoodStuffList.getMaksimalnayaCena(cursor)//
					, Request_FoodStuffList.getSkidka(cursor)//
					, Request_FoodStuffList.getVidSkidki(cursor)//
					, Request_FoodStuffList.getMinKolichestvo(cursor)//
					, Request_FoodStuffList.getKoefficientMest(cursor)//
					, Request_FoodStuffList.getSebestoimost(cursor)//
					, Request_FoodStuffList.getLastPrice(cursor)//
					, false//
					//,false
			//, ""//
			//, ""//
			//,Request_FoodStuffList.getByHand(cursor)
			);
			mNomenclaureList.add(foodstaff);
		}
		while (cursor.moveToNext());
	}
	public ZayavkaPokupatelya_Foodstaff getFoodstuff(int index) {
		return (ZayavkaPokupatelya_Foodstaff) mNomenclaureList.get(index);
	}
	public void newFoodstuff(String nomenklaturaID, String artikul//
			, String nomenklaturaNaimenovanie//
			, String edinicaIzmereniya//
			, String edinicaIzmereniyaNaimenovanie//
			, double kolichestvo//
			, double cena//
			, double cenaSoSkidkoy//
			, double minimalnayaCena//
			, double maksimalnayaCena//
			, double skidka//
			, String vidSkidki//
			, double minNorma//
			, double koefMest//
			, double basePrice//
			, double lastPrice//
	//, String skidkaProcent//
	//, String skidkaNaimenovanie//
	) {
		System.out.println("newFoodstuff "+vidSkidki);

		if(minimalnayaCena>0){
			if(vidSkidki.toLowerCase().trim().equals("цр")){
				vidSkidki="x'"+sweetlife.android10.supervisor.Cfg.skidkaIdCenovoyeReagirovanie+"'";
			}
			if(vidSkidki.toLowerCase().trim().equals("тг") || vidSkidki.toLowerCase().trim().equals("т.г.")){
				vidSkidki="x'"+sweetlife.android10.supervisor.Cfg.skidkaId_Heroy+"'";
				//System.out.println("newFoodstuff "+vidSkidki);
			}
			double cr=Cfg.findCRGroup(artikul);
			if(cr>0) {
				if(minimalnayaCena<=cr && cr<=maksimalnayaCena) {
					skidka = 0.0D;
					vidSkidki = "x'"+sweetlife.android10.supervisor.Cfg.skidkaIdCenovoyeReagirovanie+"'";//Sales.CR_ID;
					cenaSoSkidkoy = cr;
				}
			}
		}else{
			if(vidSkidki.toLowerCase().trim().equals("цр")||vidSkidki.toLowerCase().trim().equals("тг") || vidSkidki.toLowerCase().trim().equals("т.г.")){
				cenaSoSkidkoy=cena;
				skidka=0;
			}

		}
		System.out.println("set newFoodstuff "+vidSkidki);
		mNomenclaureList.add(0, new ZayavkaPokupatelya_Foodstaff(0, ++mNomenclatureNumber, nomenklaturaID, artikul, nomenklaturaNaimenovanie
				                                                        , mZayavka.getIDRRef(), edinicaIzmereniya, edinicaIzmereniyaNaimenovanie
				                                                        , kolichestvo, 0.00D, cena, cenaSoSkidkoy
				                                                        , minimalnayaCena
				                                                        , maksimalnayaCena, skidka, vidSkidki, minNorma, koefMest, basePrice, lastPrice, true//, skidkaProcent
				//, skidkaNaimenovanie
				//,false
				));
		//mFoodstaff.setCenaSoSkidkoy(Double.parseDouble(mEditPrice.getText().toString()));

	}
	public boolean IsFoodstuffAlreadyInList(String nomenklatureID) {
		//System.out.println("IsFoodstuffAlreadyInList "+nomenklatureID);
		for (NomenclatureBasedItem item : mNomenclaureList) {
			//System.out.println("IsFoodstuffAlreadyInList check "+item.getNomenklaturaID());
			if (item.getNomenklaturaID().toUpperCase().compareToIgnoreCase(nomenklatureID.toUpperCase()) == 0) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void WriteToDataBase(SQLiteDatabase db) {
		//System.out.println(this.getClass().getName()+" write to db start");
		db.beginTransactionNonExclusive();
		try {
			for (NomenclatureBasedItem item : mNomenclaureList) {
				//System.out.println(item.getClass().getName()+" setToDataBase");
				item.setToDataBase(db);
			}
			for (Integer id : mIDsForDelete) {
				db.execSQL("delete from ZayavkaPokupatelyaIskhodyaschaya_Tovary where _id = " + id.toString());
			}
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
		//System.out.println(this.getClass().getName()+" write to db end");
	}
	@Override
	public boolean IsAllDataFilled() {
		return true;
	}
	public boolean getPartnerSkidka(String artikul, String dataOtgruzki) {
		//System.out.println("SkidkaPartneraKarta " + artikul + " / " + dataOtgruzki);
		String sql = "select max(ProcentSkidkiNacenki) as partner from SkidkaPartneraKarta"// 
				+ "\n 		join nomenklatura_sorted n on artikul=" + artikul//
				+ "\n 		where PoluchatelSkidki=n.[_IDRRef]"// 
				+ "\n 			and date(period)<=date('" + dataOtgruzki + "') "//
				+ "\n 			and date(DataOkonchaniya)>=date('" + dataOtgruzki + "') ";
		//System.out.println(sql);
		Bough b = Auxiliary.fromCursor(db.rawQuery(sql, null));
		if (b.children.size() > 0) {
			double r = Numeric.string2double(b.children.get(0).child("partner").value.property.value());
			if (r > 0) {
				//System.out.println(artikul + " partner");
				return true;
			}
		}
		//System.out.println(artikul + " nope");
		return false;
	}
	public double getNacenka(String mClientID) {
		//System.out.println("NacenkiKontr for " + mClientID);
		String dataOtgruzki=DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime());
				//"2013-01-30";//
		String sql = "select ProcentSkidkiNacenki"//
				+ " from nacenkikontr"// 
				+ " where poluchatelskidki=" + mClientID //
				+" and nacenkikontr.period<=date("+dataOtgruzki+")"//
				+" and nacenkikontr.DataOkonchaniya>=date("+dataOtgruzki+")"//
				+ " limit 1";
		//System.out.println(sql);
		Bough b = Auxiliary.fromCursor(db.rawQuery(sql, null));
		//System.out.println(b.dumpXML());
		if (b.children.size() > 0) {
			double r = Numeric.string2double(b.children.get(0).child("ProcentSkidkiNacenki").value.property.value());
			if (r > 0) {
				return r;
			}
		}
		sql = "select max(period),ProcentSkidkiNacenki"//
				+ " from nacenkikontr"// 
				+ " join kontragenty on kontragenty.golovnoykontragent=nacenkikontr.poluchatelskidki"//
				+ " where kontragenty._idrref=" + mClientID//
				+" and nacenkikontr.period<=date("+dataOtgruzki+")"//
				+" and nacenkikontr.DataOkonchaniya>=date("+dataOtgruzki+")"//
				+ " limit 1";
		b = Auxiliary.fromCursor(db.rawQuery(sql, null));
		//System.out.println(b.dumpXML());
		if (b.children.size() > 0) {
			double r = Numeric.string2double(b.children.get(0).child("ProcentSkidkiNacenki").value.property.value());
			if (r > 0) {
				return r;
			}
		}
		//System.out.println("NacenkiKontr for " + mClientID+" done");
		return 0;
	}
	public double getVozvrat(String mClientID, String dataOtgruzki) {
		//System.out.println("getVozvrat for " + mClientID+" by "+dataOtgruzki);
		double orderVozvrat = 0;
		//try{
		double nacenka=0;
		
		if(currentNacenka==null){
			currentNacenka = getNacenka(mClientID);
		}
		nacenka=currentNacenka;
		
		//nacenka = getNacenka(mClientID);
		//System.out.println("count getVozvrat for");
		for (NomenclatureBasedItem item : mNomenclaureList) {
			ZayavkaPokupatelya_Foodstaff food = (ZayavkaPokupatelya_Foodstaff) item;
			
			if(item.currentPartnerState==null){
				//System.out.println("getPartnerSkidka(item.getArtikul(), dataOtgruzki) for "+item.getArtikul()+" / "+ dataOtgruzki);
				item.currentPartnerState = getPartnerSkidka(item.getArtikul(), dataOtgruzki);
			}
			boolean partner =item.currentPartnerState; 
					//getPartnerSkidka(item.getArtikul(), dataOtgruzki);
			if (!partner && nacenka > 0) {
				double foodVozvrat = food.getSummaSoSkidkoy() * (nacenka - 2) / (100 + nacenka);
				//double quantity = food.getKolichestvo();
				orderVozvrat = orderVozvrat +  foodVozvrat;
				//System.out.println("getVozvrat " + food.getSummaSoSkidkoy() + "*("+nacenka+"-2)/"+(100+nacenka)+"="+foodVozvrat);
			}
		}//}catch(Throwable t){
		//	t.printStackTrace();
		//}
		//System.out.println("getVozvrat for " + mClientID+" by "+dataOtgruzki+" done");
		return orderVozvrat;
	}
	/*public double getSebestoimost(){
		double sbst=0;
				return sbst;
	}*/
	public double getBasePriceAmount() {
		double amount = 0;
		//double nacenka = getNacenka(mClientID);
		//System.out.println("NacenkiKontr " + nacenka);
		for (NomenclatureBasedItem item : mNomenclaureList) {
			ZayavkaPokupatelya_Foodstaff food = (ZayavkaPokupatelya_Foodstaff) item;
			amount = amount + food.getBasePriceAmount();
			/*boolean partner = getPartnerSkidka(item.getArtikul(), dataOtgruzki);
			if (!partner && nacenka > 0) {
				double vozvrat = food.getBasePrice() * (nacenka - 2) / (100 + nacenka);
				double quantity = food.getKolichestvo();
				System.out.println("vozvrat " + vozvrat + " for " + item.getArtikul());
				amount = amount + food.getBasePriceAmount();
			}
			else {
				amount = amount + food.getBasePriceAmount();
			}*/
			//System.out.println(food.getArtikul() + ": " + food.getNomenklaturaNaimenovanie() + "=" + food.getBasePrice());
			//amount = amount + ((ZayavkaPokupatelya_Foodstaff) item).getBasePriceAmount();
		}
		return amount;
	}
	public double getAmount() {
		//double nacenka = getNacenka(mClientID);
		double amount = 0;
		for (NomenclatureBasedItem item : mNomenclaureList) {
			amount = amount + ((ZayavkaPokupatelya_Foodstaff) item).getSummaSoSkidkoy();
		}
		
			//amount=amount*(100.0+nacenka)/100.0;
		
		return amount;
	}
	public double getAmount(String sklad) {
		//double nacenka = getNacenka(mClientID);
		double amount = 0;
		for (NomenclatureBasedItem item : mNomenclaureList) {
			amount = amount + ((ZayavkaPokupatelya_Foodstaff) item).getSummaSoSkidkoyForStore(sklad);
		}
		
			//amount=amount*(100.0+nacenka)/100.0;
		
		return amount;
	}
}
