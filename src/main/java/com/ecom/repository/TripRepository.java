package com.ecom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecom.model.Trip;
import java.time.LocalDate;



public interface TripRepository extends JpaRepository<Trip, Integer> {
	List<Trip> findByStartPointContainingIgnoreCaseOrEndPointContainingIgnoreCase(String ch, String ch2);
	
	public Boolean existsById(int id);
	
	List<Trip> findByStartPointAndEndPoint(String startPoint, String endPoint);
	@Query("SELECT DISTINCT t.startPoint FROM Trip t WHERE t.startPoint LIKE %:keyword%")
	List<String> findDistinctStartPointsBy(@Param("keyword") String keyword);
	
	@Query("SELECT DISTINCT t.startPoint FROM Trip t WHERE t.startPoint LIKE %:keyword%")
	List<String> findDistinctEndPointsBy(@Param("keyword") String keyword);
	
	@Query("SELECT DISTINCT t.startPoint FROM Trip t")
    List<String> findAllDistinctStartPoints();
	
	@Query("SELECT DISTINCT t.endPoint FROM Trip t")
    List<String> findAllDistinctEndPoints();
	
	List<Trip> findByStartPointContainingIgnoreCase(String startPoint);
    List<Trip> findByEndPointContainingIgnoreCase(String endPoint);
    
    
} 