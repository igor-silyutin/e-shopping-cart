package ecart;

public class CartItem {

	   private int film_id;
	   private String title;
	   private String description;
	   private float rental_rate;
	   
	 
	   //Class Constructor
	   public CartItem(int film_id, String title, String description, float rental_rate) {
	      this.film_id = film_id;
	      this.title = title;
	      this.description = description;
	      this.rental_rate = rental_rate;
	      
	   }
	 
	   public int getId() {
	      return film_id;
	   }
	 
	   public String getTitle() {
	      return title;
	   }
	   
	   public String getDescription() {
		      return description;
		   }
	   
	   public float getRate() {
	      return rental_rate;
	   }	
}
