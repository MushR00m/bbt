package utils;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dates {
    private static final Map<String, SimpleDateFormat> formatCache = new ConcurrentHashMap<>();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat CHINESE_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日");
    private static final SimpleDateFormat CHINESE_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    private static final SimpleDateFormat CHINESE_DATE_TIME_FORMAT_NEW = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT_New = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT_New = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat MONTHANDDAY_FORMAT = new SimpleDateFormat("MM月dd日");
    private static final SimpleDateFormat DATE_TIME_FORMAT_New = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private static final SimpleDateFormat WEEK_FORMAT = new SimpleDateFormat("EEEE");

    /**
     * yyyy年MM月dd日
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return CHINESE_DATE_FORMAT.format(date);
    }

    /**
     * yyyy年MM月dd日 HH:mm:ss
     * @param date
     * @return
     */
    public static String formatDateTime(Date date) {
        return CHINESE_DATE_TIME_FORMAT.format(date);
    }
    /**
     * yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formatDateTime_New(Date date) {
        return CHINESE_DATE_TIME_FORMAT_NEW.format(date);
    }
    /**
     * yyyy.MM.dd
     * @param date
     * @return
     */
    public static String formatDateNew(Date date) {
    	return DATE_FORMAT_New.format(date);
    }
    /**
     * yyyy-MM-dd
     * @param date
     * @return
     */
    public static String formatDate2(Date date) {
    	return DATE_FORMAT2.format(date);
    }
    /**
     * HH:mm
     * @param date
     * @return
     */
    public static String formatTimeNew(Date date) {
    	return TIME_FORMAT_New.format(date);
    }
    /**
     * yyyy.MM.dd HH:mm
     * @param date
     * @return
     */
    public static String formatDateTimeNew(Date date) {
    	return DATE_TIME_FORMAT_New.format(date);
    }
    /**
     * 转成类似11月5日
     * @param date
     * @return
     */
    public static String formatMonthAndDay(Date date) {
    	return MONTHANDDAY_FORMAT.format(date);
    }
    /**
     * 转成星期几
     * @param date
     * @return
     */
	public static String format2Week(Date date) {
		final String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		return dayNames[dayOfWeek - 1];
		// return WEEK_FORMAT.format(date);
	}

    public static String formatDate(Date date, String format) {
        if (null == date || StringUtils.isBlank(format)) {
            return null;
        }
        SimpleDateFormat formatter;
        if (formatCache.containsKey(format)) {
            formatter = formatCache.get(format);
        } else {
            formatter = new SimpleDateFormat(format);
        }

        return formatter.format(date);
    }

    public static Date parseDate(String str) {
        try {
            return DATE_FORMAT.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDate(String source, String format) {
        if (StringUtils.isBlank(source) || StringUtils.isBlank(format)) {
            return null;
        }
        SimpleDateFormat formatter;
        if (formatCache.containsKey(format)) {
            formatter = formatCache.get(format);
        } else {
            formatter = new SimpleDateFormat(format);
        }

        try {
            return formatter.parse(source);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String convert(String source, String sourceFormat, String targetFormat) {
        if (StringUtils.isBlank(source) || StringUtils.isBlank(sourceFormat) || StringUtils.isBlank(targetFormat)) {
            return null;
        }

        return formatDate(parseDate(source, sourceFormat), targetFormat);
    }


    /**
     * 获取当前日期的0点0分0秒
     * @param date
     * @return
     */
    public static Date getBeginOfDay(Date date) {
        if (null == date) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 获取当前时间的23点59分59秒
     * @param date
     * @return
     */
    public static Date getEndOfDay(Date date) {
        if (null == date) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    public static boolean isBetween(Date date, Date begin, Date end) {
        if (null == date) {
            return false;
        }

        if (begin != null && end != null) {
            return !date.before(begin) && !date.after(end);
        }

        if (begin != null) {
            return !date.before(begin);
        }

        if (end != null) {
            return !date.after(end);
        }

        return true;
    }

    public static String getCurrentTime(){
    	return formatDateTime(new Date());
    }
    
    public static String getCurrentDay(){
    	return formatDate(new Date());
    }
    /**
     * 将系统毫秒数转成日期
     * @param time
     * @return
     */
    public static Date parseDateByLong(Long time){
    	Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
    	return calendar.getTime();
    }
    
    /**
     * 获取指定时间后的addDate天内的所有时间
     * @param dBegin
     * @param addDate
     * @return
     */
    public static List<Date> findDates(Date dBegin,int addDate) {  
    	List<Date> lDate = Lists.newArrayList();  
        Calendar calBegin = Calendar.getInstance();  
        // 使用给定的 Date 设置此 Calendar 的时间    
        calBegin.setTime(dBegin);  
        Calendar calEnd = Calendar.getInstance();  
        calEnd.setTime(calBegin.getTime());  
        while (addDate-->0) {  
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量    
            calBegin.add(Calendar.DAY_OF_MONTH, 1);  
            lDate.add(calBegin.getTime());  
        }  
        return lDate;  
    }  
    public static void main(String[] args) {
    	List<Date> a = findDates(new Date(), 7);
    	for(Date b:a){
    		System.out.println(b);
    	}
	}
}
