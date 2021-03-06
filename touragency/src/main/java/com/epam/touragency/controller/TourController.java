package com.epam.touragency.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/tour",loadOnStartup=1)
public class TourController extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/resources/tour.jsp");
		requestDispatcher.forward(request, response);
	}
		
}
