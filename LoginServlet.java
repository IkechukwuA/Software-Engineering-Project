package com.wrox;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Map;

@WebServlet(
        name = "loginServlet",
        urlPatterns = "/login"
)
public class LoginServlet extends HttpServlet
{
    private static final Map<String, String> userDatabase = new Hashtable<>();

    static {
    	try{  
    		Class.forName("com.mysql.jdbc.Driver");  
    		Connection con=DriverManager.getConnection(  
    		"jdbc:mysql://localhost:3306/sonoo","root","root");  
    		//here sonoo is database name, root is username and password  
    		Statement stmt=con.createStatement();  
    		ResultSet rs=stmt.executeQuery("select * from emp");  
    		while(rs.next())  
    		System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));  
    		con.close();  
    		}catch(Exception e){ System.out.println(e);}  
    		}  

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        if(request.getParameter("logout") != null)
        {
            session.invalidate();
            response.sendRedirect("login");
            return;
        }
        else if(session.getAttribute("username") != null)
        {
            response.sendRedirect("tickets");
            return;
        }

        request.setAttribute("loginFailed", false);
        request.getRequestDispatcher("/WEB-INF/jsp/view/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        if(session.getAttribute("username") != null)
        {
            response.sendRedirect("tickets");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if(username == null || password == null ||
                !LoginServlet.userDatabase.containsKey(username) ||
                !password.equals(LoginServlet.userDatabase.get(username)))
        {
            request.setAttribute("loginFailed", true);
            request.getRequestDispatcher("/WEB-INF/jsp/view/login.jsp")
                   .forward(request, response);
        }
        else
        {
            session.setAttribute("username", username);
            request.changeSessionId();
            response.sendRedirect("tickets");
        }
    }
}
