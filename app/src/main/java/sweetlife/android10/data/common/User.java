package sweetlife.android10.data.common;

public class User {

	private Integer mID;
	private String mStringType;
	private Integer mIntType;
	private String mName;
	private String mPassword;

	public User(Integer id, Integer type, String name, String password) {
		//System.out.println("new User: id: "+id);
		//System.out.println("new User: type: "+type);
		//System.out.println("new User: name: "+name);
		//System.out.println("new User: password: "+password);
		mID = id;
		mIntType = type;
		mStringType = type.toString();
		mName = name;
		mPassword = password;
		if (mPassword == null) {
			mPassword = "";
		}
	}

	public String getName() {

		return mName;
	}

	public String getType() {
		return mStringType;
	}

	public Integer getID() {

		return mID;
	}

	public Integer getTypeInt() {

		return mIntType;
	}

	public String getPassword() {

		return mPassword;
	}
}
