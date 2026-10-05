package com.jne.repo;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jne.model.InstantaneousData;
import com.jne.model_Id.InstantaneousDataId;

@Repository
public interface InstantaneousDataRepository extends JpaRepository<InstantaneousData, InstantaneousDataId> {

}