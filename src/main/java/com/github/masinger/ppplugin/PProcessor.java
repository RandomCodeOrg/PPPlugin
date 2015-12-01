package com.github.masinger.ppplugin;

public interface PProcessor {

	void init(PContext context);
	
	void run(PContext context);
	
}
