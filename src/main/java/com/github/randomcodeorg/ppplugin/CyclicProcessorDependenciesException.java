package com.github.randomcodeorg.ppplugin;

public class CyclicProcessorDependenciesException extends RuntimeException {

	private static final long serialVersionUID = 8114081829213670348L;

	public CyclicProcessorDependenciesException() {
	}
	
	public CyclicProcessorDependenciesException(Class<? extends PProcessor> p1, Class<? extends PProcessor> p2){
		this(String.format("One or more cyclic dependencies were found during the processor evaluation. Involved classes: %s, %s", p1.getCanonicalName(), p2.getCanonicalName()));
	}

	public CyclicProcessorDependenciesException(String message) {
		super(message);
	}

	public CyclicProcessorDependenciesException(Throwable cause) {
		super(cause);
	}

	public CyclicProcessorDependenciesException(String message, Throwable cause) {
		super(message, cause);
	}

	public CyclicProcessorDependenciesException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
