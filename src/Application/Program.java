package Application;

import model.dao.DaoFactory;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		Seller seller2 = DaoFactory.createSellerDao().findById(1);
		
		if (seller2 != null) {
			System.out.println(seller2);
		}
	}

}
