package com.jne.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ObisFieldMapping {

    // ✅ Single Phase Mapping
    private static final Map<String, String> DAILY_LP_FIELD_MAPPING_SINGLE_PHASE = new HashMap<>();

    // ✅ Three Phase Mapping
    private static final Map<String, String> dailyLP3PHMapping = new HashMap<>();
    
    private static final Map<String, String> DELTA_FIELD_MAPPING_SINGLE_PHASE = new HashMap<>();
    
    private static final Map<String, String> DELTA_FIELD_MAPPING_THREE_PHASE = new HashMap<>();
    
    private static final Map<String, String> BILLING_FIELD_DATA_SINGLE_PHASE = new HashMap<>();
    
    private static final Map<String, String> BILLING_FIELD_DATA_Three_PHASE = new HashMap<>();

    private static final Map<String, Map<Integer, String>> OBIS_INDEX_MAPPING = new HashMap<>();
    
    private static final Map<String, Map<Integer, String>> OBIS_INDEX_MAPPING3P = new HashMap<>();

    
    private static final Map<String, String> INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE = new HashMap<>();
    
    private static final Map<String, Map<Integer, String>> OBIS_INDEX_MAPPING_SINGLE_PHASE = new HashMap<>();

    
    private static final Map<String, String> INSTANTREAD_FIELD_MAPPING_THREE_PHASE = new HashMap<>();


    private static final Map<String, Map<Integer, String>> OBIS_INDEX_MAPPING_THREE_PHASE = new HashMap<>();

    private static final Map<String, String> EVENT_FIELD_MAPPING_SINGLE_PHASE = new HashMap<>();

    private static final Map<String, String> EVENT_FIELD_MAPPING_THREE_PHASE = new HashMap<>();
    

    private static final Map<String, String> EVENT_TYPE_MAPPING = new HashMap<>();

    private static final Map<String, String> NAMEPLATE_MAPPING = new HashMap<>();



    static {
        // 🔶 Single Phase OBIS Mapping
        DAILY_LP_FIELD_MAPPING_SINGLE_PHASE.put("1.0.2.8.0.255", "cumulative_energy_kwh_export");
        DAILY_LP_FIELD_MAPPING_SINGLE_PHASE.put("1.0.10.8.0.255", "cumulative_energy_kvah_export");
        DAILY_LP_FIELD_MAPPING_SINGLE_PHASE.put("1.0.1.8.0.255", "cumulative_energy_kwh_import");
        DAILY_LP_FIELD_MAPPING_SINGLE_PHASE.put("1.0.9.8.0.255", "cumulative_energy_kvah_import");
      //  DAILY_LP_FIELD_MAPPING_SINGLE_PHASE.put("0.0.1.0.0.255", "datetime");
        
        
       // dailyLP3PHMapping.put("0.0.1.0.0.255", "datetime");
        dailyLP3PHMapping.put("1.0.1.8.0.255", "cumulative_energy_kwh_import");
        dailyLP3PHMapping.put("1.0.9.8.0.255", "cumulative_energy_kvah_import");
        dailyLP3PHMapping.put("1.0.2.8.0.255", "cumulative_energy_kwh_export");
        dailyLP3PHMapping.put("1.0.10.8.0.255", "cumulative_energy_kvah_export");

        
        

        // 🔶 Three Phase Delta OBIS Mapping
        DELTA_FIELD_MAPPING_SINGLE_PHASE.put("0.0.1.0.0.255", "intervalDatetime");
        DELTA_FIELD_MAPPING_SINGLE_PHASE.put("1.0.12.27.0.255", "averageVoltage");
        DELTA_FIELD_MAPPING_SINGLE_PHASE.put("1.0.1.29.0.255", "blockEnergyKwhImport");
        DELTA_FIELD_MAPPING_SINGLE_PHASE.put("1.0.9.29.0.255", "blockEnergyKvahImport");
        DELTA_FIELD_MAPPING_SINGLE_PHASE.put("1.0.2.29.0.255", "blockEnergyKwhExport");
        DELTA_FIELD_MAPPING_SINGLE_PHASE.put("1.0.10.29.0.255", "blockEnergyKvahExport");
        DELTA_FIELD_MAPPING_SINGLE_PHASE.put("1.0.11.27.0.255", "averageCurrent");                 // Add this field to your entity

       
        
        
        DELTA_FIELD_MAPPING_THREE_PHASE.put("0.0.1.0.0.255", "intervalDatetime");

        DELTA_FIELD_MAPPING_THREE_PHASE.put("1.0.31.27.0.255", "phaseCurrentL1");
        DELTA_FIELD_MAPPING_THREE_PHASE.put("1.0.51.27.0.255", "phaseCurrentL2");
        DELTA_FIELD_MAPPING_THREE_PHASE.put("1.0.71.27.0.255", "phaseCurrentL3");

        DELTA_FIELD_MAPPING_THREE_PHASE.put("1.0.32.27.0.255", "phaseVoltageL1");
        DELTA_FIELD_MAPPING_THREE_PHASE.put("1.0.52.27.0.255", "phaseVoltageL2");
        DELTA_FIELD_MAPPING_THREE_PHASE.put("1.0.72.27.0.255", "phaseVoltageL3");

        DELTA_FIELD_MAPPING_THREE_PHASE.put("1.0.1.29.0.255", "blockEnergyKwhImport");
        DELTA_FIELD_MAPPING_THREE_PHASE.put("1.0.2.29.0.255", "blockEnergyKwhExport");
        DELTA_FIELD_MAPPING_THREE_PHASE.put("1.0.9.29.0.255", "blockEnergyKvahImport");
        DELTA_FIELD_MAPPING_THREE_PHASE.put("1.0.10.29.0.255", "blockEnergyKvahExport");
        
       
        
        
     //   BILLING_FIELD_DATA_SINGLE_PHASE.put("0.0.0.1.2.255", "billingDatetime");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.13.0.0.255", "average_power_factor_for_billing_period");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.1.8.0.255", "cumulative_energy_kwh_import");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.1.8.1.255", "cumulative_energy_kwh_tier1");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.1.8.2.255", "cumulative_energy_kwh_tier2");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.1.8.3.255", "cumulative_energy_kwh_tier3");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.1.8.4.255", "cumulative_energy_kwh_tier4");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.1.8.5.255", "cumulative_energy_kwh_tier5");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.1.8.6.255", "cumulative_energy_kwh_tier6");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.1.8.7.255", "cumulative_energy_kwh_tier7");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.1.8.8.255", "cumulative_energy_kwh_tier8");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.9.8.0.255", "cumulative_energy_kvah_import");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.9.8.1.255", "cumulative_energy_kvah_tier1");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.9.8.2.255", "cumulative_energy_kvah_tier2");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.9.8.3.255", "cumulative_energy_kvah_tier3");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.9.8.4.255", "cumulative_energy_kvah_tier4");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.9.8.5.255", "cumulative_energy_kvah_tier5");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.9.8.6.255", "cumulative_energy_kvah_tier6");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.9.8.7.255", "cumulative_energy_kvah_tier7");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.9.8.8.255", "cumulative_energy_kvah_tier8");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("0.0.94.91.13.255", "billing_power_on_duration_in_billing");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.2.8.0.255", "cumulative_energy_kwh_export");
        BILLING_FIELD_DATA_SINGLE_PHASE.put("1.0.10.8.0.255", "cumulative_energy_kvah_export");

        // OBIS + Index Mapping for duplicate OBIS codes
        Map<Integer, String> obis_1_0_1_6_0_255 = new HashMap<>();
        obis_1_0_1_6_0_255.put(0, "maximum_demand_kw");
        obis_1_0_1_6_0_255.put(1, "maximum_demand_kw_datetime");
        OBIS_INDEX_MAPPING.put("1.0.1.6.0.255", obis_1_0_1_6_0_255);

        Map<Integer, String> obis_1_0_9_6_0_255 = new HashMap<>();
        obis_1_0_9_6_0_255.put(0, "maximum_demand_kva");
        obis_1_0_9_6_0_255.put(1, "maximum_demand_kva_datetime");
        OBIS_INDEX_MAPPING.put("1.0.9.6.0.255", obis_1_0_9_6_0_255);

        	
        
        
        

       // BILLING_FIELD_DATA_Three_PHASE.put("0.0.0.1.2.255", "billingDatetime");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.13.0.0.255", "system_power_factor_billing_period");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.1.8.0.255", "cumulative_energy_kwh_import");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.1.8.1.255", "cumulative_energy_kwh_tier1");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.1.8.2.255", "cumulative_energy_kwh_tier2");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.1.8.3.255", "cumulative_energy_kwh_tier3");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.1.8.4.255", "cumulative_energy_kwh_tier4");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.1.8.5.255", "cumulative_energy_kwh_tier5");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.1.8.6.255", "cumulative_energy_kwh_tier6");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.1.8.7.255", "cumulative_energy_kwh_tier7");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.1.8.8.255", "cumulative_energy_kwh_tier8");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.9.8.0.255", "cumulative_energy_kvah_import");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.9.8.1.255", "cumulative_energy_kvah_tier1");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.9.8.2.255", "cumulative_energy_kvah_tier2");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.9.8.3.255", "cumulative_energy_kvah_tier3");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.9.8.4.255", "cumulative_energy_kvah_tier4");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.9.8.5.255", "cumulative_energy_kvah_tier5");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.9.8.6.255", "cumulative_energy_kvah_tier6");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.9.8.7.255", "cumulative_energy_kvah_tier7");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.9.8.8.255", "cumulative_energy_kvah_tier8");
        BILLING_FIELD_DATA_Three_PHASE.put("0.0.94.91.13.255", "power_on_duration_mins");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.2.8.0.255", "cumulative_energy_kwh_export");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.10.8.0.255", "cumulative_energy_kvah_export");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.5.8.0.255", "cumulative_energy_kvarh_q1");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.6.8.0.255", "cumulative_energy_kvarh_q2");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.7.8.0.255", "cumulative_energy_kvarh_q3");
        BILLING_FIELD_DATA_Three_PHASE.put("1.0.8.8.0.255", "cumulative_energy_kvarh_q4");

        // ✅ Index Based OBIS Mapping
        for (int tier = 0; tier <= 8; tier++) {
            String obisKw = String.format("1.0.1.6.%d.255", tier);
            Map<Integer, String> kwMap = new HashMap<>();
            if (tier == 0) {
                kwMap.put(0, "maximum_demand_kw");
                kwMap.put(1, "maximum_demand_kw_date");
            } else {
                kwMap.put(0, String.format("maximum_demand_kw_tier%d", tier));
                kwMap.put(1, String.format("maximum_demand_kw_tier%d_date", tier));
            }
            OBIS_INDEX_MAPPING3P.put(obisKw, kwMap);

            String obisKva = String.format("1.0.9.6.%d.255", tier);
            Map<Integer, String> kvaMap = new HashMap<>();
            if (tier == 0) {
                kvaMap.put(0, "maximum_demand_kva");
                kvaMap.put(1, "maximum_demand_kva_date");
            } else {
                kvaMap.put(0, String.format("maximum_demand_kva_tier%d", tier));
                kvaMap.put(1, String.format("maximum_demand_kva_tier%d_date", tier));
            }
            OBIS_INDEX_MAPPING3P.put(obisKva, kvaMap);
        }


        
        
        

        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.1.0.0.255", "datetime");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.12.7.0.255", "instant_voltage");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.11.7.0.255", "phase_current");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.91.7.0.255", "neutral_current");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.14.7.0.255", "frequency");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.9.7.0.255", "apparent_power_kva");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.1.7.0.255", "active_power_kw");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.1.8.0.255", "cumulative_energy_kwh_import");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.9.8.0.255", "cumulative_energy_kvah_import");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.94.91.14.255", "cumulative_power_on_duration");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.94.91.0.255", "cumulative_tamper_count");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.0.1.0.255", "cumulative_bill_count");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.96.2.0.255", "cumulative_program_count");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.2.8.0.255", "cumulative_energy_kwh_export");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.10.8.0.255", "cumulative_energy_kvah_export");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.17.0.0.255", "load_limit");
        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.13.7.0.255", "power_factor");
       // INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.96.3.10.255", "load_limit_status");


        Map<Integer, String> mdKwMap = new HashMap<>();
        mdKwMap.put(0, "maximum_demand_kw");
        mdKwMap.put(1, "maximum_demand_kw_datetime");
        OBIS_INDEX_MAPPING_SINGLE_PHASE.put("1.0.1.6.0.255", mdKwMap);

        Map<Integer, String> mdKvaMap = new HashMap<>();
        mdKvaMap.put(0, "maximum_demand_kva");
        mdKvaMap.put(1, "maximum_demand_kva_datetime");
        OBIS_INDEX_MAPPING_SINGLE_PHASE.put("1.0.9.6.0.255", mdKvaMap);

//        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.94.91.14.255", "");
//        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.94.91.0.255", "");
//        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.96.2.0.255", "");
//        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.2.8.0.255", "");
//        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("1.0.10.8.0.255", "");
//        INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE.put("0.0.96.3.10.255", "");
        
        
        
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("0.0.1.0.0.255", "datetime");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.31.7.0.255", "phase_current_l1");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.51.7.0.255", "phase_current_l2");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.71.7.0.255", "phase_current_l3");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.32.7.0.255", "instant_voltage_l1");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.52.7.0.255", "instant_voltage_l2");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.72.7.0.255", "instant_voltage_l3");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.33.7.0.255", "power_factor_l1");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.53.7.0.255", "power_factor_l2");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.73.7.0.255", "power_factor_l3");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.13.7.0.255", "total_pf");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.14.7.0.255", "frequency");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.9.7.0.255", "apparent_power_kva");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.1.7.0.255", "active_power_kw");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.3.7.0.255", "reactive_power_kvar");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("0.0.96.7.0.255", "no_of_power_failure");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("0.0.94.91.8.255", "cumm_power_off_duration_in_mins");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("0.0.94.91.0.255", "cumulative_tamper_count");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("0.0.0.1.0.255", "cumulative_bill_count");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("0.0.96.2.0.255", "cumulative_program_count");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("0.0.0.1.2.255", "last_billing_datetime");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.1.8.0.255", "cumulative_energy_kwh_import");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.2.8.0.255", "cumulative_energy_kwh_export");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.9.8.0.255", "cumulative_energy_kvah_import");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.10.8.0.255", "cumulative_energy_kvah_export");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("0.0.96.3.10.255", "load_limit_status");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("0.0.17.0.0.255", "load_limit");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.5.8.0.255", "cumulative_energy_kvarh_q1");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.6.8.0.255", "cumulative_energy_kvarh_q2");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.7.8.0.255", "cumulative_energy_kvarh_q3");
        INSTANTREAD_FIELD_MAPPING_THREE_PHASE.put("1.0.8.8.0.255", "cumulative_energy_kvarh_q4");

        
        Map<Integer, String> mdKwMap3 = new HashMap<>();
        mdKwMap3.put(0, "maximum_demand_kw");
        mdKwMap3.put(1, "maximum_demand_kw_datetime");
        OBIS_INDEX_MAPPING_THREE_PHASE.put("1.0.1.6.0.255", mdKwMap3);

        Map<Integer, String> mdKvaMap3 = new HashMap<>();
        mdKvaMap3.put(0, "maximum_demand_kva");
        mdKvaMap3.put(1, "maximum_demand_kva_datetime");
        OBIS_INDEX_MAPPING_THREE_PHASE.put("1.0.9.6.0.255", mdKvaMap3);

        
    // ["1.0.181.8.0.255","1.0.91.7.0.255","1.0.31.7.124.255","1.0.51.7.124.255","1.0.71.7.124.255","1.0.32.7.124.255","1.0.52.7.124.255","1.0.72.7.124.255","0.0.96.9.128.255","0.1.96.12.6.255","0.1.96.12.7.255","0.1.96.12.8.255","0.1.96.12.5.255","1.0.129.8.0.255","1.0.96.7.30.255","1.0.81.7.1.255","1.0.81.7.12.255","1.0.96.5.3.255"]

  //      EVENT_FIELD_MAPPING_THREE_PHASE.put("0.0.1.0.0.255", "eventDatetime");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("0.0.96.11.0.255", "eventCode");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("0.0.96.11.1.255", "eventCode");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("0.0.96.11.2.255", "eventCode");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("0.0.96.11.3.255", "eventCode");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("0.0.96.11.4.255", "eventCode");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("0.0.96.11.6.255", "eventCode");

        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.31.7.0.255", "currentIr");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.51.7.0.255", "currentIy");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.71.7.0.255", "currentIb");

        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.32.7.0.255", "voltageVrn");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.52.7.0.255", "voltageVyn");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.72.7.0.255", "voltageVbn");

        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.33.7.0.255", "powerFactorRPhase");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.53.7.0.255", "powerFactorYPhase");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.73.7.0.255", "powerFactorBPhase");

        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.1.8.0.255", "cumulativeEnergyKwhImport");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("1.0.2.8.0.255", "cumulativeEnergyKwhExport");
        EVENT_FIELD_MAPPING_THREE_PHASE.put("0.0.94.91.0.255", "cumulativeTamperCount");

        // ❌ DO NOT map eventDatetime here (it’s in EmbeddedId)


     //   1.0.9.8.0.255 , 1.0.10.8.0.255 , 1.0.91.7.0.255 
        

        
//        commandMappings.put("CurrentRelatedEvents3ph" , Collections.unmodifiableMap(EVENT_FIELD_MAPPING_THREE_PHASE));
//        commandMappings.put("PowerRelatedEvents3ph" , Collections.unmodifiableMap(EVENT_FIELD_MAPPING_THREE_PHASE));
//        commandMappings.put("ControlRelatedEvents3ph" , Collections.unmodifiableMap(EVENT_FIELD_MAPPING_THREE_PHASE));
//        commandMappings.put("VoltageRelatedEvents3ph" , Collections.unmodifiableMap(EVENT_FIELD_MAPPING_THREE_PHASE));
//        commandMappings.put("OtherRelatedEvents3ph" , Collections.unmodifiableMap(EVENT_FIELD_MAPPING_THREE_PHASE));
//        commandMappings.put("TransactionRelatedEvents3ph" , Collections.unmodifiableMap(EVENT_FIELD_MAPPING_THREE_PHASE));
        
     // EVENT_FIELD_MAPPING_SINGLE_PHASE.put("0.0.1.0.0.255", "eventDatetime");
        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("0.0.96.11.0.255", "eventCode");
        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("0.0.96.11.1.255", "eventCode");
        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("0.0.96.11.2.255", "eventCode");
        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("0.0.96.11.3.255", "eventCode");
        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("0.0.96.11.4.255", "eventCode");
        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("0.0.96.11.6.255", "eventCode");

        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("1.0.12.7.0.255", "voltage");
        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("1.0.94.91.14.255", "current");
        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("1.0.1.8.0.255", "cumulativeEnergy");
        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("0.0.94.91.0.255", "tamperCount");
        EVENT_FIELD_MAPPING_SINGLE_PHASE.put("1.0.13.7.0.255", "powerFactor");

        
        
        //"1.0.94.91.14.255","1.0.12.7.0.255","1.0.13.7.0.255","0.0.94.91.0.255" o/r
        
        
        // NamePlate  SP
        NAMEPLATE_MAPPING.put("0.0.96.1.2.255", "deviceId");
        NAMEPLATE_MAPPING.put("0.0.96.1.1.255", "manufacturerName");
        NAMEPLATE_MAPPING.put("1.0.0.2.0.255", "firmwareVersion");
        NAMEPLATE_MAPPING.put("0.0.94.91.9.255", "meterType");
        NAMEPLATE_MAPPING.put("0.0.94.91.11.255", "category");
        NAMEPLATE_MAPPING.put("0.0.94.91.12.255", "currentRatings");
        NAMEPLATE_MAPPING.put("0.0.96.1.4.255", "manufacturerYear");
         
        
        
        // event type mapping
        
        EVENT_TYPE_MAPPING.put("1", "R-Phase PT Missing Occurrence");
        EVENT_TYPE_MAPPING.put("2", "R-Phase PT Missing Restoration");
        EVENT_TYPE_MAPPING.put("3", "Y-Phase PT Missing Occurrence");
        EVENT_TYPE_MAPPING.put("4", "Y-Phase PT Missing Restoration");
        EVENT_TYPE_MAPPING.put("5", "B-Phase PT Missing Occurrence");
        EVENT_TYPE_MAPPING.put("6", "B-Phase PT Missing Restoration");
        EVENT_TYPE_MAPPING.put("7", "Over Voltage Occurrence");
        EVENT_TYPE_MAPPING.put("8", "Over voltage Restoration");
        EVENT_TYPE_MAPPING.put("9", "Low Voltage Occurrence");
        EVENT_TYPE_MAPPING.put("10", "Low Voltage Restoration");
        EVENT_TYPE_MAPPING.put("11", "Voltage Unbalance Occurrence");
        EVENT_TYPE_MAPPING.put("12", "Voltage Unbalance Restoration");
        EVENT_TYPE_MAPPING.put("51", "R Phase CT reverse Occurrence");
        EVENT_TYPE_MAPPING.put("52", "R Phase CT reverse Restoration");
        EVENT_TYPE_MAPPING.put("53", "Y Phase CT reverse Occurrence");
        EVENT_TYPE_MAPPING.put("54", "Y Phase CT reverse Restoration");
        EVENT_TYPE_MAPPING.put("55", "B Phase CT reverse Occurrence");
        EVENT_TYPE_MAPPING.put("56", "B Phase CT reverse Restoration");
        EVENT_TYPE_MAPPING.put("57", "R Phase CT Open Occurence");
        EVENT_TYPE_MAPPING.put("58", "R Phase CT Open Restoration");
        EVENT_TYPE_MAPPING.put("59", "Y Phase CT Open Occurence");
        EVENT_TYPE_MAPPING.put("60", "Y Phase CT Open Restoration");
        EVENT_TYPE_MAPPING.put("61", "B Phase CT Open Occurence");
        EVENT_TYPE_MAPPING.put("62", "B Phase CT Open Restoration");
        EVENT_TYPE_MAPPING.put("63", "CT Unbalance Occurrence");
        EVENT_TYPE_MAPPING.put("64", "CT Unbalance Restoration");
        EVENT_TYPE_MAPPING.put("65", "CT Bypass Occurrence");//c
        EVENT_TYPE_MAPPING.put("66", "CT Bypass Restoration");
        EVENT_TYPE_MAPPING.put("67", "Over Current Occurrence");
        EVENT_TYPE_MAPPING.put("68", "Over Current Restoration");
        EVENT_TYPE_MAPPING.put("69", "Earth Loading-Occurrence");
        EVENT_TYPE_MAPPING.put("70", "Earth Loading -Restoration");
        EVENT_TYPE_MAPPING.put("101", "Power Failure Occurrence");
        EVENT_TYPE_MAPPING.put("102", "Power Failure Restoration");
        EVENT_TYPE_MAPPING.put("151", "RTC Change");
        EVENT_TYPE_MAPPING.put("152", "Demand Integration Period");
        EVENT_TYPE_MAPPING.put("153", "Profile Capture Period");
        EVENT_TYPE_MAPPING.put("154", "Billing Date Change");
        EVENT_TYPE_MAPPING.put("155", "TOD Zones Change");
        EVENT_TYPE_MAPPING.put("157", "New firmware activated");
        EVENT_TYPE_MAPPING.put("158", "Load limit(kW) set");
        EVENT_TYPE_MAPPING.put("159", "Connect");
        EVENT_TYPE_MAPPING.put("160", "Disconnect");
        EVENT_TYPE_MAPPING.put("161", "LLS (MR) Change");
        EVENT_TYPE_MAPPING.put("162", "HLS (US) Change");
        EVENT_TYPE_MAPPING.put("163", "HLS (FW) Change");
        EVENT_TYPE_MAPPING.put("164", "Global Key Change");
        EVENT_TYPE_MAPPING.put("165", "ESWF Change");
        EVENT_TYPE_MAPPING.put("166", "MD Reset");
        EVENT_TYPE_MAPPING.put("201", "Magnet Occurrence");
        EVENT_TYPE_MAPPING.put("202", "Magnet Restoration");
        EVENT_TYPE_MAPPING.put("203", "ND Occurrence");
        EVENT_TYPE_MAPPING.put("204", "ND Restoration");
        EVENT_TYPE_MAPPING.put("205", "Low PF Occurrence");
        EVENT_TYPE_MAPPING.put("206", "Low PF Restoration");
        EVENT_TYPE_MAPPING.put("207", "Neutral missing Occurrence");
        EVENT_TYPE_MAPPING.put("208", "Neutral missing Restoration");
        EVENT_TYPE_MAPPING.put("209", "Pluging comm. removal Occurrence");//c
        EVENT_TYPE_MAPPING.put("210", "Pluging comm. removal Restoration");
        EVENT_TYPE_MAPPING.put("211", "Change to Postpaid mode");
        EVENT_TYPE_MAPPING.put("212", "Change to Prepaid Mode");
        EVENT_TYPE_MAPPING.put("213", "Change to forward Mode");
        EVENT_TYPE_MAPPING.put("214", "Change to Imp-Exp Mode");
        EVENT_TYPE_MAPPING.put("215", "OverLoad Occurrence");
        EVENT_TYPE_MAPPING.put("216", "OverLoad Restoration");
        EVENT_TYPE_MAPPING.put("251", "Meter cover opening-Occurrence");//c
        EVENT_TYPE_MAPPING.put("301", "Load -Disconnected");
        EVENT_TYPE_MAPPING.put("302", "Load -Connected");

    }
    
 // SAT
    
    private static final Set<String> SAT_METERS = Set.of(
            "MZ2011281","MZ2011539","MZ2011289","MZ2011259","MZ2011592",
            "MZ2011597","MZ2011287","MZ2011288","MZ2011563","MZ2011260",
            "MZ2011035","MZ2011080","MZ2011572","MZ2011587","MZ2011429",
            "MZ2011282","MZ2011044","MZ2011040","MZ2011085","MZ2011586",
            "MZ4000001","MZ4000002","MZ4000003","MZ4000004","MZ4000005",
            "MZ4000006","MZ4000007","MZ4000008","MZ4000009","MZ4000010",
            "MZ4000011","MZ4000012","MZ4000013","MZ4000014","MZ4000015",
            "MZ4000016","MZ4000017","MZ4000018","MZ4000019","MZ3000003",
            "MZ0004724","MZ0004723","MZ0000022","MZ0000019","MZ9751793",
            "MZ9751794"
        );
    
    public static String resolveOwner(String meterNo) {
        if (meterNo != null && SAT_METERS.contains(meterNo.trim())) {
            return "SAT";
        }
        return "MPDCL"; // default
    }

    // ✅ Getters for mappings
    public static Map<String, String> getDailyLpFieldMappingSinglePhase() {
        return DAILY_LP_FIELD_MAPPING_SINGLE_PHASE;
    }
    
    public static Map<String, String> getDailyLpFieldMappingThreePhase() {
        return dailyLP3PHMapping;
    }

    public static Map<String, String> getDeltaFieldMappingSinglePhase() {
        return DELTA_FIELD_MAPPING_SINGLE_PHASE;
    }
    
    public static Map<String, String> getDeltaFieldMappingThreePhase() {
        return DELTA_FIELD_MAPPING_THREE_PHASE;
    }
    
    public static Map<String, String> getBillingFieldMappingSinglePhase() {
        return BILLING_FIELD_DATA_SINGLE_PHASE;
    }
    
    
    public static Map<String, String> getBillingFieldMappingThreePhase() {
        return BILLING_FIELD_DATA_Three_PHASE;
    }
    
    public static Map<String, Map<Integer, String>> getObisIndexMapping() {  
        return OBIS_INDEX_MAPPING;
    }
    
    public static Map<String, Map<Integer, String>> getObisIndexMapping3p() {  
        return OBIS_INDEX_MAPPING3P;
    }
    
    
    public static Map<String, String> getInstantReadFieldMappingSinglePhase() {
        return INSTANTREAD_FIELD_MAPPING_SINGLE_PHASE;
    }
    
    public static Map<String, String> getInstantReadFieldMappingThreePhase() {
        return INSTANTREAD_FIELD_MAPPING_THREE_PHASE;
    }
    
    public static Map<String, String> getEventsFieldMappingThreePhase() {
        return EVENT_FIELD_MAPPING_THREE_PHASE;
    }
    
    public static Map<String, String> getEventsFieldMappingSinglePhase() {
        return EVENT_FIELD_MAPPING_SINGLE_PHASE;
    }
    
    public static Map<String, Map<Integer, String>> getObisIndexMappingSinglePhase() {
        return OBIS_INDEX_MAPPING_SINGLE_PHASE;
    }

    public static Map<String, Map<Integer, String>> getObisIndexMappingThreePhase() {
        return OBIS_INDEX_MAPPING_THREE_PHASE;
    }
    
    public static Map<String, String> getEventTypeMapping() {
        return EVENT_TYPE_MAPPING;
    }
    
    public static Map<String, String> getNameplateFieldMappingSinglePhase() {
        return NAMEPLATE_MAPPING;
    }
    
}
