package sweetlife.android10.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public final class MemoryStatus 
{
	public static final int ERROR = -1;
	/*
	 * Функция проверяет доступность внешнего хранилища данных
	 * return boolean - true если доступно, false - иначе
	 */
    static public boolean externalMemoryAvailable() 
    {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    /*
	 * Функция возвращает размер свободной памяти во внутреннем хранилище данных
	 * return long - количество байт памяти
	 */
    static public long getAvailableInternalMemorySize() 
    {
        File fPath = Environment.getDataDirectory();
        
        StatFs stat = new StatFs(fPath.getPath());
        long lBlockSize = stat.getBlockSize();
        long lAvailableBlocks = stat.getAvailableBlocks();
        
        return lAvailableBlocks * lBlockSize;
    }
    /*
	 * Функция возвращает размер доступной памяти во внутреннем хранилище данных
	 * return long - количество байт памяти
	 */
    static public long getTotalInternalMemorySize() 
    {
        File fPath = Environment.getDataDirectory();
        
        StatFs stat = new StatFs(fPath.getPath());
        long lBlockSize = stat.getBlockSize();
        long lTotalBlocks = stat.getBlockCount();
        
        return lTotalBlocks * lBlockSize;
    }
    /*
	 * Функция возвращает размер свободной памяти во внешнем хранилище данных
	 * return long - количество байт памяти
	 */
    static public long getAvailableExternalMemorySize() 
    {
    	if(externalMemoryAvailable()) 
    	{
            File fPath = Environment.getExternalStorageDirectory();
            
            StatFs stat = new StatFs(fPath.getPath());
            long lBlockSize = stat.getBlockSize();
            long lAvailableBlocks = stat.getAvailableBlocks();
            
            return lAvailableBlocks * lBlockSize;
        } 
    	else 
    	{
            return ERROR;
        }
    }
    /*
	 * Функция возвращает размер доступной памяти во внешнем хранилище данных
	 * return long - количество байт памяти
	 */
    static public long getTotalExternalMemorySize() 
    {
        if(externalMemoryAvailable()) 
        {
            File fPath = Environment.getExternalStorageDirectory();
            
            StatFs stat = new StatFs(fPath.getPath());
            long lBlockSize = stat.getBlockSize();
            long lTotalBlocks = stat.getBlockCount();
            
            return lTotalBlocks * lBlockSize;
        } 
        else 
        {
            return ERROR;
        }
    }
    /*
	 * Функция форматирует в строку размер входной памяти
	 * @lSize - размер памяти в байтах
	 * return String - отформатированная строка
	 */
    static public String formatSize(long lSize) 
    {
        String sSuffix = null;
        //если больше 1024 байт
        if (lSize >= 1024) 
        {
        	sSuffix = " KB";
            lSize /= 1024;
            //если остаток больше 1024 Кб
            if (lSize >= 1024) {
            	sSuffix = " MB";
                lSize /= 1024;
            }
        }
    
        //форматируем строку
        StringBuilder resultBuffer = new StringBuilder(Long.toString(lSize));
    
        //вставляем разделитель
        int nCommaOffset = resultBuffer.length() - 3;
        while (nCommaOffset > 0) 
        {
            resultBuffer.insert(nCommaOffset, ',');
            nCommaOffset -= 3;
        }
    
        if (sSuffix != null)
            resultBuffer.append(sSuffix);
        //возвращаем результат
        return resultBuffer.toString();
    }
}
