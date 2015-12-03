package com.github.randomcodeorg.ppplugin.internals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.logging.Log;

import com.github.randomcodeorg.ppplugin.PProcessor;
import com.github.randomcodeorg.ppplugin.PostProcessMojo;

public class InternalInvoker {

	private final PostProcessMojo mojo;
	private final ContextBuilder contextBuilder;
	private final Log log;
	private final ProcessorManager processorManager = new ProcessorManager();

	public InternalInvoker(PostProcessMojo mojo) {
		this.mojo = mojo;
		this.contextBuilder = new ContextBuilder();
		this.log = mojo.getLog();
	}

	protected Log getLog() {
		return mojo.getLog();
	}

	public void invoke()
			throws FileNotFoundException, IOException, DependencyResolutionRequiredException, ClassNotFoundException {
		contextBuilder.init(mojo);
		ClassLoadingErrorHandler handler = new ClassLoadingErrorHandler(log);
		ClassLoader parentLoader = getClass().getClassLoader();
		PContextImpl context;
		PProcessor processor;
		do {
			context = contextBuilder.createContext(log, parentLoader, handler);
			try {
				processor = processorManager.next(context);
			} catch (InstantiationException e) {
				log.debug(e);
				log.warn("Skipping processor because it could not be instantiated");
				continue;
			} catch (IllegalAccessException e) {
				log.debug(e);
				log.warn("Skipping processor because it could not be instantiated");
				continue;
			}
			if (processor == null)
				break;
			processor.init(context);
			processor.run(context);
			context.complete();
		} while (true);
	}

}
