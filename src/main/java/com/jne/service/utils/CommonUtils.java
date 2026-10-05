package com.jne.service.utils;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {
	
	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);
	private static ObjectMapper mapper;
	
	public static ObjectMapper getMapper() {
		return mapper;
	}
	
    static 
	{
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY); 
	}

	public static void setMapper(ObjectMapper mapper) {
		CommonUtils.mapper = mapper;
	}
 
	public static String getHierDBFieldName(String reqLevelName) {
		String levelName = "owner_name"; 
		if(Constants.HierLevelName.ALL.equals(reqLevelName)) {
			levelName = "owner_name";
		}
		else if(Constants.HierLevelName.METER.equals(reqLevelName)) {
			levelName = "device_serial_number";
		}
		else if(Constants.HierLevelName.SUBSTATION.equals(reqLevelName)) {
			levelName = "substation_name";
		}
		else if(Constants.HierLevelName.SUBDEVISION.equals(reqLevelName)) {
			levelName = "subdevision_name";
		}
		else if(Constants.HierLevelName.FEEDER.equals(reqLevelName)) {
			levelName = "feeder_name";
		}
		else if(Constants.HierLevelName.DTMETER.equals(reqLevelName)) {
			levelName = "dt_name";
		}
		return levelName;
	}
	
	
	// sql injection 
	
	
	 public static final Set<String> ALLOWED_DEV_TYPES = Set.of(
		        "All", "Single Phase", "Three Phase", "CT Meter", "HT Meter", "METER" , "ALL"
		    );
	 
	// public static final String LEVEL_VALUE_REGEX = "^[a-zA-Z0-9_-]+$";
	 public static final String LEVEL_VALUE_REGEX = "^[a-zA-Z0-9 _()\\-]+$";


	 public static boolean isValidLevelValue(String levelValue) {
	     if (levelValue == null) return false;
	     return levelValue.matches(LEVEL_VALUE_REGEX);
	 }
	 
	 public static boolean isValidHierarchy(String divisionName) {
	     if (divisionName == null) return false;
	     return divisionName.matches(LEVEL_VALUE_REGEX);
	 }
	 
	 public static boolean isValidLevelName(String levelName) {
		    if (levelName == null) return false;
		    return ALLOWED_DEV_TYPES.contains(levelName.trim().toUpperCase());
		}
	 
	// Allowed values for commandType
	 public static final Set<String> ALLOWED_COMMAND_TYPES = Set.of(
	         "LastComm", "INSTAN", "DAILY_LP", "DELTA_LP" , "EVENTS" , "BILLING"
	 );

	 // Allowed values for status
	 public static final Set<String> ALLOWED_STATUS = Set.of(
	         "Success", "Failure", "Pending"
	 );

	 // Allowed values for meterType
	 public static final Set<String> ALLOWED_METER_TYPES = Set.of(
	         "All", "Single Phase", "Three Phase", "CT Meter", "HT Meter"
	 );
	 
	// Allowed values for devType
	 public static final Set<String> ALLOWED_DEV_TYPE = Set.of(
	      "1P", "3P", "CT", "HT"
	 );
	 

	 // Regex for date (yyyy-MM-dd)
	 public static final String DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";
	 
	 public static final String DATETIME_REGEX =
			    "^\\d{4}-\\d{2}-\\d{2}([ T]\\d{2}:\\d{2}:\\d{2})?$";
	 
	 

	   public static String sanitizeSqlValue(String value) {
	        String trimmed = Optional.ofNullable(value).orElse("").trim();
	        if (!isValidSqlValue(trimmed)) {
	            throw new IllegalArgumentException("Invalid input value: " + value);
	        }
	        return trimmed;
	    }
	   public static boolean isValidSqlValue(String value) {
		    if (value == null || value.trim().isEmpty()) return false;
		    return value.matches(LEVEL_VALUE_REGEX);
		}


		    public static String getHierDBDevTypeFieldName(String reqDevType) {
		        if (reqDevType == null) return "All";

		        switch (reqDevType.trim()) {
		            case "All":
		                return "All";
		            case "Single Phase":
		                return "Single Phase";
		            case "Three Phase":
		                return "Three Phase";
		            case "CT Meter":
		                return "CT Meter";
		            case "HT Meter":
		                return "HT Meter";
		            default:
		                // Log suspicious input
		            	LOG.error("❌ Suspicious or invalid devType: {}", reqDevType);
		                return "All"; // or return null and handle as rejection
		        }
		    }
		    
		    public static String extractTokenOrNull(String authHeader) {
		        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
		            return null;
		        }
		        return authHeader.substring(7); // remove "Bearer "
		    }

		    public static String generateTrackingIdUI() {
		        int randomNum = 10000000 + new java.util.Random().nextInt(90000000); // ensures 8 digits
		        return "ODR-" + randomNum;
		    }
}
