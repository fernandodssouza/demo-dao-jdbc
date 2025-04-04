package Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import db.DB;
import model.dao.DaoFactory;
import model.entities.Department;

public class Program2 {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		List<Department> deps = new ArrayList<>();
		
		System.out.println("============== TEST 1 - FIND ALL =========================");
		deps = DaoFactory.createDepartmentDao().findAll();
		
		for(Department d: deps) {
			System.out.println(d);
		}
		
		System.out.println("\n============== TEST 2 - FIND BY ID =========================");
		Department dep1 = DaoFactory.createDepartmentDao().findById(6);
		System.out.println(dep1);
		
		System.out.println("\n============== TEST 3 - INSERT =========================");
		Department dep2 = new Department(0, "Food");
		DaoFactory.createDepartmentDao().insert(dep2);
		System.out.println(dep2);
		
		System.out.println("\n============== TEST 4 - UPDATE =========================");
		dep1.setName("Shoppings");
		DaoFactory.createDepartmentDao().update(dep1);
		System.out.println(dep1);
		
		System.out.println("\n============== TEST 5 - DELETE =========================");
		System.out.print("Enter with the department id that you want delete: ");
		int id = sc.nextInt();
		DaoFactory.createDepartmentDao().deleteById(id);
		System.out.println("Done!");
		
		sc.close();
		DB.closeConnection();
	}

}
