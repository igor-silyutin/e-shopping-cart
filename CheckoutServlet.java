package ecart;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;

import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;
/**
 * Servlet implementation class CheckoutServlet
 */
@WebServlet("/CheckoutServlet")
public class CheckoutServlet extends HttpServlet {
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
	         //Lookup the DataSource.
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
	      //ResultSet rset = null;
	      String sqlStr = null;
	      HttpSession session = null;
	      Cart cart = null;	 
	      try {
	         conn = pool.getConnection();  //Get a connection from the pool
	         stmt = conn.createStatement();
	 
	         out.println("<html><head><title>ECart</title></head><body>");
	         out.println("<h2>Checkout</h2>");
	 
	         //Retrieve the Cart
	         session = request.getSession(true);
	         if (session == null) {
	            out.println("<h3>Shopping cart is empty</h3></body></html>");
	            return;
	         }
	         synchronized (session) {
	            cart = (Cart) session.getAttribute("cart");
	            if (cart == null) {
	               out.println("<h3>Shopping cart is empty</h3></body></html>");
	               return;
	            }
	         }
	         
	         
	         //Retrieve and process request parameters: id(s), cust_name, cust_email, cust_phone
	         String custName = request.getParameter("cust_name");
	         boolean hasCustName = custName != null && ((custName = custName.trim()).length() > 0);
	         String custEmail = request.getParameter("cust_email").trim();
	         boolean hasCustEmail = custEmail != null && ((custEmail = custEmail.trim()).length() > 0);
	         String custPhone = request.getParameter("cust_phone").trim();
	         boolean hasCustPhone = custPhone != null && ((custPhone = custPhone.trim()).length() > 0);
	 
	         //Validate inputs
	         if (!hasCustName) {
	            out.println("<h4>Enter customer name. First Name, Last Name. </h4></body></html>");
	            return;
	         } else if (!hasCustEmail || (custEmail.indexOf('@') == -1)) {
	            out.println("<h4>Enter valid customer email.</h4></body></html>");
	            return;
	         } else if (!hasCustPhone || custPhone.length() != 10) {
	            out.println("<h4>Enter phone number. 10 digits. No spaces.</h4></body></html>");
	            return;
	         }
	 
	         //Display the name, email and phone 
	         out.println("<table>");
	         out.println("<tr>");
	         out.println("<td>Customer Name:</td>");
	         out.println("<td>" + custName + "</td></tr>");
	         out.println("<tr>");
	         out.println("<td>Customer Email:</td>");
	         out.println("<td>" + custEmail + "</td></tr>");
	         out.println("<tr>");
	         out.println("<td>Customer Phone Number:</td>");
	         out.println("<td>" + custPhone + "</td></tr>");
	         out.println("</table>");
	 
	         //Print movie(s) ordered
	         out.println("<br />");
	         out.println("<table border='1' cellpadding='1'>");	         	         	         	        
	         float totalRate = 0f;
	         for (CartItem item : cart.getItems()) {
	            int film_id = item.getId();
	            String title = item.getTitle();
	            String description = item.getDescription();
	            float rate = item.getRate();
	 	             
	            //Show movie(s) ordered
	            out.println("<p>" + "<input type='checkbox' name='film_id' value='" + film_id + "' />" + " " +
	                    title + " " + description + " " +
	                    		rate + "</p>"); 
	            
	            totalRate += rate * 1;
	         }
	         out.println("<tr><td colspan='1' align='right'>Total Rate: $");
	         out.printf("%.2f</td></tr>", totalRate);
	         out.println("</table>");
	         out.println("</body></html>");
	 
	         cart.clear();   //Empty the cart
	      } catch (SQLException ex) {
	         cart.clear();   //Empty the cart
	         out.println("<h3>Request failed</h3></body></html>");
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
