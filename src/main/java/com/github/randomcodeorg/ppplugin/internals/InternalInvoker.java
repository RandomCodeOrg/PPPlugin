package com.github.randomcodeorg.ppplugin.internals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.logging.Log;

import com.github.randomcodeorg.ppplugin.PostProcessMojo;

public class InternalInvoker {

	private final PostProcessMojo mojo;
	private final ContextBuilder contextBuilder;
	
	
	public InternalInvoker(PostProcessMojo mojo) {
		this.mojo = mojo;
		this.contextBuilder = new ContextBuilder();
	}
	
	protected Log getLog(){
		return mojo.getLog();
	}
	
	public void invoke() throws FileNotFoundException, IOException, DependencyResolutionRequiredException{
		contextBuilder.init(mojo);
		ClassLoader initializationClassLoader = contextBuilder.createInitializationClassLoader(mojo.getClass().getClassLoader());
		
	}
	
	

}
