package services;

import services.cache.CacheMem;
import services.cache.ICacheService;
import services.cache.JedisHelper;

public class ServiceFactory {

	/**
	 * 获取Cache操作对象；
	 * 若redis异常，则用Play自带的Cache系统，否则用redis
	 * @return
	 */
	public static ICacheService getCacheService()
	{
		try {
			return JedisHelper.getInstance();
		} catch (Exception e) {
			System.err.print("redis连接失败，错误信息为："+e.toString());
			return CacheMem.instance;
		}
	}
	
	
}
