package com.epam.touragency.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.epam.touragency.dao.TourTypeDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;
import com.epam.touragency.dao.exception.DBException;
import com.epam.touragency.entity.TourType;

public class TourTypeDAOimpl extends AbstractDAO<TourType> implements TourTypeDAO{

	private static final String INSERT_SQL = "INSERT INTO TourType (tourTypeName) VALUES (?)";
	private static final String UPDATE_SQL = "UPDATE TourType SET tourTypeName=? WHERE id=?;";
	private static final String GET_ALL_SQL = "SELECT * FROM TourType";
	private static final String GET_BY_ID_SQL = "SELECT * FROM TourType WHERE id=?;";
	
	@Override
	public int insert(TourType tourType) {
		if(tourType.getId()!=0){
			throw new DBException("TourType id must be obtained from DB");
		}
		
		int id = 0;
	
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS)){
			preparedStatement.setString(1,tourType.getTourTypeName());
			int rows = preparedStatement.executeUpdate();
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if (rows == 1 && resultSet!=null  && resultSet.next()) {
                id = resultSet.getInt("GENERATED_KEY");
                tourType.setId(id);
			}else{
				throw new DBException("Can't get TourType id from database.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return id;
	}
	
	

	@Override
	public void update(TourType tourType) {
		if(tourType.getId()==0){
			throw new DBException("TourType id must not equal 0");
		}
		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
			preparedStatement.setString(1, tourType.getTourTypeName());
			preparedStatement.setInt(2, tourType.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
					
	}

	@Override
	public List<TourType> getAll() {
		List<TourType> result = new ArrayList<>();
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
	public TourType getById(int id) {		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID_SQL)) {
			preparedStatement.setInt(1, id);
			List<TourType> cities = parseResultSet(preparedStatement.executeQuery());
			return cities.size()==0? null: cities.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void delete(int id) {
		delete(id, "TourType");		
	}

	private List<TourType>  parseResultSet(ResultSet resultSet) throws SQLException {
		List<TourType> result = new ArrayList<>();
		while (resultSet.next()) {
			TourType tourType = new TourType();
			tourType.setId(resultSet.getInt("id"));
			tourType.setTourTypeName(resultSet.getString("tourTypeName"));
			result.add(tourType);
		}	
		return result;
	}
	

}
