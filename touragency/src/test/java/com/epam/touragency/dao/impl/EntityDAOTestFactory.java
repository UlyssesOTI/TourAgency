package com.epam.touragency.dao.impl;

import java.util.List;

import com.epam.touragency.entity.Board;
import com.epam.touragency.entity.City;
import com.epam.touragency.entity.Country;
import com.epam.touragency.entity.Hotel;
import com.epam.touragency.entity.Tour;
import com.epam.touragency.entity.TourType;

public class EntityDAOTestFactory {
		
	public static Country getCountry(String countryName) {
		Country country = new Country();
		country.setCountryName(countryName);
		return country;
	}
	
	public static City getCity(String cityName, Country country) {
		City city = new City();
		city.setCityName(cityName);
		city.setCountry(country);
		return city;
	}
		
	public static Hotel getHotel(String hotelName, int stars, City city) {
		Hotel hotel = new Hotel();
		hotel.setHotelName(hotelName);
		hotel.setStars(stars);
		hotel.setCity(city);
		return hotel;
	}
	
	public static TourType getTourType(String tourTypeName){
		TourType tourType = new TourType();
		tourType.setTourTypeName(tourTypeName);
		return tourType;
	}
	
	public static Tour getTour(String tourName, int adults, int kids, Board board, String descr, TourType tourType, List<Hotel> hotels){
		Tour tour = new Tour();
		tour.setTourName(tourName);
		tour.setAdults(adults);
		tour.setKids(kids);
		tour.setBoard(board);
		tour.setDescription(descr);
		tour.setHotels(hotels);
		tour.setTourType(tourType);
		return tour;
	}
}
