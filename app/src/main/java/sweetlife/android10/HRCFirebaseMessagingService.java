package sweetlife.android10;

import android.os.Environment;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
//import com.google.firebase.quickstart.fcm.R;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;

import java.io.File;
import java.util.Map;

import reactive.ui.Auxiliary;

public class HRCFirebaseMessagingService extends FirebaseMessagingService {
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		System.out.println("onMessageReceived getNotification " + remoteMessage.getNotification());
		//saveText("onMessageReceived~"+remoteMessage.getNotification());
		if (remoteMessage.getNotification() != null) {
			String msg = remoteMessage.getNotification().getBody();
			String[] tt = remoteMessage.getNotification().getBody().split("~");
			if (tt.length > 1) {
				msg = tt[1];
			}
			String kod = "0";
			String body = remoteMessage.getNotification().getBody();
			String[] parts = body.split("~");
			if (parts.length > 0) kod = parts[0];
			body = "";
			for (int i = 1; i < parts.length; i++) {
				body = body + "~" + parts[i];
			}
			//saveText(remoteMessage.getNotification().getTitle()+"~"+remoteMessage.getNotification().getBody(),kod);
			saveText(remoteMessage.getNotification().getTitle() + "~" + body, kod);
			reactive.ui.Auxiliary.sendNotification(//remoteMessage.getFrom()+": "+
					remoteMessage.getNotification().getTitle()
					//,remoteMessage.getNotification().getBody()
					, msg
					, this
					, sweetlife.android10.supervisor.Activity_FireBaseMessages.class);
		} else {
			//saveText("data size "+remoteMessage.getData().size());
			//System.out.println("data size "+remoteMessage.getData().size());
			//saveText(""+remoteMessage.getMessageId()+"~?");
            if(remoteMessage.getData().size()>0){
                Map<String, String> data=remoteMessage.getData();
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    System.out.println("remoteMessage.getData "+entry.getKey() + "/" + entry.getValue());
                }
            }
			if (remoteMessage.getData().size() > 0) {
				Map<String, String> data = remoteMessage.getData();
				String title = data.get("title");
				String body = data.get("body");
				String End = data.get("End");
				/*for (Map.Entry<String, String> entry : data.entrySet()) {
					title = title + "[" + entry.getKey() + ":" + entry.getValue() + "]";
					System.out.println(entry.getKey() + ":" + entry.getValue());
				}*/
				String kod = "0";
				String[] parts = body.split("~");
				if (parts.length > 0) kod = parts[0];
				body = "";
				for (int i = 1; i < parts.length; i++) {
					body = body + "~" + parts[i];
				}
				reactive.ui.Auxiliary.sendNotification(//remoteMessage.getFrom()+": ?"
						title, body, this, sweetlife.android10.supervisor.Activity_FireBaseMessages.class);
				saveText(End+":"+title + body, kod.trim());
			}
		}
	}

	@Override
	public void onDeletedMessages() {
		//
	}


	public static void saveText(String txt, String kod) {
		String folderpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/messages/";
		File folder = new File(folderpath);
		folder.mkdirs();
		String filepath = folderpath + "message." + kod + ".txt";
		System.out.println("write to: " + filepath);
		System.out.println("txt: " + txt);
		new File(filepath).delete();
		Auxiliary.writeTextToFile(new File(filepath), txt);

		File[] files = folder.listFiles();
		java.util.Arrays.sort(files);
		int cnt = files.length;
		int nn = 0;

		while (cnt > 25) {
			System.out.println("delete: " + files[nn].getAbsolutePath());
			files[nn].delete();
			nn++;
			cnt--;
		}
	}
}
