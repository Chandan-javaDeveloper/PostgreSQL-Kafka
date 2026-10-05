package com.jne.repo;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jne.model.InstantaneousDataThreePhase;
import com.jne.model.InstantaneousDataThreePhaseId;



@Repository
public interface InstantaneousThreePhaseRepo extends JpaRepository<InstantaneousDataThreePhase, InstantaneousDataThreePhaseId>{
	


}
