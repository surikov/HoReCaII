package sweetlife.android10.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.net.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;

import sweetlife.android10.Settings;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.supervisor.Cfg;

import android.os.Environment;
import android.util.Base64;

public class ftpClient
{
	//объявление объекта - клиент для работы с фтп
	//private FTPClient 	m_ftpClient;
	//объявление описания ошибок
	private static final String	ERROR_FTP_CONNECT 			= "Connect to %s failed!";
	private static final String	ERROR_FTP_LOGIN   			= "User or Password isn't valid!";
	private static final String	ERROR_FTP_FILE_NOT_FOUND   	= "File %s not found!";
	private static final String	ERROR_FTP_STORAGE   		= "There isn't enough disk storage!";
	//сообщение о завершении загрузки
	public static final String MSG_LOAD_FILES_FINISHED = "LoadFilesFinished"; 
	
	public static ArrayList<String> mResponces = new ArrayList<String>();
	//конструктор класса
	//public ftpClient()
	//{
	//}
	//функция провеяет наличие соединения с фтп сервером
	public static boolean isConnected(FTPClient ftpClient)
	{
		//проверяем входные данные, если не инициализированы, то возвращаем false
		if(ftpClient == null)
			return false;
		//если нет соединения - возвращаем false, иначе true
		if(!ftpClient.isConnected())
			return false;
		
		return true;
	}
	//функция подключения к фтп
	private static boolean connect(FTPClient ftpClient) throws IOException
	{
		//System.out.println("connect ftp");
		Settings settings = Settings.getInstance();
		try 
        {
			//проверяем входные данные, если не инициализированы, то возвращаем false
			if(ftpClient == null)
				return false;
			//устанавливаем порт подключения
			ftpClient.setDefaultPort(settings.getFTP_PORT());
			//устанавливаем соединение к фтп-серверу
			ftpClient.connect(InetAddress.getByName(settings.getFTP_SERVER()));
			//m_ftpClient.connect(m_sServer);
			//если фтп недоступен
			if (ftpClient.getReplyCode() != 220)
			{
				//отключаемся
				ftpClient.disconnect();
				//возвращаем false - неудача
				return false;
			}
        } 
        catch (IOException e) 
        {
       	throw new IOException(String.format(ERROR_FTP_CONNECT, settings.getFTP_SERVER()));
        }
        
        try 
        {
        	//логинимся на фтп сервер
        	ftpClient.login(settings.getFTP_USER(), settings.getFTP_PASSWORD());
			//если неверные данные
	        if (ftpClient.getReplyCode() != 230)
			{
	        	//выходим и отключаемся от фтп сервера
	        	ftpClient.logout();
	        	ftpClient.disconnect();
				//возвращаем false - неудача
				return false;
			}
	        //System.out.println("set ftp mode");
			//устанавливаем активный режим работы с фтп(для Windows-сервера, пассивный для *nix-сервера)
	        ftpClient.enterLocalActiveMode();
	        //ftpClient.enterLocalPassiveMode();
	        
	        
			//устанавливаем бинарный тип файлов для работы
	        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
	        //ftpClient.setFileTransferMode(FTP.BLOCK_TRANSFER_MODE);
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        	throw new IOException(ERROR_FTP_LOGIN);
        }
        //System.out.println("ftp connected");
      //возвращаем true - удача
		return true;
	}
	//функция отключения от фтп-сервера
	private static void disconnect(FTPClient ftpClient)
	{
        try 
        {
        	//проверяем входные данные, если не инициализированы, то выходим
			if(ftpClient == null)
				return;
        	//если соединение с фтп не установлено - выходим из функции
        	if(!ftpClient.isConnected())
        		return;
        	//выходим и отключаемся от фтп-сервера
        	ftpClient.logout();
        	ftpClient.disconnect();
		} 
        catch (Exception e) 
        {
		}
	}
	//функция проверяет наличие файла/каталога на фтп по передаваемому пути или начальной части пути при выставленном флаге bStartsWith
	//и возвращает полный путь к файлу
	public static String getPathIfExist(String deltaPath, String responsePath)
	{
		LogHelper.debug("getPathIfExist "+deltaPath+", "+responsePath);
		mResponces.clear();
		
		String serverDeltaPath = null;
		//инициализация фтп-клиента
		FTPClient oFtpClient = new FTPClient();
		oFtpClient.setConnectTimeout(30*1000);
		oFtpClient.setDataTimeout(30*1000);
		//инициализация движка поиска на фтп-сервере
		FTPListParseEngine engine = null;
		
		try {
			//коннектимся к FTP серверу
			connect(oFtpClient);
			//если нет соединения к фтп - возвращаем null
			if(!isConnected(oFtpClient)){
				//System.out.println("not connected");
				return null;
				}
			//получаем индекс последнего разделителя в пути
			int nLastIndexSeparator = deltaPath.lastIndexOf(File.separator);
			//выставляем начальные значения для пути и имени файла
			String sRemotePath = File.separator;
			String sDeltaFileName = "";
			String sResponseFileName = "";
			//если разделитель найден
			if(nLastIndexSeparator > 0)
			{
				//получаем путь к каталогу
				sRemotePath = deltaPath.substring(0, nLastIndexSeparator+1);
				//получаем имя файла
				sDeltaFileName = deltaPath.substring(nLastIndexSeparator+1, deltaPath.length());
				sResponseFileName = deltaPath.substring(nLastIndexSeparator+1, responsePath.length());
			}
			else {//если разделитель не найден - значит передано имя файла
				sDeltaFileName = deltaPath;
				sResponseFileName = responsePath;
			}
			
			//инициализируем движок поиска на фтп-сервере путем к каталогу
			engine = oFtpClient.initiateListParsing("");
			//если движок не инициализировался - возвращаем null
			if(engine == null)
			{
				disconnect(oFtpClient);
				return null;
			}
			//System.out.println("probe "+sDeltaFileName);
	        //цикл по всем файлам каталога
	        while (engine.hasNext()) 
	        {
	        	//получаем список <= 25 файлов
	        	FTPFile[] files = engine.getNext(25);
		        //FTPFile[] files = m_ftpClient.listFiles(sRemotePath); 
	        	//цикл по полученным файлам
				for(FTPFile file : files) 
				{
					//получаем имя файла
					String sName = file.getName();
					//System.out.println("	 "+sName);
					if(sName.startsWith(sDeltaFileName)) {
						
						serverDeltaPath = sRemotePath + sName;
					}
					else if(sName.startsWith(sResponseFileName)) {
						
						mResponces.add( sName );
					}
				}
	        }
	        
	        return serverDeltaPath;
		} 
		catch (IOException e) 
		{
			//отключаемся от фтп сервера
			disconnect(oFtpClient);

			//возвращаем null - неудача
			return null;
		}
        finally
        {
        	//отключаемся от фтп сервера
        	disconnect(oFtpClient);
        }
	}
	static String extractFileName(String sSourcePath){
		//String r=sSourcePath;
		int nLastIndexSeparator = sSourcePath.lastIndexOf(File.separator);
		//выставляем начальные значения для пути и имени файла
		String sRemotePath = File.separator;
		String sRemoteFileName = sSourcePath;
		//если разделитель найден
		if(nLastIndexSeparator > -1)
		{
			//получаем путь к каталогу
			sRemotePath = sSourcePath.substring(0, nLastIndexSeparator+1);
			//получаем имя файла
			sRemoteFileName = sSourcePath.substring(nLastIndexSeparator+1, sSourcePath.length());
		}
		
		return sRemoteFileName;
	}
//\\10.10.0.17\productphoto\test
//http://78.40.186.186/photo/test/
	//http://10.10.0.17:81/android/Horeca2.apk
	//http://78.40.186.186/androbmen/
	public static String downloadFile(String toFolder, String remoteFile) throws Exception // throws IOException
	{
		//String base=Settings.getInstance().getBaseURL()+"androbmen/";
		String base=Settings.getInstance().getBaseFileStoreURL();
		String name=extractFileName(remoteFile);
		//System.out.println(base+remoteFile+" -> "+toFolder+" : "+name);
		
		
		URL url;
		HttpURLConnection connection = null;
		//InputStream i = null;
		BufferedInputStream bis = null;
		//ByteArrayOutputStream out = null;
		//FileOutputStream out=new FileOutputStream("");
		//try {
		String urlReal=base+remoteFile;
		//urlReal=urlReal.replace(".apk", ".zip");
		System.out.println("downloadFile "+urlReal);
			url = new URL(urlReal);

			connection = (HttpURLConnection) url.openConnection();
			//Cfg.currentHRC(),Cfg.hrcPersonalPassword()
//if(Cfg.hrcPersonalLogin!=null && Cfg.hrcPersonalPassword!=null) {
		//Cfg.currentHRC(),Cfg.hrcPersonalPassword()
	String userCredentials =Cfg.whoCheckListOwner() + ":" +Cfg.hrcPersonalPassword();
	String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));
	connection.setRequestProperty("Authorization", basicAuth);
//}
connection.connect();
		//if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			//throw new Exception("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
		//}
		InputStream input = (InputStream)connection.getInputStream();
bis = new BufferedInputStream(input, 1024 * 8);



			//i = (InputStream) m.getContent();
			//bis = new BufferedInputStream(i, 1024 * 8);
			//out = new ByteArrayOutputStream();
			FileOutputStream out = new FileOutputStream(toFolder+""+name);
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = bis.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush();
			out.close();
			bis.close();
			//byte[] data = out.toByteArray();
			
		
			//System.out.println("ok");
		return toFolder+""+name;
	}	
	//функция загружает файл sSourcePath с фтп в каталог sDestDir и возвращает путь к загруженному файлу
	public static String downloadFile_(String sDestDir, String sSourcePath) throws Exception // throws IOException
	{
		LogHelper.debug("downloadFile "+			sSourcePath+" to "+sDestDir);	
		//инициализация фтп-клиента
		FTPClient oFtpClient = new FTPClient();
		oFtpClient.setConnectTimeout(30*1000);
		oFtpClient.setDataTimeout(30*1000);
		//инициализация движка поиска на фтп-сервере
		FTPListParseEngine engine = null;
		try
		{
			//валидация входных данных - при неверных данных возвращаем null
			if(sSourcePath == null || sDestDir == null)
				return null;
			//добавляем разделитель в конец пути при его отсутствии
			if(!sDestDir.endsWith(File.separator))
        		sDestDir += File.separator;
			//получаем индекс последнего разделителя в пути
			int nLastIndexSeparator = sSourcePath.lastIndexOf(File.separator);
			//выставляем начальные значения для пути и имени файла
			String sRemotePath = File.separator;
			String sRemoteFileName = "";
			//если разделитель найден
			if(nLastIndexSeparator > 0)
			{
				//получаем путь к каталогу
				sRemotePath = sSourcePath.substring(0, nLastIndexSeparator+1);
				//получаем имя файла
				sRemoteFileName = sSourcePath.substring(nLastIndexSeparator+1, sSourcePath.length());
			}
			else //если разделитель не найден - значит передано имя файла
			{
				sRemotePath = "";
				sRemoteFileName = sSourcePath.substring(1);
				sSourcePath = sRemoteFileName;
			}
			LogHelper.debug("connect to FTP");
			//коннектимся к FTP серверу
			connect(oFtpClient);
			//если нет соединения к фтп - возвращаем null
			if(!isConnected(oFtpClient)){
				LogHelper.debug("can't connect to FTP");
				return null;
				}
			LogHelper.debug("connected to FTP");
			//m_ftpClient.enterLocalActiveMode();
			//m_ftpClient.enterLocalPassiveMode();
		 
			//m_ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
	        //хранит размер необходимого под файл места на диске
	        long nNeededMemorySize = -1;
	        //инициализируем движок поиска на фтп-сервере путем к каталогу
	        engine = oFtpClient.initiateListParsing(sRemotePath);
	        //флаг отрицательного результата поиска
	        boolean bNotFound = true;
	        //цикл по всем файлам, пока не будет найден искомый файл
	        //System.out.println("	 need "+sRemoteFileName);
	        while (engine.hasNext() && bNotFound) 
	        {
	        	//получаем список из <= 25 файлов
	        	FTPFile[] files = engine.getNext(25);
		        //FTPFile[] files = m_ftpClient.listFiles(sRemotePath); 
	        	//цикл по полученным файлам
				for(FTPFile file : files) 
				{
					//System.out.println("	 "+file.getName());
					//сравниваем искомое имя файла с полученным именем
					int nResCode = sRemoteFileName.compareToIgnoreCase(file.getName());
					//если совпадают
					if(nResCode == 0)
					{
						//получаем размер файла
						nNeededMemorySize = file.getSize();
						//выставляем флаг об успешном поиске
						bNotFound = false;
						//выходим из цикла
						break;
					}
				}
	        }
			//если файл не найден - кидаем исключение с ошибкой
			if(nNeededMemorySize < 0)
				throw new IOException(ERROR_FTP_FILE_NOT_FOUND);
	        //хранит количество доступной памяти на диске
	        long nAvailableMemorySize = -1;
	        //если сохраняем на внешнем диске, то получаем размер доступной памяти на нем, иначе на внутреннем
	        if(!sDestDir.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath()))
	        	nAvailableMemorySize = MemoryStatus.getAvailableInternalMemorySize();
	        else
	        	nAvailableMemorySize = MemoryStatus.getAvailableExternalMemorySize();
	        //если недостаочно места для сохранения файла, то кидаем сообщение об ошибке
	        if(nNeededMemorySize >= nAvailableMemorySize )
	        	throw new IOException(ERROR_FTP_STORAGE);
	        //собираем полный путь к файлу
	        String sPath = sDestDir + sRemoteFileName;

	        //инициализируем выходной поток
	        OutputStream out = new FileOutputStream(sPath);
	        LogHelper.debug("read from FTP "+sPath);
	        //загружаем файл с фтп
	        oFtpClient.retrieveFile(sSourcePath, out);
	        LogHelper.debug("close "+sPath);
	        //закрываем выходной поток
	        out.close();
	        LogHelper.debug("done "+sPath);
	        //возвращаем путь к загруженному файлу
	        return sPath;
		}
		catch(Throwable t){
			t.printStackTrace();
			//disconnect(oFtpClient);
			throw new Exception();
		}
		/*
		catch(SocketException e)
		{
			//отключаемся от фтп сервера
        	disconnect(oFtpClient);

			//если произошло исключение, то транслируем его в функцию вызова
			throw new IOException(e.toString());
		}
		catch(UnknownHostException e)
		{
			//отключаемся от фтп сервера
        	disconnect(oFtpClient);

			//если произошло исключение, то транслируем его в функцию вызова
			throw new IOException(e.toString());
		}
		catch(IOException e)
		{
			//отключаемся от фтп сервера
        	disconnect(oFtpClient);

			//если произошло исключение, то транслируем его в функцию вызова
			throw new IOException(e.toString());
		}
		*/
		finally
		{
			//отключаемся от фтп сервера
        	disconnect(oFtpClient);
		}
	}
	
	public static boolean deleteFile(String remoteFTPPath) {
		
		FTPClient oFtpClient = new FTPClient();
		oFtpClient.setConnectTimeout(300*1000);
		oFtpClient.setDataTimeout(300*1000);
		try
		{			
			//коннектимся к FTP серверу
			connect(oFtpClient);
			//если нет соединения к фтп - выходим
			if(!isConnected(oFtpClient)) {
			
				return false;
			}
			
			return oFtpClient.deleteFile(remoteFTPPath);
		}
		catch (Exception e) {
			
			e.printStackTrace();
			return false;
		}
		finally {
			//отключаемся от фтп сервера
        	disconnect(oFtpClient);
		}
	}
	public static void uploadFile(String sLocalFilePath, String sRemoteFTPPath) throws IOException
	{
		
	}
	//функция выгружает файл sLocalFilePath на фтп-сервер по пути sRemoteFTPPath
	public static void uploadFile_(String sLocalFilePath, String sRemoteFTPPath) throws IOException
	{
		//инициализация фтп-клиента
		FTPClient oFtpClient = new FTPClient();
		oFtpClient.setConnectTimeout(300*1000);
		oFtpClient.setDataTimeout(300 * 1000);
		try
		{			
			//коннектимся к FTP серверу
			connect(oFtpClient);
			//если нет соединения к фтп - выходим
			if(!isConnected(oFtpClient))
				return;
			
			//m_ftpClient.enterLocalActiveMode();
			//m_ftpClient.enterLocalPassiveMode();
		 
			//инициализируем входной поток
			BufferedInputStream buffIn = null;
	    	FileInputStream InputFile = new FileInputStream(sLocalFilePath);
	        buffIn = new BufferedInputStream(InputFile);
	    	//загружаем файл на фтп-сервер
	        oFtpClient.storeFile(sRemoteFTPPath, buffIn);
	        //закрываем входной поток
	        buffIn.close();
		}
		catch(SocketException e)
		{
			//отключаемся от фтп сервера
        	disconnect(oFtpClient);

			//если произошло исключение, то транслируем его в функцию вызова
			throw new IOException(e.toString());
		}
		catch(UnknownHostException e)
		{
			//отключаемся от фтп сервера
        	disconnect(oFtpClient);

			//если произошло исключение, то транслируем его в функцию вызова
			throw new IOException(e.toString());
		}
		catch(IOException e)
		{
			//отключаемся от фтп сервера
        	disconnect(oFtpClient);

			//если произошло исключение, то транслируем его в функцию вызова
			throw new IOException(e.toString());
		}
		finally
		{
			//отключаемся от фтп сервера
        	disconnect(oFtpClient);
		}
	}
}
