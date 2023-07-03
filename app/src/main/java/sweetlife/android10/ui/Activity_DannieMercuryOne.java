package sweetlife.android10.ui;

import android.app.Activity;
import android.content.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.text.*;
import android.view.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import sweetlife.android10.supervisor.*;
import reactive.ui.*;
import sweetlife.android10.*;
import tee.binding.*;
import tee.binding.it.*;
import tee.binding.task.Task;

public class Activity_DannieMercuryOne extends Activity {
	final int RETURN_FROM_GALLERY = 123;
	final int RETURN_FROM_PHOTO = 321;
	Layoutless layoutless;
	MenuItem menuUpload;
	MenuItem menuDelete;
	MenuItem menuFile;
	Note mercuryGUID = new Note();
	//Note uploadComment = new Note();
	Note filePath = new Note();
	//Note kontragentName = new Note();
	//Numeric kontragentKode = new Numeric();
	Note id = new Note();
	Bough extras;
	Bough data;
	TextWatcher textWatcher = new TextWatcher() {
		//a80c2ef5-155-c-43fd-a9cf-badc527d5294
		//72954a5c-4d9b-4d39-bbbe-f3b54e7a2db0
		boolean lock;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!lock) {
				lock = true;
				int n = 8;
				if (s.length() > n && (s.toString().charAt(n) != '-')) {
					s.insert(n, "-");
				}
				n = n + 4 + 1;
				if (s.length() > n && (s.toString().charAt(n) != '-')) {
					s.insert(n, "-");
				}
				n = n + 4 + 1;
				if (s.length() > n && (s.toString().charAt(n) != '-')) {
					s.insert(n, "-");
				}
				n = n + 4 + 1;
				if (s.length() > n && (s.toString().charAt(n) != '-')) {
					s.insert(n, "-");
				}
				n = n + 12 + 1;
				if (s.length() > n && (s.toString().charAt(n) != ' ')) {
					s.insert(n, " ");
				}
				lock = false;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layoutless = new Layoutless(this);
		setContentView(layoutless);
		extras = Auxiliary.activityExatras(this);
		String sql = "select DannieMercury._id as id,comment,klient,guid,file,saved,kontragenty.naimenovanie as naimenovanie,file as file"//
				+ " from DannieMercury"//
				+ " join kontragenty on kontragenty.kod=DannieMercury.klient"//
				+ " where DannieMercury._id=" + extras.child("id").value.property.value()//
				;
		data = Auxiliary.fromCursor(ApplicationHoreca.getInstance().getDataBase().rawQuery(sql, null));
		mercuryGUID.value(data.child("row").child("guid").value.property.value());
		filePath.value(data.child("row").child("file").value.property.value());
		this.setTitle("GUID Меркурий для " + data.child("row").child("naimenovanie").value.property.value());//extras.child("id").value.property.value());
		RedactText guidField = new RedactText(this);
		guidField.addTextChangedListener(textWatcher);
		layoutless//
				.child(new Decor(this).labelText.is("GUID").labelAlignRightCenter()//
						.left().is(0 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(5 * Auxiliary.tapSize).height().is(Auxiliary.tapSize)//
				)//
				.child(guidField.text.is(mercuryGUID)//
						.left().is(5.5 * Auxiliary.tapSize).top().is(1 * Auxiliary.tapSize).width().is(9 * Auxiliary.tapSize).height().is(Auxiliary.tapSize)//
				)//
				.child(new Decor(this).labelText.is("Файл").labelAlignRightCenter()//
						.left().is(0 * Auxiliary.tapSize).top().is(2 * Auxiliary.tapSize).width().is(5 * Auxiliary.tapSize).height().is(Auxiliary.tapSize)//
				)//
				.child(new RedactText(this).text.is(filePath)//
						.left().is(5.5 * Auxiliary.tapSize).top().is(2 * Auxiliary.tapSize).width().is(9 * Auxiliary.tapSize).height().is(Auxiliary.tapSize)//
				)//
		;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menuUpload = menu.add("Выгрузить");
		menuDelete = menu.add("Удалить");
		menuFile = menu.add("Выбрать файл");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == menuUpload) {
			promptUpload();
		}
		if (item == menuDelete) {
			promptDelete();
		}
		if (item == menuFile) {
			promptFile();
		}
		return super.onOptionsItemSelected(item);
	}

	boolean checkGUID() {
		String guid = mercuryGUID.value().trim().replace(' ', '+').replace('\n', '+');
		//http://api.vetrf.ru/docs/
		String pattern = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";
		return guid.matches(pattern);

	}

	void promptUpload() {
		if (!checkGUID()) {
			Auxiliary.warn("Неверный GUID. Пример заполнения: \n12345678-abcd-eeee-ffff-1234567890ab", this);
			return;
		}
		new Expect().status.is("Выгрузка...").task.is(new Task() {
			@Override
			public void doTask() {
				saveOne();
				String hrc = ApplicationHoreca.getInstance().getCurrentAgent().getAgentName().trim();
				String guid = mercuryGUID.value().trim().replace(' ', '+').replace('\n', '+');
				String rash = "";
				String kod = data.child("row").child("klient").value.property.value();

				//System.out.println(url);
				//String url = "http://89.109.7.162/GolovaNew/hs/danniemercury/hrc23/80104?guid=72954a5c-4d9b-4d39-bbbe-f3b54e7a2db0&rash=jpeg";
				//              http://89.109.7.162/GolovaNew/hs/danniemercury/х0085/101075?guid=ggfff
				String file = filePath.value();
				byte[] bytes = null;
				String comment = "";
				try {
					if (filePath.value().length() > 1) {
						String filenameArray[] = filePath.value().split("\\.");
						String extension = filenameArray[filenameArray.length - 1];
						rash = "&rash=" + extension.toLowerCase().trim();
						File iofile = new File(filePath.value());
						int length = (int) iofile.length();
						bytes = new byte[length];
						FileInputStream fileInputStream = new FileInputStream(iofile);
						DataInputStream dataInputStream = new DataInputStream(fileInputStream);
						dataInputStream.readFully(bytes);
						dataInputStream.close();
					}
					//String url = sweetlife.horeca.Settings.getInstance().getBaseURL() + "/GolovaNew/hs/danniemercury/" + hrc + "/" + kod + "?guid=" + guid +  rash;
					String url = sweetlife.android10.Settings.getInstance().getBaseURL() + sweetlife.android10.Settings.getInstance().selectedBase1C() + "/hs/danniemercury/" + hrc + "/" + kod + "?guid=" + guid + rash;
					//String url = "http://89.109.7.162/GolovaNew/hs/danniemercury/" + hrc + "/" + kod + "?guid=" + guid +  rash;
					//System.out.println(url);
					//Bough txt = Auxiliary.loadTextFromPOST(url, bytes, 300 * 1000);
					Bough txt = Auxiliary.loadTextFromPrivatePOST(url, bytes, 300 * 1000, Cfg.whoCheckListOwner(), Cfg.hrcPersonalPassword());

					//System.out.println(txt.dumpXML());
					comment = txt.child("message").value.property.value() + ": " + txt.child("raw").value.property.value();
				} catch (Throwable t) {
					t.printStackTrace();
				}
				String sql = "update DannieMercury set comment='"//
						+ comment.replace('\n', ' ').replace('\'', '"').replace('\r', ' ').trim()//
						+ "' where _id=" + extras.child("id").value.property.value();
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
			}
		}).afterDone.is(new Task() {
			@Override
			public void doTask() {
				Activity_DannieMercuryOne.this.finish();
			}
		})//
				.start(this);
	}

	void promptDelete() {
		Auxiliary.pickConfirm(this, "Удалить запись?", "Удалить", new Task() {
			@Override
			public void doTask() {
				String sql = "delete from DannieMercury"//
						+ " where _id=" + extras.child("id").value.property.value()//
						;
				ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
				Activity_DannieMercuryOne.this.finish();
			}
		});
	}

	void promptFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		try {
			Intent ch = Intent.createChooser(intent, "Выбор");
			startActivityForResult(ch, RETURN_FROM_GALLERY);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	void saveOne() {
		String sql = "update DannieMercury set"//
				+ " file='" + filePath.value() + "'"//
				+ " ,guid='" + mercuryGUID.value() + "'"//
				+ " where _id=" + extras.child("id").value.property.value()//
				;
		ApplicationHoreca.getInstance().getDataBase().execSQL(sql);
	}

	void doPhoto() throws Exception {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			File image = new File(Environment.getExternalStorageDirectory(), "/horeca/camera" + String.valueOf(System.currentTimeMillis()) + ".jpg");
			String photoPath = image.getAbsolutePath();
			Uri photoURI = Uri.fromFile(image);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
			startActivityForResult(takePictureIntent, RETURN_FROM_PHOTO);
		}
	}

	@Override
	public void onStop() {
		saveOne();
		super.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//System.out.println("onActivityResult " + requestCode + "/" + resultCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case RETURN_FROM_GALLERY:
					Uri uri = data.getData();
					//System.out.println("RETURN_FROM_GALLERY " + uri);
					//String filePath = Auxiliary.pathForMediaURI(Activity_DannieMercuryOne.this, uri);
					//addFilePath(filePath);
					//System.out.println(filePath);
					filePath.value(Auxiliary.pathForMediaURI(Activity_DannieMercuryOne.this, uri));
					break;
				case RETURN_FROM_PHOTO:
					//addFilePath(photoPath);
					//System.out.println(photoPath);
					break;
			}
		}
	}
}
