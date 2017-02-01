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
 * Servlet implementation class CartServlet
 */

@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {
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
	 
	      //Retrieve current HTTPSession object. If none, create one.
	      HttpSession session = request.getSession(true);
	      Cart cart;
	      synchronized (session) {  //Synchronized to prevent concurrent updates
	         //Retrieve the shopping cart for this session, if any. Otherwise, create one.
	         cart = (Cart) session.getAttribute("cart");
	         if (cart == null) {  //No cart, create one.
	            cart = new Cart();
	            session.setAttribute("cart", cart);  //Save it into session
	         }
	      }
	
	Connection conn   = null;
    Statement  stmt   = null;
    String     sqlStr = null;

    try {
       conn = pool.getConnection();  //Get a connection from the pool
       stmt = conn.createStatement();

       out.println("<html><head><title>ECart</title></head><body>");
       out.println("<h2>Shopping Cart</h2>");

       //This servlet can be extended to handle 4 cases:
       //(1) todo=add 
       //(2) todo=update 
       //(3) todo=remove 
       //(4) todo=view

       String todo = request.getParameter("todo");
       if (todo == null) todo = "view";  //To prevent null pointer

       if (todo.equals("add") || todo.equals("update")) {
          //(1) todo=add
          
    	  //(2) todo=update
          String[] ids = request.getParameterValues("film_id");
          if (ids == null) {
             out.println("<h4>Selection is not done</h4></body></html>");
             return;
          }
          
          for (String film_id : ids) {
        	  
             sqlStr = "select title, description, rental_rate from sakila.film where film_id = " + film_id;
             
             //out.println(sqlStr);  //For debugging purposes
             ResultSet rset = stmt.executeQuery(sqlStr);                  
             while(rset.next()) {
            	
                out.println("<p>" + "<input type='checkbox' name='film_id' value='" + film_id + "' />" + " " +
                rset.getString("title") + " " + rset.getString("description") + " " +
                		rset.getString("rental_rate") + "</p>");                
             }
                          
          }

       } else if (todo.equals("remove")) {
          String id = request.getParameter("film_id");  //Only one film_id for remove case
          cart.remove(Integer.parseInt(id));
       }

       //All cases - Always display the shopping cart
       if (!cart.isEmpty()) {
          out.println("<p>ECart is empty</p>");
       } else {                                 	   
    	   	float totalRate = 0f;
    	   	for (CartItem item : cart.getItems()) {
    	   	int film_id = item.getId();
    	   	String title = item.getTitle();
    	   	String description = item.getDescription();
    	   	float rate = item.getRate();               	   	
             totalRate += rate;
          }
          out.println("<br />");         
          out.println("<tr><td colspan='1' align='right'>Total Rate: $");
          out.printf("%.2f</td></tr>", totalRate);         
       }
     
       // Display the Checkout
       if (cart.isEmpty()) {
          out.println("<br /><br />");
          out.println("<form method='get' action='checkout'>");
          out.println("<input type='submit' value='Check Out'>");
          out.println("<p>Customer contact information</p>");
          out.println("<table>");
          out.println("<tr>");
          out.println("<td>First Name, Last Name:</td>");
          out.println("<td><input type='text' name='cust_name' /></td></tr>");
          out.println("<tr>");
          out.println("<td>Customer email:</td>");
          out.println("<td><input type='text' name='cust_email' /></td></tr>");
          out.println("<tr>");
          out.println("<td>Customer phone number:</td>");
          out.println("<td><input type='text' name='cust_phone' /></td></tr>");
          out.println("</table>");
          out.println("</form>");
       }

       out.println("</body></html>");

    } catch (SQLException ex) {
       out.println("<h4>Cart request failed</h4></body></html>");
       ex.printStackTrace();
    } finally {
       out.close();
       try {
          if (stmt != null) stmt.close();
          if (conn != null) conn.close();  // return the connection to the pool
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
