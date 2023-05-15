package sweetlife.android10.data.common;

import android.database.sqlite.SQLiteDatabase;

public abstract class NomenclatureBasedItem {

	protected int m_id;
	protected int mNomerStroki;
	protected String mNomenklaturaID;
	protected String mArtikul;
	protected String mNomenklaturaNaimenovanie;
	protected String mZayavka_IDRRef;
	protected boolean mNew;

	public Boolean currentPartnerState = null;

	//public String skidkaProcent="";
	//public String skidkaNaimenovanie="";


	public NomenclatureBasedItem(int _id,
								 int nomerStroki,
								 String nomenklaturaID,
								 String artikul,
								 String nomenklaturaNaimenovanie,
								 String zayavka,
								 boolean New
								 //, String skidkaProcent//
								 //, String skidkaNaimenovanie//
	) {

		m_id = _id;
		mNomerStroki = nomerStroki;
		mNomenklaturaID = nomenklaturaID;
		mArtikul = artikul;
		mNomenklaturaNaimenovanie = nomenklaturaNaimenovanie;
		mZayavka_IDRRef = zayavka;
		mNew = New;
		//this.skidkaProcent=skidkaProcent;
		//	this.skidkaNaimenovanie=skidkaNaimenovanie;
	}

	public abstract void setToDataBase(SQLiteDatabase db);

	public int getNomerStroki() {

		return mNomerStroki;
	}

	public String getNomenklaturaID() {

		return mNomenklaturaID;
	}

	public String getArtikul() {

		return mArtikul;
	}

	public String getNomenklaturaNaimenovanie() {

		return mNomenklaturaNaimenovanie;
	}

	public boolean isNew() {

		return mNew;
	}

	public String getZayavka_IDRRef() {

		return mZayavka_IDRRef;
	}

	public int get_id() {

		return m_id;
	}
}
