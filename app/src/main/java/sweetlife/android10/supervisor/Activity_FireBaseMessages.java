package sweetlife.android10.supervisor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;

import reactive.ui.Auxiliary;
import reactive.ui.Column;
import reactive.ui.ColumnDescription;
import reactive.ui.DataGrid;
import reactive.ui.Layoutless;
import tee.binding.*;
import tee.binding.task.*;

public class Activity_FireBaseMessages extends Activity {
	DataGrid grid;
	Layoutless layoutless;
	Bough data;
	ColumnDescription kod;

	@Override
	public void onCreate(Bundle savedInstanceState) {
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

	void deleteOldFiles(File folder) {
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			long day=24 * 60 * 60 * 1000;
			long modifiedMs = f.lastModified()/day;
			long currentMs = new java.util.Date().getTime()/day;

			//System.out.println(f.getAbsolutePath()+" "+((currentMs - modifiedMs)/day)+"/"+currentMs +"/"+ modifiedMs);
			if (currentMs - modifiedMs > 99 ) {
				f.delete();
			}
		}
	}

	void fillData() {

		String folderpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/messages/";
		File folder = new File(folderpath);
		folder.mkdirs();
		deleteOldFiles(folder);
		File[] files = folder.listFiles();
		java.util.Arrays.sort(files);
		SimpleDateFormat frmt = new SimpleDateFormat("dd.MM.yy HH:mm");
		for (int i = 0; i < files.length; i++) {
			File f = files[files.length - 1 - i];
			String num = f.getName().replace(".txt", "");
			//double dd = tee.binding.it.Numeric.string2double(num);
			long dd = f.lastModified();
			java.util.Date date = new java.util.Date((long) dd);

			String title = frmt.format(date);
			String ur = "!";
			String dateEndTxt = null;
			java.util.Date dateEnd = null;
			//f.getAbsolutePath()
			String bdy = Auxiliary.strings2text(Auxiliary.readTextFromFile(f));
			String[] parts1 = bdy.split("~");
			if (parts1.length > 0) {
				String[] parts2 = parts1[0].split(":");
				if (parts2.length > 1) {
					dateEndTxt = Auxiliary.tryReFormatDate(parts2[0], "yyyyMMdd", "dd.MM.yyyy");
					SimpleDateFormat endfrmt = new SimpleDateFormat("dd.MM.yyyy");
					try {
						dateEnd = endfrmt.parse(dateEndTxt);

						dateEnd.setHours(23);
						dateEnd.setMinutes(59);
					} catch (Throwable t) {
						t.printStackTrace();
					}
					title = frmt.format(date) + " (до " + dateEndTxt + ") " + parts2[1];
				} else {
					title = frmt.format(date) + " " + parts1[0];
				}
				if (parts1.length > 1) {
					bdy = parts1[1];
					if (parts1.length > 2) {
						ur = parts1[2];
					}
				}
			}
            /*if (parts1.length > 0) {
                title = frmt.format(date) + " " + parts1[0];
                if (parts1.length > 1) {
                    bdy = parts1[1];
                    String[] p2 = bdy.split("\\{");
                    if (p2.length > 1) {
                        bdy = p2[0];
                        ur = p2[1];
                    }
                    String[] p3 = ur.split("\\}");
                    if (p3.length > 1) {
                        ur = p3[0];
                    }
                }
            }*/
			if (dateEnd != null && dateEnd.before(new java.util.Date())) {
				System.out.println("delete message " + f.getAbsolutePath());
				f.delete();
			} else {
				final String tourl = ur.replace("%ПользовательКод%", Cfg.whoCheckListOwner());
				final String totitle = title;
				Task tap = new Task() {
					@Override
					public void doTask() {
						if (tourl.equals("!")) {
							System.out.println("no url");
						} else {
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
				kod.cell(title, tap, bdy);
			}
		}
		grid.refresh();
	}
}
