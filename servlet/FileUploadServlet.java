package com.blog.servlet;
import static com.blog.dao.ConstantUtil.CHAR_ENCODING;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blog.dao.PhotoControl;
import com.jspsmart.upload.File;
import com.jspsmart.upload.Files;
import com.jspsmart.upload.SmartUpload;


public class FileUploadServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
		req.setCharacterEncoding(CHAR_ENCODING);
		this.doPost(req, resp);
	}
	
	
	
	
    @SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
           throws IOException, ServletException {
		request.setCharacterEncoding(CHAR_ENCODING);
		String action = request.getParameter("action");
		System.out.println(action);
		
		if(action.equals("uploadHead"))
		{
			System.out.println("�ϴ�ͷ��");
			String hdes = request.getParameter("hdes");		//��ȡ����
			String uno = request.getParameter("uno");		//��ȡ�ϴ���
			PhotoControl controler = new PhotoControl();
			
			SmartUpload load = new SmartUpload();
			load.initialize(this.getServletConfig(), request, response);
			load.setMaxFileSize(1024*1024*3);//��Լ�����ļ��Ĵ�С5M
			
			try
			{
				load.upload();
				Files files = load.getFiles();
				File file = files.getFile(0);
				String fileName = file.getFileName();
				
				String extension = fileName.substring(fileName.lastIndexOf("."),fileName.length());
				Date date = new Date();
				String newName = date.getTime() + extension;
				String   aa = getServletContext().getRealPath("upload_folder"); 
				System.out.println(aa);
		        String   trace = aa + "/" + newName; 
		        System.out.println("�ļ�����"+trace);
				file.saveAs(trace);
				java.io.File javaFile = new java.io.File(trace);
				int result = controler.insertHeadFile(javaFile,hdes,uno);
				
				com.jspsmart.upload.Request juRequest = load.getRequest();
				if(-1 != result)
				{
					javaFile.delete();  //ɾ����ʱ�ļ�
					request.setAttribute("result", "success");
					request.getRequestDispatcher("personalInfo.jsp").forward(request,response);
				}
				
				
			} catch (Exception e)
			{
				e.printStackTrace();
				request.setAttribute("result", "error");
				request.getRequestDispatcher("personalInfo.jsp").forward(request,response);
			}
			
		}
		
		
		//�ϴ����������Ƭ
		else if(action.equals("upload")) {   
			String p_name = new String(request.getParameter("photoName").getBytes("iso-8859-1"),"UTF-8");
			String p_desc = new String(request.getParameter("photoDes").getBytes("iso-8859-1"),"UTF-8");
			String x_id = new String(request.getParameter("album").getBytes("iso-8859-1"),"UTF-8");
			
			System.out.println(p_name + p_desc + x_id);
			PhotoControl controler = new PhotoControl();
			SmartUpload load = new SmartUpload();
			load.initialize(this.getServletConfig(), request, response);
			load.setMaxFileSize(1024*1024*5);//��Լ�����ļ��Ĵ�С5M
			
			
			try
			{
				load.upload();
				Files files = load.getFiles();
				File file = files.getFile(0);
				String fileName = file.getFileName();
				
				String extension = fileName.substring(fileName.lastIndexOf("."),fileName.length());
				String newName = p_name + extension;  //��Ƭ�ļ���
				String   aa = getServletContext().getRealPath("upload_folder"); 
				System.out.println(aa);
		        String   trace = aa + "/" + newName; 
		        System.out.println("�ļ�����"+trace);
				file.saveAs(trace);
				java.io.File javaFile = new java.io.File(trace);
				int result = controler.insertPhoto(javaFile,p_name,p_desc,x_id);
				
				com.jspsmart.upload.Request juRequest = load.getRequest();
				if(-1 != result)
				{
					javaFile.delete();  //ɾ����ʱ�ļ�
					request.setAttribute("loadPhotoResult", "success");
					request.getRequestDispatcher("uploadImage.jsp").forward(request,response);
				}
				
				
			} catch (Exception e)
			{
				e.printStackTrace();
				request.setAttribute("result", "error");
				request.getRequestDispatcher("personalInfo.jsp").forward(request,response);
			}
			
		}
		}
		
    } 
		
