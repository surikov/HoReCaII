package sweetlife.android10.update;

public class TableField {

	private int	 	mType;
	private String	mName;
	
	TableField( int type, String name ) {
		
		setType(type);
		setName(name);
	}

	public int getType() {
		
		return mType;
	}

	public void setType(int type) {
		
		mType = type;
	}

	public String getName() {
		
		return mName;
	}

	public void setName(String name) {
		
		mName = name;
	}
}
