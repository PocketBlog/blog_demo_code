package com.blog.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionUtil
{
	public static Connection getConnection(){
		Connection con = null;
		//ʹ��JDBCֱ�ӷ������ݿ�
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost/kdwb_db","root","root");			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return con;
	}
	
	//�ر�����
	public static void closeConnect(Connection con,PreparedStatement ps)
	{
		try
		{
			if(con != null)
			{
				con.close();
			}
			con = null;
			
			if(ps != null)
			{
				ps.close();
			}
			ps = null;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}	
	
	
	
	//����:��ȡָ�����еĵ�ǰid����ţ��÷���Ϊͬ������
	public static synchronized int getMax(String table){
		int max = -1;
		Connection con = null;			//�������ݿ����Ӷ���
		Statement st = null;
		ResultSet rs = null;
		try{
			con = getConnection();		//��ȡ���ݿ�����
			st = con.createStatement();	//����һ��Statement����
			String sql = "update max_id set "+table+"="+table+"+1";
			st.executeUpdate(sql);					//���������
			rs = st.executeQuery("select "+table+" from max_id");				//��ѯ�����
			if(rs.next()){
				max = rs.getInt(1);
				return max;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(rs != null){
					rs.close();
					rs = null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				if(st != null){
					st.close();
					st = null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				if(con != null){
					con.close();
					con = null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return max;
	}

}
