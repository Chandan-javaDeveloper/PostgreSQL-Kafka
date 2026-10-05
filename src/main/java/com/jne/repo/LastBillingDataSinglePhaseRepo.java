package com.jne.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jne.model.LastBillingDataSinglePhase;
import com.jne.model.LastBillingDataSinglePhaseId;

@Repository
public interface LastBillingDataSinglePhaseRepo extends JpaRepository<LastBillingDataSinglePhase, LastBillingDataSinglePhaseId>
{
	
//	List<LastBillingDataSinglePhase> findByOwnerNameAndBillingDatetimeBetween(String ownerName, Date start, Date end);
//
//    // Filter by device serial number and meter_datetime
//    List<LastBillingDataSinglePhase> findByDeviceSerialNumberAndBillingDatetimeBetween(String deviceSerialNumber, Date start, Date end);

//    List<LastBillingDataSinglePhase> findByOwnerNameAndIdBillingDatetimeBetween(
//            String ownerName, Date start, Date end);



//        List<LastBillingDataSinglePhase> findByIdDeviceSerialNumberAndIdBillingDatetimeBetween(
//            String deviceSerialNumber, Date from, Date to);


}
