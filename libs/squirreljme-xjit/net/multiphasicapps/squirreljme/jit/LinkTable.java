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

import java.util.LinkedHashMap;
import java.util.Map;
import net.multiphasicapps.squirreljme.linkage.Export;
import net.multiphasicapps.squirreljme.linkage.Linkage;

/**
 * This is the internal linkage table which used to provide imports and exports
 * that classes use.
 *
 * This class is not thread safe.
 *
 * @since 2017/04/02
 */
public class LinkTable
{
	/** Exports. */
	private final Map<Export, Integer> _exports =
		new LinkedHashMap<>();
	
	/** Linkages (imports). */
	private final Map<Linkage, Integer> _links =
		new LinkedHashMap<>();
	
	/**
	 * Initializes the link table.
	 *
	 * @since 2017/04/02
	 */
	LinkTable()
	{
	}
	
	/**
	 * Adds the specified export to the link table.
	 *
	 * @param __e The export to add.
	 * @return The export index in the link table.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/02
	 */
	public int export(Export __e)
		throws NullPointerException
	{
		// Check
		if (__e == null)
			throw new NullPointerException("NARG");
		
		// Only export once
		Map<Export, Integer> exports = this._exports;
		Integer rv = exports.get(__e);
		if (rv != null)
			return rv;
		
		// Add to the table
		exports.put(__e, (rv = exports.size()));
		return rv;
	}
	
	/**
	 * Adds the specified linkage to the link table.
	 *
	 * @param __l The linkage to add.
	 * @return The link index in the link table.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/02
	 */
	public int link(Linkage __l)
		throws NullPointerException
	{
		// Check
		if (__l == null)
			throw new NullPointerException("NARG");
		
		// Only link once
		Map<Linkage, Integer> links = this._links;
		Integer rv = links.get(__l);
		if (rv != null)
			return rv;
		
		// Add to the table
		links.put(__l, (rv = links.size()));
		return rv;
	}
}

