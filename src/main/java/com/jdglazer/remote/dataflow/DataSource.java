package com.jdglazer.remote.dataflow;

import java.io.Serializable;

import com.jdglazer.remote.dataflow.access.AccessCredentials;
import com.jdglazer.remote.dataflow.parsers.ParserModelBase;

public class DataSource implements Serializable {

	private static final long serialVersionUID = 1543980989675665607L;

	//in milliseconds
	private int updateInterval;
	
	private String name;
	
	private AccessCredentials access;
	
	private ParserModelBase datasourceParser;
	
	public int getUpdateInterval() {
		return updateInterval;
	}
	
	public String getName() {
		return name;
	}
	
	public AccessCredentials getAccess() {
		return access;
	}
	
	public ParserModelBase getDatasourceParser() {
		return datasourceParser;
	}
	
	public void setUpdateInterval( int updateInterval) {
		this.updateInterval = updateInterval;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public void setAccess( AccessCredentials access ) {
		this.access = access;
	}
	
	public void setDatasourceParser( ParserModelBase datasourceParser ) {
		this.datasourceParser = datasourceParser;
	}
	
	public enum Protocol implements Serializable {
		http, https, ssh, socket
	}
}
