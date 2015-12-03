package com.github.randomcodeorg.ppplugin.internals;

public interface ErrorHandler<T extends Throwable> {

	boolean handleError(T e);
	
}
