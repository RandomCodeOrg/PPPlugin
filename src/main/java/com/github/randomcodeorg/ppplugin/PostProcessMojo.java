package com.github.randomcodeorg.ppplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.github.randomcodeorg.ppplugin.data.maven.MavenBuildDataSource;
import com.github.randomcodeorg.ppplugin.internals.InternalInvoker;

@Mojo(name = "postprocess", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PostProcessMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
	private String projectBuildDir;
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			new InternalInvoker(new MavenBuildDataSource(this)).invoke();
		} catch (Throwable e) {
			getLog().error(e);
			throw new MojoFailureException(
					"An exception occured during the execution of this plugin. See the log output for exception details.");
		}
	}

	public String getProjectBuildDir() {
		return projectBuildDir;
	}

	public MavenProject getProject() {
		return project;
	}

	public String getCompiledClassesDir() {
		return null;
	}

	@Override
	public Log getLog() {
		return super.getLog();
	}

}
