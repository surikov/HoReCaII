package sweetlife.android10.log;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

//класс для фильтрации сообщений для записи в лог
public class LogFilter implements Filter {
	//конструктор класса
	public LogFilter() {
	}

	//функция проверяет возможность записи в лог входного сообщения
	//возвращает true, если запись возможна, иначе false
	@Override
	public boolean isLoggable(LogRecord record) {
		//результат проверки
		boolean result = false;

		//получаем массив входных параметров
		Object[] objs = record.getParameters();
		//если нет, то запись запрещена
		if (objs == null)
			return result;

		//если входящее сообщение типа LogMessage или включен режим отладки
		//то запись разрешена
		if ((objs[0] instanceof LogMessage)) {
			result = true;
		}
		//возвращаем результат операции
		return result;
	}
}
