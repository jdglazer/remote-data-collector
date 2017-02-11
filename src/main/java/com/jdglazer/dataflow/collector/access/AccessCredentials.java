/**
 * A class to hold access credentials needed to use datasource
 */
package com.jdglazer.remote.dataflow.access;

import java.io.Serializable;

import com.jdglazer.remote.dataflow.DataSource;

public class AccessCredentials implements Serializable {
	
	private DataSource.Protocol protocol;
	
	public void setProtocol( DataSource.Protocol protocol ) {
		this.protocol = protocol;
	}
	
	public DataSource.Protocol getProtocol() {
		return protocol;
	}

}