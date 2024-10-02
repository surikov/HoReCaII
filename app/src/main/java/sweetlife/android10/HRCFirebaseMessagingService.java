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

public class HRCFirebaseMessagingService extends FirebaseMessagingService{
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage){
		System.out.println("HRCFirebaseMessagingService onMessageReceived notification: " + remoteMessage.getNotification() + ", data: " + remoteMessage.getData());
		if(remoteMessage.getNotification() != null){
			String title = remoteMessage.getNotification().getTitle();
			String body = remoteMessage.getNotification().getBody();
			System.out.println("HRCFirebaseMessagingService onMessageReceived remoteMessage - title: " + title + ", body: " + body);
			String[] parts = body.split("~");
			String bodyNum = "N#";
			String bodyDate = "ddMMyyyy";
			String bodyCaption = "Caption";
			String bodyURL = "URL";
			if(parts.length > 0)
				bodyNum = parts[0].trim();
			//if(parts.length > 1)
			//	bodyDate = parts[1].trim();
			if(parts.length > 2)
				bodyCaption = parts[2].trim();
			//if(parts.length > 3)
			//	bodyURL = parts[3].trim();
			saveText(title + "~" + body, bodyNum);
			reactive.ui.Auxiliary.sendNotification(//remoteMessage.getFrom()+": "+
					remoteMessage.getNotification().getTitle().trim()
					, bodyCaption.trim()
					, this
					, sweetlife.android10.supervisor.Activity_FireBaseMessages.class);
		}
	}
/*
	public void ______onMessageReceived(RemoteMessage remoteMessage){
		//saveText("onMessageReceived~"+remoteMessage.getNotification());
		if(remoteMessage.getNotification() != null){
			System.out.println("HRCFirebaseMessagingService onMessageReceived remoteMessage - title: "
					+ remoteMessage.getNotification().getTitle()
					+ ", body: "
					+ remoteMessage.getNotification().getBody()
			);
			String msg = remoteMessage.getNotification().getBody();
			String[] tt = remoteMessage.getNotification().getBody().split("~");
			if(tt.length > 1){
				msg = tt[1];
			}
			String kod = "0";
			String body = remoteMessage.getNotification().getBody();
			String[] parts = body.split("~");
			if(parts.length > 0)
				kod = parts[0];
			body = "";
			for(int i = 1; i < parts.length; i++){
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
		}else{
			//saveText("data size "+remoteMessage.getData().size());
			//System.out.println("data size "+remoteMessage.getData().size());
			//saveText(""+remoteMessage.getMessageId()+"~?");
			if(remoteMessage.getData().size() > 0){
				Map<String, String> data = remoteMessage.getData();
				for(Map.Entry<String, String> entry: data.entrySet()){
					System.out.println("remoteMessage.getData " + entry.getKey() + "/" + entry.getValue());
				}
			}
			if(remoteMessage.getData().size() > 0){
				Map<String, String> data = remoteMessage.getData();
				String title = data.get("title");
				String body = data.get("body");
				String End = data.get("End");
				/for (Map.Entry<String, String> entry : data.entrySet()) {
					title = title + "[" + entry.getKey() + ":" + entry.getValue() + "]";
					System.out.println(entry.getKey() + ":" + entry.getValue());
				}/
				String kod = "0";
				String[] parts = body.split("~");
				if(parts.length > 0)
					kod = parts[0];
				body = "";
				for(int i = 1; i < parts.length; i++){
					body = body + "~" + parts[i];
				}
				reactive.ui.Auxiliary.sendNotification(//remoteMessage.getFrom()+": ?"
						title, body, this, sweetlife.android10.supervisor.Activity_FireBaseMessages.class);
				saveText(End + ":" + title + body, kod.trim());
			}
		}
	}
*/
	@Override
	public void onDeletedMessages(){
		System.out.println("HRCFirebaseMessagingService onDeletedMessages");
	}


	public static void saveText(String txt, String kod){
		System.out.println("HRCFirebaseMessagingService saveText: " + kod+"/"+txt);
		String folderpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/horeca/messages/";
		File folder = new File(folderpath);
		folder.mkdirs();
		String filepath = folderpath + "message." + kod + ".txt";
		System.out.println("HRCFirebaseMessagingService write to: " + filepath);

		new File(filepath).delete();
		Auxiliary.writeTextToFile(new File(filepath), txt);

		File[] files = folder.listFiles();
		java.util.Arrays.sort(files);
		int cnt = files.length;
		int nn = 0;

		while(cnt > 25){
			System.out.println("delete: " + files[nn].getAbsolutePath());
			files[nn].delete();
			nn++;
			cnt--;
		}
	}
}
