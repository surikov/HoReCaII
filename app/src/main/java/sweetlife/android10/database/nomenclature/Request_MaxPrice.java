package sweetlife.android10.database.nomenclature;

public class Request_MaxPrice extends 
//Request_NomenclatureBase 
Request_Nomenclature
{
	public Request_MaxPrice(boolean degustacia){
		super( degustacia);
	}

	@Override
	protected void SetRequestString(boolean degustacia) {
/*
		mStrQuery =  "select n._id, n.[_IDRRef], n.[Artikul], " +
				"n.[Naimenovanie], n.[OsnovnoyProizvoditel], " +
				"n.[ProizvoditelNaimenovanie], n.[Cena], "+ 
				"n.Skidka, n.CenaSoSkidkoy, n.VidSkidki, " +
				"n.[EdinicyIzmereniyaNaimenovanie], n.[MinNorma], " +
				"n.Koephphicient,  n.[EdinicyIzmereniyaID], n.Roditel, "+       
				"n.[MinCena], n.[MaxCena], n.[BasePrice], n.[LastPrice] "+
				"from CurNomenklaturaTovaryMax n";		*/
		super.SetRequestString( degustacia);
		//mStrQuery=mStrQuery+" where 1=2 ";
	}
}
