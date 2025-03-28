package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conn;
	
	//injeção de depencia
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(int id) {
		
		String sql = "SELECT seller.* , department.Name as depName FROM Seller INNER JOIN department WHERE seller.Id = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			//Faz a conexão com o banco de dados:
			conn = DB.getConnection();
			
			//Prepara a consulta:
			ps = conn.prepareStatement(sql);
			
			//Atribui o parâmentro id como filtro de busca: 
			ps.setInt(1, id);
			
			
			rs = ps.executeQuery();
			
			if(rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller seller = instantiateSeller(rs, dep);
				
				return seller;
			}else {
				System.out.println("No one Seller was find with the ID " + id + "!");
				return null;
			}
		
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}
	
	@Override
	public List<Seller> findByDepartment(Department dep) {
		String sql = "SELECT seller.*, department.Name AS depName FROM seller INNER JOIN department ON seller.DepartmentId = ? WHERE department.Id = ? ORDER BY Name";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			//Faz a conexão com o banco de dados:
			conn = DB.getConnection();
			
			//Prepara a consulta:
			ps = conn.prepareStatement(sql);
			
			//Atribui o parâmentro id como filtro de busca: 
			ps.setInt(1, dep.getId());
			ps.setInt(2, dep.getId());
			
			rs = ps.executeQuery();
			
			if(rs.isBeforeFirst()) {
				
				List<Seller> listSeller = new ArrayList<>();
				Map<Integer, Department> map = new HashMap<>();
				
				while(rs.next()) {
					
					Department department = map.get(rs.getInt("DepartmentId"))
;					
					if(department == null) {
						department = instantiateDepartment(rs);
						map.put(rs.getInt("DepartmentId"), department);
					}
					
					listSeller.addLast(instantiateSeller(rs, department));;
				}
				
				return listSeller;
				
			}else {
				return null;
			}
		
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(ps);
		}
	}

	@Override
	public List<Seller> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Função para instanciar um departamento
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		
		Department dep = new Department(rs.getInt("DepartmentId"), rs.getString("depName"));
		
		return dep;
	}
	
	//Função para instanciar um seller
	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException{
		Seller seller = new Seller();
		
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setDate(rs.getDate("BirthDate"));
		seller.setEmail(rs.getString("Email"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setDepartment(dep);
		
		return seller;
	}

	
	
}
