package vo.api;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 杂志详情页信息VO
 * 
 * @author luobotao
 * @Date 2015年11月10日
 */
public class MagezineInfoVO {
	public String id;
	public String title;
	public List<String> imglist = Lists.newArrayList();
}
