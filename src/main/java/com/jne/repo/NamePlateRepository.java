package com.jne.repo;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jne.model.NamePlate;
import com.jne.model_Id.NamePlateId;

@Repository
public interface NamePlateRepository
        extends JpaRepository<NamePlate, NamePlateId> {
	
	@Transactional
	@Modifying
	@Query(value = """
	INSERT INTO name_plate
	(
	 device_serial_number,
	 mdas_datetime,
	 meter_serial_number,
	 device_id,
	 meter_type,
	 owner_name,
	 status,
	 manufacturer_name,
	 manufacturer_year,
	 firmware_version,
	 category,
	 current_ratings
	)
	VALUES
	(
	 :deviceSerialNumber,
	 :mdasDatetime,
	 :meterSerialNumber,
	 :deviceId,
	 :meterType,
	 :ownerName,
	 :status,
	 :manufacturerName,
	 :manufacturerYear,
	 :firmwareVersion,
	 :category,
	 :currentRatings
	)
	ON CONFLICT
	(
	 device_serial_number,
	 mdas_datetime
	)
	DO NOTHING
	""", nativeQuery = true)
	int insertIgnore(
	        @Param("deviceSerialNumber") String deviceSerialNumber,
	        @Param("mdasDatetime") LocalDateTime mdasDatetime,
	        @Param("meterSerialNumber") String meterSerialNumber,
	        @Param("deviceId") String deviceId,
	        @Param("meterType") String meterType,
	        @Param("ownerName") String ownerName,
	        @Param("status") String status,
	        @Param("manufacturerName") String manufacturerName,
	        @Param("manufacturerYear") String manufacturerYear,
	        @Param("firmwareVersion") String firmwareVersion,
	        @Param("category") String category,
	        @Param("currentRatings") String currentRatings
	);
	
}