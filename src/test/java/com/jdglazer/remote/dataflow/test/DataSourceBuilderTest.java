package com.jdglazer.remote.dataflow.test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.jdglazer.remote.dataflow.DataSource;
import com.jdglazer.remote.dataflow.DataSourceBuilder;
import com.jdglazer.remote.dataflow.access.HTTPAccess;
import com.jdglazer.remote.dataflow.access.SSHAccess;
import com.jdglazer.remote.dataflow.access.SocketAccess;

public class DataSourceBuilderTest {
	
	public static void main( String [] args ) throws ParserConfigurationException, SAXException, IOException {
		
		long time = System.currentTimeMillis();
		DataSourceBuilder dsb = new DataSourceBuilder( new File("C:\\Users\\rglazer\\git\\generic-library\\src\\test\\resources\\dataSourceTest.xml") );
		Map<String, DataSource> ds = dsb.build();
		
		System.out.println( "Time: "+(System.currentTimeMillis() - time)+", Name: "+ds.get("test-source").getName()+", Update Interval: "+ds.get("test-source").getUpdateInterval() );
		//System.out.println( ( (HTTPAccess) ds.getAccess() ).getAddress()+", "+( (HTTPAccess) ds.getAccess() ).getGetVars().size()+", "+( (HTTPAccess) ds.getAccess() ).getPostVars().get("usrname") ); 
		System.out.println( ds.get("test-source").getAccess().getProtocol() );
		
	}
}
