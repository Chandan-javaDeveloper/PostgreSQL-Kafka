package com.jne.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jne.model.InstantaneousPushSinglePhase;
import com.jne.model_Id.InstantaneousPushSinglePhaseId;


@Repository
public interface InstantPushDataSinglePhaseRepository
        extends JpaRepository<InstantaneousPushSinglePhase, InstantaneousPushSinglePhaseId> {
}