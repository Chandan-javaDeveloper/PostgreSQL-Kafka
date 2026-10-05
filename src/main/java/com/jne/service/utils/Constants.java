package com.jne.service.utils;



import java.util.AbstractMap.SimpleEntry;

public class Constants {

	public static class Query {
		public static final String FETCH_DEVICE_SNO = "select device_serial_number from dlms_db.devices_info"
				+ " where commissioning_status = 'Up' ";
		public static final String COMMANDWISE_FETCH_DEVICE_SNO = "select device_serial_number from dlms_db.devices_info where commissioning_status = 'Up' ";
	}

	public static class Status {
		public static final String BUSY = "BUSY";
		public static final String CONNECTED = "CONNECTED";
		public static final String COMMISSIONED = "COMMISSIONED";
	}

	public static class CmdConfigStatus {
		public static final String ADDED = "ADDED";
	}

	public static class DeviceTypes {
		public static final String SINGLE_PHASE = "Single Phase";
		public static final String THREE_PHASE = "Three Phase";
		public static final String LT_METER = "LT Meter";
		public static final String CT_METER = "CT Meter";
	}

	public static class PhaseVal {
		public static final String _1PH = "1ph";
		public static final String _3PH = "3ph";
	}
	
	public final class AuthenticationConstant {

		public static final String SUCCESS = "Success";
		public static final String AUTHENTICATED = "Authenticated";
		public static final String SESSION_IS_EXPIRED = "Session Is Expired";
		public static final String KEY_IS_NOTVALID = "Key Is Not Valid";
		public static final String API_KEY = "apiKey";
		public static final String DATA = "data";
		public static final String RESPONSE = "response";
		public static final String TRACKING_ID = "trackingId";
		public static final String RESULT = "result";
		public static final String MESSAGE = "message";
	}

	public static class ObisCode {

		public static class INSTANT_3P {

			public static final String RTC_DATETIME = "0.0.1.0.0.255";
			public static final String CURR_L1 = "1.0.31.7.0.255";
			public static final String CURR_L2 = "1.0.51.7.0.255";
			public static final String CURR_L3 = "1.0.71.7.0.255";
			public static final String VOL_L1 = "1.0.32.7.0.255";
			public static final String VOL_L2 = "1.0.52.7.0.255";
			public static final String VOL_L3 = "1.0.72.7.0.255";
			public static final String PF_L1 = "1.0.33.7.0.255";
			public static final String PF_L2 = "1.0.53.7.0.255";
			public static final String PF_L3 = "1.0.53.7.0.255";
			public static final String PF_3P = "1.0.13.7.0.255";
			public static final String FREQ = "1.0.14.7.0.255";
			public static final String APP_POW_KVA = "1.0.9.7.0.255";
			public static final String ACTIVE_POWER_KW = "1.0.1.7.0.255";
			public static final String REACTIVE_POWER_KVAR = "1.0.3.7.0.255";
			public static final String NO_POWER_FAILURE = "0.0.96.7.0.255";
			public static final String POW_OFF_DURATION_MINS = "0.0.94.91.8.255";
			public static final String TAMPER_COUNT = "0.0.94.91.0.255";
			public static final String BILLING_COUNT = "0.0.0.1.0.255";
			public static final String PROGRAMMING_COUNT = "0.0.96.2.0.255";
			public static final String BILLING_DATE = "0.0.0.1.2.255";
			public static final String KWH_IMPORT = "1.0.1.8.0.255";
			public static final String KWH_EXPORT = "1.0.2.8.0.255";
			public static final String KAVH_IMPORT = "1.0.9.8.0.255";
			public static final String KVAH_EXPORT = "1.0.10.8.0.255";
			public static final String MD_ACTIVE_IMPORT_KW = "1.0.1.6.0.255";
			public static final String MD_ACTIVE_IMPORT_DATETIME_KW = "1.0.1.6.0.255"; // SAME INDEX
			public static final String MD_ACTIVE_IMPORT_KVA = "1.0.9.6.0.255";
			public static final String MD_ACTIVE_IMPORT_DATETIME_KVA = "1.0.9.6.0.255"; // SAME INDEX
			public static final String LOAD_LIMIT_FUNCTION_STATUS = "0.0.96.3.10.255";
			public static final String LOAD_LIMIT_KW = "0.0.17.0.0.255";
			public static final String KVARH_Q1 = "1.0.5.8.0.255";
			public static final String KVARH_Q2 = "1.0.6.8.0.255";
			public static final String KVARH_Q3 = "1.0.7.8.0.255";
			public static final String KVARH_Q4 = "1.0.8.8.0.255";
		}

		public static class DAILY_LP_3P {
			public static final String RTC_DATETIME = "0.0.1.0.0.255";
			public static final String KWH_IMPORT = "1.0.1.8.0.255";
			public static final String KWH_EXPORT = "1.0.2.8.0.255";
			public static final String KVAH_IMPORT = "1.0.9.8.0.255";
			public static final String KVAH_EXPORT = "1.0.10.8.0.255";
			public static final String KVARH_Q1 = "1.0.5.8.0.255";
			public static final String KVARH_Q2 = "1.0.6.8.0.255";
			public static final String KVARH_Q3 = "1.0.7.8.0.255";
			public static final String KVARH_Q4 = "1.0.8.8.0.255";
			public static final String MD_KW = "1.0.1.6.0.255";
			public static final String MD_KW_DATETIME = "1.0.1.6.0.255";
			public static final String MD_KW_EXPORT = "1.0.1.6.0.255";
			public static final String MD_KW_EXPORT_DATETIME = "1.0.1.6.0.255";
			public static final String MD_KVA = "1.0.9.6.0.255";
			public static final String MD_KVA_DATETIME = "1.0.9.6.0.255";
		}

		public static class DELTA_LP_3P {
			public static final String RTC_DATETIME = "0.0.1.0.0.255";
			public static final String CURR_L1 = "1.0.31.27.0.255";
			public static final String CURR_L2 = "1.0.51.27.0.255";
			public static final String CURR_L3 = "1.0.71.27.0.255";
			public static final String VOL_L1 = "1.0.32.27.0.255";
			public static final String VOL_L2 = "1.0.52.27.0.255";
			public static final String VOL_L3 = "1.0.72.27.0.255";
			public static final String KWH_IMPORT = "1.0.1.29.0.255";
			public static final String KWH_EXPORT = "1.0.2.29.0.255";
			public static final String KVAH_IMPORT = "1.0.9.29.0.255";
			public static final String KVAH_EXPORT = "1.0.10.29.0.255";
			public static final String KVARH_Q1 = "1.0.5.29.0.255";
			public static final String KVARH_Q2 = "1.0.6.29.0.255";
			public static final String KVARH_Q3 = "1.0.7.29.0.255";
			public static final String KVARH_Q4 = "1.0.7.29.0.255";
			public static final String STATUS_BYTE = "0.0.96.10.1.255";
			public static final String AVG_SIGNAL_STRENGTH = "0.1.96.12.5.255";
			public static final String POWER_DOWNTIME_IN_MINS = "0.0.94.7.8.255";
			public static final String R_PHASE_ACTIVE_POWER_KW = "1.0.35.27.0.255";
			public static final String Y_PHASE_ACTIVE_POWER_KW = "1.0.55.27.0.255";
			public static final String B_PHASE_ACTIVE_POWER_KW = "1.0.75.27.0.255";
		}

	}

	public final class HierLevelName {
		public static final String SUBDEVISION = "SUBDEVISION";
		public static final String FEEDER = "FEEDER";
		public static final String SUBSTATION = "SUBSTATION";
		public static final String DTMETER = "DT";
		public static final String DCU = "DCU";
		public static final String METER = "METER";
		public static final String ALL = "ALL";

	}
	
	public final static class EVIT_DB {
		public static final String DB_NAME = "dlms_db" ;
		public static final String SUBDEVISION_TABLE = "subdevisions";
		public static final String FEEDERS_TABLE = "feeders";
		public static final String SUBSTATION_TABLE = "substations";
		public static final String DT_TABLE = "dt_trans";
		public static final String DCU_TABLE = "dcu_info";
		public static final String DEVICE_TABLE = "devices_info";
		public static final String NAME_PLATE = "name_plate";
		public static final String DEVICE_CONFIG = "devices_config";
		public static final String DEVICE_TOD_CONFIG = "devices_tod_config";
		public static final String DEVICE_CONFIG_LOGS = "devices_config_logs";
		public static final String DEVICES_HISTORY_TABLE = "devices_history";
		public static final String LOGS = "logs";
		public static final String DEVICES_COMMANDS = "devices_commands";
		public static final String DEVICES_COMMANDS_LOGS = "devices_commands_logs";
		public static final String DEVICES_STATUS_COMMANDS_LOGS = "devices_status_commands_logs";
		public static final String DEVICES_STATUS = "devices_status";
		public static final String DEVICES_COMMANDS_PREPAY_LOGS = "devices_commands_prepay_logs";
		public static final String PREPAY_DATA = "prepay_data";
		public static final String FIRMWARE_COMMANDS = "firmware_config";
		public static final String FULL_CONFIG = "devices_config_logs";
		public static final String HES_SCHEDULING = "hes_scheduling";
		
		public static final String INSTANT_DATA = "instantaneous_data_singlephase";
		public static final String INSTANT_DATA_PUSH = "instantaneous_push_singlephase";
		public static final String BILLING_TABLE = "last_billing_data_singlephase";
		public static final String CURRENT_BILLING_TABLE = "billing_data_singlephase";
		public static final String DAILY_LOAD_PROFILE_TABLE = "Daily_Load_Profile_SinglePhase";
		public static final String LOAD_PROFILE_TABLE = "load_profile_data_singlephase";
		public static final String EVENT_TABLE = "event_data_singlephase";
		public static final String EVENT_DATA_PUSH = "event_push_data_singlephase";
		
		public static final String INSTANT_DATA_3P = "instantaneous_data_threephase";
		public static final String INSTANT_DATA_PUSH_3P = "instantaneous_push_threephase";
		public static final String BILLING_TABLE_3P = "last_billing_data_threephase";
		public static final String CURRENT_BILLING_TABLE_3P = "billing_data_threephase";
		public static final String DAILY_LOAD_PROFILE_TABLE_3P = "daily_load_profile_threephase";
		public static final String LOAD_PROFILE_TABLE_3P = "load_profile_data_threephase";
		public static final String EVENT_TABLE_3P = "event_data_threephase";
		public static final String EVENT_DATA_PUSH_3P = "event_push_data_threephase";
		
		public static final String DASHBOARD = "dashboard";
		public static final String DASHBOARD_COUNT = "dashboard_count";
		public static final String OWNERS = "owners";
		public static final String METER_COMM_COUNT= "meter_comm_count";
		public static final String METERS_COMM_COUNT= "comm_count";
		public static final String UserLogin = "userlogin";
		
		public static final String SLA_DATA = "sla_data";
		public static final String MONTHLY_SLA_DATA = "monthly_sla_data";
	
	}

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String IN_PROGRESS = "IN_PROGRESS";
	public static final String ADDED = "ADDED";

	public static final boolean FLAG_SUCCESS = true;
	public static final boolean FLAG_FAILURE = false;

	public static final String INSTANTANEOUS_READ = "InstantaneousRead";
	public static final String DAILY_LOAD_PROFILE = "DailyLoadProfile";
	public static final String DELTA_LOAD_PROFILE = "DeltaLoadProfile";
	public static final String BILLING_DATA = "BillingData";
	public static final String POWER_RELATED_EVENTS = "PowerRelatedEvents";
	public static final String VOLTAGE_RELATED_EVENTS = "VoltageRelatedEvents";
	public static final String TRANSACTION_RELATED_EVENTS = "TransactionRelatedEvents";
	public static final String CURRENT_RELATED_EVENTS = "CurrentRelatedEvents";
	public static final String OTHER_RELATED_EVENTS = "OtherRelatedEvents";
	public static final String CONTROL_RELATED_EVENTS = "ControlRelatedEvents";
	public static final String CONNECT = "Connect";
	public static final String DISCONNECT = "Disconnect";
	public static final String PAYMENT_MODE = "PaymentMode";
	public static final String METERING_MODE = "MeteringMode";
	public static final String LAST_TOKEN_RECHARGE_AMOUNT = "LastTokenRechargeAmount";
	public static final String LAST_TOKEN_RECHARGE_TIME = "LastTokenRechargeTime";
	public static final String TOTAL_AMOUNT_AT_lAST_RECHARGE = "TotalAmountAtLastRecharge";
	public static final String CURRENT_BALANCE_AMOUNT = "CurrentBalanceAmount";
	public static final String CURRENT_BALANCE_TIME = "CurrentBalanceTime";

	public static final String POWER_RELATED = "Power Related";
	public static final String VOLTAGE_RELATED = "Voltage Related";
	public static final String TRANSACTION_RELATED = "Transaction Related";
	public static final String CURRENT_RELATED = "Current Related";
	public static final String OTHERS_RELATED = "Others";
	public static final String CONTROL_RELATED = "Control Related";

	public static final int LIMIT = 2000;
	
	public final class OrderBy {
		public static final String ASC = "asc";
		public static final String DESC = "desc";
	}
	
	public final class Tables {
		public static final String DEVICES_INFO = "dlms_db.devices_info3";
		public static final String DEVICES_INFO_HES = "dlms_db.devices_info3";
		public static final String NAME_PLATES = "dlms_db.name_plate";
		public static final String DEVICES_CONFIG = "dlms_db.devices_config";
		public static final String DEVICES_CONFIG_LOGS = "dlms_db.devices_config_logs";
		public static final String DEVICES_COMMANDS = "dlms_db.devices_commands";
		public static final String DEVICE_STATUS_COMMMANDS_LOGS = "dlms_db.devices_status_commands_logs";
		
		public static final String LAST_SP_BILLING = "dlms_db.billing_data_singlephase";
		public static final String LAST_SP_CURRENT_BILLING = "dlms_db.billing_data_threephase";
		public static final String LAST_SP_INSTANT = "dlms_db.instantaneous_data_singlephase";
		public static final String LAST_SP_INSTANT_THREE = "dlms_db.instantaneous_data_threephase";
		public static final String LAST_SP_INSTANTPUSH = "dlms_db.instantaneous_push_singlephase";
		
		public static final String LAST_SP_INSTANTPUSHs = "dlms_db.instant_push_singlephase";
		public static final String LAST_SP_INSTANTPUSHt = "dlms_db.instant_push_threephase";


		
		public static final String LAST_SP_DAILYLP = "dlms_db.daily_load_profile_singlephase";
		public static final String LAST_TP_DAILYLP = "dlms_db.daily_load_profile_threephase";

		public static final String LAST_SP_DELTALP = "dlms_db.load_profile_data_singlephase";
		public static final String LAST_TP_DELTALP = "dlms_db.load_profile_data_threephase";

		
		public static final String LAST_SP_EVENTS = "dlms_db.event_data_singlephase";
		public static final String LAST_TP_EVENTS = "dlms_db.event_data_threephase";

		public static final String EVENT_PUSH = "dlms_db.event_push_data_singlephase";
		public static final String EVENT_PUSHs = "dlms_db.event_push_data";

		public static final String DASHBOARD = "dlms_db.dashboard";
		public static final String COMMCOUNT = "dlms_db.comm_count";
		public static final String SUBDEVISIONS = "dlms_db.subdevisions";
		public static final String DEVICECOMMMANDSLOG = "dlms_db.devices_commands_logs";
		public static final String COMMMANDLOGS = "dlms_db.command_logs";
		public static final String FULLDATACOMMMANDSLOG = "dlms_db.devices_commands";
		public static final String FIRMWARECOMMMANDSLOG = "dlms_db.firmware_config";
		public static final String SINGLECONFIGLOG = "dlms_db.devices_config";
		public static final String FULLCONFIGLOG = "dlms_db.devices_config_logs";
		public static final String FIRMWAREDATA = "dlms_db.firmware_data";
		public static final String SUBSTATION = "dlms_db.substations";
		public static final String SUBDIVISION = "dlms_db.subdevisions";
		public static final String FEEDERS = "dlms_db.feeders";
		public static final String DT_TRANS = "dlms_db.dt_trans";
		public static final String COMM_COUNT = "dlms_db.comm_count";
		
		public static final String COMM_COUNT_CT = "dlms_db.meter_comm_count_ct_phase";
		public static final String COMM_COUNT_HT= "dlms_db.meter_comm_count_ht_phase";
		public static final String COMM_COUNT_SINGLE = "dlms_db.meter_comm_count_single_phase";
		public static final String COMM_COUNT_THREE = "dlms_db.meter_comm_count_three_phase";
		
		public static final String CONFIGREADLOGS = "dlms_db.config_read_logs";
		public static final String CONFIGWRITELOGS = "dlms_db.config_write_logs";
		public static final String DEVICE_IP_PING = "dlms_db.devices_ip_ping_logs";
		public static final String lastBillTP = "dlms_db.last_billing_data_threephase";
		public static final String lastBillSP = "dlms_db.last_billing_data_singlephase";
		public static final String BillTP = "dlms_db.billing_data_threephase";
		public static final String BillSP = "dlms_db.billing_data_singlephase";





		


  }
	
	// feeder
	public final class FeedersField
	{
		public static final String SUBDIVISION = "Subdevision";
		public static final String SUBSTATION_NAME = "Substationname";
		public static final String FEEDER = "Feeder";
		public static final String LATITUDE = "Latitude";
		public static final String LONGITUDE = "Longitude";
		
	}
	
	
	// DT_TRANS
	public final class DT_TransField
	{
		public static final String DT_NAME = "Dt name";
		public static final String SUBSTATION = "Substation";
		public static final String SUBDIVISION = "Subdivision";
		public static final String FEEDER = "Feeder";
		public static final String LATITUDE = "Latitude";
		public static final String LONGITUDE = "Longitude";

		
	}
	
	
	
	// Commands Log
	public final class CommandsLogField
	{
		public static final String TRACKING_ID = "Tracking Id";
		public static final String METER_S_NO = "Meter S.No.";
		public static final String FIRMWARE_FILE = "Firmware File";
		public static final String COMMAND_NAME = "Command Name";
		public static final String MDAS_DATE_TIME = "MDAS Date Time";
		public static final String COMMAND_COMPLETION_DATE_TIME = "Command Completion Date Time";
		public static final String STATUS = "Status";
		public static final String ATTEMPTS = "Attempts";
		

	}
	
	public final class FirmwareLogField
	{
		public static final String TRACKING_ID = "Tracking Id";
		public static final String METER_S_NO = "Meter S.No.";
		public static final String COMMAND_NAME = "Command Name";
		public static final String MDAS_DATE_TIME = "MDAS Date Time";
		public static final String COMMAND_COMPLETION_DATE_TIME = "Command Completion Date Time";
		public static final String STATUS = "Status";
		public static final String ATTEMPTS = "Attempts";
	}
	
	// config Log
	
	public final class ConfigLogField
	{
		public static final String TRACKING_ID = "Tracking Id";
		public static final String METER_S_NO = "Meter S.No.";
		public static final String CONFIG_COMMAND = "Config Command";
		public static final String MDAS_DATE_TIME = "MDAS Date Time";
		public static final String COMMAND_COMPLETION_DATE_TIME = "Command Completion Date Time";
		public static final String CONFIG_COMMAND_STATUS = "Config Command Status";
		public static final String ATTEMPTS = "Attempts";
		public static final String OVERALL_STATUS = "OverAllStatus";
		

		
}
	
	// single Configuration
	public final class ConfigurationLogField
	{
		public static final String TRACKING_ID = "Tracking Id";
		public static final String METER_S_NO = "Meter S.No.";
		public static final String COMMAND_NAME = "Command Name";
		public static final String MDAS_DATE_TIME = "MDAS Date Time";
		public static final String COMMAND_COMPLETION_DATE_TIME = "Command Completion Date Time";
		public static final String ATTEMPTS = "Attempts";
		public static final String OVERALL_STATUS = "OverAll Status";
		
	}
	
	
	// subStation
	public final class SubStationField
	{
		public static final String SUBDEVISIONS = "Subdevisions";
		public static final String SUBSTATIONS = "Substations";
		public static final String LATITUDE = "Latitude";
		public static final String LONGITUDE = "Longitude";
		public static final String DIVISIONS = "division_name";
	}
	
	
	// subdevisions
	public final class SubDevisionField
	{
		public static final String SUBDEVISIONS = "Subdevisions";
		public static final String LATITUDE = "Latitude";
		public static final String LONGITUDE = "Longitude";
	}
	
	// commcount
	public final class CommCountField
	{
//		public static final String OWNER_NAME = "Owner name";
//		public static final String DEV_TYPE = "Dev type";
		public static final String INACTIVE_DEV = "INACTIVE_DEV";
		public static final String ACTIVE_DEV = "ACTIVE_DEV";
		public static final String FAULTY_DEV = "FAULTY_DEV";
		public static final String BILLING_DAY_FAILURE_COUNT = "BILLING_DAY_FAILURE_COUNT";
		public static final String BILLING_DAY_SUCCESS_COUNT = "BILLING_DAY_SUCCESS_COUNT";
		public static final String BILLING_MONTH_FAILURE_COUNT = "BILLING_MONTH_FAILURE_COUNT";
		public static final String BILLING_MONTH_SUCCESS_COUNT = "BILLING_MONTH_SUCCESS_COUNT";
		public static final String BILLING_WEEK_FAILURE_COUNT = "BILLING_WEEK_FAILURE_COUNT";
		public static final String BILLING_WEEK_SUCCESS_COUNT = "BILLING_WEEK_SUCCESS_COUNT";
		public static final String BILLING_YESTERDAY_FAILURE_COUNT = "BILLING_YESTERDAY_FAILURE_COUNT";
		public static final String BILLING_YESTERDAY_SUCCESS_COUNT = "BILLING_YESTERDAY_SUCCESS_COUNT";
		public static final String COMM_DAY_FAILURE_COUNT = "COMM_DAY_FAILURE_COUNT";
		public static final String COMM_DAY_SUCCESS_COUNT = "COMM_DAY_SUCCESS_COUNT";
		public static final String COMM_MONTH_FAILURE_COUNT = "COMM_MONTH_FAILURE_COUNT";
		public static final String COMM_MONTH_SUCCESS_COUNT = "COMM_MONTH_SUCCESS_COUNT";
		public static final String COMM_WEEK_FAILURE_COUNT = "COMM_WEEK_FAILURE_COUNT";
		public static final String COMM_WEEK_SUCCESS_COUNT = "COMM_WEEK_SUCCESS_COUNT";
		public static final String COMM_YESTERDAY_FAILURE_COUNT = "COMM_YESTERDAY_FAILURE_COUNT";
		public static final String COMM_YESTERDAY_SUCCESS_COUNT = "COMM_YESTERDAY_SUCCESS_COUNT";
		public static final String DAILY_DAY_FAILURE_COUNT = "DAILY_DAY_FAILURE_COUNT";
		public static final String DAILY_DAY_SUCCESS_COUNT = "DAILY_DAY_SUCCESS_COUNT";
		public static final String DAILY_MONTH_FAILURE_COUNT = "DAILY_MONTH_FAILURE_COUNT";
		public static final String DAILY_MONTH_SUCCESS_COUNT = "DAILY_MONTH_SUCCESS_COUNT";
		public static final String DAILY_WEEK_FAILURE_COUNT = "DAILY_WEEK_FAILURE_COUNT";
		public static final String DAILY_WEEK_SUCCESS_COUNT = "DAILY_WEEK_SUCCESS_COUNT";
		public static final String DAILY_YESTERDAY_FAILURE_COUNT = "DAILY_YESTERDAY_FAILURE_COUNT";
		public static final String DAILY_YESTERDAY_SUCCESS_COUNT = "DAILY_YESTERDAY_SUCCESS_COUNT";
		public static final String DELTA_DAY_FAILURE_COUNT = "DELTA_DAY_FAILURE_COUNT";
		public static final String DELTA_DAY_SUCCESS_COUNT = "DELTA_DAY_SUCCESS_COUNT";
		public static final String DELTA_MONTH_FAILURE_COUNT = "DELTA_MONTH_FAILURE_COUNT";
		public static final String DELTA_MONTH_SUCCESS_COUNT = "DELTA_MONTH_SUCCESS_COUNT";
		public static final String DELTA_WEEK_FAILURE_COUNT = "DELTA_WEEK_FAILURE_COUNT";
		public static final String DELTA_WEEK_SUCCESS_COUNT = "DELTA_WEEK_SUCCESS_COUNT";
		public static final String DELTA_YESTERDAY_FAILURE_COUNT = "DELTA_YESTERDAY_FAILURE_COUNT";
		public static final String DELTA_YESTERDAY_SUCCESS_COUNT = "DELTA_YESTERDAY_SUCCESS_COUNT";
		public static final String EVENT_DAY_FAILURE_COUNT = "EVENT_DAY_FAILURE_COUNT";
		public static final String EVENT_DAY_SUCCESS_COUNT = "EVENT_DAY_SUCCESS_COUNT";
		public static final String EVENT_MONTH_FAILURE_COUNT = "EVENT_MONTH_FAILURE_COUNT";
		public static final String EVENT_MONTH_SUCCESS_COUNT = "EVENT_MONTH_SUCCESS_COUNT";
		public static final String EVENT_WEEK_FAILURE_COUNT = "EVENT_WEEK_FAILURE_COUNT";
		public static final String EVENT_WEEK_SUCCESS_COUNT = "EVENT_WEEK_SUCCESS_COUNT";
		public static final String EVENT_YESTERDAY_FAILURE_COUNT = "EVENT_YESTERDAY_FAILURE_COUNT";
		public static final String EVENT_YESTERDAY_SUCCESS_COUNT = "EVENT_YESTERDAY_SUCCESS_COUNT";
		public static final String INSTANT_DAY_FAILURE_COUNT = "INSTANT_DAY_FAILURE_COUNT";
		public static final String INSTANT_DAY_SUCCESS_COUNT = "INSTANT_DAY_SUCCESS_COUNT";
		public static final String INSTANT_MONTH_FAILURE_COUNT = "INSTANT_MONTH_FAILURE_COUNT";
		public static final String INSTANT_MONTH_SUCCESS_COUNT = "INSTANT_MONTH_SUCCESS_COUNT";
		public static final String INSTANT_WEEK_FAILURE_COUNT = "INSTANT_WEEK_FAILURE_COUNT";
		public static final String INSTANT_WEEK_SUCCESS_COUNT = "INSTANT_WEEK_SUCCESS_COUNT";
		public static final String INSTANT_YESTERDAY_FAILURE_COUNT = "INSTANT_YESTERDAY_FAILURE_COUNT";
		public static final String INSTANT_YESTERDAY_SUCCESS_COUNT = "INSTANT_YESTERDAY_SUCCESS_COUNT";
		public static final String DT = "DT";
		public static final String FEEDER = "FEEDER";
		public static final String SUBDIVISION = "SUBDIVISION";
		public static final String SUBSTATION = "SUBSTATION";
		public static final String DCU = "DCU";
		public static final String LAST_UPDATED_TIME = "LAST_UPDATED_TIME-12-10 05:03:00";
		public static final String METERS = "Meters";
		public static final String CT_METERS = "CT_METERS";
		public static final String HT_METERS = "HT_METERS";
		public static final String SINGLE_PHASE_METERS = "SINGLE_PHASE_METERS";
		public static final String THREE_PHASE_METERS = "THREE_PHASE_METERS";
		public static final String CTC_METERS = "CTC_METERS";
		public static final String HTC_METERS = "HTC_METERS";



}

	
	
	
	
	public final class DevicesUIFields{
		public static final String CONSUMER_NAME = "Consumer Name";
		public static final String METER_SNO = "Meter S.No.";
		public static final String CONSUMER_NO = "Consumer No";
		public static final String METER_TYPE = "Meter Type";
		public static final String SUBDIVISION = "Subdivision Name";
		public static final String SUBSTATION = "Substation Name";
		public static final String FEEDER = "Feeder Name";
		public static final String NETWORK = "Network";
		public static final String NIC_IPV6 = "NIC IPV6";
		public static final String LATITUDE = "Latitude";
		public static final String LONGITUTDE = "Longitude";
		public static final String ADDRESSS = "Address";
		public static final String PHONE_NO = "Phone_no";
		public static final String INSTALLATION_DATE = "InstallationDate";
		public static final String METER_MODE = "Meter Mode";
        public static final String BILL_MODE = "BillMode";
        public static final String MANUFACTURER = "Manufacturer";
        public static final String PAYMENT_MODE = "Payment Mode";
        public static final String METERING_MODE = "Metering mode";
        public static final String GROUPS = "Groups";



		
	}
	
	// mpdcl
	public final class DevicesUIFieldsMpdcl{
		public static final String CONSUMER_NAME = "Consumer Name";
		public static final String METER_SNO = "Meter S.No.";
		public static final String METER_MODE = "Meter Mode";
		public static final String CONSUMER_NO = "Consumer No";
		public static final String NIC_IPV6 = "NIC IPV6";
		public static final String OWNER_NAME = "Owner name";
		public static final String SUBDIVISION = "Subdivision Name";
		public static final String SUBSTATION = "Substation Name";
		public static final String FEEDER = "Feeder Name";
		public static final String DT_NAME = "Dt Name";
		public static final String LATITUDE = "Latitude";
		public static final String LONGITUTDE = "Longitude";
	}

	public final class NamePlatesUIFields{
		public static final String METER_SNO = "Meter S.No.";
		public static final String DEVICE_SNO = "Device S.No.";
		public static final String CURRENT_RATING = "Current Rating";
		public static final String STATUS  = "Status";
		public static final String DEVICE_ID = "Device ID";
		public static final String FIRMWARE_VERSION = "Fw Version";
		public static final String MANUFACTURER_NAME = "Manufacturer Name";
		public static final String MANUFACTURER_YEAR = "Manufacturer Year";
		public static final String MDAS_DATETIME = "Mdas Datetime";
		public static final String METER_TYPE = "Meter Type";
	}
	
	public final class DevicesConfigUIFields{
		public static final String METER_SNO = "Meter S.No.";
		public static final String MDAS_DATETIME = "MDAS Date Time";
		public static final String COMMAND_NAME = "Command Name";
		public static final String COMMAND_COMPLETION_DATETIME = "Command Completion Date Time";
		public static final String STATUS = "Status";
		public static final String ATTEMPTS = "Attempts";
	}
	
	public final class OnDmdCmdName
	{
		public static final String BILLING = "Billing";
		public static final String INSTAN = "InstantRead";
		public static final String EVENTS = "Events";
		public static final String CONNECT = "Connect";
		public static final String DISCONNECT = "Disconnect";
		public static final String NAMEPLATE = "NamePlate";
		public static final String DAILY_LP = "DailyLP";
		public static final String DELTA_LP = "DeltaLP";
		public static final String LAST_COMM = "LastComm";
		public static final String FULL_DATA = "FullData";
		public static final String FULL_DATA_METER_STATUS = "MeterStatus";
		public static final String POWER_EVENTS = "PowerEvents";
		public static final String VOLTAGE_EVENTS = "VoltageEvents";
		public static final String CURRENT_EVENTS = "CurrentEvents";
		public static final String OTHER_EVENTS = "OtherEvents";
		public static final String TRANSACTION_EVENTS = "TransactionEvents";
		public static final String CONTROL_EVENTS = "ControlEvents";
	}
	
	public final class InstantUIFields{
		public static final String METER_SNO = "Meter S.No.";
		public static final String METER_DATETIME = "Meter Date Time";
		public static final String MDAS_DATETIME = "MDAS Date Time";
		public static final String EXPORT_KVAH = "Energy Export(Kvah)";
		public static final String IMPORT_KVAH = "Energy Import(Kvah)";
		public static final String EXPORT_KWH = "Energy Export(Kwh)";
		public static final String IMPORT_KWH = "Energy Import(Kwh)";
		public static final String VOLTAGE = "Voltage";
		public static final String PHASE_CURRENT = "Phase Current";
		public static final String NEUTRAL_CURRENT = "Neutral Current";
		public static final String ACTIVE_POWER_KW = "Active Power(Kw)";
		public static final String APPARENT_POWER_KVA = "Apparent Power(Kva)";
		public static final String PF = "PF";
		public static final String FREQ = "Frequency";
		public static final String LOAD_LIMIT = "Load Limit";
		public static final String LOAD_STATUS = "Load Status";
		public static final String MD_KVA = "MD(Kva)";
		public static final String MD_KVA_DATETIME = "MD Kva Date Time";
		public static final String MD_KW = "MD(Kw)";
		public static final String MD_KW_DATETIME = "MD Kw Date Time";
		public static final String POWER_ON_DURATION = "Power On Duration";
		public static final String PROGRAM_COUNT = "Program Count";
		public static final String TAMPER_COUNT = "Tamper Count";
	}
	
	// three Phase
	
	public final class InstantUITPFields {
	    public static final String METER_SNO = "Meter S.No.";
	    public static final String METER_DATE_TIME = "Meter Date Time";
	    public static final String MDAS_DATE_TIME = "MDAS Date Time";
	    public static final String ENERGY_IMPORT_KWH = "Energy Import(Kwh)";
	    public static final String ENERGY_EXPORT_KWH = "Energy Export(Kwh)";
	    public static final String ENERGY_IMPORT_KVAH = "Energy Import(Kvah)";
	    public static final String ENERGY_EXPORT_KVAH = "Energy Export(Kvah)";
	    
	    public static final String Q1_KVARH = "Q1(Kvarh)";
	    public static final String Q2_KVARH = "Q2(Kvarh)";
	    public static final String Q3_KVARH = "Q3(Kvarh)";
	    public static final String Q4_KVARH = "Q4(Kvarh)";
	    
	    public static final String ACTIVE_POWER_KW = "Active Power(Kw)";
	    public static final String APPARENT_POWER_KVA = "Apparent Power(Kva)";
	    public static final String REACTIVE_POWER_KVAR = "Reactive Power(Kvar)";
	    public static final String R_PH_VOLTAGE = "R Ph Voltage";
	    public static final String Y_PH_VOLTAGE = "Y Ph Voltage";
	    public static final String B_PH_VOLTAGE = "B Ph Voltage";
	    public static final String R_PH_CURRENT = "R Ph Current";
	    public static final String Y_PH_CURRENT = "Y Ph Current";
	    public static final String B_PH_CURRENT = "B Ph Current";
	    public static final String R_PH_PF = "R Ph PF";
	    public static final String Y_PH_PF = "Y Ph PF";
	    public static final String B_PH_PF = "B Ph PF";
	    public static final String TOTAL_PF = "Total PF";
	    public static final String FREQUENCY = "Frequency";
	    public static final String LOAD_LIMIT = "Load Limit";
	    public static final String LOAD_STATUS = "Load Status";
	    public static final String MD_KVA = "MD(Kva)";
	    public static final String MD_KVA_DATE_TIME = "MD Kva Date Time";
	    public static final String MD_KW = "MD(Kw)";
	    public static final String MD_KW_DATE_TIME = "MD Kw Date Time";
	    public static final String POWER_OFF_DURATION = "Power Off Duration";
	    public static final String PROGRAM_COUNT = "Program Count";
	    public static final String TAMPER_COUNT = "Tamper Count";
	    
	    
	}

	
	public final class BillingUIFields{
		public static final String MDAS_DATETIME = "MDAS Date Time";
		public static final String METER_SNO = "Meter S.No.";
		public static final String BILLING_DATETIME = "Billing Date Time";
		public static final String AVG_PF = "Avg.PF";
		public static final String POWER_ON_DURATION = "Power On Duration";
		public static final String POWER_OFF_DURATION = "Power Off Duration";
		public static final String ENERGY_EXPORT_KVAH = "Energy Export(Kvah)";
		public static final String ENERGY_IMPORT_KVAH = "Energy Import(Kvah)";
		public static final String ENERGY_KVAH_T1 = "Energy(Kvah) T1";
		public static final String ENERGY_KVAH_T2 = "Energy(Kvah) T2";
		public static final String ENERGY_KVAH_T3 = "Energy(Kvah) T3";
		public static final String ENERGY_KVAH_T4 = "Energy(Kvah) T4";
		public static final String ENERGY_KVAH_T5 = "Energy(Kvah) T5";
		public static final String ENERGY_KVAH_T6 = "Energy(Kvah) T6";
		public static final String ENERGY_KVAH_T7 = "Energy(Kvah) T7";
		public static final String ENERGY_KVAH_T8 = "Energy(Kvah) T8";
		public static final String ENERGY_EXPORT_KWH = "Energy Export(Kwh)";
		public static final String ENERGY_IMPORT_KWH = "Energy Import(Kwh)";
		public static final String ENERGY_KWH_T1 = "Energy(Kwh) T1";
		public static final String ENERGY_KWH_T2 = "Energy(Kwh) T2";
		public static final String ENERGY_KWH_T3 = "Energy(Kwh) T3";
		public static final String ENERGY_KWH_T4 = "Energy(Kwh) T4";
		public static final String ENERGY_KWH_T5 = "Energy(Kwh) T5";
		public static final String ENERGY_KWH_T6 = "Energy(Kwh) T6";
		public static final String ENERGY_KWH_T7 = "Energy(Kwh) T7";
		public static final String ENERGY_KWH_T8 = "Energy(Kwh) T8";
        public static final String MD_KVA = "MD(Kva)";
		public static final String MD_KVA_DATETIME = "MD Kva Date Time";
		public static final String MD_KW = "MD(Kw)";
		public static final String MD_KW_DATETIME = "MD Kw Date Time";
	}
	
	// current billing data profile
	public final class BillingCurrentUIFields{
		public static final String METER_SNO = "Meter S.No.";
		public static final String MDAS_DATETIME = "MDAS Date Time";
		public static final String BILLING_DATETIME = "Billing Date Time";
		public static final String AVG_PF = "Avg.PF";
		public static final String POWER_ON_DURATION = "Power On Duration";
		
		public static final String ENERGY_EXPORT_KVAH = "Energy Export(Kvah)";
		public static final String ENERGY_IMPORT_KVAH = "Energy Import(Kvah)";
		public static final String ENERGY_KVAH_T1 = "Energy(Kvah) T1";
		public static final String ENERGY_KVAH_T2 = "Energy(Kvah) T2";
		public static final String ENERGY_KVAH_T3 = "Energy(Kvah) T3";
		public static final String ENERGY_KVAH_T4 = "Energy(Kvah) T4";
		public static final String ENERGY_KVAH_T5 = "Energy(Kvah) T5";
		public static final String ENERGY_KVAH_T6 = "Energy(Kvah) T6";
		public static final String ENERGY_KVAH_T7 = "Energy(Kvah) T7";
		public static final String ENERGY_KVAH_T8 = "Energy(Kvah) T8";
		public static final String ENERGY_EXPORT_KWH = "Energy Export(Kwh)";
		public static final String ENERGY_IMPORT_KWH = "Energy Import(Kwh)";
		public static final String ENERGY_KWH_T1 = "Energy(Kwh) T1";
		public static final String ENERGY_KWH_T2 = "Energy(Kwh) T2";
		public static final String ENERGY_KWH_T3 = "Energy(Kwh) T3";
		public static final String ENERGY_KWH_T4 = "Energy(Kwh) T4";
		public static final String ENERGY_KWH_T5 = "Energy(Kwh) T5";
		public static final String ENERGY_KWH_T6 = "Energy(Kwh) T6";
		public static final String ENERGY_KWH_T7 = "Energy(Kwh) T7";
		public static final String ENERGY_KWH_T8 = "Energy(Kwh) T8";
		public static final String Q1 = "Q1(Kvarh)";
		public static final String Q2 = "Q2(Kvarh)";		
		public static final String Q3 = "Q3(Kvarh)";
		public static final String Q4 = "Q4(Kvarh)";
		public static final String MD_KVA = "MD(Kva)";
		public static final String MD_KVA_DATETIME = "MD Kva Date Time";
		public static final String MD_KW = "MD(Kw)";
		public static final String MD_KW_DATETIME = "MD Kw Date Time";
	}
	
	
	
	
	public final class DailyLPFields{
		public static final String MDAS_DATETIME = "MDAS Date Time";
		public static final String METER_SNO = "Meter S.No.";
		public static final String DATETIME = "Date Time";
		public static final String ENERGY_EXPORT_KVAH = "Energy Export(Kvah)";
		public static final String ENERGY_IMPORT_KVAH = "Energy Import(Kvah)";
		public static final String ENERGY_EXPORT_KWH = "Energy Export(Kwh)";
		public static final String ENERGY_IMPORT_KWH = "Energy Import(Kwh)";
	}
	
	public final class DeltaLPFields{
		public static final String MDAS_DATETIME = "MDAS Date Time";
		public static final String METER_SNO = "Meter S.No.";
		public static final String DATETIME = "Interval Date Time";
		public static final String AVG_CURRENT = "Avg. Current";
		public static final String AVG_VOLTAGE = "Avg. Voltage";
		public static final String BLOCK_ENERGY_EXPORT_KVAH = "Block Energy Export(Kvah)";
		public static final String BLOCK_ENERGY_IMPORT_KVAH = "Block Energy Import(Kvah)";
		public static final String BLOCK_ENERGY_EXPORT_KWH = "Block Energy Export(Kwh)";
		public static final String BLOCK_ENERGY_IMPORT_KWH = "Block Energy Import(Kwh)";
	}
	
	public final class DeltaLPTPFields{
		public static final String MDAS_DATETIME = "MDAS Date Time";
		public static final String METER_SNO = "Meter S.No.";
		public static final String DATETIME = "Interval Date Time";
		
		public static final String R_Ph_Current = "R_Ph_Current";
		public static final String Y_Ph_Current = "Y_Ph_Current";
		public static final String B_Ph_Current = "B_Ph_Current";
		public static final String R_Ph_Voltage = "R_Ph_Voltage";
		public static final String Y_Ph_Voltage = "Y_Ph_Voltage";
		public static final String B_Ph_Voltage = "B_Ph_Voltage";

		public static final String BLOCK_ENERGY_EXPORT_KVAH = "Block Energy Export(Kvah)";
		public static final String BLOCK_ENERGY_IMPORT_KVAH = "Block Energy Import(Kvah)";
		public static final String BLOCK_ENERGY_EXPORT_KWH = "Block Energy Export(Kwh)";
		public static final String BLOCK_ENERGY_IMPORT_KWH = "Block Energy Import(Kwh)";
	}
	
	
	public final class EventsFields{
		public static final String METER_SNO = "Meter S.No.";
		public static final String MDAS_DATETIME = "MDAS Date Time";
		public static final String DATETIME = "Event Date Time";
		public static final String EVENT_CATEGORY = "Event Category";
		public static final String EVENT_CODE  = "Event Code";
		public static final String EVENT_TYPE = "Event Type";
		public static final String CURRENT = "Current";
		public static final String VOLTAGE = "Voltage";
		public static final String PF = "PF";
		public static final String ENERGY_KWH = "Energy(Kwh)";
		public static final String TAMPER_COUNT = "Tamper Count";
	}
	
	//  three Phase
	
	public final class EventsTpFields{
		public static final String METER_SNO = "Meter S.No.";
		public static final String MDAS_DATETIME = "MDAS Date Time";
		public static final String DATETIME = "Event Date Time";
		public static final String EVENT_CATEGORY = "Event Category";
		public static final String EVENT_CODE  = "Event Code";
		public static final String EVENT_TYPE = "Event Type";
		
		public static final String R_PH_CURRENT = "R_PH_CURRENT";
		public static final String Y_PH_CURRENT = "Y Ph Current";
		public static final String B_PH_CURRENT = "B Ph Current";
		public static final String R_PH_VOLTAGE = "R Ph Voltage";
		public static final String Y_PH_VOLTAGE = "Y Ph Voltage";
		public static final String B_PH_VOLTAGE = "B Ph Voltage";
		public static final String R_PH_PF = "R Ph PF";
		public static final String Y_PH_PF = "Y Ph PF";
		public static final String B_PH_PF = "B Ph PF";
		public static final String IMPORT_KWH = "Import(Kwh)";
		public static final String EXPORT_KWH = "Export(Kwh)";
		public static final String TAMPER_COUNT = "Tamper Count";


	}
	
	
	// event push
	public final class EventsPushFields{
		public static final String METER_SNO = "Meter S.No.";
		public static final String METER_DATETIME = "Meter Date Time";
		public static final String DATETIME = "Mdas Date Time";
		public static final String DATA = "Data";
        public static final String COMMAND_TYPE = "Event Type";
		public static final String EVENT_STATUS_WORD = "Event Status Word";

	}
	
	/**
	 * @author Krishna
	 *
	 */
	public final class CmdName
	{
		public static final String BILLING = "Billing";
		public static final String INSTAN = "Instant";
		public static final String EVENTS = "Events";
		public static final String CONNECT = "Connect";
		public static final String DISCONNECT = "Disconnect";
		public static final String DAILY_LP = "DailyLP";
		public static final String DELTA_LP = "DeltaLP";
		public static final String LAST_COMM = "LastComm";
	}
	/**
	 * @author Krishna
	 *
	 */
	public final class DevType
	{
		public static final String ALL = "All";
		public static final String SINGLE = "Single Phase";
		public static final String THREE = "Three Phase";
		public static final String HT= "HT Meter";
		public static final String CT = "CT Meter";
	}
	public final class Secret
	{
		public static final String secretKey="asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf41";
		//public static final String T
	}
	public final class ResMessage{
		public static final String INVALID_REQ = "Please provide valid inputs";
		public static final String INVALID_STR = "You are sending garbage string in inputs";
		public static final String INVALID_CHARS = "Please provide valid characters in inputs";
		public static final String DATA_NOT_FOUNT = "Data Not Found";
		public static final String BAD_REQ = "Bad Request";
		public static final String NOT_SUPPORTED = "This level is not supported";
		public static final String INVALID_DATE = "Please check the dates";
		public static final String INVALID_LAT_LONG = "Please provide valid lattitude/longitude";
		public static final String SUCCESS_CODE_200 = "200";
		public static final String FAIL_CODE_404 = "404";
		public static final String SUCCESSFULLY = "successfully";
		public static final String UNAUTHORIZE_403 = "403";
		
	
	}
	// dashBoard
	public final class DashBoardField
	{
		public static final String DEVICE = "Device";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "Longitude";
		public static final String DEVICE_TYPE = "Ddevice Type";
		public static final String STATUS = "Status";
		public static final String CONSUMER_NAME = "Consumer Name";
		public static final String SUBDIVISION = "Subdivision";
		public static final String FEEDER = "Feeder";
		public static final String SUBSTATION = "Substation";
		public static final String DT = "Dt";
		public static final String METERTYPE = "Meter Type";
		public static final String NICIP = "Nic Ip";
		public static final String CRN = "Crn";
 
	}
	
	
	
}
