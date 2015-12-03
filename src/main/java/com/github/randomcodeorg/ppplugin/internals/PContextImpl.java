package com.github.randomcodeorg.ppplugin.internals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;

import com.github.randomcodeorg.ppplugin.PContext;

class PContextImpl extends PContext {

	PContextImpl(Log logger, File classRoot, List<String> classPaths, Set<Class<?>> classes,
			Map<Class<?>, File> classFileMap) {
		super(logger, classRoot, classPaths, classes, classFileMap);
	}

	
	@Override
	public void complete() throws IOException {
		super.complete();
	}

}
