package com.epam.touragency.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.epam.touragency.dao.TicketDAO;
import com.epam.touragency.dao.TourDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;
import com.epam.touragency.dao.exception.DBException;
import com.epam.touragency.entity.Ticket;


public class TicketDAOimpl extends AbstractDAO<Ticket> implements TicketDAO{

	private static final String INSERT_SQL = "INSERT INTO Ticket (startDate, endDate, price, tourId) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE Ticket SET startDate=?, endDate=?, price=?, tourId=? WHERE id=?;";
	private static final String GET_ALL_SQL = "SELECT "
												+ "* FROM "
												+ "Ticket;";
	private static final String GET_BY_ID_SQL ="SELECT "
												+ "* FROM "
												+ "Ticket "					
												+ "WHERE Ticket.id=?;";
	
	@Override
	public int insert(Ticket ticket) {
		if(ticket.getId()!=0){
			throw new DBException("Ticket id must be obtained from DB");
		}
		
		int id=0;
	
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS)){
			preparedStatement.setDate(1,ticket.getStartDate());
			preparedStatement.setDate(2, ticket.getEndDate());
			preparedStatement.setDouble(3, ticket.getPrice());
			preparedStatement.setObject(4, ticket.getTour()==null? null : ticket.getTour().getId(), Types.INTEGER);		
			int  rowCount = preparedStatement.executeUpdate();
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if(rowCount==1 && resultSet!=null && resultSet.next()){
				id = resultSet.getInt("GENERATED_KEY");
				ticket.setId(id);
			}else{
				throw new DBException("Can't get Ticket id from database.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		return id;
	}
	
	

	@Override
	public void update(Ticket ticket) {
		if(ticket.getId()==0){
			throw new DBException("Ticket id must not equal 0");
		}
		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
			preparedStatement.setDate(1,ticket.getStartDate());
			preparedStatement.setDate(2, ticket.getEndDate());
			preparedStatement.setDouble(3, ticket.getPrice());
			preparedStatement.setObject(4, ticket.getTour()==null? null : ticket.getTour().getId(), Types.INTEGER);	
			preparedStatement.setInt(5, ticket.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
					
	}

	@Override
	public List<Ticket> getAll() {
		List<Ticket> result = new ArrayList<>();
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
	public Ticket getById(int id) {		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID_SQL)) {
			preparedStatement.setInt(1, id);
			List<Ticket> cities = parseResultSet(preparedStatement.executeQuery());
			return cities.size()==0? null: cities.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void delete(int id) {
		delete(id, "Ticket");		
	}

	private List<Ticket>  parseResultSet(ResultSet resultSet) throws SQLException {
		List<Ticket> result = new ArrayList<>();
		while (resultSet.next()) {
			Ticket ticket = new Ticket();
			ticket.setId(resultSet.getInt("id"));
			ticket.setStartDate(resultSet.getDate("startDate"));
			ticket.setEndDate(resultSet.getDate("endDate"));
			ticket.setPrice(resultSet.getDouble("price"));
			if(resultSet.getObject("tourId",Integer.class)!=null){
				TourDAO tourDAO = new TourDAOimpl();					
				ticket.setTour(tourDAO.getById(resultSet.getInt("tourId")));
			}
			result.add(ticket);
		}	
		return result;
	}
	

}
