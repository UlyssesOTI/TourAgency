package com.epam.touragency.dao.factory;

import com.epam.touragency.dao.BasketDAO;
import com.epam.touragency.dao.CityDAO;
import com.epam.touragency.dao.CountryDAO;
import com.epam.touragency.dao.HotelDAO;
import com.epam.touragency.dao.TicketDAO;
import com.epam.touragency.dao.TourDAO;
import com.epam.touragency.dao.TourTypeDAO;
import com.epam.touragency.dao.UserDAO;
import com.epam.touragency.dao.impl.BasketDAOimpl;
import com.epam.touragency.dao.impl.CityDAOimpl;
import com.epam.touragency.dao.impl.CountryDAOimpl;
import com.epam.touragency.dao.impl.HotelDAOimpl;
import com.epam.touragency.dao.impl.TicketDAOimpl;
import com.epam.touragency.dao.impl.TourDAOimpl;
import com.epam.touragency.dao.impl.TourTypeDAOimpl;
import com.epam.touragency.dao.impl.UserDAOimpl;

public class DAOFactory extends AbstractDAOFactory{

	@Override
	public BasketDAO getBasketDAO() {
		return new BasketDAOimpl();
	}

	@Override
	public CityDAO getCityDAO() {
		return new CityDAOimpl();
	}

	@Override
	public CountryDAO getCountryDAO() {
		return new CountryDAOimpl();
	}

	@Override
	public HotelDAO getHotelDAO() {
		return new HotelDAOimpl();
	}

	@Override
	public TicketDAO getTicketDAO() {
		return new TicketDAOimpl();
	}

	@Override
	public TourDAO getTourDAO() {
		return new TourDAOimpl();
	}

	@Override
	public TourTypeDAO getTourTypeDAO() {
		return new TourTypeDAOimpl();
	}

	@Override
	public UserDAO getUserDAO() {
		return new UserDAOimpl();
	}



}
