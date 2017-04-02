package com.epam.touragency.dao.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.epam.touragency.dao.CityDAO;
import com.epam.touragency.dao.CountryDAO;
import com.epam.touragency.dao.HotelDAO;
import com.epam.touragency.dao.factory.DAOFactory;
import com.epam.touragency.entity.City;
import com.epam.touragency.entity.Country;
import com.epam.touragency.entity.Hotel;

public class HotelDAOTest {
	
	private final static String COUNTRY_NAME="Україна";
	private final static String CITY_NAME="Тернопіль";
	private final static String CITY_NAME_FOR_UPDATE="NEW YORK";
	private final static String HOTEL_NAME="PLAZA";
	private final static String HOTEL_NAME_AFTER_UPDATE="CHERATON";
	private final static int HOTEL_STARS=5;
	private DAOFactory daoFactory;
	private HotelDAO hotelDAO;
	private CityDAO cityDAO;
	private CountryDAO countryDAO;
	
	public HotelDAOTest() {
		this.daoFactory = new DAOFactory();
		this.hotelDAO = daoFactory.getHotelDAO();
		this.countryDAO = daoFactory.getCountryDAO();
		this.cityDAO = daoFactory.getCityDAO();
	}
	
	@Test
	public void testCreate() {
		Country country = EntityDAOTestFactory.getCountry(COUNTRY_NAME);
		countryDAO.insert(country);
		
		City city = EntityDAOTestFactory.getCity(CITY_NAME, country);
		cityDAO.insert(city);
		
		Hotel hotel = EntityDAOTestFactory.getHotel(HOTEL_NAME, HOTEL_STARS, city);
		int hotelId = hotelDAO.insert(hotel);
		assertTrue("Hotel ID after creation must be not '0'", hotelId>0);		
	}
	
	@Test
	public void testGetByID() {
		Country country = EntityDAOTestFactory.getCountry(COUNTRY_NAME);
		countryDAO.insert(country);
		
		City city = EntityDAOTestFactory.getCity(CITY_NAME, country);
		int cityId = cityDAO.insert(city);
		
		Hotel hotel = EntityDAOTestFactory.getHotel(HOTEL_NAME, HOTEL_STARS, city);
		int hotelId = hotelDAO.insert(hotel);
		assertTrue("Hotel ID after creation must be not '0'", hotelId>0);	
		
		Hotel foundHotel = hotelDAO.getById(hotelId);
		assertNotNull("No Hotel found by id="+hotelId,foundHotel);
		assertNotNull("No City found in Hotel",foundHotel.getCity());
		assertTrue("City id must equals"+cityId,foundHotel.getCity().getId()==cityId);
		
	}
	
	@Test
	public void testGetAll(){
		List<Hotel> hotels = hotelDAO.getAll();
		assertNotNull("No Cities found ",hotels.size()>0);		
	}
	
	@Test
	public void testUpdate(){
		
		Country country = EntityDAOTestFactory.getCountry(COUNTRY_NAME);
		int countryIdBeforeUpdate = countryDAO.insert(country);
		assertTrue("Country ID after creation must be not '0'",countryIdBeforeUpdate>0);
		
		City city = EntityDAOTestFactory.getCity(CITY_NAME, country);
		int cityId = cityDAO.insert(city);
		assertTrue("City ID after creation must be not '0'",cityId>0);
				
		
		Hotel hotel = EntityDAOTestFactory.getHotel(HOTEL_NAME, HOTEL_STARS, city);
		int hotelId = hotelDAO.insert(hotel);
		assertTrue("Hotel ID after creation must be not '0'",hotelId>0);
		
		Hotel hotelForUpdate = hotelDAO.getById(hotelId);
		assertNotNull("Hotel before update must not be null"+hotelId ,hotelForUpdate);
		
		City cityAfterUpdate  = EntityDAOTestFactory.getCity(CITY_NAME_FOR_UPDATE, country);
		cityId=0;
		cityId = cityDAO.insert(cityAfterUpdate);
		assertTrue("City ID after creation must be not '0'",cityId>0);
		
		hotelForUpdate.setHotelName(HOTEL_NAME_AFTER_UPDATE);
		hotelForUpdate.setStars(4);
		hotelForUpdate.setCity(cityAfterUpdate);
		hotelDAO.update(hotelForUpdate);
		
		Hotel hotelAfterUpfate = hotelDAO.getById(hotelId);
		assertNotNull("Hotel after update must not be null"+hotelId ,hotelAfterUpfate);
		assertEquals("Hotel name after update must equals "+HOTEL_NAME_AFTER_UPDATE, HOTEL_NAME_AFTER_UPDATE, hotelAfterUpfate.getHotelName());
		assertEquals("Hotel stars after update must equals "+4, 4, hotelAfterUpfate.getStars());
		assertTrue("City id after Hotel update must equals "+cityId, hotelAfterUpfate.getCity().getId()==cityId);
	}
	
	@Test
	public void testDelete(){
		Country country = EntityDAOTestFactory.getCountry(COUNTRY_NAME);
		int countryIdBeforeUpdate = countryDAO.insert(country);
		assertTrue("Country ID after creation must be not '0'",countryIdBeforeUpdate>0);
		
		City city =EntityDAOTestFactory.getCity(CITY_NAME, country);
		int cityId = cityDAO.insert(city);
		assertTrue("City ID after creation must be not '0'",cityId>0);
				
		
		Hotel hotel = EntityDAOTestFactory.getHotel(HOTEL_NAME, HOTEL_STARS, city);
		int hotelId = hotelDAO.insert(hotel);
		assertTrue("Hotel ID after creation must be not '0'",hotelId>0);
				
		hotelDAO.delete(hotelId);
				
		Hotel hotelAfterDelete = hotelDAO.getById(hotelId);
		assertNull("Hotel after delete must be null",hotelAfterDelete);				
	}
	
	
}
