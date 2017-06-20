package sysu.lwt.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sysu.lwt.service.MyService;

/**
 * Servlet implementation class RegisterrServlet
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;  
    static int LOGIN_FAILED = 0;  
    static int LOGIN_SUCCEEDED = 1;  
    static int REGISTER_FAILED = 2;  
    static int REGISTER_SUCCEEDED = 3;  
         
    public RegisterServlet() {  
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
        // 设置编码形式    
        request.setCharacterEncoding("utf-8");      
        // 获取传入数据    
        String id = request.getParameter("id");
        String username = request.getParameter("username");  
        String password = request.getParameter("password");  
        System.out.println("id:" + id + " --try to register");  
    
        // 访问数据库    
        int value = MyService.register(id, username, password);
        if(value == REGISTER_SUCCEEDED) {    
            responseMsg = "SUCCEEDED";    
        }  
        System.out.println("register servlet responseMsg:" + responseMsg);    
        out.print(responseMsg);  
    }  

}
