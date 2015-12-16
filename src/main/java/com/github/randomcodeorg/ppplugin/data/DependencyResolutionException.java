package com.github.randomcodeorg.ppplugin.data;

public class DependencyResolutionException extends Exception {

	
	private static final long serialVersionUID = 8082289132517896627L;

	public DependencyResolutionException() {
	}

	public DependencyResolutionException(String message) {
		super(message);
	}

	public DependencyResolutionException(Throwable cause) {
		super(cause);
	}

	public DependencyResolutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public DependencyResolutionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
