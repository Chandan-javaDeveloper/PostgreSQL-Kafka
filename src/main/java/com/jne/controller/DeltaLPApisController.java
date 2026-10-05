package com.jne.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jne.request.MeterRequestTime;
import com.jne.response.DeltaLPRes;
import com.jne.response.UIResponse;
import com.jne.service.utils.CommonUtils;
import com.jne.ui_service.DeltaUiService;

@RestController
@CrossOrigin(origins = "", allowedHeaders = "")
@RequestMapping(value = "/api/v1/delta")
public class DeltaLPApisController {
	 private static final Logger LOG = LoggerFactory.getLogger(DeltaLPApisController.class);
	 @Autowired
	    private DeltaUiService deltaUiService;
	    
	    
	    @PostMapping(value = "/getUiLoadProfileData",
	            consumes = "application/json",
	            produces = "application/json")
	    public ResponseEntity<?> getLoadProfileDataByOwnerName1(
	            @Validated @RequestBody MeterRequestTime req
	           ) {

	        // ================= Validation =================

	        if (req.getLevelName() == null 
	                || req.getStartDate() == null || req.getEndDate() == null) {

	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(new UIResponse(false, Collections.emptyList(), null,
	                            "Invalid inputs"));
	        }

//	        if (!CommonUtils.ALLOWED_DEV_TYPES.contains(req.getLevelName().trim())) {
//	            return invalidResponse(authHeader, "Invalid levelName");
//	        }
//
//	        if (req.getStartDate().isAfter(req.getEndDate())) {
//	            return invalidResponse(authHeader, "Start date cannot be after End date");
//	        }
//
//	        if (!CommonUtils.ALLOWED_DEV_TYPE.contains(req.getDevType().trim())) {
//	            return invalidResponse(authHeader, "Invalid devTypes");
//	        }

	       // String token = CommonUtils.extractTokenOrNull(authHeader);

	        LOG.info("Delta LP request : {}", req.getLevelValue());

	        // ================= Cassandra Service Call =================

	        List<DeltaLPRes> deltaList;

	        try {
	            deltaList = deltaUiService.getDeltaLoadProfile(req);
	        } catch (Exception e) {

	            LOG.error("Delta LP error", e);

	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(Map.of(
	                            "result", false,
	                            "data", Collections.emptyList(),
	                            "message", "Internal Server Error"
	                    ));
	        }

	        boolean hasData = deltaList != null && !deltaList.isEmpty();

	        // ================= Headers =================

	        List<String> headers = new ArrayList<>(List.of(
	                "Meter S.No.", "MDAS Date Time", "Interval Date Time"
	        ));

	        if ("3P".equalsIgnoreCase(req.getDevType())) {
	            headers.addAll(List.of(
	                    "R Ph Current", "Y Ph Current", "B Ph Current",
	                    "R Ph Voltage", "Y Ph Voltage", "B Ph Voltage"
	            ));
	        } else {
	            headers.addAll(List.of("Avg. Current", "Avg. Voltage"));
	        }

	        headers.addAll(List.of(
	                "Block Energy Export(Kvah)", "Block Energy Import(Kvah)",
	                "Block Energy Export(Kwh)", "Block Energy Import(Kwh)"
	        ));

	        if ("3P".equalsIgnoreCase(req.getDevType())) {
	            headers.addAll(List.of(
	                    "Block Kvarh Lag", "Block Kvarh Lead",
	                    "Average Signal Strength",
	                    "Block Kvarh Export Lag", "Block Kvarh Export Lead"
	            ));
	        }

	        // ================= Data =================

	        Map<String, Object> dataMap = new LinkedHashMap<>();
	        dataMap.put("1", headers);

	        if (hasData) {

	            int index = 2;

	            for (DeltaLPRes d : deltaList) {

	                List<Object> row = new ArrayList<>();

	                row.add(d.getDeviceSno());
	                row.add(d.getMdastDatetime());
	                row.add(d.getIntervalDatetime());

	                if ("3P".equalsIgnoreCase(req.getDevType())) {

	                    row.add(d.getrPhCurrent());
	                    row.add(d.getyPhCurrent());
	                    row.add(d.getbPhCurrent());

	                    row.add(d.getrPhVoltage());
	                    row.add(d.getyPhVoltage());
	                    row.add(d.getbPhVoltage());

	                } else {

	                    row.add(d.getAvgCurrent());
	                    row.add(d.getAvgVoltage());
	                }

	                row.add(d.getBlockEnergyExportKvah());
	                row.add(d.getBlockEnergyImportKvah());
	                row.add(d.getBlockEnergyExportKwh());
	                row.add(d.getBlockEnergyImportKwh());

	                if ("3P".equalsIgnoreCase(req.getDevType())) {
	                    row.add(0.0);
	                    row.add(0.0);
	                    row.add(d.getAvgSignalStrength());
	                    row.add(0.0);
	                    row.add(0.0);
	                }

	                dataMap.put(String.valueOf(index++), row);
	            }
	        }

	        // ================= Final Response =================

	        Map<String, Object> response = new LinkedHashMap<>();
	        response.put("result", hasData);
	        response.put("data", hasData ? List.of(dataMap) : Collections.emptyList());
	   //     response.put("apiKey", token);
	        response.put("message", hasData ? "Success" : "Empty Data");

	        return ResponseEntity.ok(response);
	    }

	    private ResponseEntity<UIResponse> invalidResponse(String authHeader, String message) {
	  	    String token = CommonUtils.extractTokenOrNull(authHeader);

	  	    UIResponse uiResponse = new UIResponse();
	  	    uiResponse.setResult(false);
	  	    uiResponse.setData(new ArrayList<>());   // ✅ always empty list instead of null
	  	    uiResponse.setApiKey(token);
	  	    uiResponse.setMessage("✅ " + message);

	  	    return ResponseEntity.ok(uiResponse);
	  	}
}
