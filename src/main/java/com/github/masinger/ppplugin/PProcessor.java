package com.github.masinger.ppplugin;

/**
 * A processor that will be called after the compilation.
 * 
 * The plugin will search the project for implementations of this interface and
 * invoke {@link PProcessor#run(PContext)}. Implementations are ignored if they
 * don't have a default constructor, are abstract or can not be instantiated for
 * any other reasons.
 * 
 * @author Marcel Singer
 *
 */
public interface PProcessor {

	/**
	 * <p>
	 * Will be called at the startup of this processor. Use this method to
	 * initialize all required resources.
	 * </p>
	 * <b>Note:</b> Don't do the actual work on this method (instead use
	 * {@link PProcessor#run(PContext)}).
	 * 
	 * @param context
	 */
	void init(PContext context);

	/**
	 * <p>
	 * Use this method to do the actual work on the given context or the
	 * class-files.
	 * </p>
	 * <b>Note:</b> There must be no lock on any class files after this method returns. To modify a .class-file one may use {@link PContext#modify(Class)} instead of accessing the file directly.
	 * Ignoring this will cause serious problems if there are multiple processors.
	 * 
	 * @param context The context of the compilation containing the information about the involved classes and files.
	 */
	void run(PContext context);

}
