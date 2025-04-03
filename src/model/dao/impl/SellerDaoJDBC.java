package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		String sql = "INSERT INTO seller(Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES (?, ?, ?, ?, ?)";
		
		PreparedStatement st = null;
		
		if(searchByEmail(obj.getEmail(), st)) {
			System.out.println("Alread exists a seller with this email!");
		}else {
		
			try {			
				st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				
				st.setString(1, obj.getName());
				st.setString(2, obj.getEmail());
				st.setDate(3, obj.getDate());
				st.setDouble(4, obj.getBaseSalary());
				st.setInt(5, obj.getDepartment().getId());
				
				int rowsAffected = st.executeUpdate();
				
				if(rowsAffected > 0) {
					ResultSet rs = st.getGeneratedKeys();
					if(rs.next()) {
						obj.setId(rs.getInt(1));
					}
				}else {
					throw new DbException("Fallure while trying to insert data! No rows affected!");
				}
				
			}catch(SQLException e) {
				throw new DbException(e.getMessage());
			}finally {
				DB.closeStatement(st);
			}
		}
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
		String sql = "SELECT seller.*, department.Name AS depName FROM seller INNER JOIN department ON seller.DepartmentId = department.Id ORDER BY Name";
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		List<Seller> sellers = new ArrayList<>();
		
		try {
			conn = DB.getConnection();
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
			
			Map<Integer, Department> map = new HashMap<>();
						
			while(rs.next()) {
				//instanciar o departamento, se não estiver instanciado;
				Department dep = map.get(rs.getInt("DepartmentId"));
				if(dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				//Instanciar o Seller
				sellers.add(instantiateSeller(rs, dep));
			}
			
			return sellers;
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally{
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
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
	
	private boolean searchByEmail(String email, PreparedStatement st) {
		String sql = "SELECT * FROM seller WHERE Email = ?";
		ResultSet rs = null;
		boolean result;
		
		try {
			st = conn.prepareStatement(sql);
			
			st.setString(1, email);
			
			rs = st.executeQuery();
			
			if(rs.next()) {
				result = true;
			}else {
				result = false;
			}
			return result;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally{
			DB.closeResultSet(rs);
			st = null;
		}
		
	}
}
