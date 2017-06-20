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
        // 返回字符串    
        String responseMsg="FAILED";    
        // 输出流    
        PrintWriter out = response.getWriter();
        // 设置客户端解码方式为utf-8
        response.setContentType("text/html;charset=utf-8"); 
        // 设置编码形式    
        request.setCharacterEncoding("utf-8");    
        // 获取传入数据    
        String id = request.getParameter("id");    
        String password = request.getParameter("password");  
        System.out.println("id:" + id + " --try to login");  
    
        // 访问数据库    
        int value = MyService.login(id, password);  
        if(value == LOGIN_SUCCEEDED) {    
            responseMsg = "SUCCEEDED";
        }
        System.out.println("login servlet responseMsg:" + responseMsg);
        out.print(responseMsg);
    }

}
