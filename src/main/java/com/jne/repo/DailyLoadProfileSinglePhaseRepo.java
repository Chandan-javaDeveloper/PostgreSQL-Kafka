package com.jne.repo;



import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jne.model.DailyLoadProfileSinglePhase;
import com.jne.model.DailyLoadProfileSinglePhaseId;

@Repository
public interface DailyLoadProfileSinglePhaseRepo extends JpaRepository<DailyLoadProfileSinglePhase, DailyLoadProfileSinglePhaseId>
{    
	  
	        List<DailyLoadProfileSinglePhase> findByIdDeviceSerialNumberAndIdDatetimeBetween(
	            String deviceSerialNumber, Date from, Date to);
	
}
