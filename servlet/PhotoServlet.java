package com.blog.servlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.blog.bean.User;
import com.blog.dao.PhotoControl;
import static com.blog.dao.ConstantUtil. CREATE_ALBUM_SUCESS;
import static com.blog.dao.ConstantUtil. CREATE_ALBUM_FAIL;
import static com.blog.dao.ConstantUtil.CHAR_ENCODING;



public class PhotoServlet extends HttpServlet
{

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		request.setCharacterEncoding(CHAR_ENCODING);
		String action = request.getParameter("action");
		System.out.println("action==="+action);
		 if(action.equals("seeAlbum")){			//actionΪ�鿴���
			 
			HttpSession session = request.getSession();
			User user = (User)session.getAttribute("user");
			if(user!=null){
				request.setAttribute("u_no", user.u_no);	//���÷�������
			}
			request.getRequestDispatcher("album.jsp").forward(request, response);
		}
		 
		 else if(action.equals("createAlbum")){				//actionΪ�������
				String albumName = request.getParameter("albumName");
				String u_no = request.getParameter("u_no");		//��ȡ���������id
				PhotoControl controler = new PhotoControl();
				if(controler.createAlbum(albumName, u_no) == 1){		//�����ɹ�
					request.setAttribute("result", CREATE_ALBUM_SUCESS);
				}
				else{
					request.setAttribute("result", CREATE_ALBUM_FAIL);
				}
				request.getRequestDispatcher("uploadImage.jsp").forward(request, response);
			}
		
		 
		 else if(action.equals("change_album_access")){			//actionΪ�޸����Ȩ��
				String xid = request.getParameter("xid");
				String access = request.getParameter("album_access");
				System.out.println("xid  -- "+xid+" access:"+access);
				PhotoControl controler = new PhotoControl();
				controler.changeAlbumAccess(xid, access);
				request.getRequestDispatcher("album.jsp").forward(request, response);
			}
		 
		 else if(action.equals("deletePhoto")){				//actionΪɾ��ָ��ͼƬ
				String pid = request.getParameter("p_id");
				PhotoControl controler = new PhotoControl();
				int result = controler.deletePhoto(pid);
				request.getRequestDispatcher("album.jsp").forward(request, response);
			}
		 
		 else if(action.equals("addPhotoComment")){				//actionΪaddPhotoComment
				String content = request.getParameter("content");
				String uno = request.getParameter("u_no");
				String pid = request.getParameter("p_id");
				PhotoControl controler = new PhotoControl();
				int result = controler.addPhotoComment(content, pid, uno);
				if(result == 1){
					request.getRequestDispatcher("album.jsp").forward(request, response);
				}
		 	}
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		}
	

}
		
		
		


