package com.github.randomcodeorg.ppplugin.data.maven;

import org.apache.maven.plugin.logging.Log;

import com.github.randomcodeorg.ppplugin.data.BuildLog;

public class MavenBuildLog implements BuildLog {

	private final Log log;
	
	public MavenBuildLog(Log log) {
		this.log = log;
	}

	public void debug(String message) {
		log.debug(message);
	}

	public void debug(Throwable th) {
		log.debug(th);
	}

	public void warn(String message) {
		log.warn(message);
	}

	public void warn(Throwable th) {
		log.warn(th);
	}

	public void error(String message) {
		log.error(message);
	}

	public void error(Throwable th) {
		log.error(th);
	}

	public void info(String message) {
		log.info(message);
	}

	public void info(Throwable th) {
		log.info(th);
	}

}
