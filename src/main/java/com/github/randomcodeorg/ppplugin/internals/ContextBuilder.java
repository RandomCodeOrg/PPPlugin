package com.github.randomcodeorg.ppplugin.internals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.randomcodeorg.ppplugin.PProcessor;
import com.github.randomcodeorg.ppplugin.data.BuildDataSource;
import com.github.randomcodeorg.ppplugin.data.BuildLog;
import com.github.randomcodeorg.ppplugin.data.DependencyResolutionException;
import com.github.randomcodeorg.ppplugin.data.ProjectData;

class ContextBuilder {

	private File buildRoot;
	private File compilationResultsRoot;
	private Set<String> fixedClassPathEntries = new HashSet<String>();
	private Set<URL> fixedClassPaths = new HashSet<URL>();
	private URL[] fixedClassPathsArray;
	private static final String DEFAULT_COMPILED_CLASSES_SUB = "classes";
	private boolean initialized = false;
	private BuildLog log;
	private List<String> declaredProcessors;

	public ContextBuilder() {

	}

	public void init(BuildDataSource dataSource)
			throws FileNotFoundException, IOException, DependencyResolutionException {
		if (initialized)
			return;
		declaredProcessors = dataSource.getDeclaredProcessors();
		setupDirectories(dataSource);
		log = dataSource.getLog();
		setupFixedClassPathEntries(dataSource);
		initialized = true;
	}

	public PContextImpl createContext(BuildLog log, ClassLoader parentLoader,
			ErrorHandler<? super Throwable, ? super File> handler)
			throws ClassNotFoundException, MalformedURLException {
		Set<File> classFiles = createClassFilesSet();
		ClassLoader loader = createInitializationClassLoader(parentLoader);
		Map<Class<?>, File> classMap = createClassFileMap(loader, classFiles, handler);
		List<String> classPathList = new ArrayList<String>();
		classPathList.addAll(fixedClassPathEntries);
		Set<Class<? extends PProcessor>> declaredProcessors = buildDeclaredProcessors(loader, null); // TODO:
																										// Edit
		PContextImpl result = new PContextImpl(log, compilationResultsRoot, classPathList, classMap.keySet(), classMap,
				declaredProcessors);
		return result;
	}

	@SuppressWarnings("unchecked")
	public Set<Class<? extends PProcessor>> buildDeclaredProcessors(ClassLoader initializationClassLoader,
			ErrorHandler<? super Throwable, ? super String> handler) throws ClassNotFoundException {
		Set<Class<? extends PProcessor>> result = new HashSet<Class<? extends PProcessor>>();
		if (declaredProcessors.isEmpty())
			return result;
		log.info("Searching for explicit declared processors...");
		Class<PProcessor> expectedClass = PProcessor.class;
		Class<?> tmp;
		for (String declared : declaredProcessors) {
			log.debug(String.format("Searching class for declared processor with the given name: %s", declared));
			try {
				tmp = initializationClassLoader.loadClass(declared);
				if (expectedClass.isAssignableFrom(tmp)) {
					result.add((Class<? extends PProcessor>) tmp);
				} else {
					throw new RuntimeException(String.format(
							"The declared processor '%s' does not implement the PProcessor interface.", declared));
				}
			} catch (RuntimeException e) {
				executeHandler(handler, e, declared);
			} catch (ClassNotFoundException e) {
				executeHandler(handler, e, declared);
			} catch (NoClassDefFoundError e) {
				executeHandler(handler, e, declared);
			}
		}
		return result;
	}

	public Map<Class<?>, File> createClassFileMap(ClassLoader loader, Set<File> classFileSet,
			ErrorHandler<? super Throwable, ? super File> handler) throws ClassNotFoundException {
		Map<Class<?>, File> result = new HashMap<Class<?>, File>();
		String className;
		Class<?> someClass;
		for (File f : classFileSet) {
			try {
				className = f.getAbsolutePath().substring(compilationResultsRoot.getAbsolutePath().length() + 1,
						f.getAbsolutePath().length() - 6).replace("/", ".").replace("\\", ".");
				log.debug(String.format("Loading class '%s'", className));
				someClass = loader.loadClass(className);
				someClass.getCanonicalName();
				result.put(someClass, f);
			} catch (RuntimeException e) {
				executeHandler(handler, e, f);
			} catch (ClassNotFoundException e) {
				executeHandler(handler, e, f);
			} catch (NoClassDefFoundError e) {
				executeHandler(handler, e, f);
			}
		}
		return result;
	}

	private <T extends Throwable, S> void executeHandler(ErrorHandler<? super T, ? super S> handler, T e, S extra)
			throws T {
		if (handler == null)
			throw e;
		if (!handler.handleError(e, extra))
			throw e;
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

	private void setupFixedClassPathEntries(BuildDataSource dataSource)
			throws DependencyResolutionException, MalformedURLException {
		ProjectData project = dataSource.getProject();
		fixedClassPathEntries.addAll(project.getTestClasspathElements());
		fixedClassPathEntries.addAll(project.getRuntimeClasspathElements());
		fixedClassPathEntries.addAll(project.getCompileClasspathElements());
		for (String path : fixedClassPathEntries) {
			fixedClassPaths.add(new File(path).toURI().toURL());
		}
		fixedClassPathsArray = fixedClassPaths.toArray(new URL[fixedClassPaths.size()]);
	}

	private void setupDirectories(BuildDataSource dataSource) throws FileNotFoundException, IOException {
		buildRoot = new File(dataSource.getProjectBuildDir());
		if (!buildRoot.exists())
			throw new FileNotFoundException(
					String.format("The build directory '%s' does not exist", dataSource.getProjectBuildDir()));
		if (!buildRoot.isDirectory())
			throw new IOException(
					String.format("The build path '%s' is not a directory", dataSource.getProjectBuildDir()));
		String compiledClassesDir = dataSource.getCompiledClassesDir();
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
