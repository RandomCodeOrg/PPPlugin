package com.github.masinger.ppplugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;

import com.github.masinger.ppplugin.internals.ConnectedStreams;

/**
 * Contains information about the compiled sources and classes.
 * @author Marcel Singer
 *
 */
public class PContext {

	private final Set<Class<?>> classes;
	private final Map<Class<?>, File> classFileMap;
	private final Log log;
	private final File classRoot;
	private final Map<Class<?>, InputStream> modifications = new HashMap<Class<?>, InputStream>();
	private final List<String> classPaths;

	PContext(final Log logger, File classRoot, List<String> classPaths, Set<Class<?>> classes, Map<Class<?>, File> classFileMap) {
		this.classes = Collections.unmodifiableSet(classes);
		this.classFileMap = classFileMap;
		this.log = logger;
		this.classRoot = classRoot;
		this.classPaths = Collections.unmodifiableList(classPaths);
	}

	/**
	 * Returns a unmodifiable set containing the projects compiled classes.
	 * @return A unmodifiable set containing the projects compiled classes.
	 */
	public Set<Class<?>> getClasses() {
		return classes;
	}

	/**
	 * Returns the file object representing the .class-file of the given class.
	 * @param c The class thats corresponding file should be returned.
	 * @return The file object representing the .class-file of the given class or {@code null} if there is none.
	 */
	public File getFile(Class<?> c) {
		if (classFileMap.containsKey(c))
			return classFileMap.get(c);
		else
			return null;
	}

	/**
	 * Returns the root directory of all compiled classes.
	 * @return The root directory of all compiled classes.
	 */
	public File getClassRoot() {
		return classRoot;
	}

	/**
	 * Returns the maven build log.
	 * @return The maven build log.
	 */
	public Log getLog() {
		return log;
	}

	/**
	 * Marks the .class-file representing the given class as edited and returns an {@link OutputStream} that will hold the new data.
	 * <b>Note</b>: Calling this method a second time will discard all previous changes.
	 * @param cl The class thats corresponding .class-file should be overwritten. 
	 * @return An output stream that will hold the new data.
	 */
	public OutputStream modify(Class<?> cl) {
		ConnectedStreams cs = new ConnectedStreams();
		modifications.put(cl, cs);
		return cs.getOut();
	}

	/**
	 * Returns an unmodifiable set containing all additional class paths. This includes compile-, test- and runtime-scoped libraries/dependencies.
	 * @return An unmodifiable set containing all additional class paths. This includes compile-, test- and runtime-scoped libraries/dependencies.
	 */
	public List<String> getClassPaths(){
		return classPaths;
	}
	
	
	void complete() throws IOException {
		for (Class<?> cl : modifications.keySet()) {
			InputStream in = modifications.get(cl);
			File f = getFile(cl);
			FileOutputStream fos = new FileOutputStream(f, false);
			IOUtil.copy(in, fos);
			fos.flush();
			fos.close();
			in.close();
		}
	}

}
