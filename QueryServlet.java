package ecart;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * Servlet implementation class QueryServlet
 */

@WebServlet("/QueryServlet")
public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	private DataSource pool;  //Database connection pool
	 
	   @Override
	   public void init(ServletConfig config) throws ServletException {
	      try {
	         //Create a JNDI Initial context which is able to lookup the DataSource
	         InitialContext ctx = new InitialContext();
	         //Lookup the DataSource
	         pool = (DataSource)ctx.lookup("java:comp/env/jdbc/ECartDB");
	         if (pool == null)
	            throw new ServletException("Unknown DataSource 'jdbc/ECartDB'");
	      } catch (NamingException ex) {
	    	  ex.printStackTrace();
	      }
	   }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	   
	@Override   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
	      PrintWriter out = response.getWriter();
	 
	      Connection conn = null;
	      Statement stmt = null;
	      
	      try {
	         conn = pool.getConnection();  // Get a connection from the pool
	         stmt = conn.createStatement();
	         String sqlStr = "select film_id, title, description, rental_rate from sakila.film limit 100";
	         ResultSet rset = stmt.executeQuery(sqlStr);
	 
	         out.println("<html><head><title>ECart</title></head><body>");
	         out.println("<h2>Movies</h2>");
	         
	         //Print the result in HTML form 
             out.println("<form method='get' action='cart'>");
             out.println("<input type='hidden' name='todo' value='add' />");
             
             int count=0;
             while(rset.next()) {
            	 String film_id = rset.getString("film_id");
                out.println("<p>" + "<input type='checkbox' name='film_id' value='" +
            	 film_id + "' />" + " " + rset.getString("title") + " " +
                		rset.getString("description") + " " + rset.getString("rental_rate") + "</p>");
                ++count;
             }
      
              //Submit button
              out.println("<input type='submit' value='Add to ecart' />");
              out.println("</form>");
	         
              //Show "View Shopping Cart" if the cart is not empty
	         HttpSession session = request.getSession(false); 
	         //Verify if session exists
	         if (session != null) {
	            Cart cart;
	            synchronized (session) {
	               //Retrieve the shopping cart for this session, if any. Otherwise, create one.
	               cart = (Cart) session.getAttribute("cart");
	               if (cart != null && !cart.isEmpty()) {
	                  out.println("<p><a href='cart?todo=view'>View ecart</a></p>");
	               }
	            }
	         }
	 
	         out.println("</body></html>");
	      } catch (SQLException ex) {
	         out.println("<h4>Movies DB request failed</h4></body></html>");
	         ex.printStackTrace();
	      } finally {
	         out.close();
	         try {
	            if (stmt != null) stmt.close();
	            if (conn != null) conn.close();  // Return the connection to the pool
	         } catch (SQLException ex) {
	        	 ex.printStackTrace();
	         }
	      }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
