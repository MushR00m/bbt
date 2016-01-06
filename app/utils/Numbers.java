package utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import play.Logger;


public class Numbers {
	/**
	 * 将src除以div后返回
	 * @param src
	 * @param div
	 * @return
	 */
	public static String intToStringWithDiv(Integer src,Integer div){
		double srcD = Double.valueOf(src);
		double divD = Double.valueOf(div);
		Double result =srcD/divD;
		return doubleTrans(result);
	}
	/**
	 * double类型如果小数点后为零显示整数否则保留
	 * @param num
	 * @return
	 */
	public static String doubleTrans(double num) {
		if (num % 1.0 == 0) {
			return String.valueOf((long) num);
		}
		return String.valueOf(num);
	}

	/**
	 * num类型除以div保留cel位小数
	 * num/div 取cel位小数
	 * @param num
	 * @param div
	 * @param cel
	 * @return
	 */
	public static String doubleWithOne(double num,int div,int cel) {
		BigDecimal b = new BigDecimal(num);
		double f1 = b.divide(new BigDecimal(div)).setScale(cel, BigDecimal.ROUND_HALF_UP).doubleValue();
		return String.valueOf(f1);
	}
	public static Integer parseInt(String str, Integer defaultValue){
		try{
			str = str.replace(".0", "");
			return Integer.parseInt(str);
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	public static Long parseLong(String str, Long defaultValue){
		try{
			str = str.replace(".0", "");
			return Long.parseLong(str);
		}catch(Exception e){
			return defaultValue;
		}
	}
	public static Double parseDouble(String str, Double defaultValue){
		try{
			return Double.parseDouble(str);
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	public static String formatDouble(double str, String regex){
		DecimalFormat df = new DecimalFormat(regex);
		return df.format(str);
	}
	
	
	public static void main(String[] args) {
		String a="22.0";
		System.out.println(intToStringWithDiv(10001, 100));
	}
}
