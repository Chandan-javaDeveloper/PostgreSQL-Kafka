package com.jne.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jne.model.BillingDataThreePhase;
import com.jne.model.BillingDataThreePhaseId;

@Repository
public interface BillingDataThreePhaseRepo extends JpaRepository<BillingDataThreePhase, BillingDataThreePhaseId>
{
	
//	 List<BillingDataThreePhase> findByOwnerNameAndBillingDatetimeBetween(String ownerName, Date start, Date end);
//
//	    // Filter by device serial number and meter_datetime
//	    List<BillingDataThreePhase> findByDeviceSerialNumberAndBillingDatetimeBetween(String deviceSerialNumber, Date start, Date end);

	    
//	    List<BillingDataThreePhase> findByOwnerNameAndIdBillingDatetimeBetween(
//	            String ownerName, Date start, Date end);
//
//
//
//	        List<BillingDataThreePhase> findByIdDeviceSerialNumberAndIdBillingDatetimeBetween(
//	            String deviceSerialNumber, Date from, Date to);


}
