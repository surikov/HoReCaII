package reactive.ui;

public class CheckRow {
	public String id;
	public String txt;
	public boolean checked = false;

	public CheckRow(boolean checked, String id, String txt) {
		this.id = id;
		this.txt = txt;
		this.checked = checked;
	}
}
