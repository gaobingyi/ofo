package com.gby.bike;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bike", "root", "3666404");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static String QueryID(String id) {
		Connection conn = getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("select * from bikes where id = ?;");
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				
				return rs.getString("password");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "fail";
	}
	
	public static Boolean Submit(String id, String password) {
		Boolean flag = false;//提交成功标志
		
		Connection conn = getConnection();
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("insert into bikes (id, password) values (?, ?);");
			ps.setString(1, id);
			ps.setString(2, password);
			int rows = ps.executeUpdate();
			System.out.println(rows + "");
			if (rows != 0) {
				flag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return flag;		
	}
}
