// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.meep.lui;

import javax.microedition.lui.Display;
import net.multiphasicapps.squirreljme.lcduilui.CommonDisplay;

/**
 * This is the base class for classes which implement drivers to line based
 * interfaces.
 *
 * @since 2016/10/08
 */
public abstract class LUIDisplay
	extends CommonDisplay<Display>
{
	/**
	 * {@inheritDoc}
	 * @since 2016/10/08
	 */
	@Override
	public abstract LUIDisplayInstance createInstance();
}

