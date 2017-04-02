package com.epam.touragency.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.epam.touragency.dao.CityDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;
import com.epam.touragency.dao.exception.DBException;
import com.epam.touragency.entity.City;
import com.epam.touragency.entity.Country;

public class CityDAOimpl extends AbstractDAO<City> implements CityDAO{

	private static final String INSERT_SQL = "INSERT INTO City (cityName, countryID) VALUES (?, ?)";
	private static final String UPDATE_SQL = "UPDATE City SET cityName=?, countryID=? WHERE id=?;";
	private static final String GET_ALL_SQL = "SELECT * FROM City LEFT JOIN Country ON City.countryId = Country.id";
	private static final String GET_BY_ID_SQL = "SELECT "
												+ "	* FROM City "
												+ "LEFT JOIN Country ON City.countryId = Country.id "
												+ "WHERE City.id=?;";
	
	@Override
	public int insert(City city) {
		if(city.getId()!=0){
			throw new DBException("City id must be obtained from DB");
		}
		
		int id=0;
	
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS)){
			preparedStatement.setString(1,city.getCityName());
			preparedStatement.setObject(2, city.getCountry() == null ? null : city.getCountry().getId(), Types.INTEGER);
			int rows = preparedStatement.executeUpdate();
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if (rows == 1 && resultSet!=null  && resultSet.next()) {
                id = resultSet.getInt("GENERATED_KEY");
				city.setId(id);
			}else{
				throw new DBException("Can't get City id from database.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return id;
	}
	
	

	@Override
	public void update(City city) {
		if(city.getId()==0){
			throw new DBException("City id must not equal 0");
		}
		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
			preparedStatement.setString(1, city.getCityName());
			preparedStatement.setObject(2, city.getCountry()==null? null : city.getCountry().getId(), Types.INTEGER);
			preparedStatement.setInt(3, city.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
					
	}

	@Override
	public List<City> getAll() {
		List<City> result = new ArrayList<>();
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
	public City getById(int id) {		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID_SQL)) {
			preparedStatement.setInt(1, id);
			List<City> cities = parseResultSet(preparedStatement.executeQuery());
			return cities.size()==0? null: cities.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void delete(int id) {
		delete(id, "City");		
	}

	private List<City>  parseResultSet(ResultSet resultSet) throws SQLException {
		List<City> result = new ArrayList<>();
		while (resultSet.next()) {
			City city = new City();
			city.setId(resultSet.getInt("id"));
			city.setCityName(resultSet.getString("cityName"));
			if(resultSet.getObject("countryId")!=null){
				Country country = new Country();
				country.setId(resultSet.getInt("countryId"));
				country.setCountryName(resultSet.getString("countryName"));
				city.setCountry(country);
			}
			result.add(city);
		}	
		return result;
	}
	

}
