package com.epam.touragency.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.epam.touragency.dao.CountryDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;
import com.epam.touragency.dao.exception.DBException;
import com.epam.touragency.entity.Country;

public class CountryDAOimpl extends AbstractDAO<Country> implements CountryDAO{

	private static final String INSERT_SQL = "INSERT INTO Country (countryName) VALUES (?)";
	private static final String UPDATE_SQL = "UPDATE Country SET countryName=? WHERE id=?;";
	private static final String GET_ALL_SQL = "SELECT * FROM Country";
	private static final String GET_BY_ID_SQL = "SELECT * FROM Country WHERE id=?;";
	
	@Override
	public int insert(Country country) {
		if(country.getId()!=0){
			throw new DBException("Country id must be obtained from DB");
		}
		
		int id = 0;
	
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS)){
			preparedStatement.setString(1,country.getCountryName());
			int rows = preparedStatement.executeUpdate();
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if (rows == 1 && resultSet!=null  && resultSet.next()) {
                id = resultSet.getInt("GENERATED_KEY");
				country.setId(id);
			}else{
				throw new DBException("Can't get Country id from database.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return id;
	}
	
	

	@Override
	public void update(Country country) {
		if(country.getId()==0){
			throw new DBException("City id must not equal 0");
		}
		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
			preparedStatement.setString(1, country.getCountryName());
			preparedStatement.setInt(2, country.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
					
	}

	@Override
	public List<Country> getAll() {
		List<Country> result = new ArrayList<>();
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
	public Country getById(int id) {		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID_SQL)) {
			preparedStatement.setInt(1, id);
			List<Country> cities = parseResultSet(preparedStatement.executeQuery());
			return cities.size()==0? null: cities.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void delete(int id) {
		delete(id, "Country");		
	}

	private List<Country>  parseResultSet(ResultSet resultSet) throws SQLException {
		List<Country> result = new ArrayList<>();
		while (resultSet.next()) {
			Country country = new Country();
			country.setId(resultSet.getInt("id"));
			country.setCountryName(resultSet.getString("countryName"));
			result.add(country);
		}	
		return result;
	}
	

}
