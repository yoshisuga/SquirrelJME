// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.swm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is the base class for suite installers.
 *
 * @since 2016/06/24
 */
public abstract class SuiteInstaller
{
	/** Listeners for suites. */
	private final List<SuiteInstallListener> _listeners =
		new ArrayList<>();
	
	/**
	 * Internal use only.
	 *
	 * @since 2016/06/24
	 */
	SuiteInstaller()
	{
	}
	
	/**
	 * Adds a suite installation listener which can be given status updates
	 * when a suite's installation status has changed.
	 *
	 * @param __sl The listener to add.
	 * @since 2016/06/24
	 */
	public final void addInstallationListener(SuiteInstallListener __sl)
	{
		// Ignore
		if (__sl == null)
			return;
		
		this._listeners.add(__sl);
	}
	
	/**
	 * Cancels the current installation.
	 *
	 * @since 2016/06/24
	 */
	public final void cancel()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Removes the given installation listener so that it no longer is notified
	 * of installation updates.
	 *
	 * @param __sl The listener to remove.
	 * @since 2016/06/24
	 */
	public final void removeInstallationListener(SuiteInstallListener __sl)
	{
		// Ignore
		if (__sl == null)
			return;
		
		for (Iterator<SuiteInstallListener> it = this._listeners.iterator();
			it.hasNext();)
			if (__sl == it.next())
				it.remove();
	}
	
	/**
	 * Starts installation of the given suite.
	 *
	 * If this is called twice then the next installation attempt is
	 * enqueued regardless if the previous installation has succeeded or
	 * failed.
	 *
	 * If there is not enough permission to install the given suite then
	 * {@link InstallErrorCodes.UNAUTHORIZED_INSTALL} is set.
	 *
	 * @return The tracker for the given suite.
	 * @since 2016/06/24
	 */
	public final SuiteManagementTracker start()
	{
		throw new todo.TODO();
	}
}

