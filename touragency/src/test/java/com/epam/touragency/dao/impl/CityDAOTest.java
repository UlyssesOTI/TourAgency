package com.epam.touragency.dao.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.epam.touragency.dao.CityDAO;
import com.epam.touragency.dao.CountryDAO;
import com.epam.touragency.dao.factory.DAOFactory;
import com.epam.touragency.entity.City;
import com.epam.touragency.entity.Country;

public class CityDAOTest {
	
	private final static String COUNTRY_NAME="Україна";
	private final static String CITY_NAME="Тернопіль";
	private final static String CITY_NAME_FOR_UPDATE="NEW YORK";
	private DAOFactory daoFactory;
	private CountryDAO countryDAO;
	private CityDAO cityDAO;
	
	public CityDAOTest() {
		this.daoFactory = new DAOFactory();
		this.countryDAO = daoFactory.getCountryDAO();
		this.cityDAO = daoFactory.getCityDAO();
	}
	
	@Test
	public void testCreate() {
		Country country = new Country();
		country.setCountryName(COUNTRY_NAME);
		countryDAO.insert(country);
		
		City city = new City();
		city.setCityName(CITY_NAME);
		city.setCountry(country);
		int id = cityDAO.insert(city);
		assertTrue("City ID after creation must be not '0'", id>0);		
	}
	
	@Test
	public void testGetByID() {
		Country country = new Country();
		country.setCountryName(COUNTRY_NAME);
		int countryId = countryDAO.insert(country);
		
		City city = new City();
		city.setCityName(CITY_NAME);
		city.setCountry(country);
		int cityId = cityDAO.insert(city);
		assertTrue("City ID after creation must be not '0'", cityId>0);		
		
		City foundCity = cityDAO.getById(cityId);
		assertNotNull("No City found by id="+cityId,foundCity);
		assertNotNull("No Country found in City",foundCity.getCountry());
		assertTrue("Country id must equals"+countryId,foundCity.getCountry().getId()==countryId);
		
	}
	
	@Test
	public void testGetAll(){
		List<City> cities = cityDAO.getAll();
		assertNotNull("No Cities found ",cities.size()>0);		
	}
	
	@Test
	public void testUpdate(){
		
		Country countryBeforeUpdate = new Country();
		countryBeforeUpdate.setCountryName(COUNTRY_NAME);
		int countryIdBeforeUpdate = countryDAO.insert(countryBeforeUpdate);
		assertTrue("Country ID after creation must be not '0'",countryIdBeforeUpdate>0);
		
		City city = new City();
		city.setCityName(CITY_NAME);
		city.setCountry(countryBeforeUpdate);
		int cityId = cityDAO.insert(city);
				
		City cityForUpdate = cityDAO.getById(cityId);
		assertNotNull("City before update must not be null"+cityId ,cityForUpdate);
		
		Country countryAfterUpdate = new Country();
		countryAfterUpdate.setCountryName("USA");
		int countryIdAfterUpdate = countryDAO.insert(countryAfterUpdate);
		cityForUpdate.setCountry(countryAfterUpdate);
		
		cityForUpdate.setCityName(CITY_NAME_FOR_UPDATE);
		
		cityDAO.update(cityForUpdate);
		
		City cityAfterUpdate = cityDAO.getById(cityId);
		assertNotNull("City after update must not be null"+cityId ,cityAfterUpdate);
		assertEquals("City name after update must equals "+CITY_NAME_FOR_UPDATE, CITY_NAME_FOR_UPDATE, cityAfterUpdate.getCityName());
		assertTrue("City id after Country update must equals "+countryIdAfterUpdate, cityAfterUpdate.getCountry().getId()==countryIdAfterUpdate);
	}
	
	@Test
	public void testDelete(){
		City city = new City();
		city.setCityName(CITY_NAME);
		Country country = new Country();
		country.setCountryName(COUNTRY_NAME);
		countryDAO.insert(country);
		city.setCountry(country);
		int cityId = cityDAO.insert(city);
				
		cityDAO.delete(cityId);
				
		City cityAfterDelete = cityDAO.getById(cityId);
		assertNull("City after delete must be null",cityAfterDelete);				
	}
	
	
}
