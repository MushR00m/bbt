package services.cache;

import java.util.List;

import play.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import utils.SerializeUtil;

/**
 * 读取redis缓存内容信息内容
 * 
 * @author luo
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JedisHelper implements ICacheService {
	private static final int hotResultsNum = 10;
	private int hashCodeNum = 120;

	/**
	 * 非切片客户端链接
	 */
	private Jedis jedis = null;

	/**
	 * 非切片链接池
	 */
	private JedisPool jedisPool;

	/* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
	private static JedisHelper instance = null;

	/* 静态工程方法，创建实例 */
	public static JedisHelper getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new JedisHelper();
		}
	}

	/* 私有构造方法，防止被实例化 */
	private JedisHelper() {
		jedisPool = getPool();
	}

	/**
	 * 获取非切片连接池.
	 * 
	 * @return 非切片连接池实例
	 */
	public static JedisPool getPool() {
		String host = play.Configuration.root().getString("redis.host");
		int port = play.Configuration.root().getInt("redis.port");
		JedisPool pool = null;
		JedisPoolConfig config = new JedisPoolConfig();
		int maxIdle = play.Configuration.root().getInt("redis.jedisPoolConfig.maxIdle");
		boolean testOnBorrow = play.Configuration.root().getBoolean("redis.jedisPoolConfig.testOnBorrow");
		boolean testOnReturn = play.Configuration.root().getBoolean("redis.jedisPoolConfig.testOnReturn");
		config.setMaxIdle(maxIdle);
		config.setTestOnBorrow(testOnBorrow);// 当调用borrow Object方法时，是否进行有效性检查
		config.setTestOnReturn(testOnReturn);// 当调用return Object方法时，是否进行有效性检查
		try {
			/**
			 * 如果你遇到 java.net.SocketTimeoutException: Read timed out
			 * exception的异常信息 请尝试在构造JedisPool的时候设置自己的超时值.
			 * JedisPool默认的超时时间是2秒(单位毫秒)
			 */
			String name = play.Configuration.root().getString("redis.name");
			pool = new JedisPool(config, host, port, 200, name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pool;
	}

	public String get(String key) {
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public String clear(String key) {
		try {
			jedis = jedisPool.getResource();
			return jedis.del(key) + "";
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 保存字符串
	 * 
	 * @return
	 */
	public boolean set(String key, String value) {
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return true;
	}

	/**
	 * 带有有效期的redis缓存 若为0则也是不失效
	 * 
	 * @return
	 */
	public boolean setWithOutTime(String key, String value, int second) {
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
			jedis.expire(key, second);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return true;
	}

	@Override
	public boolean setObject(String key, Object value, int second) {
		try {
			jedis = jedisPool.getResource();
			jedis.set(key.getBytes(), SerializeUtil.serialize(value));
			if (second != 0) {
				jedis.expire(key.getBytes(), second);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisPool.returnResource(jedis);
		}
		return true;

	}

	@Override
	public Object getObject(String key) {
		try {
			jedis = jedisPool.getResource();
			byte[] result = jedis.get(key.getBytes());
			return SerializeUtil.unserialize(result);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public Object getObjectAndRefreshTimeOut(String key, int timeout) {
		int set = Configuration.root().getInt("web.timeout", 3000);
		try {
			jedis = jedisPool.getResource();
			byte[] result = jedis.get(key.getBytes());
			if (result != null) {// 如果存在则刷新存活时间
				jedis.set(key.getBytes(), result);
				jedis.expire(key, set);
			}
			return SerializeUtil.unserialize(result);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public List<String> getDataByMap(String key) {
		try {
			jedis = jedisPool.getResource();
			return jedis.hvals(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	

}
