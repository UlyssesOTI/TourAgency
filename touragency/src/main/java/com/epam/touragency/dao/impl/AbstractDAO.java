package com.epam.touragency.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.epam.touragency.dao.GenericDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;

public abstract class AbstractDAO<T> implements GenericDAO<T>{

	@Override
	public abstract int insert(T t);

	@Override
	public abstract void update(T t);

	@Override
	public abstract List<T> getAll();

	@Override
	public abstract T getById(int id);

	@Override
	public abstract void delete(int id);
	
	public void delete(int id, String tableName){
		final String DELETE_SQL = "DELETE FROM " + tableName + " WHERE id = ?";

        try (Connection connection = ConnetctionPool.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {

            statement.setInt(1, id);
            statement.executeUpdate();

            //logger.log(Level.INFO, "DELETE ENTITY WITH ID = " + id + " FROM TABLE " + tableName);

        } catch (SQLException ex) {
            //logger.log(Level.SEVERE, ex.getMessage(), ex);
        	ex.printStackTrace();
            //throw new DatabaseException(ex);
        }
	}

}
