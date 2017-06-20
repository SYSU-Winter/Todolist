package sysu.lwt.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sysu.lwt.service.MyService;

/**
 * Servlet implementation class LoginServlet
 */

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;  
    static int LOGIN_FAILED = 0;
    static int LOGIN_SUCCEEDED = 1;
         
    public LoginServlet() {  
        super();  
    }  
  
    protected void doGet(HttpServletRequest request, HttpServletResponse response)   
            throws ServletException, IOException {  
        doPost(request, response);    
        response.getWriter().append("Served at: ").append(request.getContextPath());  
    }  
      
    protected void doPost(HttpServletRequest request, HttpServletResponse response)   
            throws ServletException, IOException {  
        // �����ַ���    
        String responseMsg="FAILED";    
        // �����    
        PrintWriter out = response.getWriter();
        // ���ÿͻ��˽��뷽ʽΪutf-8
        response.setContentType("text/html;charset=utf-8"); 
        // ���ñ�����ʽ    
        request.setCharacterEncoding("utf-8");    
        // ��ȡ��������    
        String id = request.getParameter("id");    
        String password = request.getParameter("password");  
        System.out.println("id:" + id + " --try to login");  
    
        // �������ݿ�    
        int value = MyService.login(id, password);  
        if(value == LOGIN_SUCCEEDED) {    
            responseMsg = "SUCCEEDED";
        }
        System.out.println("login servlet responseMsg:" + responseMsg);
        out.print(responseMsg);
    }

}
