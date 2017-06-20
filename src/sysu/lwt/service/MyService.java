package sysu.lwt.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import sysu.lwt.db.DBManager;

public class MyService {
	static PreparedStatement preparedStatement = null;
	static PreparedStatement preparedStatement2 = null;
    static ResultSet resultSet = null;
    static int updateRowCnt = 0;
    static int LOGIN_FAILED = 0;
    static int LOGIN_SUCCEEDED = 1;
    static int REGISTER_FAILED = 2;
    static int REGISTER_SUCCEEDED = 3;

    public static int login(String id, String password) {
        int result = LOGIN_FAILED;
        resultSet = null;
        // ִ�� SQL ��ѯ���
        String sql = "select * from user_list where id='" + id +"'";
        try {
            Connection connect = DBManager.getConnection();
            preparedStatement = connect.prepareStatement(sql);
            try{
                resultSet = preparedStatement.executeQuery();
                // ��ѯ���
                if(resultSet.next()){
                    if(resultSet.getString("password").equals(password)){
                        result = LOGIN_SUCCEEDED;
                        System.out.println("id:" + id
                            + " username:" + resultSet.getString("username")
                            + " --login");
                    }
                }
                preparedStatement.close();
                resultSet.close();
                connect.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("login service result:" + result);
        return result;
    }

    public static int register(String id, String username, String password) {
        int result = REGISTER_FAILED;
        updateRowCnt = 0;
        
        // ִ�� SQL �������
        String sql = "insert into user_list(`id`, `username`,`password`) values ('"
                + id + "', '" + username + "', '" + password + "')";
        String csql = "select * from user_list where id='" + id +"'" + " limit 1";
        try {
            Connection connect = DBManager.getConnection();
            // ���id�Ƿ��Ѿ�����
            preparedStatement2 = connect.prepareStatement(csql);
            if (preparedStatement2.executeQuery().next()) {
            	preparedStatement2.close();
            	connect.close();
            	result = REGISTER_FAILED;
            } else {
            	preparedStatement = connect.prepareStatement(sql);
            	try{
            		updateRowCnt = preparedStatement.executeUpdate();
            		// ������
            		if(updateRowCnt != 0) {
                        result = REGISTER_SUCCEEDED;
                        System.out.println("id:" + id
                        	+ " username:" + username //resultSet.getString("username")
                           	+ " --register");
            		}
            		preparedStatement.close();
            		//resultSet.close();
            		connect.close();
            	} catch(Exception ee){
            		ee.printStackTrace();
            	}
            }
        } catch(Exception ee){
        	ee.printStackTrace();
        }
        System.out.println("register service result:" + result);
        return result;
    }
}
