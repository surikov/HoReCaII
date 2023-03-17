package sweetlife.android10.database.nomenclature;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.utils.DateTimeHelper;

public class Request_Search extends Request_Nomenclature implements ISearchBy {
	private int mSearchBy = SEARCH_ARTICLE;
	private String mSearchString = null;
	String _kuhnya = null;
	String _tochka = null;
	 String _receptID;

	public Request_Search(int searchBy, String searchString,boolean degustacia) {
		super(degustacia);
		mSearchBy = searchBy;
		mSearchString = searchString;
		AddSearchString(degustacia);
	}
	public Request_Search(int searchBy, String searchString,String kuhnya,String tochka,boolean degustacia, String receptID) {
		super(degustacia);
		//System.out.println("kuhnya "+kuhnya+", tochka "+tochka);
		mSearchBy = searchBy;
		mSearchString = searchString;
		_tochka=tochka;
		_kuhnya=kuhnya;
		_receptID=receptID;
		AddSearchString2(degustacia);
	}
	protected void AddSearchString(boolean degustacia) {
		mStrQuery= composeSQL(//
				DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
				, ApplicationHoreca.getInstance().getClientInfo().getID()//
				, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				, ""//
				, ""//
				, mSearchString//
				, mSearchBy//
				, false//
				, false//
				, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya(),200,0,false,false,degustacia,null,null);
		//System.out.println(mStrQuery);
		/*
		if (1==1)return ;

		if (mSearchBy == SEARCH_ARTICLE) {
			mStrQuery = mStrQuery + " where ( n.[Artikul] = '" + mSearchString + "' )";
			//mStrQuery = mStrQuery +" and n.TovarPodZakaz=x'00'";
		}
		else
			if (mSearchBy == SEARCH_NAME) {
				mStrQuery = mStrQuery + " where ( n.[UpperName] like '%" + mSearchString.toUpperCase() + "%')";
				//mStrQuery = mStrQuery +" and n.TovarPodZakaz=x'00'";
			}
			else
				if (mSearchBy == SEARCH_IDRREF) {
					mStrQuery = mStrQuery + " where ( n.[_IDRRef] = " + mSearchString + " )";
					//mStrQuery = mStrQuery +" and n.TovarPodZakaz=x'00'";
				}
				else
					if (mSearchBy == SEARCH_VENDOR) {
						mStrQuery = mStrQuery + " where ( p.[Naimenovanie] like '" + mSearchString + "' || '%')";
						//mStrQuery = mStrQuery +" and n.TovarPodZakaz=x'00'";
					}
					else
						if (mSearchBy == SEARCH_ARTICLE) {
							mStrQuery = mStrQuery + " where ( n.[Artikul] like '" + mSearchString + "' || '%')";
							//mStrQuery = mStrQuery +" and n.TovarPodZakaz=x'00'";
						}
		*/
	}
	protected void AddSearchString2(boolean degustacia) {
		mStrQuery= composeSQLall(//
				DateTimeHelper.SQLDateString(ApplicationHoreca.getInstance().getShippingDate().getTime())//
				, ApplicationHoreca.getInstance().getClientInfo().getID()//
				, ApplicationHoreca.getInstance().getCurrentAgent().getAgentIDstr()//
				, ""//
				, ""//
				, mSearchString//
				, mSearchBy//
				, false//
				, false//
				, ApplicationHoreca.getInstance().getCurrentAgent().getSkladPodrazdeleniya(),500,0,false,false
		,_kuhnya,_tochka,false,degustacia
				//, _receptID
				,null,null,null
		);
	}
	/*protected String getSearchString() {
		if (mSearchBy == SEARCH_ARTICLE) {
			return " where ( n.[Artikul] = '" + mSearchString + "' )";
		}
		else {
			if (mSearchBy == SEARCH_NAME) {
				return " where ( n.[UpperName] like '%" + mSearchString + "%')";
			}
			else {
				if (mSearchBy == SEARCH_IDRREF) {
					return " where ( n.[_IDRRef] = " + mSearchString + " )";
				}
				else {
					if (mSearchBy == SEARCH_VENDOR) {
						return " where ( n.[ProizvoditelNaimenovanie] like '" + mSearchString + "' || '%')";
					}
					else {
						if (mSearchBy == SEARCH_ARTICLE_PART) {
							return " where ( n.[Artikul] like '" + mSearchString + "' || '%')";
						}
						else {
							return "";
						}
					}
				}
			}
		}
	}*/
}
