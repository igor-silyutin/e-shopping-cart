package ecart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cart {
	private List<CartItem> cart;  //List of CartItems	 
	   //Class constructor
	   public Cart() {
	      cart = new ArrayList<CartItem>();
	   }
	 
	   //Add a CartItem into this Cart
	   public void add(int film_id, String title, String description, float rental_rate) {
	      // Check if the id is already in the shopping cart
	      Iterator<CartItem> iter = cart.iterator();
	      while (iter.hasNext()) {
	         CartItem item = iter.next();
	         if (item.getId() == film_id) {
	             // id found
	             return;
	          }
	      }
	      // film_id not found, create a new CartItem
	      cart.add(new CartItem(film_id, title, description, rental_rate));
	   }
	  
	   //Remove a CartItem given its id
	   public void remove(int film_id) {
	      Iterator<CartItem> iter = cart.iterator();
	      while (iter.hasNext()) {
	         CartItem item = iter.next();
	         if (item.getId() == film_id) {
	            cart.remove(item);
	            return;
	         }
	      }
	   }
	//Get the number of CartItems in this Cart
	   public int size() {
	      return cart.size();
	   }
	//Check if this Cart is empty   
	public boolean isEmpty() {
		return size() == 0;
	}
	//Return all the CartItems in a List<CartItem>
	   public List<CartItem> getItems() {
	      return cart;
	   }
	 
	   //Remove all the items in this Cart
	   public void clear() {
	      cart.clear();
	   }	
}
