package utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 * 
 * @author luobotao
 * @Date 2015年11月12日
 */
public class PinyinUtil {

	/**
	 * 将汉字转换为全拼
	 * @param src
	 * @return
	 */
	public static String getPingYin(String src) {
		char[] pinyinChar = src.toCharArray();// 将汉字转成char型

		String[] oneToOne = new String[pinyinChar.length];// 单个取出字符

		// 设置汉字拼音输出的格式
		HanyuPinyinOutputFormat formatToLow = new HanyuPinyinOutputFormat();
		formatToLow.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写
		formatToLow.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		formatToLow.setVCharType(HanyuPinyinVCharType.WITH_V);

		String result = "";
		int t0 = pinyinChar.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				if (java.lang.Character.toString(pinyinChar[i]).matches("[\\u4E00-\\u9FA5]+")) {
					oneToOne = PinyinHelper.toHanyuPinyinStringArray(pinyinChar[i], formatToLow);// 将汉字的几种读音都存到oneToOne数组中(有字是多音字)
					char c = oneToOne[0].charAt(0);// 大写第一个字母
					String pinyinFirstBig = String.valueOf(c).toUpperCase().concat(oneToOne[0].substring(1));
					result += pinyinFirstBig;// 取出该汉字全拼的第一种读音并连接到字符串result后
				} else {
					result += java.lang.Character.toString(pinyinChar[i]);// 如果不是汉字字符，直接取出字符并连接到字符串t4后
				}
			}
			return result;
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取首个汉字的首字母
	 * @param src
	 * @return
	 */
	public static String getFirstPingYin(String src) {
		String pinyin = getPingYin(src);
		char[] pinyinChar = pinyin.toCharArray();//转成char型
		if(pinyinChar.length>0){
			return  String.valueOf(pinyinChar[0]).toUpperCase();
		}else{
			return "";
		}
	}
}
