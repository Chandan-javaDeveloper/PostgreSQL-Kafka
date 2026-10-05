package com.jne.response;


import com.fasterxml.jackson.annotation.JsonProperty;

public class DeltaLPRes {

	@JsonProperty("Meter S.No.")
	private String deviceSno;

	@JsonProperty("MDAS Date Time")
	private String mdastDatetime;
	
	@JsonProperty("Interval Date Time")
	private String intervalDatetime;
	
	 @JsonProperty("R Ph Current")
	    private String rPhCurrent;

	    @JsonProperty("Y Ph Current")
	    private String yPhCurrent;

	    @JsonProperty("B Ph Current")
	    private String bPhCurrent;

	    @JsonProperty("R Ph Voltage")
	    private String rPhVoltage;

	    @JsonProperty("Y Ph Voltage")
	    private String yPhVoltage;

	    @JsonProperty("B Ph Voltage")
	    private String bPhVoltage;
	
	@JsonProperty("Avg. Current")
	private double avgCurrent;

	@JsonProperty("Avg. Voltage")
	private double avgVoltage;
	
	@JsonProperty("Block Energy Import(Kwh)")
	private double blockEnergyImportKwh;

	@JsonProperty("Block Energy Import(Kvah)")
	private double blockEnergyImportKvah;

	@JsonProperty("Block Energy Export(Kwh)")
	private double blockEnergyExportKwh;

	@JsonProperty("Block Energy Export(Kvah)")
	private double blockEnergyExportKvah;
	
	@JsonProperty("Avg. Signal Strength")
	private double avgSignalStrength;
	
	@JsonProperty("Block Kvarh Export Lag")
	private double blockKvarhExportLag;
	
	@JsonProperty("Block Kvarh ExportLead")
	private double blockKvarhExportLead;

	public double getBlockKvarhExportLag() {
		return blockKvarhExportLag;
	}

	public void setBlockKvarhExportLag(double blockKvarhExportLag) {
		this.blockKvarhExportLag = blockKvarhExportLag;
	}

	public double getBlockKvarhExportLead() {
		return blockKvarhExportLead;
	}

	public void setBlockKvarhExportLead(double blockKvarhExportLead) {
		this.blockKvarhExportLead = blockKvarhExportLead;
	}

	public double getAvgSignalStrength() {
		return avgSignalStrength;
	}

	public void setAvgSignalStrength(double avgSignalStrength) {
		this.avgSignalStrength = avgSignalStrength;
	}

	public String getDeviceSno() {
		return deviceSno;
	}

	public void setDeviceSno(String deviceSno) {
		this.deviceSno = deviceSno;
	}

	public String getMdastDatetime() {
		return mdastDatetime;
	}

	public void setMdastDatetime(String mdastDatetime) {
		this.mdastDatetime = mdastDatetime;
	}

	public String getIntervalDatetime() {
		return intervalDatetime;
	}

	public void setIntervalDatetime(String intervalDatetime) {
		this.intervalDatetime = intervalDatetime;
	}

	public double getAvgCurrent() {
		return avgCurrent;
	}

	public void setAvgCurrent(double avgCurrent) {
		this.avgCurrent = avgCurrent;
	}

	public double getAvgVoltage() {
		return avgVoltage;
	}

	public void setAvgVoltage(double avgVoltage) {
		this.avgVoltage = avgVoltage;
	}

	public double getBlockEnergyImportKwh() {
		return blockEnergyImportKwh;
	}

	public void setBlockEnergyImportKwh(double blockEnergyImportKwh) {
		this.blockEnergyImportKwh = blockEnergyImportKwh;
	}

	public double getBlockEnergyImportKvah() {
		return blockEnergyImportKvah;
	}

	public void setBlockEnergyImportKvah(double blockEnergyImportKvah) {
		this.blockEnergyImportKvah = blockEnergyImportKvah;
	}

	public double getBlockEnergyExportKwh() {
		return blockEnergyExportKwh;
	}

	public void setBlockEnergyExportKwh(double blockEnergyExportKwh) {
		this.blockEnergyExportKwh = blockEnergyExportKwh;
	}

	public double getBlockEnergyExportKvah() {
		return blockEnergyExportKvah;
	}

	public void setBlockEnergyExportKvah(double blockEnergyExportKvah) {
		this.blockEnergyExportKvah = blockEnergyExportKvah;
	}

	public String getrPhCurrent() {
		return rPhCurrent;
	}

	public void setrPhCurrent(String rPhCurrent) {
		this.rPhCurrent = rPhCurrent;
	}

	public String getyPhCurrent() {
		return yPhCurrent;
	}

	public void setyPhCurrent(String yPhCurrent) {
		this.yPhCurrent = yPhCurrent;
	}

	public String getbPhCurrent() {
		return bPhCurrent;
	}

	public void setbPhCurrent(String bPhCurrent) {
		this.bPhCurrent = bPhCurrent;
	}

	public String getrPhVoltage() {
		return rPhVoltage;
	}

	public void setrPhVoltage(String rPhVoltage) {
		this.rPhVoltage = rPhVoltage;
	}

	public String getyPhVoltage() {
		return yPhVoltage;
	}

	public void setyPhVoltage(String yPhVoltage) {
		this.yPhVoltage = yPhVoltage;
	}

	public String getbPhVoltage() {
		return bPhVoltage;
	}

	public void setbPhVoltage(String bPhVoltage) {
		this.bPhVoltage = bPhVoltage;
	}

	
}
