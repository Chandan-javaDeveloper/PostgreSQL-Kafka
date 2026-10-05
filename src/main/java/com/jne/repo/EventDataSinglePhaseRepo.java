package com.jne.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jne.model.EventDataSinglePhase;
import com.jne.model.EventDataSinglePhaseId;

@Repository
public interface EventDataSinglePhaseRepo extends JpaRepository<EventDataSinglePhase, EventDataSinglePhaseId>{
	


}
