package sweetlife.android10.monitor;

import java.net.InetAddress;
import java.util.*;
import java.io.*;

import sweetlife.android10.ApplicationHoreca;
import sweetlife.android10.Settings;
import sweetlife.android10.utils.Hex;

import android.database.Cursor;

import org.apache.commons.net.ftp.FTPClient;

public class SQLexec2 extends TimerTask {
	static SQLexec2 me = null;
	int cntr = 0;
	boolean lock = false;
	boolean go = false;
	Timer timer;//= new Timer();

	private SQLexec2() {
	}
	public static void go() {
		if (me == null) {
			//System.out.println("sweetlife.horeca.monitor.SQLexec.go()");
			//System.out.println(ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod());
			me = new SQLexec2();
			me.timer = new Timer();
			me.timer.scheduleAtFixedRate(me, new Date(), 15*60 * 1000);
			//me.execute(true);
		}
	}
	public static void end() {
		if (me != null) {
			//System.out.println("sweetlife.horeca.monitor.SQLexec.end()");
			me.stopListen();
			me = null;
		}
	}
	void stopListen() {
		//done = true;
		timer.cancel();
	}
	void deleteCommand() {
		//System.out.println("deleteCommand");
		try {
			//System.out.println("execute");
			FTPClient ftpClient = new FTPClient();
			ftpClient.setDefaultPort(Settings.getInstance().getFTP_PORT());
			ftpClient.connect(InetAddress.getByName(Settings.getInstance().getFTP_SERVER()));
			if (ftpClient.getReplyCode() == 220) {
				ftpClient.login(Settings.getInstance().getFTP_USER(), Settings.getInstance().getFTP_PASSWORD());
				if (ftpClient.getReplyCode() == 230) {
					ftpClient.enterLocalActiveMode();
					ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
					String path = "/android/monitor/" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + ".sql";
					ftpClient.deleteFile(path);
				}
				else {
					System.out.println(this.getClass().getCanonicalName() +"can't login");
				}
				ftpClient.logout();
			}
			else {
				System.out.println(this.getClass().getCanonicalName() +"can't connect");
			}
			ftpClient.disconnect();
		}
		catch (Throwable t) {
			System.out.println(this.getClass().getCanonicalName() + ".deleteCommand() " + t.getMessage());
		}
	}
	void uploadResult(String result) {
		//System.out.println("uploadResult " + result);
		try {
			//System.out.println("execute");
			FTPClient ftpClient = new FTPClient();
			ftpClient.setDefaultPort(Settings.getInstance().getFTP_PORT());
			ftpClient.connect(InetAddress.getByName(Settings.getInstance().getFTP_SERVER()));
			if (ftpClient.getReplyCode() == 220) {
				ftpClient.login(Settings.getInstance().getFTP_USER(), Settings.getInstance().getFTP_PASSWORD());
				if (ftpClient.getReplyCode() == 230) {
					ftpClient.enterLocalActiveMode();
					ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
					String path = "/android/monitor/" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + "." + (new Date().getTime() + ".csv");
					BufferedInputStream buffIn = null;
					buffIn = new BufferedInputStream(new ByteArrayInputStream(result.getBytes("windows-1251")));
					ftpClient.storeFile(path, buffIn);
				}
				else {
					System.out.println(this.getClass().getCanonicalName() +"can't login");
				}
				ftpClient.logout();
			}
			else {
				System.out.println(this.getClass().getCanonicalName() +"can't connect");
			}
			ftpClient.disconnect();
		}
		catch (Throwable t) {
			System.out.println(this.getClass().getCanonicalName() + ".uploadResult() " + t.getMessage());
		}
	}
	String executeCommand(String command) {
		//System.out.println("executeCommand " + command);
		//String result = "";
		StringBuffer sb=new StringBuffer();
		try {
			Cursor cursor = ApplicationHoreca.getInstance().getDataBase().rawQuery(command, null);
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				//result = result + ",'" + cursor.getColumnName(i) + "'";
				sb.append(";'");
				sb.append( cursor.getColumnName(i) );
				sb.append("'");
			}
			while (cursor.moveToNext()) {
				//result = result + "\n";
				sb.append("\n");
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					String v = "";
					try {
						v = cursor.getString(i);
					}
					catch (Throwable t) {
						v = Hex.encodeHex(cursor.getBlob(i));
					}
					//result = result + ",'" + v + "'";
					sb.append( ";'");
					sb.append(v);
					sb.append("'");
				}
			}
		}
		catch (Throwable t) {
			//result = result + "\n" + t.getMessage();
			sb.append("\n");
			sb.append(t.getMessage());
			//t.printStackTrace();
		}
		sb.append( "\n\ncommand:\n" );
		sb.append( command);
		return sb.toString();// + "\n\ncommand:\n" + command;
	}
	String downloadCommand() {
		String result = "";
		try {
			//System.out.println("downloadCommand");
			FTPClient ftpClient = new FTPClient();
			ftpClient.setDefaultPort(Settings.getInstance().getFTP_PORT());
			ftpClient.connect(InetAddress.getByName(Settings.getInstance().getFTP_SERVER()));
			if (ftpClient.getReplyCode() == 220) {
				ftpClient.login(Settings.getInstance().getFTP_USER(), Settings.getInstance().getFTP_PASSWORD());
				if (ftpClient.getReplyCode() == 230) {
					ftpClient.enterLocalActiveMode();
					ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
					String path = "/android/monitor/" + ApplicationHoreca.getInstance().getCurrentAgent().getAgentKod().trim() + ".sql";
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ftpClient.retrieveFile(path, baos);
					result = new String(baos.toByteArray(), "windows-1251");
				}
				else {
					System.out.println(this.getClass().getCanonicalName() +"can't login");
				}
				ftpClient.logout();
			}
			else {
				System.out.println(this.getClass().getCanonicalName() +"can't connect");
			}
			ftpClient.disconnect();
		}
		catch (Throwable t) {
			System.out.println(this.getClass().getCanonicalName() + ".downloadCommand() " + t.getMessage());
		}
		//System.out.println("result is " + result);
		return result;
	}
	/*@Override
	protected String doInBackground(Boolean... params) {
		while (!done) {
			try {
				execute();
				Thread.sleep(3 * 1000);
			}
			catch (Throwable t) {
				return "SQLexec: " + t.getMessage();
			}
		}
		return "";
	}
	@Override
	protected void onProgressUpdate(String... progress) {
		//setProgressPercent(progress[0]);
	}
	@Override
	protected void onPostExecute(String result) {
		//showDialog("Downloaded " + result + " bytes");
	}*/
	@Override
	public void run() {
		//System.out.println(this.getClass().getCanonicalName() +".run "+lock);
		try {
			if (!lock) {
				lock = true;
				String command = downloadCommand();
				if (command.length() > 0) {
					deleteCommand();
					String result = executeCommand(command);
					uploadResult(result);
				}
			}
		}
		catch (Throwable t) {
			System.out.println(t.getMessage());
		}
		lock = false;
		//System.out.println(this.getClass().getCanonicalName() +" done"                                                                                                                                                                                                                                                               );
	}
}
