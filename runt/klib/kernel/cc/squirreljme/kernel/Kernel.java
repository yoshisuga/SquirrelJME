// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.kernel;

import cc.squirreljme.runtime.cldc.system.SystemCallImplementation;
import cc.squirreljme.runtime.cldc.system.SystemFunction;

/**
 * This class represents the kernel which manages the entire SquirrelJME
 * system and all of the needed IPC and running tasks/threads.
 *
 * @since 2017/12/08
 */
public final class Kernel
	implements SystemCallImplementation
{
	/** The owning primitive kernel. */
	protected final PrimitiveKernel primitive;
	
	/**
	 * Initializes the kernel.
	 *
	 * @param __pk The primitive kernel.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/03
	 */
	public Kernel(PrimitiveKernel __pk)
		throws NullPointerException
	{
		if (__pk == null)
			throw new NullPointerException("NARG");
		
		this.primitive = __pk;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/03
	 */
	@Override
	public final Object systemCall(SystemFunction __func, Object... __args)
		throws NullPointerException
	{
		if (__func == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
}

