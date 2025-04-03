package Application;

import java.util.List;

import db.DB;
import model.dao.DaoFactory;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		Seller seller2 = DaoFactory.createSellerDao().findById(1);
		
		Department dep = DaoFactory.createDepartmentDao().findById(2);
		
		List<Seller> sellers = DaoFactory.createSellerDao().findByDepartment(dep);
		List<Seller> allSellers = DaoFactory.createSellerDao().findAll();
		
		System.out.println("========== Find Seller by ID ==========");
		if (seller2 != null) {
			System.out.println(seller2);
		}
		
		System.out.println("\n========== Find Sellers by department ==========");
		if(sellers != null) {
			for(Seller s: sellers) {
				System.out.println(s);
			}
		}else {
			System.out.println("No one Seller was find in this department ID");
		}
		
		System.out.println("\n========== Find All Sellers ==========");
		if(allSellers != null) {
			for(Seller s: allSellers) {
				System.out.println(s);
			}
		}else {
			System.out.println("No one Seller was find!");
		}
		
		DB.closeConnection();
	}

}
