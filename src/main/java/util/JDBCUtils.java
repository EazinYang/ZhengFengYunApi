package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.testng.annotations.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCUtils{
    public String url1;
    public String username1;
    public String password1;
    public String url2;
    public String username2;
    public String password2;

    //定义数据库连接对象
    private static Connection conn = null;
    public static Connection getConnection() {
        ObjectMapper objectMapper=new ObjectMapper(new YAMLFactory());
        try {
            JDBCUtils jdbcUtils=objectMapper.readValue(JDBCUtils.class.getResource("/util/mysql.yaml"), JDBCUtils.class);
            //你导入的数据库驱动包， mysql。
            conn = DriverManager.getConnection(jdbcUtils.url2,jdbcUtils.username2, jdbcUtils.password2);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static Object[] queryOne(String sql) {
        QueryRunner qr=new QueryRunner();
        try{
            Object[] query = qr.query(getConnection(),sql, new ArrayHandler());
            return query;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<Object[]> queryMulti(String sql) {
        QueryRunner qr=new QueryRunner();
        try{
            List<Object[]> query = qr.query(getConnection(),sql, new ArrayListHandler());
            return query;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
