// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package java.lang;

import cc.squirreljme.jvm.Assembly;

/**
 * This represents a boxed integer.
 *
 * @since 2019/05/25
 */
public final class Integer
	extends Number
{
	/** Minimum value. */
	public static final int MIN_VALUE =
		0x80000000;
	
	/** Maximum value. */
	public static final int MAX_VALUE =
		0x7FFFFFFF;
	
	/** The value of this integer. */
	private transient int _value;
	
	/**
	 * Initializes this integer.
	 *
	 * @param __v The value used.
	 * @since 2019/06/14
	 */
	public Integer(int __v)
	{
		this._value = __v;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/06/18
	 */
	@Override
	public String toString()
	{
		return Integer.toString(this._value, 10);
	}
	
	/**
	 * Converts the value to a string using the given radix.
	 *
	 * @param __v The input value.
	 * @param __r The radix of the string, if it exceeds the maximum
	 * permitted radix specified in {@link Character} then this is set to 10.
	 * @return The resulting string.
	 * @since 2018/09/23
	 */
	public static String toString(int __v, int __r)
	{
		// If the radix is not valid, then just force to 10
		if (__r < Character.MIN_RADIX || __r > Character.MAX_RADIX)
			__r = 10;
		
		StringBuilder sb = new StringBuilder();
		
		// Negative? Remember it but we need to swap the sign
		boolean negative;
		if ((negative = (__v < 0)))
			__v = -__v;
		
		// Insert characters at the end of the string, they will be reversed
		// later, it is easier this way
		for (boolean digit = false;;)
		{
			// Determine the current place
			int mod = (int)(__v % __r);
			
			// Do not print if any other digit was stored
			if (__v == 0 && digit)
				break;
			
			// Print character
			sb.append((char)(mod < 10 ? '0' + mod : 'a' + (mod - 10)));
			digit = true;
			
			// Stop printing characters
			if (__v == 0)
				break;
			
			// Use the remaining division
			else
				__v = __v / __r;
		}
		
		// Add the sign in
		if (negative)
			sb.append('-');
			
		// Because the values are added in the opposite order, reverse it
		sb.reverse();
		
		return sb.toString();
	}
	
	/**
	 * Returns a boxed value.
	 *
	 * @param __v The value to use.
	 * @return The boxed value.
	 * @since 2019/05/26
	 */
	public static final Integer valueOf(int __v)
	{
		return new Integer(__v);
	}
}

