package com.github.randomcodeorg.ppplugin.data;

public interface BuildLog {

	void debug(String message);
	void debug(Throwable th);

	void warn(String message);
	void warn(Throwable th);
	
	void error(String message);
	void error(Throwable th);
	
	void info(String message);
	void info(Throwable th);
}
