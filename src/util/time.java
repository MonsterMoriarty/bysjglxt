package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class time {
	static SimpleDateFormat formatter;

	// ��þ�ȷ�����ʱ��
	public static Date secondDate(Date tttt) throws ParseException {
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date secondDate = tttt;
		String date = formatter.format(secondDate);
		secondDate = formatter.parse(date);
		return secondDate;
	}
}
