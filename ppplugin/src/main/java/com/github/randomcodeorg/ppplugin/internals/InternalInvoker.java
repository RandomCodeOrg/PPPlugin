package com.github.randomcodeorg.ppplugin.internals;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.github.randomcodeorg.ppplugin.PProcessor;
import com.github.randomcodeorg.ppplugin.data.BuildDataSource;
import com.github.randomcodeorg.ppplugin.data.BuildLog;
import com.github.randomcodeorg.ppplugin.data.DependencyResolutionException;

public class InternalInvoker {

	private final BuildDataSource dataSource;
	private final ContextBuilder contextBuilder;
	private final BuildLog log;
	private final ProcessorManager processorManager = new ProcessorManager();

	public InternalInvoker(BuildDataSource dataSource) {
		this.dataSource = dataSource;
		this.contextBuilder = new ContextBuilder();
		this.log = dataSource.getLog();
	}

	protected BuildLog getLog() {
		return dataSource.getLog();
	}

	public void invoke()
			throws FileNotFoundException, IOException, DependencyResolutionException, ClassNotFoundException {
		contextBuilder.init(dataSource);
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
			log.info(String.format("Executing processor %s", processor.getClass().getCanonicalName()));
			processor.init(context);
			processor.run(context);
			context.complete();
		} while (true);
	}

}
