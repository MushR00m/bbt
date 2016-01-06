package services.cache;

import java.util.List;
import play.cache.Cache;

/**
 * play自带Cache
 * 作为redis失败时的备用
 * @author luo
 *
 */
public class CacheMem implements ICacheService{

	public static CacheMem instance = new CacheMem();
	
	@Override
	public boolean setWithOutTime(String key, String value, int timeout) {
		Cache.set(key,value,timeout);
		return false;
	}

	@Override
	public boolean set(String key, String value) {
		Cache.set(key,value,0);
		return true;
	}

	@Override
	public String get(String key) {
		if(Cache.get(key)==null){
			return null;
		}else{
			return Cache.get(key).toString();
		}
		
	}

	
	@Override
	public boolean setObject(String key, Object value,int timeout) {
		Cache.set(key,value,timeout);
		return true;
	}
	
	public Object getObject(String key) {
		return Cache.get(key);
	}
	
	
	@Override
	public Object getObjectAndRefreshTimeOut(String key, int timeout) {
		if(Cache.get(key)==null){
			return null;
		}else{
			return Cache.get(key);
		}
	}

	@Override
	public List<String> getDataByMap(String key) {
		if(Cache.get(key)==null){
			return null;
		}else{
			List<String> result=(List<String>) Cache.get(key);
			return result;
		}
	}

	@Override
	public String clear(String key) {
		Cache.set(key,null,0);
		return null;
	}

}
