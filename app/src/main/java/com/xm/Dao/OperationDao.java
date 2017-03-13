package com.xm.Dao;

import java.io.Serializable;
import java.net.Socket;

public class OperationDao implements Serializable{
	private Socket socket;
	private String ID;
	private String currentAction;
	private String state;
	private String plantname;
	private String inflatetimesurplus;
	private String holdtimesurplus;
	private String bleedtimesurplus;
	private String timessurplus;
	private String cycletimesurplus;
	private String inflatetimetotal;
	private String holdtimetotal;
	private String bleedtimetotal;
	private String timestotal;
	private String cycletimetotal;
	public OperationDao() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public String getCurrentAction() {
		return currentAction;
	}
	public void setCurrentAction(String currentAction) {
		this.currentAction = currentAction;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public String getPlantname() {
		return plantname;
	}
	public void setPlantname(String plantname) {
		this.plantname = plantname;
	}
	public String getID() {
		return ID;
	}
	public void setID(String machineid) {
		ID = machineid;
	}
	public String getInflatetimesurplus() {
		return inflatetimesurplus;
	}
	public void setInflatetimesurplus(String inflatetimesurplus) {
		this.inflatetimesurplus = inflatetimesurplus;
	}
	public String getHoldtimesurplus() {
		return holdtimesurplus;
	}
	public void setHoldtimesurplus(String holdtimesurplus) {
		this.holdtimesurplus = holdtimesurplus;
	}
	public String getBleedtimesurplus() {
		return bleedtimesurplus;
	}
	public void setBleedtimesurplus(String bleedtimesurplus) {
		this.bleedtimesurplus = bleedtimesurplus;
	}
	public String getTimessurplus() {
		return timessurplus;
	}
	public void setTimessurplus(String timessurplus) {
		this.timessurplus = timessurplus;
	}
	public String getCycletimesurplus() {
		return cycletimesurplus;
	}
	public void setCycletimesurplus(String cycletimesurplus) {
		this.cycletimesurplus = cycletimesurplus;
	}
	public String getInflatetimetotal() {
		return inflatetimetotal;
	}
	public void setInflatetimetotal(String inflatetimetotal) {
		this.inflatetimetotal = inflatetimetotal;
	}
	public String getHoldtimetotal() {
		return holdtimetotal;
	}
	public void setHoldtimetotal(String holdtimetotal) {
		this.holdtimetotal = holdtimetotal;
	}
	public String getBleedtimetotal() {
		return bleedtimetotal;
	}
	public void setBleedtimetotal(String bleedtimetotal) {
		this.bleedtimetotal = bleedtimetotal;
	}
	public String getTimestotal() {
		return timestotal;
	}
	public void setTimestotal(String timestotal) {
		this.timestotal = timestotal;
	}
	public String getCycletimetotal() {
		return cycletimetotal;
	}
	public void setCycletimetotal(String cycletimetotal) {
		this.cycletimetotal = cycletimetotal;
	}
	
}
