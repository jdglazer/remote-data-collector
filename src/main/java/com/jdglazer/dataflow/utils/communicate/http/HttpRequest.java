/**
 * An object that supports one http connection at a time
 */

package com.jdglazer.dataflow.utils.communicate.http;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jdglazer.dataflow.utils.communicate.Request;
import com.jdglazer.dataflow.utils.communicate.TypeConversion;
import com.jdglazer.dataflow.utils.communicate.http.HttpRequestParams.RequestType;

public class HttpRequest extends Request implements HttpResponse {
	
	Logger logger = LoggerFactory.getLogger(HttpRequest.class);
	
	private HttpRequestParams params;
	private HttpURLConnection connection;
	private InputStream httpInput;
	private long responseLength;
	private byte [] inputBuffer;
	private volatile long inputProgress;
	private boolean keepDownloading = true;
	private boolean running = false;
	
	public HttpRequest( HttpRequestParams params ) {
		super( HttpRequest.class.getName() );
		this.params = params;
		inputBuffer = new byte[1000];
	}
	
	protected boolean buildConnection() throws IOException {	
		try {
			//we now need to set the request method
			RequestType type = params.getRequestType();
			//if we are using post method, we need to do some extra work to set post variables
			if( type.equals(RequestType.POST) ) {
				//we pass post variables in output stream
				connection.setDoOutput(true);
				//creates a valid string for post variables (substring(1) included to exclude the ? character returned by formatHttpURL)
				byte [] posts = HttpRequestParams.formatHttpURL("", params.getPostGetVars() ).substring(1).getBytes();
				//set post variables
				connection.getOutputStream().write( posts );
			}
			connection.setRequestMethod( type.toString() );
			
			responseLength = connection.getContentLength();
			httpInput = connection.getInputStream();
			return true;
		} catch( IOException ioe) {
			logger.error("Could not build a connection to the location "+params.getRequestURL() );
			return false;
		}
	}

	public synchronized byte[] readAsByteArray() {
		running = true;
		try {
			if( connection == null ) {
				setConnection( (HttpURLConnection) params.getRequestURL().openConnection() );
				buildConnection();
			}
			
			responseLength = connection.getContentLength();
			inputProgress = params.getStartReadOffset();
			
			if( responseLength > 0 ) {
				byte [] byteOutput = new byte[ (int) responseLength ];
				int readLen = 0;
				
				while( (readLen = httpInput.read( inputBuffer ) ) >= 0 && keepDownloading ) {
					System.arraycopy(inputBuffer, 0, byteOutput, (int) inputProgress, readLen);
					inputProgress += readLen;
				}
				
				return byteOutput;
			}
			else {
				logger.warn( "Null response from "+params.getRequestURL() );
			}
		} catch( IOException ioe) {
			try {
				logger.error("Error reading from "+params.getRequestURL() );
			} catch (MalformedURLException e) {
			}
		}
		running = false;
		return null;
	}
	
	public synchronized String readAsString() {
		running = true;
		String content = null;
		try {
			if( connection == null ) {
				setConnection ( (HttpURLConnection) params.getRequestURL().openConnection() );
				buildConnection();
			}
			responseLength = connection.getContentLength();
			inputProgress = 0;
			httpInput = connection.getInputStream();
			
			int readLen = 0;
			content = "";
			
			while( ( readLen = httpInput.read( inputBuffer ) ) >= 0 && keepDownloading ) {
				inputProgress += readLen;
				content += TypeConversion.byteArrayToString(
							inputBuffer.length == readLen ? 
									inputBuffer : Arrays.copyOf(inputBuffer, readLen) 
							);
			}
			
		} catch( IOException ioe ) {
			try {
				logger.error("Failed to read from "+params.getRequestURL());
			} catch (MalformedURLException e) {
			}
		}
		running = false;
		return content;
	}
	
	public synchronized boolean readIntoFile( File file ) {
		running = true;
		try {
			if( connection == null ) {
				setConnection( (HttpURLConnection) params.getRequestURL().openConnection() );
				buildConnection();
			}
			responseLength = connection.getContentLength();
			inputProgress = 0;
			httpInput = connection.getInputStream();
			
			int readLen = 0;
			
			while( ( readLen = httpInput.read( inputBuffer ) ) >= 0 && keepDownloading ) {
				inputProgress += readLen;
				FileUtils.writeByteArrayToFile(file, inputBuffer, true);
			}	
			
			running = false;
		} catch( IOException ioe ) {
			try {
				logger.error("Could not read from "+params.getRequestURL()+" to file "+file);
			} catch (MalformedURLException e) {	}
			return false;
		}
	
		return true;
	}
	
	public void stop() {
		keepDownloading = false;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public HttpRequestParams getUpdatedHttpRequestParams() {
		try{
			return (new HttpRequestParams())
					.setRequestURL( params.getRequestURL().toString() )
					.setRequestType( params.getRequestType() )
					.setStartReadOffset( inputProgress );
		}
		catch( MalformedURLException mue ) {
			logger.error( "Invalid url. Failed to fetch new RequestParams object");
			return null;			
		}
	}
	
	public long getHttpInputSize() {
		return connection == null ?
			-1 :
			responseLength;
	}
	
	public long getHttpInputProgress() {
		return connection == null ?
				-1 : 
				inputProgress;
	}
	
	protected HttpRequestParams getParams() {
		return params;
	}
	
	protected void setConnection( HttpURLConnection connection ) {
		this.connection = connection;
	}
	
	protected HttpURLConnection getConnection() {
		return connection;
	}
}
