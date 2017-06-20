package sysu.lwt.db;

import java.sql.*;   

public class DBManager {
    
    public static Connection getConnection() {
	    try {
	    	Class.forName("com.mysql.jdbc.Driver");     //����MYSQL JDBC��������   
	    	System.out.println("Success loading Mysql Driver!");
	    } catch (Exception e) {
	    	System.out.print("Error loading Mysql Driver!");
	    	e.printStackTrace();
	    }
	    try {
	    	Connection connect = DriverManager.getConnection(
	    			"jdbc:mysql://localhost:3306/db_test","root","7758");
	        //����URLΪ   jdbc:mysql//��������ַ/���ݿ���  �������2�������ֱ��ǵ�½�û���������

	    	System.out.println("Success connect Mysql server!");
	    	return connect;
	    } catch (Exception e) {
	    	System.out.print("get data error!");
	    	e.printStackTrace();
	    	return null;
	    }
    }
    
    
}
