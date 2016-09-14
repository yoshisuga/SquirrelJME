// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.markdownwriter;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Formatter;

/**
 * This is a class which writes markdown formatted text to the specified
 * {@link Appendable} which may be any implentation of one. This handle all
 * of the standard formatting details that markdown supports.
 *
 * This writer supports closing and flushing, however those operations will
 * only be performed on the wrapped {@link Appendable} if those also implement
 * such things.
 *
 * This class is not thread safe.
 *
 * @since 2016/09/13
 */
public class MarkdownWriter
	implements Appendable, Closeable, Flushable
{
	/** Markdown right column limit. */
	public static final int RIGHT_COLUMN =
		72;
	
	/** Where text may be written to. */
	protected final Appendable append;
	
	/** Formatter to write output text. */
	protected final Formatter formatter;
	
	/** The current style stack which selects which style to use. */
	private final Deque<__State__> _stack =
		new ArrayDeque<>();
	
	/** The current column being written to. */
	private volatile int _column;
	
	/** Columns on the last line. */
	private volatile int _lastcols;
	
	/**
	 * Initializes the default state.*
	 *
	 * @since 2016/09/13
	 */
	{
		__State__ init;
		this._stack.push((init = new __State__()));
		init._level = __LevelType__.HEADER;
	}
	
	/**
	 * Initializes the markdown writer.
	 *
	 * @param __a The appendable to send characters to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/13
	 */
	public MarkdownWriter(Appendable __a)
		throws NullPointerException
	{
		// Check
		if (__a == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.append = __a;
		
		// Setup formatter
		this.formatter = new Formatter(this);
	}
	
	/**
	 * {@inheritDoc}
	 * @sicne 2016/09/13
	 */
	@Override
	public MarkdownWriter append(char __c)
		throws IOException
	{
		__put(__c);
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * @sicne 2016/09/13
	 */
	@Override
	public MarkdownWriter append(CharSequence __cs)
		throws IOException
	{
		return this.append(__cs, 0, __cs.length());
	}
	
	/**
	 * {@inheritDoc}
	 * @sicne 2016/09/13
	 */
	@Override
	public MarkdownWriter append(CharSequence __cs, int __s, int __e)
		throws IOException
	{
		for (int i = __s; i < __e; i++)
			__put(__cs.charAt(i));
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * @sicne 2016/09/13
	 */
	@Override
	public void close()
		throws IOException
	{
		// Only close if it is closeable
		Appendable append = this.append;
		if (append instanceof Closeable)
			((Closeable)append).close();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/13
	 */
	@Override
	public void flush()
		throws IOException
	{
		// Only flush if the target is appendable also
		Appendable append = this.append;
		if (append instanceof Flushable)
			((Flushable)append).flush();
	}
	
	/**
	 * Prints a header that is of a lower level and enters that section.
	 *
	 * @param __s The text to print.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/13
	 */
	public void headerNextLevel(String __s)
		throws IOException, NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		throw new Error("TODO");
	}
	
	/**
	 * Prints a header at the same level and enters the section.
	 *
	 * @param __s The text to print.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/13
	 */
	public void headerSameLevel(String __s)
		throws IOException, NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// Get the topmost stack item
		Deque<__State__> stack = this._stack;
		__State__ top;
		
		// Pop lists and other things until a header is reached
		while ((top = stack.peek())._level != __LevelType__.HEADER)
			throw new Error("TODO");
		
		// Setup new style and push it to the top
		__State__ now = new __State__();
		int depth = top._depth;
		now._depth = depth;
		now._level = __LevelType__.HEADER;
		
		// Add newline before the header, which may end a text style
		__put('\n');
		
		// Replace the top-most stack entry since it is forever gone when
		// new top-level headers are started
		stack.pop();
		stack.push(now);
		
		// Add new line if on a column, otherwise the header will appear
		// after text on a line.
		if (this._column > 0)
			__put('\n');
		
		// Print opening hashes
		for (int i = 1; i <= depth; i++)
			__put('#', true);
		
		// Space hash
		__put(' ');
		
		// Append the string
		int n = __s.length();
		for (int i = 0; i < n; i++)
		{
			char c = __s.charAt(i);
			
			// Ignore these
			if (c == '\n' || c == '\t')
				continue;
			
			// Place it
			__put(c);
		}
		
		// Newline after header and an extra gap
		__put('\n');
		__put('\n');
	}
	
	/**
	 * Prints formatted text to the output.
	 *
	 * @param __f The format specifier.
	 * @param __args The format arguments.
	 * @throws IOException On write errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/13
	 */
	public void printf(String __f, Object... __args)
		throws IOException, NullPointerException
	{
		// Check
		if (__f == null)
			throw new NullPointerException("NARG");
		
		// Forma
		this.formatter.format(__f, __args);
	}
	
	/**
	 * Places a single character into the output.
	 *
	 * @param __c The character to put.
	 * @throws IOException On write errors.
	 * @since 2016/09/13
	 */
	public void __put(char __c)
		throws IOException
	{
		__put(__c, false);
	}
	
	/**
	 * Places a single character into the output.
	 *
	 * @param __c The character to put.
	 * @param __nospec If {@code true} then the character is not given
	 * special handling.
	 * @throws IOException On write errors.
	 * @since 2016/09/13
	 */
	public void __put(char __c, boolean __nospec)
		throws IOException
	{
		// Write here
		Appendable append = this.append;
		int column = this._column;
		int lastcols = this._lastcols;
		__State__ state = this._stack.peek();
		MarkdownTextStyle style = state._style;
		
		// No special handling used
		if (!__nospec)
		{
			// Ignore newline on column zero following a line with no
			// columns
			if (lastcols <= 0 && column <= 0 && (__c == '\r' || __c == '\n'))
				return;
			
			// Right column exceeded? Move to the next line
			if (column >= RIGHT_COLUMN)
				__put('\n', true);
			
			// Escape?
			if ((column <= 0 && __c == '#') || __c == '`' || __c == '\\' ||
				(style.isNormal() && (__c == '*' || __c == '_' || __c == '(' ||
				__c == '[')))
				__put('\\', true);
			
			// Add it
			__put(__c, true);
			
			// Column may have changed, adjust it
			column = this._column;
		}
		
		// Add it directly
		else
		{
			append.append(__c);
		
			// Modify column, but only when raw since everything eventually
			// reaches this point.
			switch (__c)
			{
					// Tab, align to 4
				case '\t':
					column = ((column + 3) & (~3));
					break;
			
					// A new line
					// Remember old columns on line for newline based
					// skipping
				case '\r':
				case '\n':
					this._lastcols = column;
					column = 0;
					break;
				
					// Normal, add one character
				default:
					column = column + 1;
					break;
			}
		
			// Set
			this._column = column;
		}
	}
}

