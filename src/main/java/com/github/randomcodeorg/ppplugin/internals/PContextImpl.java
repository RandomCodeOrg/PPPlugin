package com.github.randomcodeorg.ppplugin.internals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.randomcodeorg.ppplugin.PContext;
import com.github.randomcodeorg.ppplugin.PProcessor;
import com.github.randomcodeorg.ppplugin.data.BuildLog;

class PContextImpl extends PContext {

	PContextImpl(BuildLog logger, File classRoot, List<String> classPaths, Set<Class<?>> classes,
			Map<Class<?>, File> classFileMap, Set<Class<? extends PProcessor>> declaredProcessors) {
		super(logger, classRoot, classPaths, classes, classFileMap, declaredProcessors);
	}

	@Override
	public void complete() throws IOException {
		super.complete();
	}

}
