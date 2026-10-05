package com.jne.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jne.model.BillingDataSinglePhase;
import com.jne.model.BillingDataSinglePhaseId;

@Repository
public interface BillingDataSinglePhaseRepo extends JpaRepository<BillingDataSinglePhase, BillingDataSinglePhaseId>{
	
//	    List<BillingDataSinglePhase> findByOwnerNameAndBillingDatetimeBetween(String ownerName, Date start, Date end);
//
//	    // Filter by device serial number and meter_datetime
//	    List<BillingDataSinglePhase> findByDeviceSerialNumberAndBillingDatetimeBetween(String deviceSerialNumber, Date start, Date end);
	    
	    
//	    List<BillingDataSinglePhase> findByOwnerNameAndIdBillingDatetimeBetween(
//	            String ownerName, Date start, Date end);
//
//
//
//	        List<BillingDataSinglePhase> findByIdDeviceSerialNumberAndIdBillingDatetimeBetween(
//	            String deviceSerialNumber, Date from, Date to);


}
