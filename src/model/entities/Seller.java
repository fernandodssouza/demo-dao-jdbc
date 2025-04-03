package model.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

public class Seller implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String email;
	private Date date;
	private double baseSalary;
	private Department department;
	
	//constructors:
	public Seller() {}

	public Seller(int id, String name, String email, Date date, double baseSalary, Department department) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.date = date;
		this.baseSalary = baseSalary;
		this.department = department;
	}
	
	public Seller(String name, String email, Date date, double baseSalary, Department department) {
		this.name = name;
		this.email = email;
		this.date = date;
		this.baseSalary = baseSalary;
		this.department = department;
	}

	// getters and setters:
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getBaseSalary() {
		return baseSalary;
	}
	public void setBaseSalary(double baseSalary) {
		this.baseSalary = baseSalary;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}
	
	//hashcode and equals:
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Seller other = (Seller) obj;
		return id == other.id;
	}
	
	//toString:
	@Override
	public String toString() {
		return "Seller [id=" + id + ", name=" + name + ", email=" + email + ", date=" + date + ", baseSalary="
				+ baseSalary + ", department=" + department + "]";
	}
	
}
