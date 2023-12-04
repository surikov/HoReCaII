package reactive.ui;

public class CheckRow{
	public String id;
	public String txt;
	public boolean checked = false;
	public int state3 = 0;

	public CheckRow(boolean checked, String id, String txt){
		this(checked, 0, id, txt);
	}

	public CheckRow(boolean checked, int state3, String id, String txt){
		this.id = id;
		this.txt = txt;
		this.checked = checked;
		this.state3 = state3;
	}
}
