package services.magazine;

import java.sql.Types;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import models.Postcompany;
import models.magazine.MagazineInfo;
import models.magazine.Magazinelist;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import play.Configuration;
import play.Logger;
import play.libs.Json;
import utils.Constants;
import utils.JdbcOper;
import utils.Numbers;
import utils.PinyinUtil;
import vo.api.MagezineListVO;

public class MagazineService {
	private static final Logger.ALogger logger = Logger.of(MagazineService.class);

	public static ObjectNode getMagezineListObject(ObjectNode result, int uid,int index,int pageSize){
		List<MagezineListVO> magezineListVOList = Lists.newArrayList();
		String sql="CALL `sp_magazine_list`(?,?,?,?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return null;
		}
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, uid);
			oper.cst.setInt(2, index);
			oper.cst.setInt(3, pageSize);
			oper.cst.registerOutParameter(4, Types.INTEGER);
			oper.rs = oper.cst.executeQuery();
			String lastindex = "";
			while (oper.rs.next()) {
				MagezineListVO magezineListVO=new MagezineListVO();
				magezineListVO.id=oper.rs.getString("magazineid");
				magezineListVO.title=oper.rs.getString("title");
				magezineListVO.desc=oper.rs.getString("remark");
				magezineListVO.imgurl=Configuration.root().getString("oss.image.url")+oper.rs.getString("imgurl");
				magezineListVO.linkurl="magazineDetail://lid="+oper.rs.getString("magazineid");
				magezineListVO.type=oper.rs.getString("typ");
				lastindex = oper.rs.getString("id");
				magezineListVOList.add(magezineListVO);
			}
			result.put("endflag", "1");
			if(!magezineListVOList.isEmpty()){
				int p_totalnum =  oper.cst.getInt(4);//总条数
				if(Numbers.parseInt(lastindex, 0)<=p_totalnum){
					result.put("endflag", "0");
				}
				result.put("lastindex", lastindex);
			}
			result.set("magazinelist", Json.toJson(magezineListVOList));
			return result;
		} catch (Exception e) {
			Logger.info(e.toString());
			return result;
		} finally {
			oper.close();
		}
	}
	
	/**
	 * 根据magid获取magid对应的详情列表
	 * 
	 * @param magid
	 * @return
	 */
	public static List<MagazineInfo> getMagazineImg(int magid) {
		List<MagazineInfo> magazineinfoImgList = Ebean.getServer(Constants.getDB()).find(MagazineInfo.class).where()
				.eq("magid", magid).findList();
		return magazineinfoImgList;
	}

	/**
	 * 根据id获取杂志列表的详情
	 * 
	 * @param id
	 * @return
	 */
	public static Magazinelist getMagazinelistById(int id) {
		return Ebean.getServer(Constants.getDB()).find(Magazinelist.class, id);
	}
	
}