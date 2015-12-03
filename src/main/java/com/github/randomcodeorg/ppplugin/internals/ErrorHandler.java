package com.github.randomcodeorg.ppplugin.internals;

public interface ErrorHandler<T extends Throwable, E> {

	boolean handleError(T e, E extra);
	
}
