package com.epam.touragency.dao.impl;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

import com.epam.touragency.dao.CountryDAO;
import com.epam.touragency.dao.factory.DAOFactory;
import com.epam.touragency.entity.Country;

public class CountryDAOTest {

	private final static String COUNTRY_NAME="USA";
	private final static String UPDATE_COUNTRY_NAME="Україна";
	private final DAOFactory daoFactory;	
	private CountryDAO countryDAO;
		
	public CountryDAOTest() {
		daoFactory = new DAOFactory();
		countryDAO = daoFactory.getCountryDAO();
	}
	
	
	@Test
	public void testCreate() {
		Country country = new Country();
		country.setCountryName(COUNTRY_NAME);
		int counryId = countryDAO.insert(country);
		assertTrue("Country ID after creation must be not '0'",counryId>0);
	}
	
	
	@Test
	public void testGetByID(){
		Country country = new Country();
		country.setCountryName(COUNTRY_NAME);
		int counryId = countryDAO.insert(country);
		assertTrue("Country ID after creation must be not '0'",counryId>0);
		Country foundCountry = countryDAO.getById(counryId);
		assertNotNull("No Country found by id="+counryId ,foundCountry);		
	}
	
	@Test
	public void testGetAll(){
		List<Country> countries = countryDAO.getAll();
		assertNotNull("No Countries found ",countries.size()>0);		
	}
	
	@Test
	public void testUpdate(){
		Country country = new Country();
		country.setCountryName(COUNTRY_NAME);
		int counryId = countryDAO.insert(country);
		
		Country countryForUpdate = countryDAO.getById(counryId);
		assertNotNull("Country before update must not be null"+counryId ,countryForUpdate);
		countryForUpdate.setCountryName(UPDATE_COUNTRY_NAME);
		countryDAO.update(countryForUpdate);
		
		Country countryAfterUpdate = countryDAO.getById(counryId);
		assertNotNull("Country after update must not be null"+counryId ,countryAfterUpdate);
		assertEquals("Country name after update must equals "+UPDATE_COUNTRY_NAME, UPDATE_COUNTRY_NAME, countryAfterUpdate.getCountryName());		
	}
	
	@Test
	public void testDelete(){
		Country country = new Country();
		country.setCountryName(COUNTRY_NAME);
		int counryId = countryDAO.insert(country);
		
		countryDAO.delete(counryId);
				
		Country countryAfterDelete = countryDAO.getById(counryId);
		assertNull("Country after delete must be null" ,countryAfterDelete);				
	}
	
	
		
}
