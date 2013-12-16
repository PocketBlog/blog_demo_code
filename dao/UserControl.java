package com.blog.dao;

import static com.blog.dao.ConnectionUtil.closeConnect;
import static com.blog.dao.ConnectionUtil.getConnection;
import static com.blog.dao.ConnectionUtil.getMax;
import static com.blog.dao.ConstantUtil.CONNECTION_OUT;
import static com.blog.dao.ConstantUtil.LOGIN_FAIL;
import static com.blog.dao.ConstantUtil.REGISTER_FAIL;
import static com.blog.dao.ConstantUtil.USER;
import static com.blog.dao.ConstantUtil.VISIT;
import static com.blog.dao.ConstantUtil.DELETE_FAIL;
import static com.blog.dao.ConstantUtil.DELETE_SUCCESS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.blog.bean.User;
import com.blog.bean.Visitor;


public class UserControl
{
	private Connection con = null;		//������ȡ���ݿ�����
	private PreparedStatement ps = null;					//����Statement����
	private ResultSet rs = null;
	
	public User checkLogin(String u_no,String u_pwd){
							//����ResultSet����
		try{
			con = getConnection();		//��ȡ���ݿ�����
			if(con == null){			//�ж����ݿ����Ӷ����Ƿ�
				ConstantUtil.errorMessages.add(CONNECTION_OUT); //��ӳ�����Ϣ
				return null;
			}
			ps = con.prepareStatement("select u_no,u_name,u_email,u_state,h_id from user where u_no=? and u_pwd=?");
			
			ps.setInt(1,Integer.valueOf(u_no));				//����Ԥ�������Ĳ���
			ps.setString(2, u_pwd);				//����Ԥ�������Ĳ���
			rs = ps.executeQuery();
			ArrayList<String> result = new ArrayList<String>();
			if(rs.next()){				//�жϽ�����Ƿ�Ϊ��
				for(int i=1;i<=5;i++)
				{
					result.add(rs.getString(i));	//������������ݴ�ŵ�ArrayList��
					System.out.println(rs.getString(i));
				}
				
				String no = result.get(0);			//����û��ĺ���
				String name = result.get(1);		//����û����ǳ�
				String email = result.get(2);		//��ȡ�û������ʼ�
				String state = result.get(3);	//��ȡ�û�״̬
				int hid = new Integer(result.get(4));			//��ȡ�û�ͷ��
				User user = new User(no, name, email, state, hid);  //�������
				
				return user;
			}
			else{						//������ݿ���޴���
				ConstantUtil.errorMessages.add(LOGIN_FAIL);	//���ص�¼������Ϣ
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			closeConnect(con, ps);  //�ر�����
			
		}
		
		return null;
	}
	
	
	
	//�û�ע��
	public User registerUser(String u_name,String u_pwd,String u_email,String u_state,int h_id){
		Connection con = null;		//�������ݿ����Ӷ���
		PreparedStatement ps = null;		//����������
		try{
			con = getConnection();
			if(con == null){			//�ж��Ƿ�ɹ���ȡ����
				ConstantUtil.errorMessages.add(CONNECTION_OUT); //��ӳ�����Ϣ
				return null;		//���ط�����ִ��
			}
			ps = con.prepareStatement("insert into user(u_no,u_name,u_pwd,u_email,u_state,h_id)" +
					"values(?,?,?,?,?,?)");		//����SQL���
			String u_no = String.valueOf(getMax(USER));	//��÷�����û����ʺ�
			int no = Integer.valueOf(u_no);
			int hid = Integer.valueOf(h_id);
			ps.setInt(1, no);			//����PreparedStatement�Ĳ���
			ps.setString(2, u_name);
			ps.setString(3, u_pwd);
			ps.setString(4, u_email);
			ps.setString(5, u_state);
			ps.setInt(6,hid);
			int count = ps.executeUpdate();			//ִ�в���
			if(count == 1){		//�������ɹ�
				User user = new User(u_no, u_name, u_email, u_state, h_id);
				return user;
			}
			else{						//���û�в�������
				ConstantUtil.errorMessages.add(REGISTER_FAIL);		//��ó�����Ϣ
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);  //�ر�����
		}
		return null;
	}
	
	
	
	
	//�޸��û���Ϣ���ǳƣ����飬���䣩
	public int changeUserInfo(String uno,String uname,String uemail,String ustate){
		int result = -1;
		try{
			con = getConnection();	//�������
			ps = con.prepareStatement("update user set u_name=?,u_email=?,u_state=? where u_no=?");	//�������
			ps.setString(1, uname);
			ps.setString(2, uemail);
			ps.setString(3, ustate);
			ps.setInt(4, Integer.valueOf(uno));	//
			result = ps.executeUpdate();		//ִ�и���
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	//�޸�ͷ��ķ���
	public int changeUserHead(String u_id,String h_id)
	{
		int result = -1;
		con = getConnection();
		try
		{
			ps = con.prepareStatement("update user set h_id=? where u_no=?");
			ps.setInt(1, Integer.valueOf(h_id));
			ps.setInt(2, Integer.valueOf(u_id));
			result = ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally{
			
			closeConnect(con, ps);
			
		}
		return result;
	}
	
	
	
	//���ӷÿ�
	public  int addVisitor(String host,String visitor){
		int result = -1;
		int hostId = Integer.valueOf(host);			//���˵�id
		int visitorId = Integer.valueOf(visitor);	//�����ߵ�id
		try{
			con = getConnection();		//������ݿ�����
			//���Ȳ鿴Visitor�Ƿ�����
			ps = con.prepareStatement("select v_no from visit where u_no=? and v_no=?");
			ps.setInt(1, hostId);	//��������id
			ps.setInt(2, visitorId);
			rs = ps.executeQuery();
			if(rs.next()){			//��ȡ���������
				ps = con.prepareStatement("update visit set v_date=now() where u_no=? and v_no=?");
				ps.setInt(1, hostId);		//��������id
				ps.setInt(2, visitorId);	//���÷ÿ�id
				result = ps.executeUpdate();	//ִ�и���
			}
			else{					//���µ��Ǹ��ÿͺ͵�ǰ�ÿͲ���ͬ
				ps = con.prepareStatement("insert into visit(v_id,u_no,v_no) values(?,?,?)");
				ps.setInt(1, getMax(VISIT));				//��������ֵ
				ps.setInt(2, Integer.valueOf(host));		//��������id
				ps.setInt(3, Integer.valueOf(visitor));		//���÷ÿ�id
				result = ps.executeUpdate();				//ִ�в�ѯ				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	
	//������ͨ��΢�����ҵ���Ӧ�û�
	public  User getUser(String uno){
		User user = null;
		try{
			con = getConnection();
			ps = con.prepareStatement("select u_name,u_email,u_state,h_id from user where u_no=?");
			ps.setString(1, uno);
			rs = ps.executeQuery();
			while(rs.next()){		//���������
				String uname = rs.getString(1);
				String uemail = rs.getString(2);
				String ustate = rs.getString(3);
				int hid = Integer.valueOf(rs.getString(4));
				user = new User(uno, uname, uemail, ustate, hid);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return user;
	}
	
	
	//�õ�ĳ���û��ķÿ��б�
	public  ArrayList<Visitor> getVisitors(String uno){
		ArrayList<Visitor> result = new ArrayList<Visitor>();
		try{
			con = getConnection();		//������ݿ�����
			ps = con.prepareStatement("select user.u_no,user.u_name,user.h_id,date_format(visit.v_date,'%Y-%c-%e %k:%i:%s') from user,visit" +
					" where user.u_no=visit.v_no and visit.u_no=? order by visit.v_date desc");	//�ǳơ�ͷ��ʱ��
			ps.setInt(1, Integer.valueOf(uno));
			rs = ps.executeQuery();			//ִ�в�ѯ
			while(rs.next()){				//���������
				String v_no = rs.getInt(1)+"";
				String v_name = rs.getString(2);
				String h_id = rs.getInt(3)+"";
				String v_date = rs.getString(4);
				Visitor v = new Visitor(v_no, v_name, h_id, v_date);
				result.add(v);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//������ɾ��ָ������
	public  String deleteFriend(String u_no,String u_noToDelete){
		String result = null;
		try{
			con = getConnection();	//��ȡ���ݿ�����
			ps = con.prepareStatement("delete from friend where u_noz=? and u_noy=?");	//�������
			ps.setInt(1, Integer.valueOf(u_no));
			ps.setInt(2, Integer.valueOf(u_noToDelete));
			int count = ps.executeUpdate();		//ִ�����
			if(count == 1){	//ɾ���ɹ�
				result = DELETE_SUCCESS;
			}
			else{
				result = DELETE_FAIL;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	//����������û����������Ƿ���ȷ
	public ArrayList<String> checkAndroidLogin(String u_no,String u_pwd){
		ArrayList<String> result = new ArrayList<String>();
		try{
			con = getConnection();		//��ȡ���ݿ�����
			if(con == null){			//�ж����ݿ����Ӷ����Ƿ�
				result.add(CONNECTION_OUT);		//
				return result;
			}
			ps = con.prepareStatement("select u_no,u_name,u_email,u_state,h_id from user where u_no=? and u_pwd=?");
			ps.setString(1, u_no);				//����Ԥ�������Ĳ���
			ps.setString(2, u_pwd);				//����Ԥ�������Ĳ���
			rs = ps.executeQuery();
			if(rs.next()){				//�жϽ�����Ƿ�Ϊ��
				for(int i=1;i<=5;i++){
					result.add(rs.getString(i));	//������������ݴ�ŵ�ArrayList��
				}
			}
			else{						//������ݿ���޴���
				result.add(LOGIN_FAIL);	//���ص�¼������Ϣ
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}

	

}
