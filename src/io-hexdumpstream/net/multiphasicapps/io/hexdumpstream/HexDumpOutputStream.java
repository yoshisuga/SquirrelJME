// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.io.hexdumpstream;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import net.multiphasicapps.io.psw.PrintStreamWriter;

/**
 * This contains a stream which when given bytes hexadecimal bytes will
 * output to the given stream.
 *
 * Closing this stream does not close the dumping stream, it only closes the
 * piped stream if one is used.
 *
 * @since 2016/08/12
 */
public class HexDumpOutputStream
	extends OutputStream
{
	/** The number of columns to print. */
	private static final int _COLUMNS =
		16;
	
	/** Where to pipe data to. */
	protected final OutputStream pipe;
	
	/** Where to write characters to. */
	protected final Writer dump;
	
	/** The write queue. */
	private final byte[] _queue =
		new byte[_COLUMNS];
	
	/** The current write position. */
	private volatile int _at;
	
	/**
	 * Initializes dumping to the given stream.
	 *
	 * @param __dump The stream to dump to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/12
	 */
	public HexDumpOutputStream(PrintStream __dump)
		throws NullPointerException
	{
		this(new PrintStreamWriter(__dump));
	}
	
	/**
	 * Initializes dumping to the given stream.
	 *
	 * @param __dump The stream to dump to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/12
	 */
	public HexDumpOutputStream(Writer __dump)
		throws NullPointerException
	{
		// Check
		if (__dump == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.pipe = null;
		this.dump = __dump;
	}
	
	/**
	 * Initializes dumping to the given stream and also copying them to
	 * another stream to act as a pipe.
	 *
	 * @param __pipe The stream to copy bytes to.
	 * @param __dump The stream to dump to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/12
	 */
	public HexDumpOutputStream(OutputStream __pipe, PrintStream __dump)
		throws NullPointerException
	{
		this(__pipe, new PrintStreamWriter(__dump));
	}
	
	/**
	 * Initializes dumping to the given stream and also copying them to
	 * another stream to act as a pipe.
	 *
	 * @param __pipe The stream to copy bytes to.
	 * @param __dump The stream to dump to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/07/12
	 */
	public HexDumpOutputStream(OutputStream __pipe, Writer __dump)
		throws NullPointerException
	{
		// Check
		if (__pipe == null || __dump == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.pipe = __pipe;
		this.dump = __dump;
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * The stream to dump to is not closed.
	 *
	 * @since 2016/08/12
	 */
	@Override
	public void close()
		throws IOException
	{
		// Only close the pipe and not the dump
		Closeable c = this.pipe;
		if (c != null)
			c.close();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/12
	 */
	@Override
	public void flush()
		throws IOException
	{
		// Flush the pipe
		Flushable f = this.pipe;
		if (f != null)
			f.flush();
		
		// And the dump
		this.dump.flush();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/12
	 */
	@Override
	public void write(int __b)
		throws IOException
	{
		// Write a single byte
		int cols = _COLUMNS;
		byte b = (byte)__b;
		byte[] queue = this._queue;
		int at = this._at;
		try
		{
			// Write to the queue
			queue[at++] = b;
			this._at = at;
			
			// Send to the output
			this.pipe.write(__b);
		}
		
		// Check if a row is to be printed
		finally
		{
			// Print entire row
			if (at == cols)
				try
				{
					Writer w = this.dump;
					
					// Print starting hex data
					for (int i = 0; i < cols; i++)
					{
						// Padding
						if (i > 0)
							w.write(' ');
						
						// Write both bytes
						int x = queue[i] & 0xFF;
						w.write(Character.forDigit(((x >>> 4) & 0xF), 16));
						w.write(Character.forDigit((x & 0xF), 16));
					}
					
					// Print ASCII version of it
					w.write("  |");
					for (int i = 0; i < cols; i++)
					{
						// Only print in the range of ASCII
						char c = (char)(queue[i] & 0xFF);
						if (c < ' ' || c > 0x7F)
							c = '.';
						
						w.write(c);
					}
					w.write('|');
				}
			
				// Always clear the position
				finally
				{
					this._at = 0;
				}
		}
	}
}

