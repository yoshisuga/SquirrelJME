// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.vm.springcoat;

import cc.squirreljme.runtime.cldc.asm.TaskAccess;
import cc.squirreljme.runtime.swm.EntryPoint;
import cc.squirreljme.runtime.swm.EntryPoints;
import cc.squirreljme.vm.VMClassLibrary;
import cc.squirreljme.vm.VMNativeDisplayAccess;
import cc.squirreljme.vm.VMSuiteManager;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.microedition.swm.TaskStatus;
import net.multiphasicapps.profiler.ProfilerSnapshot;
import net.multiphasicapps.tool.manifest.JavaManifest;

/**
 * This class manages tasks within SpringCoat and is used to launch and
 * provide access to those that are running.
 *
 * @since 2018/11/04
 */
public final class SpringTaskManager
{
	/** The manager for suites. */
	protected final VMSuiteManager suites;
	
	/** The profiling information. */
	protected final ProfilerSnapshot profiler;
	
	/** Tasks that are used. */
	private final Map<Integer, SpringTask> _tasks =
		new HashMap<>();
	
	/** System properties. */
	private final Map<String, String> _sysprops;
	
	/** Access to the native display. */
	protected final VMNativeDisplayAccess nativedisplay =
		new VMNativeDisplayAccess();
	
	/** Next task ID. */
	private int _nextid;
	
	/**
	 * Initializes the task manager.
	 *
	 * @param __sm The suite manager.
	 * @param __ps The snapshot for profiling.
	 * @param __sp System properties.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/04
	 */
	public SpringTaskManager(VMSuiteManager __sm, ProfilerSnapshot __ps,
		Map<String, String> __sp)
		throws NullPointerException
	{
		if (__sm == null)
			throw new NullPointerException("NARG");
		
		this.suites = __sm;
		this.profiler = (__ps == null ? new ProfilerSnapshot() : __ps);
		this._sysprops = (__sp == null ? new HashMap<String, String>() :
			new HashMap<>(__sp));
	}
	
	/**
	 * Starts the specified task.
	 *
	 * @param __cp The class path to use.
	 * @param __entry The entry point.
	 * @param __args Arguments used.
	 * @param __gd The current guest depth.
	 * @return The ID of the created task.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/11/04
	 */
	public final int startTask(String[] __cp, String __entry, String[] __args,
		int __gd)
		throws NullPointerException
	{
		if (__cp == null || __entry == null || __args == null)
			throw new NullPointerException("NARG");
		
		VMSuiteManager suites = this.suites;
		
		// Load classpath libraries
		int cpn = __cp.length;
		VMClassLibrary[] scl = new VMClassLibrary[cpn];
		for (int i = 0; i < cpn; i++)
		{
			VMClassLibrary lib = suites.loadLibrary(__cp[i]);
			
			// If missing, cannot continue
			if (lib == null)
			{
				todo.DEBUG.note("Could not find library: `%s`", __cp[i]);
				return TaskAccess.ERROR_MISSING_LIBRARY;
			}
			
			scl[i] = lib;
		}
		
		// Get the boot library since we need to look at the entry points
		VMClassLibrary boot = scl[cpn - 1];
		
		// Need to load the manifest where the entry points will be
		EntryPoints entries;
		try (InputStream in = boot.resourceAsStream("META-INF/MANIFEST.MF"))
		{
			// {@squirreljme.error BK1h Entry point JAR has no manifest.}
			if (in == null)
				throw new SpringVirtualMachineException("BK1h");
			
			entries = new EntryPoints(new JavaManifest(in));
		}
		
		// {@squirreljme.error BK1i Failed to read the manifest.}
		catch (IOException e)
		{
			throw new SpringVirtualMachineException("BK1i", e);
		}
		
		// Determine the entry point used
		int bootdx = -1;
		for (int i = 0, n = entries.size(); i < n; i++)
			if (__entry.equals(entries.get(i).entryPoint()))
			{
				bootdx = i;
				break;
			}
		
		// Could not find the boot point
		if (bootdx < 0)
			return TaskAccess.ERROR_INVALID_ENTRY;
		
		// Build machine for the task
		SpringMachine machine = new SpringMachine(suites,
			new SpringClassLoader(scl), this, null, false, bootdx, __gd + 1,
			this.profiler, this.nativedisplay, this._sysprops, __args);
		
		// Lock on tasks
		Map<Integer, SpringTask> tasks = this._tasks;
		synchronized (tasks)
		{
			// Next task ID
			int tid = ++this._nextid;
			
			// Setup task in a new thread to run and such
			SpringTask rv = new SpringTask(tid, machine);
			
			// Store task in active set
			tasks.put(tid, rv);
			
			// Start a thread for this task, which is that task's main thread
			new Thread(rv, "MainTask-" + tid + "-" + __entry).start();
			
			// The task ID is our handle
			return tid;
		}
	}
	
	/**
	 * Returns the status for a task.
	 *
	 * @param __tid The task ID.
	 * @return The status.
	 * @since 2018/11/04
	 */
	public final int taskStatus(int __tid)
	{
		// Lock on tasks
		Map<Integer, SpringTask> tasks = this._tasks;
		synchronized (tasks)
		{
			// Must be a valid task
			SpringTask rv = tasks.get(__tid);
			if (rv == null)
				return -1;
			
			// Get the exit code
			int exitcode = rv._exitcode;
			
			// Still running
			if (exitcode == Integer.MIN_VALUE)
				return TaskStatus.RUNNING.ordinal();
			
			// Terminated with success
			else if (exitcode == 0)
				return TaskStatus.EXITED_REGULAR.ordinal();
			
			// Terminate with something else
			else
				return TaskStatus.EXITED_FATAL.ordinal();
		}
	}
}

