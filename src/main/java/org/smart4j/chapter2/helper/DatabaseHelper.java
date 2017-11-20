package org.smart4j.chapter2.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.model.Customer;
import org.smart4j.chapter2.util.CollectionUtil;
import org.smart4j.chapter2.util.PropsUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DatabaseHelper {
    private static final org.slf4j.Logger LOGGER= LoggerFactory.getLogger(PropsUtil.class);
    private static final ThreadLocal<Connection> CONNECTION_THREAD_LOCAL;
    private static final QueryRunner QUERY_RUNNER;
    private static final BasicDataSource DATA_SOURCE;
    static {
        CONNECTION_THREAD_LOCAL=new ThreadLocal<Connection>();
        QUERY_RUNNER=new QueryRunner();
        Properties conf= null;
        try {
            conf = PropsUtil.loadProps("config.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String driver=conf.getProperty("jdbc.driver");
        String url=conf.getProperty("jdbc.url");
        String username=conf.getProperty("jdbc.username");
        String password=conf.getProperty("jdbc.password");

        DATA_SOURCE=new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);
    }
    public static Connection getConnection(){
        Connection conn=CONNECTION_THREAD_LOCAL.get();
        if(conn==null){
            try{
                conn= DATA_SOURCE.getConnection();
            }catch (SQLException e){
                LOGGER.error("get connection failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_THREAD_LOCAL.set(conn);
            }
        }
        return conn;
    }
    public static void closeConnection(){
        Connection conn=CONNECTION_THREAD_LOCAL.get();
        if(conn !=null){
            try{
                conn.close();
            }catch (SQLException e){
                LOGGER.error("close connection failure",e);
                throw new RuntimeException(e);

            }finally {
                CONNECTION_THREAD_LOCAL.remove();
            }

        }
    }

    /**
     * 查询实体列表
     * @param entityClass
     * @param sql
     * @return
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql){
        List<T> entityList;
        Connection conn=CONNECTION_THREAD_LOCAL.get();
        try{
            entityList=QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entityClass));
        }catch (SQLException e){
            LOGGER.error("query entity list failure",e);
            throw  new RuntimeException(e);
        }finally {
            closeConnection();
        }
        return entityList;
}
/**
 * 查询实体
 */
public static <T> T queryEntity(Class<T> entityClass, String sql){
    T entity;
    Connection conn=CONNECTION_THREAD_LOCAL.get();
    try{
        entity=QUERY_RUNNER.query(conn,sql,new BeanHandler<T>(entityClass));
    }catch (SQLException e){
        LOGGER.error("query entity list failure",e);
        throw  new RuntimeException(e);
    }finally {
        closeConnection();
    }
    return entity;
}
/**
 * 执行查询语句
 */
public static List<Map<String,Object>> executeQuery(String sql,Object...params){
    List<Map<String,Object>> result;
    try{
        Connection conn=getConnection();
        result=QUERY_RUNNER.query(conn,sql,new MapListHandler(),params);
    }catch (Exception e){
        LOGGER.error("execute query failure",e);
        throw new RuntimeException(e);
    }
    return result;
}
/**
 * 执行更新语句
 */
public static int executeUpdate(String sql,Object... params){
    int rows=0;
    try{
        Connection conn=getConnection();
        rows=QUERY_RUNNER.update(conn,sql,params);
    }catch (SQLException e){
        LOGGER.error("execute update failure",e);
        throw new RuntimeException(e);

    }finally {
        closeConnection();
    }
    return rows;
}
/**
 * 插入实体
 */
public static <T> boolean insertEntity(Class<T> entityClass,Map<String,Object> fieldMap){
    if(CollectionUtil.isEmpty(fieldMap)){
        LOGGER.error("can not insert entity:fieldMap is empty");
        return false;
    }
    String sql="INSERT INTO"+getTableName(entityClass);
    StringBuilder columns=new StringBuilder("(");
    StringBuilder values=new StringBuilder("(");
    for(String fieldName:fieldMap.keySet()){
        columns.append(fieldName).append(",");
        values.append("?,");
    }
    columns.replace(columns.lastIndexOf(","),columns.length(),")");
    sql+=columns+"VALUES"+values;

    Object[] params=fieldMap.values().toArray();
    return executeUpdate(sql,params)==1;
}
/**
 * 更新实体
 */
public static <T> boolean updateEntity(Class<T> entityClass,long id,Map<String,Object> fieldMap){
    if(CollectionUtil.isEmpty(fieldMap)){
        LOGGER.error("can not update entity:fieldMap is empty");
        return false;
    }
    String sql="UPDATE"+getTableName(entityClass)+"SET";
    StringBuilder columns=new StringBuilder();
    for(String fieldName:fieldMap.keySet()){
        columns.append(fieldName).append("=?,");
    }
    sql+=columns.substring(0,columns.lastIndexOf(","))+"WHERE id=?";
    List<Object> paramList=new ArrayList<Object>();
    paramList.addAll(fieldMap.values());
    paramList.add(id);
    Object[] params=paramList.toArray();
    return executeUpdate(sql,params)==1;
}
/**
 * 删除实体
 */
public static <T> boolean deleteEntity(Class<T> entityClass,long id){
    String sql="DELETE FROM "+getTableName(entityClass)+"WHERE id=?";
    return executeUpdate(sql,id)==1;
}
private static String getTableName(Class<?> entityClass){
    return entityClass.getSimpleName();
    }
}
