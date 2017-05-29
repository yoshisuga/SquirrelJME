// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit;

import java.lang.ref.Reference;

/**
 * This is a key which is used in zones to specify which part of the method
 * it has code for. This would in most cases be a range of instructions but
 * it may also be exception handlers and handling for synchronization.
 *
 * @since 2017/05/28
 */
public abstract class ZoneKey
	implements Comparable<ZoneKey>
{
	/** Reference to the owning program. */
	protected final Reference<ProgramState> program;
	
	/**
	 * Initializes the zone key with the given program used as a reference
	 * point.
	 *
	 * @param __r The owning program state.
	 * @throws NullPointerException On null arguments.
	 * @sinced 2017/05/29
	 */
	protected ZoneKey(Reference<ProgramState> __r)
		throws NullPointerException
	{
		// Check
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.program = __r;
	}
	
	/**
	 * Compares this zone key to another zone key.
	 *
	 * Since zone keys can be compared to other potentially incompatible zone
	 * keys, classes which override this zone key must call this super method.
	 *
	 * @param __o The key to compare against.
	 * @return The integer comparison.
	 * @since 2017/05/28
	 */
	@Override
	public int compareTo(ZoneKey __o)
	{
		// Compare the class names for comparison so that they appear in
		// a deterministic order
		int rv = getClass().name().compareTo(__o.getClass().name());
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/05/28
	 */
	@Override
	public abstract boolean equals(Object __o);
	
	/**
	 * {@inheritDoc}
	 * @since 2017/05/28
	 */
	@Override
	public abstract int hashCode();
}

