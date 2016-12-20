package com.github.randomcodeorg.ppplugin.data;

import java.util.List;

public interface BuildDataSource {

	BuildLog getLog();
	String getProjectBuildDir();
	String getCompiledClassesDir();
	ProjectData getProject();
	List<String> getDeclaredProcessors();
	
	
}
