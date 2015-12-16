package com.github.randomcodeorg.ppplugin.internals;

import java.io.File;

import com.github.randomcodeorg.ppplugin.data.BuildLog;

public class ClassLoadingErrorHandler implements ErrorHandler<Throwable, File> {

	private final BuildLog log;

	public ClassLoadingErrorHandler(BuildLog log) {
		this.log = log;
	}

	public boolean handleError(Throwable e, File f) {
		log.debug(e);
		log.warn(String.format("Could not load the class corresponding to the given file '%s'", f.getAbsolutePath()));
		return true;
	}

}
