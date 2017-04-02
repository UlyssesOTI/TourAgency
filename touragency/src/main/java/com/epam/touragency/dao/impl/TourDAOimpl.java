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
import com.epam.touragency.dao.TourDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;
import com.epam.touragency.dao.exception.DBException;
import com.epam.touragency.entity.Board;
import com.epam.touragency.entity.Hotel;
import com.epam.touragency.entity.Tour;
import com.epam.touragency.entity.TourType;

public class TourDAOimpl extends AbstractDAO<Tour> implements TourDAO{

	private static final String INSERT_SQL = "INSERT INTO Tour (tourName, adults, kids, board, description, typeId) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE Tour SET tourName=?, adults=?, kids=?, board=?, description=?, typeId=? WHERE id=?;";	
	private static final String INSERT_TOUR_HOTEL_SQL = "REPLACE INTO TourHotel (tourId, hotelId) VALUES (?, ?)";
	private static final String DELETE_TOUR_HOTEL_SQL = "DELETE FROM TourHotel WHERE tourId = ? AND hotelId NOT IN ";
	private static final String GET_ALL_SQL = "SELECT "
												+ "* FROM "
												+ "Tour "
												+ "LEFT JOIN TourType ON Tour.typeId = TourType.id";
	private static final String GET_BY_ID_SQL = "SELECT "
												+ "* FROM "
												+ "Tour "
												+ "LEFT JOIN TourType ON Tour.typeId = TourType.id"
												+ " WHERE Tour.id=?;";
	private static final String GET_ALL_TOUR_HOTEL_SQL = "SELECT "
															+ "hotelId FROM "
																+ "TourHotel "
																+ "WHERE tourId=?";
	
	@Override
	public int insert(Tour tour) {
		if(tour.getId()!=0){
			throw new DBException("Tour id must be obtained from DB");
		}
		
		int id=0;
	
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS)){
			connection.setAutoCommit(false);
			preparedStatement.setString(1,tour.getTourName());
			preparedStatement.setInt(2, tour.getAdults());
			preparedStatement.setInt(3, tour.getKids());
			preparedStatement.setString(4, tour.getBoard().toString());
			preparedStatement.setString(5, tour.getDescription());
			preparedStatement.setObject(6, tour.getTourType() == null ? null :tour.getTourType().getId(), Types.INTEGER);			
			int rows = preparedStatement.executeUpdate();
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if (rows == 1 && resultSet!=null  && resultSet.next()) {
                id = resultSet.getInt("GENERATED_KEY");
				tour.setId(id);
				insertTourHotels(tour, connection);
				connection.commit();
			}else{
				connection.rollback();
				throw new DBException("Can't get Tour id from database.");
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
			
		return id;
	}
	
	private void insertTourHotels(Tour tour, Connection connection) throws SQLException{
		try(PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TOUR_HOTEL_SQL)){
			for (Hotel hotel: tour.getHotels()) {
				preparedStatement.setInt(1, tour.getId());
				preparedStatement.setInt(2, hotel.getId());
				preparedStatement.executeUpdate();
			}
		}
	}
	
	@Override
	public void update(Tour tour) {
		if(tour.getId()==0){
			throw new DBException("Tour id must not equal 0");
		}
		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
			connection.setAutoCommit(false);
			preparedStatement.setString(1,tour.getTourName());
			preparedStatement.setInt(2, tour.getAdults());
			preparedStatement.setInt(3, tour.getKids());
			preparedStatement.setString(4, tour.getBoard().toString());
			preparedStatement.setString(5, tour.getDescription());
			preparedStatement.setObject(6, tour.getTourType() == null ? null :tour.getTourType().getId(), Types.INTEGER);
			preparedStatement.setInt(7, tour.getId());
			try{
				preparedStatement.executeUpdate();
				deleteTourHotels(tour, connection);
				insertTourHotels(tour, connection);
				connection.commit();
			}catch (SQLException e) {
				connection.rollback();
				e.printStackTrace();
			}			
		} catch (SQLException e) {			
			e.printStackTrace();
		}
					
	}
	
	private void deleteTourHotels(Tour tour, Connection connection) throws SQLException {
		final List<Integer> integers = new ArrayList<>();
		tour.getHotels().stream().forEach((e)->{integers.add(e.getId());});
		StringBuilder sb = new StringBuilder(DELETE_TOUR_HOTEL_SQL+"(");
		for (int i = 0; i < integers.size(); i++) {			
			sb.append("?,");
		}
		sb.deleteCharAt(sb.length() -1);
		sb.append(")");
		try(PreparedStatement preparedStatement = connection.prepareStatement(sb.toString())){
			preparedStatement.setInt(1, tour.getId());
			for (int i = 0; i < integers.size(); i++) {
				preparedStatement.setInt(i+2,integers.get(i));
			}
			preparedStatement.executeUpdate();
		}
	}
	
	@Override
	public List<Tour> getAll() {
		List<Tour> result = new ArrayList<>();
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
	public Tour getById(int id) {		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID_SQL)) {
			preparedStatement.setInt(1, id);
			List<Tour> cities = parseResultSet(preparedStatement.executeQuery());
			return cities.size()==0? null: cities.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void delete(int id) {
		delete(id, "Tour");		
	}
	
	private List<Hotel> geAllTourHotels(Tour tour){
		HotelDAO hotelDAO = new HotelDAOimpl();
		List<Hotel> result = new ArrayList<>();
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_TOUR_HOTEL_SQL)) {
				preparedStatement.setInt(1, tour.getId());
				ResultSet rs = preparedStatement.executeQuery();
				while (rs.next()) {
					result.add(hotelDAO.getById(rs.getInt("hotelID")));
				}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private List<Tour>  parseResultSet(ResultSet resultSet) throws SQLException {
		List<Tour> result = new ArrayList<>();
		while (resultSet.next()) {
			Tour tour = new Tour();
			tour.setId(resultSet.getInt("id"));
			tour.setTourName(resultSet.getString("tourName"));
			tour.setAdults(resultSet.getInt("adults"));
			tour.setKids(resultSet.getInt("kids"));
			tour.setBoard(Board.valueOf(resultSet.getString("board")));
			tour.setDescription(resultSet.getString("description"));
			if(resultSet.getObject("typeId")!=null){
				TourType tourType = new TourType();
				tourType.setId(resultSet.getInt("typeId"));
				tourType.setTourTypeName(resultSet.getString("tourTypeName"));				
				tour.setTourType(tourType);
			}
			tour.setHotels(geAllTourHotels(tour));
			result.add(tour);
		}	
		return result;
	}

}
