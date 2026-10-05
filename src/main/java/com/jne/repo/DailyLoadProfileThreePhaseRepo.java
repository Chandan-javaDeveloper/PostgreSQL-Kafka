package com.jne.repo;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jne.model.DailyLoadProfileThreePhase;
import com.jne.model.DailyLoadProfileThreePhaseId;

@Repository
public interface DailyLoadProfileThreePhaseRepo extends JpaRepository<DailyLoadProfileThreePhase, DailyLoadProfileThreePhaseId> {


        List<DailyLoadProfileThreePhase> findByIdDeviceSerialNumberAndIdDatetimeBetween(
            String deviceSerialNumber, Date from, Date to);
}


