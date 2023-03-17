package sweetlife.android10.data.orders;

public class PaymentTypeInfo {

	private String  ID;
	private String  Name;
	
	public PaymentTypeInfo( String id, String name ) {
		
		setID(id);
		setName(name);
	}

	public boolean isEmpty() {
		
		if( Name.length() == 0 ) {
			
			return true;
		}
		
		return false;
	}
	
	public String getID() {
		
		return ID;
	}

	public void setID(String iD) {
		
		ID = iD;
	}

	public String getName() {
		
		return Name;
	}

	public void setName(String name) {
		
		Name = name;
	}
}
