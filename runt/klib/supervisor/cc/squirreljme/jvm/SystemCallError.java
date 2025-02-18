// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.jvm;

/**
 * This interface contains the various error codes for all of the system calls.
 *
 * @since 2019/05/23
 */
public final class SystemCallError
{
	/** No error, or success. */
	public static final byte NO_ERROR =
		0;
	
	/** The system call is not supported. */
	public static final byte UNSUPPORTED_SYSTEM_CALL =
		-1;
	
	/** The pipe descriptor is not valid. */
	public static final byte PIPE_DESCRIPTOR_INVALID =
		-2;
	
	/** Write error when writing to the pipe. */
	public static final byte PIPE_DESCRIPTOR_BAD_WRITE =
		-3;
	
	/** Value out of range. */
	public static final byte VALUE_OUT_OF_RANGE =
		-4;
	
	/** No frame buffer exists. */
	public static final byte NO_FRAMEBUFFER =
		-5;
	
	/**
	 * Not used.
	 *
	 * @since 2019/05/23
	 */
	private SystemCallError()
	{
	}
	
	/**
	 * Returns the error state.
	 *
	 * @param __si The system call index.
	 * @return The error, 0 will be on success.
	 * @since 2019/05/23
	 */
	public static final int getError(short __si)
	{
		return Assembly.sysCallV(SystemCallIndex.ERROR_GET, __si);
	}
}

