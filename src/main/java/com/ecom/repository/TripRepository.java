package com.ecom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecom.model.Trip;
import java.time.LocalDate;



public interface TripRepository extends JpaRepository<Trip, Integer> {
	List<Trip> findByStartPointContainingIgnoreCaseOrEndPointContainingIgnoreCase(String ch, String ch2);
	
	public Boolean existsById(int id);
	
	List<Trip> findByStartPointAndEndPointAndDepartureDate(String startPoint, String endPoint, LocalDate departureDate);
	
	List<String> findDistinctStartPointsBy();

	List<String> findDistinctEndPointsBy();
} 