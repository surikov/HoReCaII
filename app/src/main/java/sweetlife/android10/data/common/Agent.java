package sweetlife.android10.data.common;

public class Agent {
	private int m_id;
	private byte[] mAgentID;
	private byte[] mPodrazdelenieID;
	private String mAgentIDstr;
	private String mPodrazdelenieIDstr;
	private String mAgentSpervisorStr;
	private String mAgentKod;
	public String updateKod = "?";
	private String mPodrazdelenieKod;
	private String mAgentName;
	private String mPodrazdelenieName;
	//public boolean isSuperVisor = false;
	private String skladPodrazdeleniya = "x'00'";

	public String getSkladPodrazdeleniya() {
		//System.out.println("getSkladPodrazdeleniya "+skladPodrazdeleniya);
		return skladPodrazdeleniya;
	}

	public void setSkladPodrazdeleniya(String n) {
		//System.out.println("setSkladPodrazdeleniya "+n);
		skladPodrazdeleniya = n;
	}

	public int get_id() {
		return m_id;
	}

	public void set_id(int m_id) {

		this.m_id = m_id;
	}

	public byte[] getAgentID() {

		return mAgentID;
	}

	public void setAgentID(byte[] agentID) {
		mAgentID = agentID;
	}

	public byte[] getPodrazdelenieID() {

		return mPodrazdelenieID;
	}

	public void setPodrazdelenieID(byte[] podrazdelenieID) {
		mPodrazdelenieID = podrazdelenieID;
	}

	public String getAgentIDstr() {

		return mAgentIDstr;
	}

	public void setAgentIDstr(String agentIDstr) {

		mAgentIDstr = agentIDstr;
	}

	public String getAgentSupervisorStr() {

		return mAgentSpervisorStr;
	}

	public void setAgentSuperVisorStr(String id) {

		mAgentSpervisorStr = id;
	}

	public String getPodrazdelenieIDstr() {

		return mPodrazdelenieIDstr;
	}

	public void setPodrazdelenieIDstr(String podrazdelenieIDstr) {
		//System.out.println(this.getClass().getCanonicalName() + ": setPodrazdelenieIDstr: " + podrazdelenieIDstr);
		mPodrazdelenieIDstr = podrazdelenieIDstr;
	}

	public String getAgentName() {

		return mAgentName;
	}

	public void setAgentName(String agentName) {
		//System.out.println(this.getClass().getCanonicalName() + ": setAgentName: " + agentName);
		mAgentName = agentName;
	}

	public String getAgentKod() {
		//System.out.println("getAgentKod " + mAgentKod);
		return mAgentKod;
	}

	public String getAgentKodTrim() {

		return mAgentKod.trim().toUpperCase();
	}

	public void setAgentKod(String agentKod) {
		//System.out.println(this.getClass().getCanonicalName() + ": setAgentKod: " + agentKod);
		mAgentKod = agentKod;
	}

	public String getPodrazdelenieName() {

		return mPodrazdelenieName;
	}

	public void setPodrazdelenieName(String podrazdelenieName) {

		mPodrazdelenieName = podrazdelenieName;
	}

	public String getPodrazdelenieKod() {

		return mPodrazdelenieKod;
	}

	public void setPodrazdelenieKod(String podrazdelenieKod) {

		mPodrazdelenieKod = podrazdelenieKod;
	}
}
