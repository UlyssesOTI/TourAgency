package com.epam.touragency.dao.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

import com.epam.touragency.dao.UserDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;
import com.epam.touragency.dao.factory.DAOFactory;
import com.epam.touragency.entity.Role;
import com.epam.touragency.entity.User;

public class UserDAOTest {

	private final static String USER_FNAME="Tomas";
	private final static String USER_LNAME="Anderson";
	private final static Date USER_BDATE= new Date(1000000000);
	private final static String USER_EMAIL= "user@mail.com";
	private final static String USER_PASSWORD= "123456";
	private final static String USER_FNAME_AFTER_UPDATE="Іван";
	private final static String USER_LNAME_AFTER_UPDATE="Баран";
	private final static Date USER_BDATE_AFTER_UPDATE= new Date(1029600000);
	private final static String USER_EMAIL_AFTER_UPDATE= "шван@mail.com";
	private final static String USER_PASSWORD_AFTER_UPDATE= "654321";
	private final DAOFactory daoFactory;	
	private UserDAO userDAO;
		
	public UserDAOTest() {
		daoFactory = new DAOFactory();
		userDAO = daoFactory.getUserDAO();
	}
	
	@Before
	public void testSetup(){
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("delete from User WHERE eMail=?")) {
			preparedStatement.setString(1, USER_EMAIL);
			preparedStatement.executeUpdate();
			preparedStatement.setString(1, USER_EMAIL_AFTER_UPDATE);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testCreate() {
		User user = new User();
		user.setfName(USER_FNAME);
		user.setlName(USER_LNAME);
		user.setBirthDate(USER_BDATE);
		user.setRole(Role.CLIENT);
		user.seteMail(USER_EMAIL);
		user.setPassword(USER_PASSWORD);
		int userId = userDAO.insert(user);
		assertTrue("User ID after creation must be not '0'",userId>0);
	}
	
	
	@Test
	public void testGetByID(){
		User user = new User();
		user.setfName(USER_FNAME);
		user.setlName(USER_LNAME);
		user.setBirthDate(USER_BDATE);
		user.setRole(Role.CLIENT);
		user.seteMail(USER_EMAIL);
		user.setPassword(USER_PASSWORD);
		int userId = userDAO.insert(user);
		assertTrue("User ID after creation must be not '0'",userId>0);
		User foundUser = userDAO.getById(userId);
		assertNotNull("No User found by id="+userId ,foundUser);		
	}
	
	@Test
	public void testGetAll(){
		List<User> users = userDAO.getAll();
		assertNotNull("No Users found ",users.size()>0);		
	}
	
	@Test
	public void testUpdate(){
		User user = new User();
		user.setfName(USER_FNAME);
		user.setlName(USER_LNAME);
		user.setBirthDate(USER_BDATE);
		user.setRole(Role.CLIENT);
		user.seteMail(USER_EMAIL);
		user.setPassword(USER_PASSWORD);
		int userId = userDAO.insert(user);
		assertTrue("User ID after creation must be not '0'",userId>0);
		
		User userForUpdate = userDAO.getById(userId);
		assertNotNull("User before update must not be null"+userId ,userForUpdate);
		userForUpdate.setfName(USER_FNAME_AFTER_UPDATE);
		userForUpdate.setlName(USER_LNAME_AFTER_UPDATE);
		userForUpdate.setBirthDate(USER_BDATE_AFTER_UPDATE);
		userForUpdate.setRole(Role.MANAGER);
		userForUpdate.seteMail(USER_EMAIL_AFTER_UPDATE);
		userForUpdate.setPassword(USER_PASSWORD_AFTER_UPDATE);
		userDAO.update(userForUpdate);
		
		User userAfterUpdate = userDAO.getById(userId);
		assertNotNull("User after update must not be null"+userId ,userAfterUpdate);
		assertEquals("User name after update must equals "+USER_FNAME_AFTER_UPDATE, USER_FNAME_AFTER_UPDATE, userAfterUpdate.getfName());		
		assertEquals("User last name after update must equals "+USER_LNAME_AFTER_UPDATE, USER_LNAME_AFTER_UPDATE, userAfterUpdate.getlName());
		assertEquals("User BirthDate after update must equals "+USER_BDATE_AFTER_UPDATE, USER_BDATE_AFTER_UPDATE, userAfterUpdate.getBirthDate());
		assertEquals("User Role after update must equals "+Role.MANAGER, Role.MANAGER, userAfterUpdate.getRole());
		assertEquals("User Mail after update must equals "+USER_EMAIL_AFTER_UPDATE, USER_EMAIL_AFTER_UPDATE, userAfterUpdate.geteMail());
		assertEquals("User Password after update must equals "+USER_PASSWORD_AFTER_UPDATE,USER_PASSWORD_AFTER_UPDATE, userAfterUpdate.getPassword());
	}
	
	@Test
	public void testDelete(){
		User user = new User();
		user.setfName(USER_FNAME);
		user.setlName(USER_LNAME);
		user.setBirthDate(USER_BDATE);
		user.setRole(Role.CLIENT);
		user.seteMail(USER_EMAIL);
		user.setPassword(USER_PASSWORD);
		int userId = userDAO.insert(user);
		assertTrue("User ID after creation must be not '0'",userId>0);
		
		userDAO.delete(userId);
				
		User userAfterDelete = userDAO.getById(userId);
		assertNull("User after delete must be null" ,userAfterDelete);				
	}
	
	
		
}
