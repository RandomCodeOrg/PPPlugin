package com.github.randomcodeorg.ppplugin.internals;

import java.io.File;

import org.apache.maven.plugin.logging.Log;

public class ClassLoadingErrorHandler implements ErrorHandler<Throwable, File> {

	private final Log log;
	
	public ClassLoadingErrorHandler(Log log) {
		this.log = log;
	}

	public boolean handleError(Throwable e, File f) {
		log.debug(e);
		log.warn(String.format("Could not load the class corresponding to the given file '%s'", f.getAbsolutePath()));
		return true;
	}

}
