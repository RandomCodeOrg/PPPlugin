package com.github.randomcodeorg.ppplugin.internals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.github.randomcodeorg.ppplugin.PContext;
import com.github.randomcodeorg.ppplugin.PProcessor;

public class ProcessorManager {

	private List<String> executedProcessors = new ArrayList<String>();

	public ProcessorManager() {
	}

	@SuppressWarnings("unchecked")
	private Class<PProcessor> searchProcessors(PContext context) {
		String name;
		for (Class<?> c : context.getClasses()) {
			if (PProcessor.class.isAssignableFrom(c)) {
				Class<PProcessor> clp = (Class<PProcessor>) c;
				if (isValid(context, clp)) {
					name = clp.getCanonicalName();
					if(!executedProcessors.contains(name)){
						executedProcessors.add(name);
						return clp;
					}
					
				}
			}
		}
		return null;
	}

	public PProcessor next(PContext context) throws InstantiationException, IllegalAccessException {
		Class<PProcessor> clp = searchProcessors(context);
		if(clp == null) return null;
		context.getLog().info(String.format("Creating processor %s", clp.getName()));
		PProcessor result = clp.newInstance();
		return result;
	}

	private boolean isValid(PContext context, Class<PProcessor> type) {
		boolean result = !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
		if (result) {
			try {
				Constructor<?> constr = type.getConstructor();
				result = constr != null;
			} catch (Throwable e) {
				e.printStackTrace();
				result = false;
			}
		}
		if (!result) {
			context.getLog().warn(String.format("Ignoring processor: %s", type.getCanonicalName()));
		}
		return result;
	}

}
