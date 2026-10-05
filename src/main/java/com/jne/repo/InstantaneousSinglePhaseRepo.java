package com.jne.repo;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jne.model.InstantaneousDataSinglePhase;
import com.jne.model.InstantaneousDataSinglePhaseId;


@Repository
public interface InstantaneousSinglePhaseRepo extends JpaRepository<InstantaneousDataSinglePhase, InstantaneousDataSinglePhaseId>{
	

}
