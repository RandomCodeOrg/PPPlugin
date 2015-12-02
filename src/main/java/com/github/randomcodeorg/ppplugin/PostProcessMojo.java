package com.github.randomcodeorg.ppplugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "postprocess", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PostProcessMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
	private String projectBuildDir;
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			execute(String.format("%s%s%s", projectBuildDir, File.separator, "classes"));
		} catch (Throwable e) {
			getLog().error(e);
			throw new MojoFailureException("An exception occured during the execution of this plugin. See the log output for exception details.");
		}
	}

	@SuppressWarnings("unchecked")
	private void execute(String path) throws MalformedURLException, InstantiationException, IllegalAccessException, DependencyResolutionRequiredException {
		getLog().info(String.format("Searching for processors in %s...", path));
		File root = new File(path);
		List<File> classFiles = new ArrayList<File>();
		search(root, classFiles);
		String rootPath = root.getAbsolutePath();
		String className;
		List<String> failedClasses = new ArrayList<String>();
		@SuppressWarnings("resource")
		URLClassLoader cl = new URLClassLoader(new URL[] { root.toPath().toUri().toURL() },
				getDependcyURLs());
		HashMap<Class<?>, File> classFileSet = new HashMap<Class<?>, File>();
		Class<?> someClass;
		List<Class<PProcessor>> processors = new ArrayList<Class<PProcessor>>();
		for (File cF : classFiles) {
			className = cF.getAbsolutePath().substring(rootPath.length() + 1, cF.getAbsolutePath().length() - 6)
					.replace("/", ".").replace("\\", ".");
			try {
				someClass = cl.loadClass(className);
				classFileSet.put(someClass, cF);
				if (!PProcessor.class.isAssignableFrom(someClass))
					continue;
			} catch (ClassNotFoundException e) {
				failedClasses.add(className);
				getLog().warn(String.format(
						"Could not load class %s because it or a referenced class could not be loaded (%s)", className,
						e.getMessage()));
				continue;
			} catch (NoClassDefFoundError e) {
				failedClasses.add(className);
				getLog().warn(String.format(
						"Could not load class %s because it or a referenced class could not be loaded (%s)", className,
						e.getMessage()));
				continue;
			}
			Class<PProcessor> pp = (Class<PProcessor>) someClass;
			if (!isValid(pp))
				continue;
			processors.add(pp);
			getLog().info(String.format("Found: %s (%s)", className, cF.getAbsolutePath()));
		}
		PContext pc = new PContext(getLog(), root, getAllClasspaths(), classFileSet.keySet(), classFileSet);
		invokeProcessors(processors, pc);
		try {
			pc.complete();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ClassLoader getDependcyURLs() throws DependencyResolutionRequiredException, MalformedURLException{
		Set<URL> urls = new HashSet<URL>();
	    List<String> elements = getAllClasspaths();
	    for (String element : elements) {
	        urls.add(new File(element).toURI().toURL());
	    }
	    return new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
	}
	
	private List<String> getAllClasspaths() throws DependencyResolutionRequiredException{
		List<String> all = new ArrayList<String>();
		all.addAll(project.getTestClasspathElements());
		all.addAll(project.getRuntimeClasspathElements());
		all.addAll(project.getCompileClasspathElements());
		return all;
	}
	
	
	
	private void invokeProcessors(Iterable<Class<PProcessor>> processors, PContext context)
			throws InstantiationException, IllegalAccessException {
		PProcessor processor;
		for (Class<PProcessor> pType : processors) {
			getLog().info(String.format("Invoking %s", pType.getName()));
			processor = pType.newInstance();
			processor.init(context);
			processor.run(context);
		}
	}

	private boolean isValid(Class<PProcessor> type) {
		boolean result = !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
		if (result) {
			try {
				Constructor<?> constr = type.getConstructor();
				result = constr != null;
			} catch (Throwable e) {
				e.printStackTrace();
				result = false;
			}
		}
		if (!result) {
			getLog().warn(String.format("Ignoring processor: %s", type.getCanonicalName()));
		}
		return result;
	}

	private void search(File parent, List<File> classFiles) {
		if (parent == null || !parent.exists() || !parent.isDirectory())
			return;
		for (File f : parent.listFiles()) {
			if (f.exists() && f.isFile() && f.getName().endsWith(".class")) {
				classFiles.add(f);
			}
			if (f.isDirectory())
				search(f, classFiles);
		}
	}

}
