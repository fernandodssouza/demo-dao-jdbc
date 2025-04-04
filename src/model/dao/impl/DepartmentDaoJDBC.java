package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao{
	
	private Connection conn;
	
	//Injeção de dependencia
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Department obj) {
		String sql = "INSERT INTO department(Name) VALUES (?)";
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		int id = searchDepartmentByName(obj.getName());
		if(id==0) {
			try {
				st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				st.setString(1, obj.getName());
				
				st.executeUpdate();
				
				rs = st.getGeneratedKeys();
				if(rs.next()) {
					obj.setId(rs.getInt(1));
				}else {
					System.out.println("Error during inserting the new department.");
				}
				
				
			}catch(SQLException e) {
				throw new DbException(e.getMessage());
			}finally {
				DB.closeResultSet(rs);
				DB.closeStatement(st);
			}
		}else {
			obj.setId(id);
		}
	}

	@Override
	public void update(Department obj) {
		String sql = "UPDATE department SET Name = ? WHERE Id = ?";
		
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement(sql);
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			
			st.executeUpdate();
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(int id) {
		String sql = "DELETE FROM department WHERE Id = ?";
		
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement(sql);
			
			st.setInt(1, id);
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				System.out.println("The department of Id-" + id + " was deleted!");
			}else {
				System.out.println("The ID-" + id + " doesn't exists!");
			}
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);			
		}
		
	}

	@Override
	public Department findById(int id) {
		
		String sqlQuery = "SELECT * FROM Department WHERE Id = ?";
		Department dep = new Department();
		
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			conn = DB.getConnection();
			st = conn.prepareStatement(sqlQuery);
			
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			if(rs.next()) {
				dep.setId(rs.getInt("Id"));
				dep.setName(rs.getString("Name"));
			}
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);			
		}
		
		
		return dep;
	}

	@Override
	public List<Department> findAll() {
		String sql = "SELECT * FROM department";
		List<Department> deps = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
			
			while(rs.next()) {
				deps.add(InstantiateDepartment(rs));
			}
			return deps;
		}catch(SQLException e){
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
		
	}
	
	private Department InstantiateDepartment(ResultSet rs) {
		Department dep;
		try {
			dep = new Department(rs.getInt("Id"), rs.getString("Name"));
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		return dep;
	}
	private int searchDepartmentByName(String name) {
		String sql = "SELECT * FROM department WHERE Name = ?";
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(sql);
			st.setString(1, name);
			rs = st.executeQuery();
			
			if(rs.next()) {
				return rs.getInt("Id");
			}else {
				return 0;
			}
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}
}
