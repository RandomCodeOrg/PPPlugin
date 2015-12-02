package com.github.randomcodeorg.ppplugin.internals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedStreams extends InputStream {

	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private ByteArrayInputStream in;
	
	public ConnectedStreams() {
		
	}
	
	public OutputStream getOut(){
		return baos;
	}

	@Override
	public int read() throws IOException {
		if(in == null) in = new ByteArrayInputStream(baos.toByteArray());
		return in.read();
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		in.close();
	}
	
	public void reset(){
		in = null;
	}
	
	

}
