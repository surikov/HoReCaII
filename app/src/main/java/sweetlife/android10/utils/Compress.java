package sweetlife.android10.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/*
 * Класс для создания Zip архива
 */
public class Compress 
{
	//буфер для чтения данных
	private static final int BUFFER = 4096;
	//список файлов для архивирования
	private Vector<String>	m_sFiles;
	//конструктор класса
	public Compress() 
	{
		//веделение памяти под список файлов
		m_sFiles = new Vector<String>();
	}
	/*
	 * Функция добавления файлов в список для архивации
	 * @sFiles - массив путей к файлам
	 * return int - количество добавленных файлов
	 */
	public int addFiles(String[] sFiles)
	{
		//получение количества файлов
		int nFilesCount = sFiles.length;
		//количество добавленных файлов
		int nAddedFilesCount = 0;
		
		//цикл по всем значениям массива
		for(int nIndex=0; nIndex < nFilesCount; nIndex++)
		{
			//если не каталог, то добавляем и увеличиваем счетчик
			if(!sFiles[nIndex].endsWith(File.separator))
			{
				m_sFiles.add(sFiles[nIndex]);
				nAddedFilesCount++;
			}
			else //в противном случае
			{
				//добавляем каталог
				m_sFiles.add(sFiles[nIndex]);
				
				//получаем список файлов каталога и рекурсивно добавляем их в список
				File fDir = new File(sFiles[nIndex]);
				if(fDir.isDirectory())
				{
					String[] sSubFiles = fDir.list();
					nAddedFilesCount += addFiles(sSubFiles);
				}
			}
		}
		//возвращаем количество добавленных файлов
		return nAddedFilesCount;
	}
	
	/*
	 * Функция архивирования
	 * @sFullPathZipFile - путь к создаваему архивируемому файлу
	 * return void
	 * throws 
	 * 	Exception
	 */
	public void zip(String sFullPathZipFile) throws Exception 
	{
		//проверяем валидность пути
		if(sFullPathZipFile == null)
			return;
		
		BufferedInputStream oOriginalFile = null;
		FileOutputStream oDestFile = null;
		ZipOutputStream oOutputFile = null;
		
		try
		{
			//инициализируем поток вывода
			oDestFile = new FileOutputStream(sFullPathZipFile);

			oOutputFile = new ZipOutputStream(new BufferedOutputStream(oDestFile));

			//создаем буфер для чтения данных
			byte data[] = new byte[BUFFER];

			//цикл по всем добавленным файлам
			int nFilesCount = m_sFiles.size();
			for(int nIndex=0; nIndex < nFilesCount; nIndex++) 
			{
				//забираем путь к файлу из массива
				String sFileName = m_sFiles.elementAt(nIndex);
				
				//если каталог, то добавляем запись о каталоге в архив
				if(sFileName.endsWith(File.separator))
				{
					ZipEntry entry = new ZipEntry(sFileName);
					oOutputFile.putNextEntry(entry);
				}
				else //если файл
				{

					//инициализируем входной поток
					FileInputStream oInputFile = new FileInputStream(sFileName);
					oOriginalFile = new BufferedInputStream(oInputFile, BUFFER);
					
					//добавляем запись о файле в архив
					ZipEntry entry = new ZipEntry(sFileName.substring(sFileName.lastIndexOf(File.separator) + 1));
					oOutputFile.putNextEntry(entry);
					
					try
					{
						//читаем оригинальный фал и пишем в архив
						int count = -1;
						while ((count = oOriginalFile.read(data, 0, BUFFER)) != -1) 
							oOutputFile.write(data, 0, count);
					}
					catch(Exception ex)
					{
						throw new Exception("Error read/write file " + sFileName);
					}
					finally
					{
						//закрываем открытые потоки
						oInputFile.close();
						oOriginalFile.close();
						oOutputFile.closeEntry();
					}
				}
			}
		}
		catch(Exception e)
		{

			throw new Exception("Error compress file " + sFullPathZipFile);
		}
		finally
		{
			//закрываем открытые потоки
			oOutputFile.finish();
			oOutputFile.close();
			oDestFile.close();
		}
	}
}
