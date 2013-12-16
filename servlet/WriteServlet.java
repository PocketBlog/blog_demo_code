package com.blog.servlet;

import static com.blog.dao.ConstantUtil.CHAR_ENCODING;
import static com.blog.dao.ConstantUtil.UPDATE_STATE_SUCCESS;
import static com.blog.dao.ConstantUtil.USER_NOT_LOGIN;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.blog.bean.User;
import com.blog.dao.WriteControl;

public class WriteServlet extends HttpServlet
{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{

		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		req.setCharacterEncoding(CHAR_ENCODING);
		String action = req.getParameter("action");
		System.out.println("action=========" + action);

		if (action.equals("new_state"))
		{ // actionΪ��������
			HttpSession session = req.getSession();
			User user = (User) session.getAttribute("user");
			WriteControl controler = new WriteControl();
			if (user == null)
			{ // �û�û�е�¼
				req.setAttribute("result", USER_NOT_LOGIN);
				req.getRequestDispatcher("write.jsp").forward(req, resp);
				return;
			}
			String u_no = user.u_no; // ����û���id
			String content = (String) req.getParameter("content");
			String result = controler.updateState(u_no, content);
			if(result.equals(UPDATE_STATE_SUCCESS))
			{
					user.u_state = content;
			}
			session.setAttribute("user", user); // ����Session������
			req.setAttribute("writeResult", result); // ��������õ�request��������
			req.getRequestDispatcher("write.jsp").forward(req, resp);// ����
		}

		else if (action.equals("new_diary")) // д������־����
		{

			HttpSession session = req.getSession();
			User user = (User) session.getAttribute("user");
			WriteControl controler = new WriteControl();
			
			String u_no = user.u_no; // ����û���id
			String title = req.getParameter("title"); //����
			String content = req.getParameter("content");//����
			String result = controler.writeNewDiary(title, content,u_no);
			
			req.setAttribute("writeResult", result); // ��������õ�request��������
			req.getRequestDispatcher("write.jsp").forward(req, resp);// ����

		}
		
		
		else if(action.equals("seeDiary")){		//actionΪ�鿴�ռ�
			HttpSession session = req.getSession();		//��ȡSession
			User user = (User)session.getAttribute("user");	//���User����
			if(user != null){
				
				req.setAttribute("u_no", user.u_no);
			}
			req.getRequestDispatcher("diary.jsp").forward(req, resp);
		}
		
		
		else if(action.equals("makeComment")){		//actionΪ��������
			String c_content = req.getParameter("comment");
			String r_id = req.getParameter("r_id");
			String visitor = req.getParameter("visitor");
			WriteControl controler = new WriteControl();
			int result = controler.addComment(c_content, r_id, visitor);
			if(result == 1){		//������۳ɹ�
				req.setAttribute("commentResult","success");
			}
			
			else
			{
				req.setAttribute("commentResult", "faild");
			}
			req.getRequestDispatcher("diary.jsp").forward(req, resp);
		}
		
		
		
		else if(action.equals("toModifyDiary")){			//actionΪȥ�޸���־��ҳ��
			req.getRequestDispatcher("modifyDiary.jsp").forward(req, resp);
		}
		
		
		
		else if(action.equals("deleteDiary")){			//actionΪɾ��ָ����־
			String rid=req.getParameter("r_id");
			WriteControl controler = new WriteControl();
			int result = controler.deleteDiary(rid);
			req.getRequestDispatcher("diary.jsp").forward(req, resp);
			
		}
		
		else if(action.equals("modifyDiary")){				//actionΪ�޸���־
			String rid = req.getParameter("r_id");
			String rtitle = req.getParameter("r_title");
			String rcontent = req.getParameter("r_content");
			WriteControl controler = new WriteControl();
			int result = controler.modifyDiary(rid, rtitle, rcontent);
			req.setAttribute("result", result);
			req.getRequestDispatcher("modifyDiary.jsp").forward(req, resp);
		}
		
		
		
		
		
		
		
		
		
		
	}
}
