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
	
	//injeção de dependencia
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Seller obj) {
		String sql = "INSERT INTO seller(Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES (?, ?, ?, ?, ?)";
		
		PreparedStatement st = null;
		
		int id = searchByEmail(obj.getEmail(), st);
		
		if(id > 0) {
			//Busca os dados do Seller com mesmo email no Banco de dados:
			Seller sellerWithTheSameEmail = findById(id);
			
			//Copia os dados do parametro do método e atribui a copia o mesmo id encontrado.
			Seller auxSeller = obj;
			auxSeller.setId(id);
			
			//confere se de fato é o mesmo objeto e se for, atribui ao objeto parametro o id.
			if(auxSeller == sellerWithTheSameEmail) {
				obj.setId(id);
			}
			
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
		String sql = "UPDATE seller SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? WHERE Id = ?";
		
		PreparedStatement st = null;
		try {			
			st = conn.prepareStatement(sql);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, obj.getDate());
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
						
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}		
	}

	@Override
	public void deleteById(int id) {
		String sql = "DELETE FROM seller WHERE Id = ?";
		
		PreparedStatement st = null;
		
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement(sql);
			
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			
			conn.commit();
			
			if(rowsAffected > 0) {
				System.out.println("The seller ID-" + id + " was deleted!");
			}else {
				throw new DbException("Error while deleting!");
			}
		}catch(SQLException e){
			try {
				conn.rollback();
			} catch (Exception e2) {
				throw new DbException("Error while rolling back the action!");
			}
			e.printStackTrace();
		}finally {
			DB.closeStatement(st);
		}
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
	
	//Função para verificar duplicidade de email:
	private int searchByEmail(String email, PreparedStatement st) {
		String sql = "SELECT * FROM seller WHERE Email = ?";
		ResultSet rs = null;
		int result = 0;
		
		try {
			st = conn.prepareStatement(sql);
			
			st.setString(1, email);
			
			rs = st.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt("Id");
			}else {
				result = 0;
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
