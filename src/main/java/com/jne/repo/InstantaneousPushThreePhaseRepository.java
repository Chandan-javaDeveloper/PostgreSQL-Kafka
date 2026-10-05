package com.jne.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jne.model.InstantaneousPushThreePhase;
import com.jne.model_Id.InstantaneousPushThreePhaseId;

@Repository
public interface InstantaneousPushThreePhaseRepository
        extends JpaRepository<InstantaneousPushThreePhase, InstantaneousPushThreePhaseId> {
}