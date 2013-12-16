package com.blog.dao;

import static com.blog.dao.ConnectionUtil.closeConnect;
import static com.blog.dao.ConnectionUtil.getConnection;
import static com.blog.dao.ConnectionUtil.getMax;
import static com.blog.dao.ConstantUtil.DIARY;
import static com.blog.dao.ConstantUtil.DIARY_FAIL;
import static com.blog.dao.ConstantUtil.DIARY_SUCCESS;
import static com.blog.dao.ConstantUtil.UPDATE_STATE_FAIL;
import static com.blog.dao.ConstantUtil.UPDATE_STATE_SUCCESS;
import static com.blog.dao.ConstantUtil.COMMENT;
import static com.blog.dao.ConstantUtil.VISIT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.blog.bean.Diary;

import com.blog.bean.Comments;

public class WriteControl
{
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;

	// ��������
	public String updateState(String u_no, String state)
	{
		String result = null;
		con = getConnection();
		try
		{
			ps = con.prepareStatement("update user set u_state=? where u_no=?");
			ps.setString(1, state);
			ps.setInt(2, Integer.valueOf(u_no));
			int count = ps.executeUpdate();
			if (count == 1) // �޸ĳɹ�
			{
				result = UPDATE_STATE_SUCCESS;
			}

			else
			{

				result = UPDATE_STATE_FAIL;
			}

		}

		catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnect(con, ps);
		}

		return result;
	}

	// д������־
	public String writeNewDiary(String title, String content, String author)
	{
		String result = null;
		int diary_id = getMax(DIARY);

		try
		{
			con = getConnection();
			ps = con
					.prepareStatement("insert into diary(r_id,r_title,r_content,u_no) values(?,?,?,?) ");
			ps.setInt(1, diary_id);
			ps.setString(2, title);
			ps.setString(3, content);
			ps.setInt(4, Integer.valueOf(author)); // �û�ID

			int count = ps.executeUpdate();

			if (count == 1)
			{
				result = DIARY_SUCCESS;
			} else
			{
				result = DIARY_FAIL;
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnect(con, ps);
		}
		return result;
	}

	// �õ�ĳ�û���־����
	public int getDiarySize(String u_no)
	{
		int result = 0;
		try
		{

			con = getConnection();
			ps = con
					.prepareStatement("select count(r_id) as count from diary where u_no=?");
			ps.setInt(1, Integer.valueOf(u_no)); // ���ò���
			rs = ps.executeQuery();
			if (rs.next())
			{ // �鿴����� ֻ��һ�����ݣ�������־����
				result = rs.getInt(1);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnect(con, ps);
		}
		return result;
	}

	// ��ѯ�û����ռ��б�ķ���
	public ArrayList<Diary> getUserDiary(String u_no, int currentPage, int span)
	{
		ArrayList<Diary> result = new ArrayList<Diary>();
		// �������������
		int start = (currentPage - 1) * span; // ������ʼλ��
		String sql = "select diary.r_id,diary.r_title,diary.r_content,date_format(diary.r_date,'%Y-%c-%e %k:%i:%s'),diary.u_no,user.u_name from diary,user where diary.u_no=? and diary.u_no=user.u_no  order by diary.r_date desc limit ?,?"; // ����������
		// �ռǱ��⡢�ռ����ݡ��ռ�ʱ�䡢�ռ������ߡ��ռ��������ǳ�
		try
		{
			con = getConnection(); // �������
			ps = con.prepareStatement(sql);

			ps.setInt(1, Integer.valueOf(u_no));
			ps.setInt(2, start);
			ps.setInt(3, span);

			rs = ps.executeQuery(); // ִ�в�ѯ

			while (rs.next())
			{ // ��ȡ����������ռǶ���
				String rid = rs.getInt(1) + "";
				String title = rs.getString(2);
				String content = rs.getString(3);
				String date = rs.getString(4);
				String uno = rs.getInt(5) + "";
				String uname = rs.getString(6);
				Diary d = new Diary(rid, title, content, uname, uno, date);
				result.add(d);
			}

			for (Diary d : result)
			{ // Ϊÿ���ռ����������б�
				ArrayList<Comments> cmtList = getComments(d.rid);
				d.setCommentList(cmtList);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnect(con, ps);
		}
		return result;
	}

	// ��ȡ�û���־�����б�ķ���
	public ArrayList<Comments> getComments(String r_id)
	{
		ArrayList<Comments> result = new ArrayList<Comments>();

		String sql = "select date_format(comment.c_date,'%Y-%c-%e %k:%i:%s'),comment.c_content,user.u_name,comment.u_no"
				+ " from comment,user where comment.r_id=? and user.u_no=comment.u_no order by comment.c_date desc";
		try
		{
			con = getConnection(); // �������
			ps = con.prepareStatement(sql); // ���Ԥ�������
			ps.setInt(1, Integer.valueOf(r_id)); // ���ò���
			rs = ps.executeQuery(); // ִ�в�ѯ
			while (rs.next())
			{
				String date = rs.getString(1);
				String content = rs.getString(2);
				String uname = rs.getString(3);
				String uno = rs.getString(4) + "";
				Comments c = new Comments(date, content, uname, uno);
				result.add(c);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	public  int addComment(String c_comment,String r_id,String u_no){
		int result = -1;
		Connection con = null;
		PreparedStatement ps = null;
		try{
			con = getConnection();	//������ݿ�����
			ps = con.prepareStatement("insert into comment(c_id,c_content,u_no,r_id) values(?,?,?,?)");
			ps.setInt(1, getMax(COMMENT));		//�����Զ���ŵ�ֵ
			ps.setString(2, c_comment);			//�������������ֶ�
			ps.setInt(3, Integer.valueOf(u_no));	//�����û����
			ps.setInt(4, Integer.valueOf(r_id));	//�����ռǱ��
			result = ps.executeUpdate();			//ִ�в������
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//��ӷ��ʼ�¼
	public  int addVisitor(String host,String visitor){
		int result = -1;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			closeConnect(con, ps);
		
			}
		return result;
	}
	
	
	//������ɾ��ָ���ռ�
	public  int deleteDiary(String rid){
		int result = -1;
		
		try{
			deleteAllCommentByDiary(rid);			//��ɾ������
			con = getConnection();
			ps = con.prepareStatement("delete from diary where r_id=?");
			ps.setInt(1, Integer.valueOf(rid));
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//������ɾ��ָ����־����������
	public  int deleteAllCommentByDiary(String rid){
		int result = 0;
		try{
			con = getConnection();
			ps = con.prepareStatement("delete from comment where r_id=?");
			ps.setInt(1, Integer.valueOf(rid));
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	//�������޸�ָ����־
	public  int modifyDiary(String rid,String rtitle,String rcontent){
		int result = 0;
		try{
			con = getConnection();
			ps = con.prepareStatement("update diary set r_title=?,r_content=?,r_date=now() where r_id=?");
			ps.setString(1,rtitle);
			ps.setString(2,rcontent);
			ps.setInt(3, Integer.valueOf(rid));
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}

}
