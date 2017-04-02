package com.epam.touragency.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.epam.touragency.dao.UserDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;
import com.epam.touragency.dao.exception.DBException;
import com.epam.touragency.entity.Role;
import com.epam.touragency.entity.User;

public class UserDAOimpl extends AbstractDAO<User> implements UserDAO{

	private static final String INSERT_SQL = "INSERT INTO User (fName, lName, birthDate, eMail, password, role) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE User SET fName=?, lName=?, birthDate=?, eMail=? , password=?, role=? WHERE id=?;";
	private static final String GET_ALL_SQL = "SELECT "
												+ "* FROM "
												+ "User ";
	private static final String GET_BY_ID_SQL = "SELECT "
												+ "* FROM "
												+ "User WHERE id=?;";
	
	@Override
	public int insert(User user) {
		if(user.getId()!=0){
			throw new DBException("User id must be obtained from DB");
		}
		
		int id=0;
	
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS)){
			preparedStatement.setString(1,user.getfName());
			preparedStatement.setString(2, user.getlName());
			preparedStatement.setDate(3, user.getBirthDate());
			preparedStatement.setString(4, user.geteMail());
			preparedStatement.setString(5, user.getPassword());
			preparedStatement.setString(6, user.getRole().toString());			
			int  rowCount = preparedStatement.executeUpdate();
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if(rowCount==1 && resultSet!=null && resultSet.next()){
				id = resultSet.getInt("GENERATED_KEY");
				user.setId(id);
			}else{
				throw new DBException("Can't get User id from database.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return id;
	}
	
	

	@Override
	public void update(User user) {
		if(user.getId()==0){
			throw new DBException("User id must not equal 0");
		}
		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
			preparedStatement.setString(1,user.getfName());
			preparedStatement.setString(2, user.getlName());
			preparedStatement.setDate(3, user.getBirthDate());
			preparedStatement.setString(4, user.geteMail());
			preparedStatement.setString(5, user.getPassword());
			preparedStatement.setString(6, user.getRole().toString());
			preparedStatement.setInt(7, user.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
					
	}

	@Override
	public List<User> getAll() {
		List<User> result = new ArrayList<>();
		try(Connection connection = ConnetctionPool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_SQL)) {
			preparedStatement.executeQuery();
			result = parseResultSet(preparedStatement.executeQuery());				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public User getById(int id) {		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID_SQL)) {
			preparedStatement.setInt(1, id);
			List<User> cities = parseResultSet(preparedStatement.executeQuery());
			return cities.size()==0? null: cities.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void delete(int id) {
		delete(id, "User");		
	}

	private List<User>  parseResultSet(ResultSet resultSet) throws SQLException {
		List<User> result = new ArrayList<>();
		while (resultSet.next()) {
			User user = new User();
			user.setId(resultSet.getInt("id"));
			user.setfName(resultSet.getString("fName"));
			user.setlName(resultSet.getString("lName"));
			user.setBirthDate(resultSet.getDate("birthDate"));
			user.seteMail(resultSet.getString("eMail"));
			user.setPassword(resultSet.getString("password"));
			user.setRole(Role.valueOf(resultSet.getString("role")));
			result.add(user);
		}	
		return result;
	}
	

}
