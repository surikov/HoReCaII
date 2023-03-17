package sweetlife.android10.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Decompress 
{
	//буфер для записи данных
	private static final int BUFFER = 2048;
	//конструктор класса
	public Decompress()
	{
	} 
	/*
	 * Функция распаковки архива
	 * @sDestDirPath - путь к каталогу распаковки
	 * @sSourcePath - путь к архиву
	 * return void
	 */ 
	public void unzip(String sDestDirPath, String sSourcePath) throws Exception 
	{
		//System.out.println("unzip "+sDestDirPath+ " : "+sSourcePath);
	    //try
	    //{  
	    	//проверяем на корректность входные данные
	    	if(sDestDirPath == null || sSourcePath == null)
				return;
	    	//добавляем слеш в конец пути к папке если его там нет
	    	if(!sDestDirPath.endsWith(File.separator))
	    		sDestDirPath += File.separator;
	    	//создаем путь со всеми каталогами если отсутствуют
			new File(sDestDirPath).mkdirs();
	    	
	    	//инициализируем входной поток
	    	FileInputStream InputFile = new FileInputStream(sSourcePath);
	    	ZipInputStream ZipInputFile = new ZipInputStream(InputFile);
	    	ZipEntry ze = null;
	    	
	    	//цикл по всем файлам и папкам архива
	    	while ((ze = ZipInputFile.getNextEntry()) != null)
	    	{
	    		//собираем полный путь к распаковываемому файлу
	    		String sInputFile = sDestDirPath + ze.getName();

	    		//если каталог, то создаем его
	    		if(ze.isDirectory())
	    		{
	    			File f = new File(sInputFile); 
	    		    f.mkdir();
	    		} 
	    		else 
	    		{ 
	    			//инициализируем выходной поток
	    			FileOutputStream OutputFile = new FileOutputStream(sInputFile);
	    			
	    			//выделяем память под буфер чтения
	    			byte data[] = new byte[BUFFER];
	    			//количество прочитанных байт
	    			int length = -1;
	    			//записываем в файл прочитанные из архива данные
	    			while ( (length = ZipInputFile.read(data, 0, BUFFER)) > 0 )
	    				OutputFile.write(data, 0, length);

	    			//закрываем выходной поток
	    			OutputFile.close();
	    			ZipInputFile.closeEntry(); 
	    		} 
	    	}
	    	//закрываем входной поток
	    	ZipInputFile.close(); 
	    /*} 
	    catch(Exception e) 
	    {
e.printStackTrace();
			//throw new Exception("Error decompress file " + sSourcePath);
	    }*/
	}  
}
