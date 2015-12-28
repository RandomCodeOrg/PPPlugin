package com.github.randomcodeorg.ppplugin.data;

public interface BuildDataSource {

	BuildLog getLog();
	String getProjectBuildDir();
	String getCompiledClassesDir();
	ProjectData getProject();
	boolean getThrowExceptionOnCyclicProcessorDependencies();
	
}
