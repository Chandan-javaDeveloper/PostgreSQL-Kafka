package com.jne.repo;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jne.model.LastBillingDataThreePhase;
import com.jne.model.LastBillingDataThreePhaseId;

@Repository
public interface LastBillingDataThreePhaseRepo extends JpaRepository<LastBillingDataThreePhase, LastBillingDataThreePhaseId>{
	
//	List<LastBillingDataThreePhase> findByOwnerNameAndBillingDatetimeBetween(String ownerName, Date start, Date end);

//    // Filter by device serial number and meter_datetime
//    List<LastBillingDataThreePhase> findByDeviceSerialNumberAndBillingDatetimeBetween(String deviceSerialNumber, Date start, Date end);

//    List<LastBillingDataThreePhase> findByOwnerNameAndIdBillingDatetimeBetween(
//            String ownerName, Date start, Date end);



//        List<LastBillingDataThreePhase> findByIdDeviceSerialNumberAndIdBillingDatetimeBetween(
//            String deviceSerialNumber, Date from, Date to);

}
