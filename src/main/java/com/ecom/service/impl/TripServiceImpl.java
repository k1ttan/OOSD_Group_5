package com.ecom.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Trip;
import com.ecom.repository.TripRepository;
import com.ecom.service.TripService;

@Service
public class TripServiceImpl implements TripService {
	@Autowired
	private TripRepository tripRepository;
	@Override
	public Trip saveTrip(Trip Trip) {
		return tripRepository.save(Trip);
	}

	@Override
	public List<Trip> getAllTrips() {
		return tripRepository.findAll();
	}
	
	@Override
	public Page<Trip> getAllTripPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return tripRepository.findAll(pageable);
	}
	@Override
	public Boolean deleteTrip(Integer id) {
		Trip Trip = tripRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(Trip)) {
			tripRepository.delete(Trip);
			return true;
		}
		return false;
	}
	@Override
	public Trip getTripById(Integer id) {
		Trip Trip = tripRepository.findById(id).orElse(null);
		
		return Trip;
	}
	
	@Override
	public Trip updateTrip(Trip Trip) {

		Trip dbTrip = getTripById(Trip.getId());


		dbTrip.setStartPoint(Trip.getStartPoint());
		dbTrip.setEndPoint(Trip.getEndPoint());
		dbTrip.setDepartureDate(Trip.getDepartureDate());

		Trip updateTrip = tripRepository.save(dbTrip);
		if (!ObjectUtils.isEmpty(updateTrip)) {
			return Trip;
		}
		return null;
	}
	@Override
	public List<Trip> searchTrip(String ch) {
		return tripRepository.findByStartPointContainingIgnoreCaseOrEndPointContainingIgnoreCase(ch, ch);
	}
	
	@Override
	public Boolean existsTrip(int id) {
		return tripRepository.existsById(id);
	}

	public List<Trip> findTrips(String startPoint, String endPoint) {
        return tripRepository.findByStartPointAndEndPoint(startPoint, endPoint);
    }
	
	public List<String> getAllStartPoint(){
		return tripRepository.findAllDistinctStartPoints();
	}

	public List<String> getAllEndPoint(){
		return tripRepository.findAllDistinctEndPoints();
	}

	
	
}
