package com.ecom.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Trip;

public interface TripService {
	public Trip saveTrip(Trip Trip);
	
	public List<Trip> getAllTrips();

	public Boolean deleteTrip(Integer id);

	public Trip getTripById(Integer id);

	public Trip updateTrip(Trip Trip);

	public List<Trip> searchTrip(String ch);
	
	public Page<Trip> getAllTripPagination(Integer pageNo, Integer pageSize);
	
	public Boolean existsTrip(int id);
	
	public List<Trip> findTrips(String startPoint, String endPoint);
	
	public List<String> getAllStartPoint();

	public List<String> getAllEndPoint();

}
