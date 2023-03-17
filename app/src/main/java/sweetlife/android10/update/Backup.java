package sweetlife.android10.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sweetlife.android10.Settings;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.Compress;
import sweetlife.android10.utils.Decompress;

//класс для работы с резервными копиями
public class Backup {
	//резервное копирование базы данных
	public synchronized static boolean reserveDatabase() {
		Settings settings = Settings.getInstance();
		String workDB = settings.getTABLET_DATABASE_FILE();
		String copyDB = settings.TABLET_DATABASE_BACKUP;
		try {
			copyFile(copyDB,workDB );
		}
		catch (Throwable t) {
			t.printStackTrace();
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_RESERVE_DB, copyDB);
			return false;
		}
		return true;
	}
	//резервное копирование базы данных
	public synchronized static boolean _reserveDatabase() {
		Settings settings = Settings.getInstance();
		//получение из настроек пути к каталогу резервных копий
		String pathReserveDir = settings.getTABLET_RESERVE_DIR();
		//создаем объект для упаковки данных
		Compress compressor = new Compress();
		//получаем из настроек путь к базе данных
		String pathToDatabase = settings.getTABLET_DATABASE_FILE();
		//добавляем файл для упаковки
		compressor.addFiles(new String[] { pathToDatabase });
		//формируем имя для архивного файла
		String pathToZipFile = pathReserveDir;
		int lastSepIndex = pathToDatabase.lastIndexOf(File.separator);
		if (lastSepIndex < 0) {
			pathToZipFile += pathToDatabase;
		}
		else {
			pathToZipFile += pathToDatabase.substring(lastSepIndex + 1, pathToDatabase.length());
		}
		pathToZipFile += ".zip";
		try {
			//упаковываем данные
			compressor.zip(pathToZipFile);
			//пишем в лог положительный результат
			LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_RESERVE_DB, pathToZipFile);
		}
		catch (Exception e) {
			//пишем в лог отрицательный результат если произошла ошибка
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_RESERVE_DB, pathToZipFile);
			//возвращаем false - ошибка операции
			return false;
		}
		//возвращаем true - удачная операция
		return true;
	}
	//восстановление базы данных из резервной копии
	public synchronized static boolean restoreDatabase() {
		Settings settings = Settings.getInstance();
		//получение из настроек пути к каталогу резервных копий
		String pathReserveDir = settings.getTABLET_RESERVE_DIR();
		String dir = "";
		String fileName = "";
		File from = null;
		File to = null;
		//получаем из настроек путь к базе данных
		String pathToDatabase = settings.getTABLET_DATABASE_FILE();
		//формируем имя для архивного файла
		int lastSepIndex = pathToDatabase.lastIndexOf(File.separator);
		if (lastSepIndex < 0) {
			fileName += pathToDatabase;
		}
		else {
			dir += pathToDatabase.substring(0, lastSepIndex);
			fileName += pathToDatabase.substring(lastSepIndex + 1, pathToDatabase.length());
		}
		fileName += ".zip";
		//если резервной кипии нет
		if (!new File(pathReserveDir + fileName).exists()) {
			//пишем в лог ошибку операции
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_NO_DB_RESERVE_COPY, pathReserveDir + fileName);
			return false;
		}
		try {
			//файловая переменная на файл базы данных
			from = new File(dir, fileName);
			//файловая переменная на новое имя базы данных
			to = new File(dir, "_" + fileName);
			//переименовываем исходный файл базы данных
			from.renameTo(to);
			//создаем объект для распаковки данных
			Decompress decompressor = new Decompress();
			//распаковываем данные
			decompressor.unzip(dir, pathReserveDir + fileName);
			//пишем в лог результат об успешной операции
			LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_RESTORE_DB, pathToDatabase);
		}
		//если произошла ошибка
		catch (Exception e) {
			//инициализируем файловую переменную путем к базе данных
			from = new File(dir, fileName);
			//если файл существует, то удаляем его
			if (from.exists()) {
				from.delete();
			}
			//переименовываем обратно исходный файл настроек
			to.renameTo(from);
			//пишем в лог результат о неудачной операции
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_RESTORE_DB, pathToDatabase);
		}
		finally {
			//удаляем переименованный файл если существует
			if (to.exists()) {
				to.delete();
			}
		}
		return true;
	}
	//резервное копирование приложения
	public synchronized static void reserveApp() throws Exception {
		Settings settings = Settings.getInstance();
		String appPath = settings.getTABLET_WORKING_DIR() + settings.getAPPLICATION_NAME();
		String appReservePath = settings.getTABLET_RESERVE_DIR() + settings.getAPPLICATION_NAME();
		if (!new File(appPath).exists()) {
			return;
		}
		try {
			copyFile(appReservePath, appPath);
			LogHelper.writeLog(LogHelper.LOG_TYPE_SUCCESS, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_RESERVE_APP, appReservePath);
		}
		catch (Exception e) {
			//пишем в лог результат о неудачной операции
			LogHelper.writeLog(LogHelper.LOG_TYPE_ERROR, LogHelper.LOG_OWNER_UPDATE, LogHelper.LOG_MESSAGE_RESERVE_APP, appReservePath);
			throw new Exception(e);
		}
	}
	/*	
	public synchronized static String checkExistReserveDelta(SQLiteDatabase db)
	{
		if(db == null)
			return null;

		String sServerName = "";
		String sNodeName = "";

		Cursor cursor = db.rawQuery("select [Kod] from SMU_Netbuki", null);
		cursor.moveToFirst();

		if(cursor.getString(0).startsWith("SMU"))
			sServerName = cursor.getString(0);
		else
			sNodeName = cursor.getString(0);

		cursor.moveToNext();
		if(cursor.getString(0).startsWith("SMU"))
			sServerName = cursor.getString(0);
		else
			sNodeName = cursor.getString(0);

		cursor.close();
		cursor = null;

		String sPathToDelta = Settings.props.get(Consts.PROP_PATH_FTP_UPDATE_DELTA);

		int nSepIndex = sPathToDelta.lastIndexOf(File.separator);
		if(nSepIndex >= 0)
			sPathToDelta = sPathToDelta.substring(nSepIndex + 1, sPathToDelta.length());

		sPathToDelta = Settings.props.get(Consts.PROP_PATH_RESERVE_DIR) + String.format(sPathToDelta, sServerName, sNodeName);

		File dir = new File(Settings.props.get(Consts.PROP_PATH_RESERVE_DIR));
			File files[] = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return (name.endsWith(".zip"));
				}
			}
		); 

		String sFilePath = "";
		int nFilesCount = files.length;
		for(int nIndex =0; nIndex < nFilesCount; nIndex++)
		{
			sFilePath = files[nIndex].getAbsolutePath();
			if(sFilePath.startsWith(sPathToDelta))
			{
				return sFilePath;
			}
		}

		return null;
	}
	 */
	public synchronized static void copyFile(String sDest, String sSource) throws Exception {
		System.out.println("copyFile "+sSource+" to "+sDest);
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(sSource);
			output = new FileOutputStream(sDest);
			byte[] buffer = new byte[1024];
			int length;
			try {
				while ((length = input.read(buffer)) > 0)
					output.write(buffer, 0, length);
				output.flush();
				output.close();
				input.close();
			}
			catch (IOException e) {
				throw new Exception("Error copy " + sDest + "file.");
			}
		}
		catch (FileNotFoundException e) {
			throw new Exception("File " + sSource + "not found.");
		}
	}
}
