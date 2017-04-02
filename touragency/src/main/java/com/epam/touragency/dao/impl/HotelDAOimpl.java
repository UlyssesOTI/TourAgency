package com.epam.touragency.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.epam.touragency.dao.HotelDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;
import com.epam.touragency.dao.exception.DBException;
import com.epam.touragency.entity.City;
import com.epam.touragency.entity.Country;
import com.epam.touragency.entity.Hotel;

public class HotelDAOimpl extends AbstractDAO<Hotel> implements HotelDAO{

	private static final String INSERT_SQL = "INSERT INTO Hotel (hotelName, stars, cityId) VALUES (?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE Hotel SET hotelName=?, stars=?, cityId=? WHERE id=?;";
	private static final String GET_ALL_SQL = "SELECT "
												+ "* FROM "
												+ "Hotel "
												+ "LEFT JOIN City ON Hotel.cityId = City.id "
												+ "LEFT JOIN Country ON City.countryId = Country.id";
	private static final String GET_BY_ID_SQL = "SELECT "
												+ "* FROM "
												+ "Hotel "
												+ "LEFT JOIN City ON Hotel.cityId = City.id "
												+ "LEFT JOIN Country ON City.countryId = Country.id "
												+ "WHERE Hotel.id=?;";
	
	@Override
	public int insert(Hotel hotel) {
		if(hotel.getId()!=0){
			throw new DBException("Hotel id must be obtained from DB");
		}
		
		int id;
	
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS)){
			preparedStatement.setString(1,hotel.getHotelName());
			preparedStatement.setInt(2, hotel.getStars());
			preparedStatement.setObject(3, hotel.getCity() == null ? null : hotel.getCity().getId(), Types.INTEGER);
			int  rowCount = preparedStatement.executeUpdate();
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if(rowCount==1 && resultSet!=null && resultSet.next()){
				id = resultSet.getInt("GENERATED_KEY");
				hotel.setId(id);
			}else{
				throw new DBException("Can't get Hotel id from database.");
			}
			
		} catch (SQLException e) {
			throw new DBException(e);
		}
			
		return id;
	}
	
	

	@Override
	public void update(Hotel hotel) {
		if(hotel.getId()==0){
			throw new DBException("Hotel id must not equal 0");
		}
		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
			preparedStatement.setString(1, hotel.getHotelName());
			preparedStatement.setInt(2, hotel.getStars());
			preparedStatement.setObject(3, hotel.getCity() == null ? null : hotel.getCity().getId(), Types.INTEGER);
			preparedStatement.setInt(4, hotel.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBException(e);
		}
					
	}

	@Override
	public List<Hotel> getAll() {
		List<Hotel> result = new ArrayList<>();
		try(Connection connection = ConnetctionPool.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_SQL)) {
			preparedStatement.executeQuery();
			result = parseResultSet(preparedStatement.executeQuery());				
		} catch (SQLException e) {
			throw new DBException(e);
		}
		return result;
	}
	
	@Override
	public Hotel getById(int id) {		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID_SQL)) {
			preparedStatement.setInt(1, id);
			List<Hotel> cities = parseResultSet(preparedStatement.executeQuery());
			return cities.size()==0? null: cities.get(0);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}
	
	@Override
	public void delete(int id) {
		delete(id, "Hotel");		
	}

	private List<Hotel>  parseResultSet(ResultSet resultSet) throws SQLException {
		List<Hotel> result = new ArrayList<>();
		while (resultSet.next()) {
			Hotel hotel = new Hotel();
			hotel.setId(resultSet.getInt("id"));
			hotel.setHotelName(resultSet.getString("hotelName"));
			hotel.setStars(resultSet.getInt("stars"));
			if(resultSet.getObject("cityId")!=null){
				City city = new City();
				city.setId(resultSet.getInt("cityId"));
				city.setCityName(resultSet.getString("cityName"));
				if(resultSet.getObject("countryId")!=null){
					Country country = new Country();
					country.setId(resultSet.getInt("countryID"));
					country.setCountryName(resultSet.getString("countryName"));
					city.setCountry(country);
				}
				hotel.setCity(city);
			}
			result.add(hotel);
		}	
		return result;
	}
	

}
