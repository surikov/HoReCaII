package sweetlife.android10;

import android.os.Environment;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.util.Map;

import reactive.ui.Auxiliary;

public class HRCFirebaseMessagingService extends FirebaseMessagingService{
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage){
		System.out.println("HRCFirebaseMessagingService onMessageReceived");
		try{
			System.out.println("HRCFirebaseMessagingService onMessageReceived notification: " + remoteMessage.getNotification() + ", data: " + remoteMessage.getData());
			if(remoteMessage.getNotification() != null){
				String title = remoteMessage.getNotification().getTitle();
				String body = remoteMessage.getNotification().getBody();

				System.out.println("HRCFirebaseMessagingService onMessageReceived remoteMessage - title: " + title + ", body: " + body);

				String[] parts = body.split("~");
				String bodyNum = "data" + Math.random();
				String bodyDate = "ddMMyyyy";
				String bodyCaption = "Caption";
				String bodyURL = "URL";
				if(parts.length > 0){
					bodyNum = parts[0].trim();
				}
				if(parts.length > 2){
					bodyCaption = parts[2].trim();
				}
				saveFCMData(title, body);
				//saveText(title + "~" + body, bodyNum);

				reactive.ui.Auxiliary.sendNotification(//remoteMessage.getFrom()+": "+
						remoteMessage.getNotification().getTitle().trim()
						, bodyCaption.trim()
						, this
						, sweetlife.android10.supervisor.Activity_FireBaseMessages.class);
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		System.out.println("done HRCFirebaseMessagingService onMessageReceived");
	}

	public static void saveFCMData(String title, String body){
		String[] parts = body.split("~");
		String bodyNum = "data" + Math.random();
		if(parts.length > 0){
			bodyNum = parts[0].trim();
		}
		saveText(title + "~" + body, bodyNum);
	}

	@Override
	public void onDeletedMessages(){
		System.out.println("HRCFirebaseMessagingService onDeletedMessages");
	}

	public static void saveText(String txt, String kod){
		System.out.println("HRCFirebaseMessagingService saveText: " + kod + "/" + txt);
		String folderpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/messages/";
		File folder = new File(folderpath);
		folder.mkdirs();
		String filepath = folderpath + "message." + kod + ".txt";
		System.out.println("HRCFirebaseMessagingService write to: " + filepath);
		new File(filepath).delete();
		Auxiliary.writeTextToFile(new File(filepath), txt);
		/*File[] files = folder.listFiles();
		java.util.Arrays.sort(files);
		int cnt = files.length;
		int nn = 0;
		while(cnt > 25){
			System.out.println("delete: " + files[nn].getAbsolutePath());
			files[nn].delete();
			nn++;
			cnt--;
		}*/
	}
}
