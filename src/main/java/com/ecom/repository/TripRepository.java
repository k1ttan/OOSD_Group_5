package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.ecom.model.Trip;


public interface TripRepository extends JpaRepository<Trip, Integer> {
	List<Trip> findByStartPointContainingIgnoreCaseOrEndPointContainingIgnoreCase(String ch, String ch2);
	
	public Boolean existsById(int id);
}
