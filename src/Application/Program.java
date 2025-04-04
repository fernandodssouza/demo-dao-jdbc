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
		
		
		//Seller sel = new Seller("Leticia Moura","leticiamqds@gmail.com", Date.valueOf("1996-1-20"), 3200.0, dep);
		
		//DaoFactory.createSellerDao().insert(sel);
		
		//System.out.println("\n\n\nSeller found or inserted: \n" + sel);
		
		DaoFactory.createSellerDao().deleteById(15);
		DaoFactory.createSellerDao().deleteById(16);
		
		//sel.setBaseSalary(4325.0);
		//sel.setDepartment(DaoFactory.createDepartmentDao().findById(4));
		
		//System.out.println("\n Now, changing the department from " + sel.getName());
		//DaoFactory.createSellerDao().update(sel);
		
		//System.out.println("\n========== Update Seller ==========");
		//System.out.println(sel);

		
		DB.closeConnection();
	}

}
