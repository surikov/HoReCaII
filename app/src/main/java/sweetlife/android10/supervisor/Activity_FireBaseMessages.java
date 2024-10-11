package sweetlife.android10.supervisor;

import android.app.*;
import android.content.*;
import android.os.*;
import android.os.Environment;
import android.view.Menu;
import android.view.*;

import java.io.*;
import java.text.*;

import reactive.ui.*;
import reactive.ui.Column;
import reactive.ui.ColumnDescription;
import reactive.ui.DataGrid;
import reactive.ui.Layoutless;
import tee.binding.*;
import tee.binding.task.*;

public class Activity_FireBaseMessages extends Activity{
	DataGrid grid;
	Layoutless layoutless;
	Bough data;
	ColumnDescription kod;
	MenuItem menuRegisterDevice1c;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setTitle("Сообщения");
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		grid = new DataGrid(this);
		kod = new ColumnDescription();
		layoutless.child(grid//
				.columns(new Column[]{ //
						kod.title.is("Сообщения").width.is(layoutless.width().property) //
				})//
				.width().is(layoutless.width().property)//
				.height().is(layoutless.height().property)//
		);
		fillData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menuRegisterDevice1c = menu.add("Проверить соединение");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item == menuRegisterDevice1c){
			doRegisterDevice1c();
		}
		return true;
	}

	void doRegisterDevice1c(){
		//Context c;
		Cfg.requeryFirebaseToken(this);
	}

	void deleteOldFiles(File folder){
		File[] files = folder.listFiles();
		for(int i = 0; i < files.length; i++){
			File f = files[i];
			long day = 24 * 60 * 60 * 1000;
			long modifiedMs = f.lastModified() / day;
			long currentMs = new java.util.Date().getTime() / day;

			//System.out.println(f.getAbsolutePath()+" "+((currentMs - modifiedMs)/day)+"/"+currentMs +"/"+ modifiedMs);
			if(currentMs - modifiedMs > 99){
				f.delete();
			}
		}
	}

	String frmtDate(java.util.Date date){
		try{
			SimpleDateFormat frmt = new SimpleDateFormat("dd.MM.yy HH:mm");
			return frmt.format(date);
		}catch(Throwable t){
			t.printStackTrace();
		}
		return "" + date;
	}

	void fillData(){

		String folderpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/messages/";
		File folder = new File(folderpath);
		folder.mkdirs();
		deleteOldFiles(folder);
		File[] files = folder.listFiles();
		java.util.Arrays.sort(files, new java.util.Comparator<File>(){
			public int compare(File a, File b){
				return (int)(a.lastModified() - b.lastModified());
			}
		});

		for(int i = 0; i < files.length; i++){
			File f = files[files.length - 1 - i];
			String num = f.getName().replace(".txt", "");
			//double dd = tee.binding.it.Numeric.string2double(num);
			long dd = f.lastModified();
			java.util.Date date = new java.util.Date((long)dd);
			String title = frmtDate(date);
			//String title = frmt.format(date);
			//String ur = "!";
			//String dateEndTxt = null;
			//java.util.Date dateEnd = null;
			//f.getAbsolutePath()
			String bdy = Auxiliary.strings2text(Auxiliary.readTextFromFile(f));
			String[] parts = bdy.split("~");
			String msgTitle = "";
			String bodyNum = "";
			String bodyDate = "";
			String bodyCaption = "";
			String bodyURL = "";
			if(parts.length > 0)
				msgTitle = parts[0].trim();
			if(parts.length > 1)
				bodyNum = parts[1].trim();
			if(parts.length > 2)
				bodyDate = parts[2].trim();
			if(parts.length > 3)
				bodyCaption = parts[3].trim();
			if(parts.length > 4)
				bodyURL = parts[4].trim();
			java.util.Date dateEnd = null;
			if(parts.length > 0){
				//String[] parts2 = parts1[0].split(":");
				if(bodyDate.length() > 1){
					String dateEndTxt = Auxiliary.tryReFormatDate(bodyDate, "dd.MM.yyyy", "dd.MM.yyyy");
					SimpleDateFormat endfrmt = new SimpleDateFormat("dd.MM.yyyy");
					try{
						dateEnd = endfrmt.parse(bodyDate);
						dateEnd.setHours(23);
						dateEnd.setMinutes(59);
					}catch(Throwable t){
						t.printStackTrace();
					}
					//title = frmt.format(date) + " (до " + dateEndTxt + ") " + title;
					title = frmtDate(date) + " (до " + dateEndTxt + ") " + title;
				}else{
					//title = frmt.format(date) + " " + title;
					title = frmtDate(date) + " " + title;
				}
				/*if (parts1.length > 1) {
					bdy = parts1[1];
					if (parts1.length > 2) {
						ur = parts1[2];
					}
				}*/
			}

			boolean msgdone = false;
			if(dateEnd != null){
				try{
					if(dateEnd.before(new java.util.Date())){
						msgdone = true;
					}
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
			//if(dateEnd != null && dateEnd.before(new java.util.Date())){
			if(msgdone){
				System.out.println("delete message " + f.getAbsolutePath());
				f.delete();
			}else{
				final String tourl = bodyURL.replace("%ПользовательКод%", Cfg.whoCheckListOwner());
				final String totitle = msgTitle;
				Task tap = new Task(){
					@Override
					public void doTask(){
						if(tourl.length() < 1){
							System.out.println("no url");
						}else{
							System.out.println("go " + tourl);
							//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tourl));
							//startActivity(browserIntent);
							Intent intent = new Intent();
							intent.setClass(Activity_FireBaseMessages.this, ActivityWebView.class);
							intent.putExtra("startup", "" + tourl);
							intent.putExtra("title", "" + totitle);
							Activity_FireBaseMessages.this.startActivity(intent);
						}
					}
				};
				kod.cell(title, tap, bodyCaption);
			}
		}
		grid.refresh();
	}
}
