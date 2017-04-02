package com.epam.touragency.dao.impl;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.epam.touragency.dao.CityDAO;
import com.epam.touragency.dao.CountryDAO;
import com.epam.touragency.dao.HotelDAO;
import com.epam.touragency.dao.TourDAO;
import com.epam.touragency.dao.TourTypeDAO;
import com.epam.touragency.dao.connectpool.ConnetctionPool;
import com.epam.touragency.dao.factory.DAOFactory;
import com.epam.touragency.entity.Board;
import com.epam.touragency.entity.City;
import com.epam.touragency.entity.Country;
import com.epam.touragency.entity.Hotel;
import com.epam.touragency.entity.Tour;
import com.epam.touragency.entity.TourType;


public class TourDAOTest {
	
	private final static String TOUR_NAME="MIAMI RESORT";
	private final static String TOUR_DESCR="MIAMI RESORT BB";
	private final static String COUNTRY_NAME="Україна";
	private final static String CITY_NAME="Тернопіль";
	private final static String HOTEL_NAME="PLAZA";
	private final static int HOTEL_STARS=5;
	
	private final DAOFactory daoFactory;
	private TourDAO tourDAO;
	private TourTypeDAO tourTypeDAO;
	private HotelDAO hotelDAO;
	private CityDAO cityDAO;
	private CountryDAO countryDAO;
	
	public TourDAOTest() {
		this.daoFactory = new DAOFactory();
		this.tourDAO = daoFactory.getTourDAO();
		this.tourTypeDAO = daoFactory.getTourTypeDAO();
		this.hotelDAO = daoFactory.getHotelDAO();
		this.countryDAO = daoFactory.getCountryDAO();
		this.cityDAO = daoFactory.getCityDAO();
	}
	
	@After
	public void setup(){
		try(Connection connection = ConnetctionPool.getConnection();
				PreparedStatement preparedStatement1 = connection.prepareStatement("delete from  TourHotel ");
				PreparedStatement preparedStatement2 = connection.prepareStatement("delete from  Tour ");
				PreparedStatement preparedStatement3 = connection.prepareStatement("delete from  Hotel ");
				PreparedStatement preparedStatement4 = connection.prepareStatement("delete from  City ");
				PreparedStatement preparedStatement5 = connection.prepareStatement("delete from  Country ");
				PreparedStatement preparedStatement6 = connection.prepareStatement("delete from  TourType ");) {
			
			preparedStatement1.executeUpdate();
			preparedStatement2.executeUpdate();
			preparedStatement3.executeUpdate();
			preparedStatement4.executeUpdate();
			preparedStatement5.executeUpdate();
			preparedStatement6.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreate() {
		TourType tourType = EntityDAOTestFactory.getTourType("Relax");
		tourTypeDAO.insert(tourType);
		
		List<Hotel> hotels = new ArrayList<>();
		
		Country country = EntityDAOTestFactory.getCountry(COUNTRY_NAME);
		int countryID = countryDAO.insert(country);
		assertTrue("Country ID after creation must be not '0'", countryID>0);
		
		City city = EntityDAOTestFactory.getCity(CITY_NAME, country);
		int cityID = cityDAO.insert(city);
		assertTrue("City ID after creation must be not '0'", cityID>0);
		
		Hotel hotel = EntityDAOTestFactory.getHotel(HOTEL_NAME, HOTEL_STARS, city);
		int hotelId = hotelDAO.insert(hotel);
		assertTrue("Hotel ID after creation must be not '0'", hotelId>0);
		
		hotels.add(hotel);
		
		Country country2 = EntityDAOTestFactory.getCountry("UK");
		int countryID2 = countryDAO.insert(country2);
		assertTrue("Country ID after creation must be not '0'", countryID2>0);
		
		City city2 = EntityDAOTestFactory.getCity("London", country2);
		int cityID2 = cityDAO.insert(city2);
		assertTrue("City ID after creation must be not '0'", cityID2>0);
		
		Hotel hotel2 = EntityDAOTestFactory.getHotel("HELL", 3, city2);
		int hotelId2 = hotelDAO.insert(hotel2);
		assertTrue("Hotel ID after creation must be not '0'", hotelId2>0);
		
		hotels.add(hotel2);
		
		Tour tour = EntityDAOTestFactory.getTour(TOUR_NAME, 2, 0, Board.BB, TOUR_DESCR, tourType, hotels);
		int tourId = tourDAO.insert(tour);
		assertTrue("Tour ID after creation must be not '0'", tourId>0);
			
	}
	
	@Test
	public void testGetByID() {
		
		TourType tourType = EntityDAOTestFactory.getTourType("Relax");
		tourTypeDAO.insert(tourType);
		
		List<Hotel> hotels = new ArrayList<>();
		
		Country country = EntityDAOTestFactory.getCountry(COUNTRY_NAME);
		int countryID = countryDAO.insert(country);
		assertTrue("Country ID after creation must be not '0'", countryID>0);
		
		City city = EntityDAOTestFactory.getCity(CITY_NAME, country);
		int cityID = cityDAO.insert(city);
		assertTrue("City ID after creation must be not '0'", cityID>0);
		
		Hotel hotel = EntityDAOTestFactory.getHotel(HOTEL_NAME, HOTEL_STARS, city);
		int hotelId = hotelDAO.insert(hotel);
		assertTrue("Hotel ID after creation must be not '0'", hotelId>0);
		
		hotels.add(hotel);
		
		Country country2 = EntityDAOTestFactory.getCountry("UK");
		int countryID2 = countryDAO.insert(country2);
		assertTrue("Country ID after creation must be not '0'", countryID2>0);
		
		City city2 = EntityDAOTestFactory.getCity("London", country2);
		int cityID2 = cityDAO.insert(city2);
		assertTrue("City ID after creation must be not '0'", cityID2>0);
		
		Hotel hotel2 = EntityDAOTestFactory.getHotel("HELL", 3, city2);
		int hotelId2 = hotelDAO.insert(hotel2);
		assertTrue("Hotel ID after creation must be not '0'", hotelId2>0);
		
		hotels.add(hotel2);
		
		Tour tour = EntityDAOTestFactory.getTour(TOUR_NAME, 2, 0, Board.BB, TOUR_DESCR, tourType, hotels);
		int tourId = tourDAO.insert(tour);
		assertTrue("Tour ID after creation must be not '0'", tourId>0);
		
		Tour foundTour = tourDAO.getById(tourId);
		assertNotNull("No Tour found by id="+tourId,foundTour);
		assertTrue("No Hotels found in Hotel",foundTour.getHotels().size()>0);
		assertTrue("No Hotel found with id "+hotelId,foundTour.getHotels().contains(hotel));
		assertTrue("No Hotel found with id "+hotelId2,foundTour.getHotels().contains(hotel2));
	}
	

	@Test
	public void testGetAll(){
		List<Tour> tours = tourDAO.getAll();
		assertNotNull("No Users found ",tours.size()>0);		
	}
	
	@Test
	public void testUpdate(){
		TourType tourType = EntityDAOTestFactory.getTourType("Relax");
		tourTypeDAO.insert(tourType);
		
		List<Hotel> hotels = new ArrayList<>();
		
		Country country = EntityDAOTestFactory.getCountry(COUNTRY_NAME);
		int countryID = countryDAO.insert(country);
		assertTrue("Country ID after creation must be not '0'", countryID>0);
		
		City city = EntityDAOTestFactory.getCity(CITY_NAME, country);
		int cityID = cityDAO.insert(city);
		assertTrue("City ID after creation must be not '0'", cityID>0);
		
		Hotel hotel = EntityDAOTestFactory.getHotel(HOTEL_NAME, HOTEL_STARS, city);
		int hotelId = hotelDAO.insert(hotel);
		assertTrue("Hotel ID after creation must be not '0'", hotelId>0);
		
		hotels.add(hotel);
		
		Country country2 = EntityDAOTestFactory.getCountry("UK");
		int countryID2 = countryDAO.insert(country2);
		assertTrue("Country ID after creation must be not '0'", countryID2>0);
		
		City city2 = EntityDAOTestFactory.getCity("London", country2);
		int cityID2 = cityDAO.insert(city2);
		assertTrue("City ID after creation must be not '0'", cityID2>0);
		
		Hotel hotel2 = EntityDAOTestFactory.getHotel("HELL", 3, city2);
		int hotelId2 = hotelDAO.insert(hotel2);
		assertTrue("Hotel ID after creation must be not '0'", hotelId2>0);
		
		hotels.add(hotel2);
		
		Tour tour = EntityDAOTestFactory.getTour(TOUR_NAME, 2, 0, Board.BB, TOUR_DESCR, tourType, hotels);
		int tourId = tourDAO.insert(tour);
		assertTrue("Tour ID after creation must be not '0'", tourId>0);
		
		// Tour UPDATE OPERATION
		
		Tour tourForUpdate = tourDAO.getById(tourId);
		assertNotNull("Tour before update must not be null"+tourId ,tourForUpdate);
		
		TourType tourTypeForUpdate = EntityDAOTestFactory.getTourType("Weekend");
		tourTypeDAO.insert(tourTypeForUpdate);

		tourForUpdate.setTourType(tourTypeForUpdate);
		
		List<Hotel> hotelsForUpdate = new ArrayList<>();
		
		hotelsForUpdate.add(hotel2);
		
		Country countryForUpdate = EntityDAOTestFactory.getCountry("Germany");
		int countryIDForUpdate = countryDAO.insert(countryForUpdate);
		assertTrue("Country ID after creation must be not '0'", countryIDForUpdate>0);
		
		City cityForUpdate = EntityDAOTestFactory.getCity("Berlin", countryForUpdate);
		int cityIDForUpdate = cityDAO.insert(cityForUpdate);
		assertTrue("City ID after creation must be not '0'", cityIDForUpdate>0);
		
		Hotel hotelForUpdate = EntityDAOTestFactory.getHotel("Hitler", HOTEL_STARS, cityForUpdate);
		int hotelIdForUpdate = hotelDAO.insert(hotelForUpdate);
		assertTrue("Hotel ID after creation must be not '0'", hotelIdForUpdate>0);
		
		hotelsForUpdate.add(hotelForUpdate);
		
		tourForUpdate.setHotels(hotelsForUpdate);
		
		tourDAO.update(tourForUpdate);
		
		Tour tourAfterUpdate = tourDAO.getById(tourId);
		assertNotNull("Tour after update must not be null"+tourId ,tourAfterUpdate);
		assertTrue("No Hotels found in Hotel",tourAfterUpdate.getHotels().size()==2);	
		assertTrue("No Hotel found with id "+hotel2.getId(),tourAfterUpdate.getHotels().contains(hotel2));
		assertTrue("No Hotel found with id "+hotelForUpdate.getId(),tourAfterUpdate.getHotels().contains(hotelForUpdate));
		assertFalse("No Hotel shoud be found with id "+hotelId,tourAfterUpdate.getHotels().contains(hotel));
		assertNotNull("TourType must not equal null",tourAfterUpdate.getTourType());
		assertTrue("TourType name must not equal "+tourAfterUpdate.getTourType().getTourTypeName(),tourAfterUpdate.getTourType().getTourTypeName().equals(tourTypeForUpdate.getTourTypeName()));
	}
	
	@Test
	public void testDelete(){
		TourType tourType = EntityDAOTestFactory.getTourType("Relax");
		tourTypeDAO.insert(tourType);
		
		List<Hotel> hotels = new ArrayList<>();
		
		Country country = EntityDAOTestFactory.getCountry(COUNTRY_NAME);
		int countryID = countryDAO.insert(country);
		assertTrue("Country ID after creation must be not '0'", countryID>0);
		
		City city = EntityDAOTestFactory.getCity(CITY_NAME, country);
		int cityID = cityDAO.insert(city);
		assertTrue("City ID after creation must be not '0'", cityID>0);
		
		Hotel hotel = EntityDAOTestFactory.getHotel(HOTEL_NAME, HOTEL_STARS, city);
		int hotelId = hotelDAO.insert(hotel);
		assertTrue("Hotel ID after creation must be not '0'", hotelId>0);
		
		hotels.add(hotel);
		
		Country country2 = EntityDAOTestFactory.getCountry("UK");
		int countryID2 = countryDAO.insert(country2);
		assertTrue("Country ID after creation must be not '0'", countryID2>0);
		
		City city2 = EntityDAOTestFactory.getCity("London", country2);
		int cityID2 = cityDAO.insert(city2);
		assertTrue("City ID after creation must be not '0'", cityID2>0);
		
		Hotel hotel2 = EntityDAOTestFactory.getHotel("HELL", 3, city2);
		int hotelId2 = hotelDAO.insert(hotel2);
		assertTrue("Hotel ID after creation must be not '0'", hotelId2>0);
		
		hotels.add(hotel2);
		
		Tour tour = EntityDAOTestFactory.getTour(TOUR_NAME, 2, 0, Board.BB, TOUR_DESCR, tourType, hotels);
		int tourId = tourDAO.insert(tour);
		assertTrue("Tour ID after creation must be not '0'", tourId>0);
		
		tourDAO.delete(tourId);
		Tour deletedTour =  tourDAO.getById(tourId);
		assertNull("Tour after delete must be null" ,deletedTour);			
	}
	
	
}
