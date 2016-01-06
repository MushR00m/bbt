package utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;

import play.Logger;
import play.db.DB;

public class JdbcOper {
	public Connection  con      = null;
	public CallableStatement cst=null;
	public ResultSet rs         = null;
	Transaction tran ;
	

	//获取一个数据库连接的操作Object,并且包装了存储过程的调用
	public static JdbcOper getCalledbleDao(String sql)
	{
		JdbcOper op = new JdbcOper();
		try{
			op.tran = Ebean.getServer( Constants.getDB() ).beginTransaction();
			op.con  = op.tran.getConnection();
			op.cst  = op.con.prepareCall(sql);
		}catch(Exception e)
		{
			Logger.info( "getCalledbleDao JdbcOper error - "+e.toString() );
			e.printStackTrace();
			op.close();
			return null;
		}
		finally{
		}
		return op;
	}
	
	//获取一个数据库连接的操作Object,并且包装了存储过程的调用
	public static JdbcOper getCalledbleDaoWithOutTran(String sql)
	{
		JdbcOper op = new JdbcOper();
		try{
			op.con =DB.getDataSource(Constants.getDB()).getConnection();
			op.cst  = op.con.prepareCall(sql);
		}catch(Exception e)
		{
			Logger.info( "getCalledbleDao JdbcOper error - "+e.toString() );
			e.printStackTrace();
			op.close();
			return null;
		}
		finally{
		}
		return op;
	}
	
	//释放数据库连接；
	public void close()
	{
		try{
			if(tran!=null) tran.commit();
			if(tran!=null) tran.end();
			if(rs!=null) rs.close();
			if(cst!=null) cst.close();
			if(con!=null)con.close();
		}
		catch(Exception e){
			Logger.info(" JdbcOper close--"+e.toString());
		}
		finally{
			tran = null;
			rs   = null;
			cst  = null;
			con  = null;
		}
	}
}
