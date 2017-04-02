package com.epam.touragency.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.epam.touragency.dao.BasketDAO;
import com.epam.touragency.dao.TicketDAO;
import com.epam.touragency.dao.UserDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;
import com.epam.touragency.dao.exception.DBException;
import com.epam.touragency.entity.Basket;


public class BasketDAOimpl extends AbstractDAO<Basket> implements BasketDAO{

	private static final String INSERT_SQL = "INSERT INTO Basket (sumPaid, date, userId, ticketId) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE Basket SET sumPaid=?, date=?, userId=?, ticketId=? WHERE id=?;";
	private static final String GET_ALL_SQL = "SELECT "
												+ "* FROM "
												+ "Hotel ;";
	private static final String GET_BY_ID_SQL = "SELECT "
												+ "* FROM "
												+ "Hotel "												
												+ " WHERE id=?;";
	
	@Override
	public int insert(Basket basket) {
		if(basket.getId()!=0){
			throw new DBException("Basket id must be obtained from DB");
		}
		
		int id;
	
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL,Statement.RETURN_GENERATED_KEYS)){
			preparedStatement.setDouble(1,basket.getSumPaid());
			preparedStatement.setDate(2, basket.getDate());
			preparedStatement.setObject(3, basket.getUser() == null ? null : basket.getUser().getId(), Types.INTEGER);			
			preparedStatement.setObject(4, basket.getTicket() == null ? null : basket.getTicket().getId(), Types.INTEGER);
			if(preparedStatement.executeUpdate()==1 && preparedStatement.getGeneratedKeys().next()){
				id = preparedStatement.getGeneratedKeys().getInt("id");
				basket.setId(id);
			}else{
				throw new DBException("Can't get Basket id from database.");
			}
			
		} catch (SQLException e) {
			throw new DBException(e);
		}
			
		return id;
	}
	
	

	@Override
	public void update(Basket basket) {
		if(basket.getId()==0){
			throw new DBException("Basket id must not equal 0");
		}
		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
			preparedStatement.setDouble(1,basket.getSumPaid());
			preparedStatement.setDate(2, basket.getDate());
			preparedStatement.setObject(3, basket.getUser() == null ? null : basket.getUser().getId(), Types.INTEGER);			
			preparedStatement.setObject(4, basket.getTicket() == null ? null : basket.getTicket().getId(), Types.INTEGER);
			preparedStatement.setInt(5, basket.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DBException(e);
		}
					
	}

	@Override
	public List<Basket> getAll() {
		List<Basket> result = new ArrayList<>();
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
	public Basket getById(int id) {		
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID_SQL)) {
			preparedStatement.setInt(1, id);
			List<Basket> cities = parseResultSet(preparedStatement.executeQuery());
			return cities.size()==0? null: cities.get(0);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}
	
	@Override
	public void delete(int id) {
		delete(id, "Basket");		
	}

	private List<Basket>  parseResultSet(ResultSet resultSet) throws SQLException {
		List<Basket> result = new ArrayList<>();
		while (resultSet.next()) {
			Basket basket = new Basket();
			basket.setId(resultSet.getInt("id"));
			basket.setDate(resultSet.getDate("date"));
			basket.setSumPaid(resultSet.getDouble("sumPaid"));
			if(resultSet.getObject("userId",Integer.class)!=null){
				UserDAO userDAO = new UserDAOimpl();				
				basket.setUser(userDAO.getById(resultSet.getInt("userId")));
			}
			if(resultSet.getObject("ticketId",Integer.class)!=null){
				TicketDAO ticketDAO = new TicketDAOimpl();
				basket.setTicket(ticketDAO.getById(resultSet.getInt("ticketId")));
			}
			result.add(basket);
		}	
		return result;
	}
	

}
