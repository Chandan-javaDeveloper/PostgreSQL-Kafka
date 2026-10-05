package com.jne.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jne.model.EventDataThreePhase;
import com.jne.model.EventDataThreePhaseId;

@Repository
public interface EventDataThreePhaseRepo extends JpaRepository<EventDataThreePhase, EventDataThreePhaseId>{
	
//	 List<EventDataThreePhase> findByDeviceSerialNumberAndEventDatetimeBetween(String deviceSerialNumber, Date start, Date end);
//	    
//	    List<EventDataThreePhase> findByOwnerNameAndEventDatetimeBetween(String ownerName, Date start, Date end);
//
//	    List<EventDataThreePhase> findByOwnerNameAndIdEventDatetimeBetween(
//	            String ownerName, Date start, Date end);
//
//
//
//	        List<EventDataThreePhase> findByIdDeviceSerialNumberAndIdEventDatetimeBetween(
//	            String deviceSerialNumber, Date from, Date to);


}
