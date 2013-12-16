package com.blog.dao;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static com.blog.dao.ConstantUtil.HEAD;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.blog.bean.HeadImg;
import com.blog.bean.P_Comments;
import com.blog.bean.PhotoInfo;
import static com.blog.dao.ConnectionUtil.closeConnect;
import static com.blog.dao.ConnectionUtil.getConnection;
import static com.blog.dao.ConnectionUtil.getMax;
import static com.blog.dao.ConstantUtil.ALBUM;
import static com.blog.dao.ConstantUtil.PHOTO;
import static com.blog.dao.ConstantUtil.P_COMMENT;
public class PhotoControl {
	private Blob result = null;
	private Connection con = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	public  Blob getHeadBlob(String hid){
		
		try{
			con = getConnection();	//�������
			ps = con.prepareStatement("select h_data from head where h_id=?");
			ps.setInt(1, Integer.valueOf(hid));		//���ò���
			rs = ps.executeQuery();		//ִ�в�ѯ
			if(rs.next()){		//���ҽ����
				result = rs.getBlob(1);		//��ֵ
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);  //�ر�����
		}
		return result;
	}
	
	
	
	
	
	public ArrayList<HeadImg> gethandList(int pageNo,int count,int u_no)
	{
		 
		 con = getConnection();	//�������
		int start = (pageNo-1)*count;	//���㿪ʼλ��
		ArrayList<HeadImg> list = new ArrayList<HeadImg>(); 
		try
		{
			ps = con.prepareStatement("select h_id from head where u_no=? limit ?,?");
			ps.setInt(1, u_no);
			ps.setInt(2,start);
			ps.setInt(3, count);
			rs = ps.executeQuery();
			while(rs.next())
			{
				HeadImg head = new HeadImg(rs.getInt(1));
				list.add(head);
			}
			return list;
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return null;
	}
	
	
	
	
	public  int getHeadSize(String u_no){
		int result = 0;
		try{
			con = getConnection();		//�������
			ps = con.prepareStatement("select count(h_id) as count from head where u_no=?");
			int uno = Integer.valueOf(u_no);
			ps.setInt(1,uno);
			rs = ps.executeQuery();	//ִ�в�ѯ
			if(rs.next()){		//�鵽����
				result = rs.getInt(1);	//��ȡ����
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con,ps);
		}
		
		return result;
	}
	
	
	
	public  int insertHeadFile(File file,String hdes,String uno){
		int result = -1;
		Connection con = null;
		PreparedStatement ps = null;
		FileInputStream fis = null;
		try{
			con = getConnection();		//������ݿ�����
			ps = con.prepareStatement("insert into head(h_id,h_des,h_data,u_no) values(?,?,?,?)");//���ò���
			int max = getMax(HEAD);
			ps.setInt(1, max);
			ps.setString(2, hdes);
			fis = new FileInputStream(file);
			ps.setBinaryStream(3, fis,(int)file.length());
			ps.setInt(4, Integer.valueOf(uno));
			result = ps.executeUpdate();		//ִ�в���
			System.out.println("����ͼƬ�ɹ�");
		}
		catch(Exception e){
			
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	//��������ȡ���е����
	public ArrayList<String []> getAlbumList(String u_no){
		ArrayList<String []> result = new ArrayList<String []>();
		Connection con = null;		//�������ݿ����Ӷ���
		PreparedStatement ps = null;	//����Ԥ�������
		ResultSet rs = null;			//����ResultSet����
		try{
			con = getConnection();		//������ݿ�����
			ps = con.prepareStatement("select x_id,x_name,x_access from album where u_no=?");
			ps.setInt(1, Integer.valueOf(u_no));	//���ò���
			rs = ps.executeQuery();		//ִ�в�ѯ
			while(rs.next()){			//���������
				String [] sa = new String[3];
				sa[0] = rs.getInt(1)+"";
				sa[1] = rs.getString(2);
				sa[2] = rs.getInt(3)+"";
				result.add(sa);				//���뵽�б���
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//����������Ȩ�޻�ȡ�����Ϣ
	public  ArrayList<String []> getAlbumListByAccess(String uno,String visitor){
		ArrayList<String []> result = new ArrayList<String []>();
		try{
			con = getConnection();
			FriendControl controler = new FriendControl();
			if(controler.isMyFriend(uno, visitor)){//�������ߺͱ��������Ƿ�Ϊ����
				//���Ǻ��ѣ���õ��Ǻ��ѿɼ��Լ����������
				ps = con.prepareStatement("select x_id,x_name from album where u_no=? and x_access<2");
			}
			
			else{
				//���Ǻ��ѣ�������ù��������
				ps = con.prepareStatement("select x_id,x_name from album where u_no=? and x_access=0");
			}
			
			ps.setInt(1, Integer.valueOf(uno));
			rs = ps.executeQuery();		//ִ�в�ѯ
			while(rs.next()){	//����������������
				String xid = rs.getInt(1)+"";  //���id
				String xname = rs.getString(2);//�������
				String [] sa = new String[]{xid,xname};
				result.add(sa);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	
	//�����������ݿ��ȡ��Ӧ����ͼƬ����Ϣ
	public  ArrayList<PhotoInfo> getPhotoInfoByAlbum(String xid,int pageNo,int span){
		ArrayList<PhotoInfo> result = new ArrayList<PhotoInfo>();
		int start = span*(pageNo-1);		//������ʼλ��
		try{
			con = getConnection();
			ps = con.prepareStatement("select p_id,p_name,p_des,x_id from photo" +
					" where x_id=? order by p_id limit "+start+","+span);		//�������
			ps.setInt(1, Integer.valueOf(xid));		//���ò���
			rs = ps.executeQuery();
			while(rs.next()){		//���������
				String p_id = rs.getInt(1)+"";
				String p_name = rs.getString(2);	//��Ƭ����
				String p_des = rs.getString(3);//��Ƭ����
				String x_id = rs.getInt(4)+"";
				PhotoInfo p = new PhotoInfo(p_id, p_name, p_des, x_id);		//���Photo����
				result.add(p);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	//�����������ݿ���ȡ���ƶ����ĳ���
	public int getAlbumSize(String xid){
		int result = -1;
		try{
			con = getConnection();		//�������
			ps = con.prepareStatement("select count(*) as count from photo where x_id=?");
			ps.setInt(1, Integer.valueOf(xid));		//���ò���
			rs = ps.executeQuery();		//ִ�в�ѯ
			if(rs.next()){
				result = rs.getInt(1);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//��������ѯָ������Ȩ��
	public  int getAlbumAccess(String xid){
		int result = 0;
		
		try{
			con = getConnection();
			ps = con.prepareStatement("select x_access from album where x_id=?");
			ps.setInt(1, Integer.valueOf(xid));
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getInt(1);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//���������ָ��ͼƬ������
	public ArrayList<P_Comments> getPhotoComment(String p_id){
		ArrayList<P_Comments> result = new ArrayList<P_Comments>();
		try{
			con = getConnection();
			ps = con.prepareStatement("select p_comment.c_content,p_comment.u_no,user.u_name,date_format(p_comment.c_date,'%Y-%c-%e %k:%i:%s')" +
					" from p_comment,user where p_comment.u_no=user.u_no and p_id=? order by p_comment.c_date");
			ps.setInt(1, Integer.valueOf(p_id));
			rs = ps.executeQuery();
			while(rs.next()){
				String content = rs.getString(1);
				String u_no = rs.getString(2);
				String u_name = rs.getString(3);
				String date = rs.getString(4);
				P_Comments pc = new P_Comments(content, u_no, u_name, date);
				result.add(pc);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	//����һ�����
	public  int createAlbum(String name,String u_no){
		int result = -1;
		try{
			con = getConnection();
			ps = con.prepareStatement("insert into album(x_id,x_name,u_no) values(?,?,?)");
			ps.setInt(1, getMax(ALBUM));
			ps.setString(2, name);
			ps.setInt(3, Integer.valueOf(u_no));		//����Ԥ�������Ĳ���
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	public  int insertPhoto(File file,String name,String desc,String x_no){
		int result = -1;
		Connection con = null;
		PreparedStatement ps = null;
		FileInputStream fis = null;
		try{
			con = getConnection();		//������ݿ�����
			ps = con.prepareStatement("insert into photo(p_id,p_name,p_des,p_data,x_id) values(?,?,?,?,?)");//���ò���
			int max = getMax(PHOTO);
			ps.setInt(1, max);
			ps.setString(2,name);
			ps.setString(3,desc);
			fis = new FileInputStream(file);
			ps.setBinaryStream(4, fis,(int)file.length());
			ps.setInt(5, Integer.valueOf(x_no));
			result = ps.executeUpdate();		//ִ�в���
			System.out.println("����ͼƬ�ɹ�");
		}
		catch(Exception e){
			
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	//�õ�ĳ����Ƭ
	public Blob getPhotoBlob(String id){
		Blob result = null;
		Connection con = null;	//���ݿ����
		PreparedStatement ps = null;	//Ԥ�������
		ResultSet rs = null;		//�����
		try{
			con = getConnection();		//�������
			ps = con.prepareStatement("select p_data from photo where p_id=?");	//�������
			ps.setInt(1, Integer.valueOf(id));	//���ò���
			rs = ps.executeQuery();		//���ò���
			if(rs.next()){	//
				result = rs.getBlob(1);		//���Blob����
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//�������޸�����Ȩ��
	public int changeAlbumAccess(String xid,String newAccess){
		int result = 0;
		try{
			con = getConnection();		//������ݿ�����
			ps = con.prepareStatement("update album set x_access=? where x_id=?");
			ps.setInt(1, Integer.valueOf(newAccess));
			ps.setInt(2, Integer.valueOf(xid));		
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//������ɾ��ָ��ͼƬ
	public  int deletePhoto(String pid){
		int result = -1;
		try{
			deleteAllCommentByPhoto(pid);
			con = getConnection();		//�������
			ps = con.prepareStatement("delete from photo where p_id=?");
			ps.setInt(1, Integer.valueOf(pid));		//����ɾ������Ƭ��id
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	//������ɾ��ָ��ͼƬ����������
	public  int deleteAllCommentByPhoto(String pid){
		int result = 0;
		try{
			con = getConnection();
			ps = con.prepareStatement("delete from p_comment where p_id=?");
			ps.setInt(1, Integer.valueOf(pid));
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			closeConnect(con, ps);
		}
		        
		return result;
	}
	
	
	
	//����������µ�ͼƬ����
	public int addPhotoComment(String content,String p_id,String u_no){
		int result = 0;
		
		try{
			con = getConnection();
			ps = con.prepareStatement("insert into p_comment(c_id,c_content,u_no,p_id) values(?,?,?,?)");
			ps.setInt(1, getMax(P_COMMENT));
			ps.setString(2, content);
			ps.setInt(3, Integer.valueOf(u_no));
			ps.setInt(4, Integer.valueOf(p_id));
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	//��������ȡĳ�����Ƭ��
	public int getAlbumSize(int x_id){
		int result = 0;
		
		try{
			con = getConnection();
			ps = con.prepareStatement("select count(p_id) as conut from photo where x_id=?");
			ps.setInt(1,x_id);
			rs = ps.executeQuery();
			
			if(rs.next())
			{
				result = rs.getInt(1);  //ȡ���������Ƭ����
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	
	//��������׿�������ݿ������Ƭ
	public  int insertPhotoFromAndroid(byte [] buf,String pname,String pdes,String x_id){
		int result =-1;
		try{
			con = getConnection();
			ps = con.prepareStatement("insert into photo(p_id,p_name,p_des,p_data,x_id) values(?,?,?,?,?)");
			ps.setInt(1, getMax(PHOTO));
			ps.setString(2, pname);
			ps.setString(3, pdes);
			InputStream in = new ByteArrayInputStream(buf);
			ps.setBinaryStream(4, in,(int)(in.available()));		
			ps.setInt(5, Integer.valueOf(x_id));
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




