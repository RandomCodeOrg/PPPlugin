package com.github.randomcodeorg.ppplugin.internals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.util.IOUtil;

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

	@Override
	protected OutputStream doModify(Class<?> cl, Map<Class<?>, InputStream> modifications) {
		ConnectedStreams cs = new ConnectedStreams();
		modifications.put(cl, cs);
		return cs.getOut();
	}
	
	@Override
	protected void doCopy(InputStream in, OutputStream out) throws IOException {
		IOUtil.copy(in, out);
	}

}
