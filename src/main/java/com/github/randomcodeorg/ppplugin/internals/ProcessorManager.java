package com.github.randomcodeorg.ppplugin.internals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.github.randomcodeorg.ppplugin.PContext;
import com.github.randomcodeorg.ppplugin.PProcessor;
import com.github.randomcodeorg.ppplugin.RunAfter;
import com.github.randomcodeorg.ppplugin.RunBefore;

public class ProcessorManager {

	private List<String> executedProcessors = new ArrayList<String>();
	private boolean throwOnCircularDependencies;

	public ProcessorManager(boolean throwOnCircularDependencies) {
		this.throwOnCircularDependencies = throwOnCircularDependencies;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends PProcessor> searchProcessors(PContext context) {
		String name;
		List<Class<? extends PProcessor>> processors = new ArrayList<Class<? extends PProcessor>>();
		for (Class<?> c : context.getClasses()) {
			if (PProcessor.class.isAssignableFrom(c)) {
				Class<PProcessor> clp = (Class<PProcessor>) c;
				if (isValid(context, clp)) {
					name = clp.getCanonicalName();
					if (!executedProcessors.contains(name)) {
						processors.add(clp);
					}

				}
			}
		}
		if (processors.isEmpty())
			return null;
		build(processors);
		Class<? extends PProcessor> clp = processors.get(0);
		executedProcessors.add(clp.getCanonicalName());
		return clp;
	}

	private void build(List<Class<? extends PProcessor>> list) {
		Map<Class<? extends PProcessor>, Set<Class<? extends PProcessor>>> successors = new HashMap<Class<? extends PProcessor>, Set<Class<? extends PProcessor>>>();
		for (Class<? extends PProcessor> p : list) {
			if (!successors.containsKey(p))
				successors.put(p, new TreeSet<Class<? extends PProcessor>>());
			if (p.isAnnotationPresent(RunBefore.class)) {
				RunBefore before = p.getAnnotation(RunBefore.class);
				successors.get(p).addAll(findAssignables(list, before.value()));
			}
			if (p.isAnnotationPresent(RunAfter.class)) {
				RunAfter after = p.getAnnotation(RunAfter.class);
				for (Class<? extends PProcessor> successor : findAssignables(list, after.value())) {
					if (!successors.containsKey(successor))
						successors.put(successor, new TreeSet<Class<? extends PProcessor>>());
					successors.get(successor).add(p);
				}
			}
		}
		depthFirstSearch(list, successors);
	}

	private void depthFirstSearch(List<Class<? extends PProcessor>> list,
			Map<Class<? extends PProcessor>, Set<Class<? extends PProcessor>>> successors) {
		Map<Class<? extends PProcessor>, Character> state = new HashMap<Class<? extends PProcessor>, Character>();
		Map<Class<? extends PProcessor>, Class<? extends PProcessor>> predecessors = new HashMap<Class<? extends PProcessor>, Class<? extends PProcessor>>();
		Map<Class<? extends PProcessor>, Integer> visitTimes = new HashMap<Class<? extends PProcessor>, Integer>();
		final Map<Class<? extends PProcessor>, Integer> finishTimes = new HashMap<Class<? extends PProcessor>, Integer>();
		for (Class<? extends PProcessor> v : list) {
			state.put(v, 'w');
			predecessors.put(v, null);
		}
		int time = 0;
		for (Class<? extends PProcessor> v : list) {
			if (state.get(v).equals('w')) {
				time = depthFirstSearchVisit(v, state, visitTimes, finishTimes, successors, predecessors, time);
			}
		}
		list.sort(new Comparator<Class<? extends PProcessor>>() {

			public int compare(Class<? extends PProcessor> o1, Class<? extends PProcessor> o2) {
				return -1 * finishTimes.get(o1).compareTo(finishTimes.get(o2));
			}
		});
	}

	private int depthFirstSearchVisit(Class<? extends PProcessor> u, Map<Class<? extends PProcessor>, Character> state,
			Map<Class<? extends PProcessor>, Integer> visitTimes, Map<Class<? extends PProcessor>, Integer> finishTimes,
			Map<Class<? extends PProcessor>, Set<Class<? extends PProcessor>>> successors,
			Map<Class<? extends PProcessor>, Class<? extends PProcessor>> predecessors, int time) {
		state.put(u, 'g');
		time++;
		visitTimes.put(u, time);
		for (Class<? extends PProcessor> v : successors.get(u)) {
			if (state.get(v).equals('w')) {
				predecessors.put(v, u);
				time = depthFirstSearchVisit(v, state, visitTimes, finishTimes, successors, predecessors, time);
			}
			if (state.get(v).equals('g')) {
				// Zyklus
			}
		}
		state.put(u, 'b');
		time++;
		finishTimes.put(u, time);
		return time;
	}

	private List<Class<? extends PProcessor>> findAssignables(List<Class<? extends PProcessor>> list,
			Class<? extends PProcessor>[] cl) {
		List<Class<? extends PProcessor>> result = new ArrayList<Class<? extends PProcessor>>();
		for (Class<? extends PProcessor> pp : cl) {
			result.addAll(findAssignables(list, pp));
		}
		return result;
	}

	private List<Class<? extends PProcessor>> findAssignables(List<Class<? extends PProcessor>> list,
			Class<? extends PProcessor> cl) {
		List<Class<? extends PProcessor>> result = new ArrayList<Class<? extends PProcessor>>();
		for (Class<? extends PProcessor> pp : list) {
			if (cl != pp && cl.isAssignableFrom(pp))
				result.add(pp);
		}
		return result;
	}

	public PProcessor next(PContext context) throws InstantiationException, IllegalAccessException {
		Class<? extends PProcessor> clp = searchProcessors(context);
		if (clp == null)
			return null;
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
