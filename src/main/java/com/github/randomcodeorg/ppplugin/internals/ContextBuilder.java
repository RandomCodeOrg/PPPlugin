package com.github.randomcodeorg.ppplugin.internals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import com.github.randomcodeorg.ppplugin.PostProcessMojo;

import javassist.expr.Handler;

class ContextBuilder {

	private File buildRoot;
	private File compilationResultsRoot;
	private Set<String> fixedClassPathEntries = new HashSet<String>();
	private Set<URL> fixedClassPaths = new HashSet<URL>();
	private URL[] fixedClassPathsArray;
	private static final String DEFAULT_COMPILED_CLASSES_SUB = "classes";
	private boolean initialized = false;

	public ContextBuilder() {

	}

	public void init(PostProcessMojo mojo)
			throws FileNotFoundException, IOException, DependencyResolutionRequiredException {
		if (initialized)
			return;
		setupDirectories(mojo);
		setupFixedClassPathEntries(mojo);
		initialized = true;
	}

	public Map<Class<?>, File> createClassFileMap(ClassLoader loader, Set<File> classFileSet, ErrorHandler<? super Throwable> handler) throws ClassNotFoundException{
		Map<Class<?>, File> result = new HashMap<Class<?>, File>();
		String className;
		Class<?> someClass;
		for(File f : classFileSet){
			try{
				className = f.getAbsolutePath().substring(compilationResultsRoot.getAbsolutePath().length() + 1, f.getAbsolutePath().length() - 6)
						.replace("/", ".").replace("\\", ".");
				someClass = loader.loadClass(className);
				result.put(someClass, f);
			}catch(RuntimeException e){
				executeHandler(handler, e);
			} catch (ClassNotFoundException e) {
				executeHandler(handler, e);
			} catch (NoClassDefFoundError e) {
				executeHandler(handler, e);
			}
		}
		return result;
	}
	
	
	
	private <T extends Throwable> void executeHandler(ErrorHandler<? super T> handler, T e) throws T{
		if(handler == null) throw e;
		if(!handler.handleError(e)) throw e;
	}
	
	public Set<File> createClassFilesSet() {
		Set<File> result = new HashSet<File>();
		searchClassFile(compilationResultsRoot, result);
		return result;
	}

	private void searchClassFile(File parent, Set<File> classFiles) {
		if (parent == null || !parent.exists() || !parent.isDirectory())
			return;
		for (File f : parent.listFiles()) {
			if (f.exists() && f.isFile() && f.getName().endsWith(".class")) {
				classFiles.add(f);
			}
			if (f.exists() && f.isDirectory())
				searchClassFile(f, classFiles);
		}
	}

	public ClassLoader createInitializationClassLoader(ClassLoader parent) throws MalformedURLException {
		return buildContextClassLoader(parent);
	}

	private ClassLoader buildContextClassLoader(ClassLoader parent) throws MalformedURLException {
		URLClassLoader cl = new URLClassLoader(new URL[] { compilationResultsRoot.toURI().toURL() },
				buildFixedClassPathLoader(parent));
		return cl;
	}

	private ClassLoader buildFixedClassPathLoader(ClassLoader parent) {
		return new URLClassLoader(fixedClassPathsArray, parent);
	}

	private void setupFixedClassPathEntries(PostProcessMojo mojo)
			throws DependencyResolutionRequiredException, MalformedURLException {
		MavenProject project = mojo.getProject();
		fixedClassPathEntries.addAll(project.getTestClasspathElements());
		fixedClassPathEntries.addAll(project.getRuntimeClasspathElements());
		fixedClassPathEntries.addAll(project.getCompileClasspathElements());
		for (String path : fixedClassPathEntries) {
			fixedClassPaths.add(new File(path).toURI().toURL());
		}
		fixedClassPathsArray = fixedClassPaths.toArray(new URL[fixedClassPaths.size()]);
	}

	private void setupDirectories(PostProcessMojo mojo) throws FileNotFoundException, IOException {
		buildRoot = new File(mojo.getProjectBuildDir());
		if (!buildRoot.exists())
			throw new FileNotFoundException(
					String.format("The build directory '%s' does not exist", mojo.getProjectBuildDir()));
		if (!buildRoot.isDirectory())
			throw new IOException(String.format("The build path '%s' is not a directory", mojo.getProjectBuildDir()));
		String compiledClassesDir = mojo.getCompiledClassesDir();
		if (compiledClassesDir == null) {
			compiledClassesDir = String.format("%s%s%s", buildRoot.getAbsolutePath(), File.separator,
					DEFAULT_COMPILED_CLASSES_SUB);
			compilationResultsRoot = new File(compiledClassesDir);
			if (!compilationResultsRoot.exists())
				throw new FileNotFoundException(
						String.format("The default class output directory '%s' was not found", compiledClassesDir));
			if (!compilationResultsRoot.isDirectory())
				throw new IOException(
						String.format("The default class output path '%s' is not a directory", compiledClassesDir));
		} else {
			compilationResultsRoot = new File(compiledClassesDir);
			if (!compilationResultsRoot.exists())
				throw new FileNotFoundException(
						String.format("The specified class output directory '%s' was not found", compiledClassesDir));
			if (!compilationResultsRoot.isDirectory())
				throw new IOException(
						String.format("The specified class output path '%s' is not a directory", compiledClassesDir));
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

}
