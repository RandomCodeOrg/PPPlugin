package com.github.randomcodeorg.ppplugin.data.maven;

import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import com.github.randomcodeorg.ppplugin.data.DependencyResolutionException;
import com.github.randomcodeorg.ppplugin.data.ProjectData;

public class MavenProjectData implements ProjectData {

	private final MavenProject project;

	public MavenProjectData(MavenProject project) {
		this.project = project;
	}

	public List<String> getTestClasspathElements() throws DependencyResolutionException {
		try {
			return project.getTestClasspathElements();
		} catch (DependencyResolutionRequiredException e) {
			throw new DependencyResolutionException(e);
		}
	}

	public List<String> getRuntimeClasspathElements() throws DependencyResolutionException {
		try {
			return project.getRuntimeClasspathElements();
		} catch (DependencyResolutionRequiredException e) {
			throw new DependencyResolutionException(e);
		}
	}

	public List<String> getCompileClasspathElements() throws DependencyResolutionException {
		try {
			return project.getCompileClasspathElements();
		} catch (DependencyResolutionRequiredException e) {
			throw new DependencyResolutionException(e);
		}
	}

}
