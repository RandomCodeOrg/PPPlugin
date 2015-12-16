package com.github.randomcodeorg.ppplugin.data.maven;

import com.github.randomcodeorg.ppplugin.PostProcessMojo;
import com.github.randomcodeorg.ppplugin.data.BuildDataSource;
import com.github.randomcodeorg.ppplugin.data.BuildLog;
import com.github.randomcodeorg.ppplugin.data.ProjectData;

public class MavenBuildDataSource implements BuildDataSource{

	private final PostProcessMojo mojo;
	private final BuildLog log;
	private final ProjectData projectData;
	
	public MavenBuildDataSource(PostProcessMojo mojo) {
		this.mojo = mojo;
		this.log = new MavenBuildLog(mojo.getLog());
		this.projectData = new MavenProjectData(mojo.getProject());
	}

	public BuildLog getLog() {
		return log;
	}

	public String getProjectBuildDir() {
		return mojo.getProjectBuildDir();
	}

	public String getCompiledClassesDir() {
		return mojo.getCompiledClassesDir();
	}

	public ProjectData getProject() {
		return projectData;
	}

}
