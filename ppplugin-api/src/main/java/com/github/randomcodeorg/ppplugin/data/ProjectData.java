package com.github.randomcodeorg.ppplugin.data;

import java.util.List;

public interface ProjectData {
	
	List<String> getTestClasspathElements() throws DependencyResolutionException;
	List<String> getRuntimeClasspathElements() throws DependencyResolutionException;
	List<String> getCompileClasspathElements() throws DependencyResolutionException;

}
