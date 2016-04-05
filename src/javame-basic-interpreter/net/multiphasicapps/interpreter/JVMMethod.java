// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU Affero General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.interpreter;

import net.multiphasicapps.classfile.CFMethodFlags;

/**
 * This represents a bound method within a class.
 *
 * @since 2016/04/04
 */
public class JVMMethod
	extends JVMMember
{
	/**
	 * Initializes the method.
	 *
	 * @since 2016/04/04
	 */
	JVMMethod()
	{
		throw new Error("TODO");
	}
	
	/**
	 * Returns the method flags.
	 *
	 * @return The method flags.
	 * @since 2016/04/04
	 */
	public CFMethodFlags flags()
	{
		throw new Error("TODO");
	}
}

