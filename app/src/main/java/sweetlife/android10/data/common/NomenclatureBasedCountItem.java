package sweetlife.android10.data.common;

public abstract class NomenclatureBasedCountItem extends NomenclatureBasedItem {

	protected double  mMinNorma;
	protected double  mKoefMest; 
	protected String  mEdinicaIzmereniyaID; 
	public String  mEdinicaIzmereniyaName;
	//protected String  ves;
	protected double  mKolichestvo; 
	
	public NomenclatureBasedCountItem(int _id, 
			int nomerStroki,
			String nomenklaturaID, 
			String artikul,
			String nomenklaturaNaimenovanie, 
			String zayavka, 
			double minNorma,
			double koefMest,
			String edinicaIzmereniyaID, 
			String edinicaIzmereniyaName,
			double kolichestvo,
			boolean New
			//, String skidkaProcent//
			//, String skidkaNaimenovanie//
			) {
		
		super(_id, 
				nomerStroki, 
				nomenklaturaID, 
				artikul, 
				nomenklaturaNaimenovanie,
				zayavka, 
				New//
				//,skidkaProcent
				//,skidkaNaimenovanie
				);

		mMinNorma = minNorma;
		mKoefMest = koefMest; 
		mEdinicaIzmereniyaID = edinicaIzmereniyaID; 
		mEdinicaIzmereniyaName = edinicaIzmereniyaName;
		mKolichestvo = kolichestvo; 
		//System.out.println("NomenclatureBasedCountItem");
	}
	
	public double getKolichestvo() {
		
		return mKolichestvo;
	}

	public void setKolichestvo(Double kolichestvo) {
		
		mKolichestvo = kolichestvo;
	}
	public String getEdinicaIzmereniyaID() {
		
		return mEdinicaIzmereniyaID;
	}

	public void setEdinicaIzmereniyaID(String edinicaIzmereniyaID) {
		
		mEdinicaIzmereniyaID = edinicaIzmereniyaID;
	}

	public String getEdinicaIzmereniyaName() {
		
		return mEdinicaIzmereniyaName;
	}

	public void _setEdinicaIzmereniyaName(String edinicaIzmereniyaName) {
		
		mEdinicaIzmereniyaName = edinicaIzmereniyaName;
	}

	public double getMinNorma() {
		
		return mMinNorma;
	}

	public void setMinNorma(double minNorma) {
		
		mMinNorma = minNorma;
	}

	public double getKoefMest() {
		//return getMinNorma();
		return mKoefMest;
	}

	public void setKoefMest(double koefMest) {
		
		mKoefMest = koefMest;
	}
	void adjustVes(){
		
	}
}
