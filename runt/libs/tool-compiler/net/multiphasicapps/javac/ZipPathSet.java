// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.javac;

import java.io.IOException;
import java.util.Iterator;
import net.multiphasicapps.zip.blockreader.ZipBlockEntry;
import net.multiphasicapps.zip.blockreader.ZipBlockReader;

/**
 * This is a path set which may be used on top of a ZIP file.
 *
 * @since 2017/11/28
 */
public final class ZipPathSet
	implements CompilerPathSet
{
	/** The ZIP to use as input. */
	protected final ZipBlockReader zip;
	
	/**
	 * Initializes the path set over ZIP files.
	 *
	 * @return __zip The ZIP file to wrap and treat as a path set.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/28
	 */
	public ZipPathSet(ZipBlockReader __zip)
		throws IOException, NullPointerException
	{
		if (__zip == null)
			throw new NullPointerException("NARG");
		
		this.zip = __zip;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/28
	 */
	@Override
	public void close()
	{
		// Close the target ZIP
		try
		{
			this.zip.close();
		}
		
		// {@squirreljme.error AQ0d Could not close the source ZIP.}
		catch (IOException e)
		{
			throw new CompilerException("AQ0d", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/28
	 */
	@Override
	public CompilerInput input(String __n)
	{
		if (__n == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/28
	 */
	@Override
	public Iterator<CompilerInput> iterator()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/28
	 */
	@Override
	public String toString()
	{
		return this.zip.toString();
	}
}

