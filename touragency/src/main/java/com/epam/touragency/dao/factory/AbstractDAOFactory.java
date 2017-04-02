package com.epam.touragency.dao.factory;

import com.epam.touragency.dao.BasketDAO;
import com.epam.touragency.dao.CityDAO;
import com.epam.touragency.dao.CountryDAO;
import com.epam.touragency.dao.HotelDAO;
import com.epam.touragency.dao.TicketDAO;
import com.epam.touragency.dao.TourDAO;
import com.epam.touragency.dao.TourTypeDAO;
import com.epam.touragency.dao.UserDAO;

public abstract class AbstractDAOFactory {

	public abstract BasketDAO getBasketDAO();
	public abstract CityDAO getCityDAO();
	public abstract CountryDAO getCountryDAO();
	public abstract HotelDAO getHotelDAO();
	public abstract TicketDAO getTicketDAO();
	public abstract TourDAO getTourDAO();
	public abstract TourTypeDAO getTourTypeDAO();
	public abstract UserDAO getUserDAO();
	
	
}
