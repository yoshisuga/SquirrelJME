// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU Affero General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package java.lang;

import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Map;
import java.util.WeakHashMap;
import net.multiphasicapps.squirreljme.magic.IVMCharSequence;
import net.multiphasicapps.squirreljme.magic.Magic;

public final class String
	implements Comparable<String>, CharSequence
{
	/** The ISO encoding. */
	private static final String _ENCODING_ISO =
		"iso-8859-1";
	
	/** The UTF-8 encoding. */
	private static final String _ENCODING_UTF =
		"utf-8";
	
	/**
	 * Strings which are intered so that the same string is referred to so that
	 * for example the {@code ==} operator is actually valid (despite not being
	 * recommended at all).
	 */
	private static final Map<String, Reference<String>> _INTERNS =
		new WeakHashMap<>();
	
	public String()
	{
		throw new Error("TODO");
	}
	
	public String(String __a)
	{
		throw new Error("TODO");
	}
	
	public String(char[] __a)
	{
		throw new Error("TODO");
	}
	
	public String(char[] __a, int __o, int __l)
	{
		throw new Error("TODO");
	}
	
	public String(byte[] __a, int __o, int __l, String __e)
		throws NullPointerException, UnsupportedEncodingException
	{
		throw new Error("TODO");
	}
	
	public String(byte[] __a, String __e)
		throws NullPointerException, UnsupportedEncodingException
	{
		this(__a, 0, __a.length, __e);
	}
	
	public String(byte[] __a, int __o, int __l)
	{
		throw new Error("TODO");
	}
	
	public String(byte[] __a)
	{
		throw new Error("TODO");
	}
	
	public String(StringBuffer __a)
		throws NullPointerException
	{
		this(__a.toString());
	}
	
	public String(StringBuilder __a)
		throws NullPointerException
	{
		this(__a.toString());
	}
	
	/**
	 * Constructs a new string which uses the specified virtual machine
	 * internal character sequence as a data source. The characters of this
	 * string would be sourced from the input object since it has no
	 * presence in the running virtual machine. In most cases, this would only
	 * be used by the interpreter.
	 *
	 * @param __ivmcs The internal virtual machine character sequence, this
	 * data is not copied.
	 * @since 2016/04/05
	 */
	private String(IVMCharSequence __ivmcs)
	{
		throw new Error("TODO");
	}
	
	public char charAt(int __a)
	{
		throw new Error("TODO");
	}
	
	/**
	 * Compares the character values of this string and compares it to the
	 * character values of the other string.
	 *
	 * Smaller strings always precede longer strings.
	 *
	 * This is equivalent to the standard POSIX {@code strcmp()} with the "C"
	 * locale.
	 *
	 * Internally this does not handle the special variants of this class and
	 * is a general purpose method.
	 *
	 * @param __os The string to compare against.
	 * @return A negative value if this string precedes the other string, a
	 * positive value if this string procedes the other string, or zero if the
	 * strings are equal.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/04/02
	 */
	public int compareTo(String __os)
		throws NullPointerException
	{
		// Check
		if (__os == null)
			throw new NullPointerException("NARG");
		
		// Get both string lengths
		int an = length();
		int bn = __os.length();
		
		// Max comparison length
		int max = Math.min(an, bn);
		
		// Compare both strings
		for (int i = 0; i < max; i++)
		{
			// Get character difference
			int diff = ((int)charAt(i)) - ((int)__os.charAt(i));
			
			// If there is a difference, then return it
			if (diff != 0)
				return diff;
		}
		
		// Remaining comparison is the length parameter, shorter strings are
		// first
		return an - bn;
	}
	
	public int compareToIgnoreCase(String __a)
	{
		throw new Error("TODO");
	}
	
	public String concat(String __a)
	{
		throw new Error("TODO");
	}
	
	public boolean contains(CharSequence __a)
	{
		throw new Error("TODO");
	}
	
	public boolean contentEquals(StringBuffer __a)
	{
		throw new Error("TODO");
	}
	
	public boolean contentEquals(CharSequence __a)
	{
		throw new Error("TODO");
	}
	
	public boolean endsWith(String __a)
	{
		throw new Error("TODO");
	}
	
	@Override
	public boolean equals(Object __o)
	{
		// Short circuit match, the same string object is always equals
		if (this == __o)
			return true;
		
		// If not a string, fail
		if (!(__o instanceof String))
			return false;
		
		// Cast
		String o = (String)__o;
		
		// Get length of both strings
		int mlen = length();
		int olen = o.length();
		
		// If the length differs, they are not equal
		if (mlen != olen)
			return false;
		
		throw new Error("TODO");
	}
	
	public boolean equalsIgnoreCase(String __a)
	{
		throw new Error("TODO");
	}
	
	/**
	 * Translates this string using into a byte array using the specified
	 * character encoding.
	 *
	 * @param __enc The character encoding to use.
	 * @return A byte array with the characters of this string converted to
	 * bytes.
	 * @throws NullPointerException If no encoding was specified.
	 * @throws UnsupportedEncodingException 
	 */
	public byte[] getBytes(String __enc)
		throws NullPointerException, UnsupportedEncodingException
	{
		// Check
		if (__enc == null)
			throw new NullPointerException("NARG");
		
		throw new Error("TODO");
	}
	
	public byte[] getBytes()
	{
		// Wrap it
		try
		{
			return getBytes(System.getProperty("microedition.encoding"));
		}
		
		// Should not occur
		catch (UnsupportedEncodingException uee)
		{
			throw new RuntimeException("WTFX", uee);
		}
	}
	
	public void getChars(int __a, int __b, char[] __c, int __d)
	{
		throw new Error("TODO");
	}
	
	@Override
	public int hashCode()
	{
		throw new Error("TODO");
	}
	
	public int indexOf(int __a)
	{
		throw new Error("TODO");
	}
	
	public int indexOf(int __a, int __b)
	{
		throw new Error("TODO");
	}
	
	public int indexOf(String __a)
	{
		throw new Error("TODO");
	}
	
	public int indexOf(String __a, int __b)
	{
		throw new Error("TODO");
	}
	
	/**
	 * This returns the unique string instance used for the current string, if
	 * the current string is not within the internal map then it is added. If
	 * it already exists in the map then that pre-existing value is returned.
	 *
	 * Although this may be used for {@code ==} to work, it is not recommended
	 * to use this method for such things.
	 *
	 * @return The unique string instance.
	 * @since 2016/04/01
	 */
	public String intern()
	{
		// Strings may be placed within an array which is internal to the
		// precompiled virtual machine into a native binary. Thus string
		// objects just as these would always exist. Thus for example, the
		// built in string array should contain a string called "intern" since
		// it is the name of a method.despite not being an actual string. It is
		// this way because
		String im = Magic.matchInternalString(this);
		if (im != null)
			return im;
		
		// Lock on the map
		Map<String, Reference<String>> map = _INTERNS;
		synchronized (map)
		{
			// In a reference?
			Reference<String> ref = map.get(this);
			String rv;
			
			// Needs map placement?
			if (ref == null || null == (rv = ref.get()))
				map.put(this, new WeakReference<>((rv = this)));
			
			// Return it
			return rv;
		}
	}
	
	public boolean isEmpty()
	{
		throw new Error("TODO");
	}
	
	public int lastIndexOf(int __a)
	{
		throw new Error("TODO");
	}
	
	public int lastIndexOf(int __a, int __b)
	{
		throw new Error("TODO");
	}
	
	public int lastIndexOf(String __a)
	{
		throw new Error("TODO");
	}
	
	public int lastIndexOf(String __a, int __b)
	{
		throw new Error("TODO");
	}
	
	public int length()
	{
		throw new Error("TODO");
	}
	
	public boolean regionMatches(int __a, String __b, int __c, int __d)
	{
		throw new Error("TODO");
	}
	
	public boolean regionMatches(boolean __a, int __b, String __c, int __d,
		int __e)
	{
		throw new Error("TODO");
	}
	
	public String replace(char __a, char __b)
	{
		// If a character is going to be replaced with itself then no
		// replacement has to actually be performed.
		if (__a == __b)
			return this;
		
		throw new Error("TODO");
	}
	
	public boolean startsWith(String __a, int __b)
	{
		throw new Error("TODO");
	}
	
	public boolean startsWith(String __a)
	{
		throw new Error("TODO");
	}
	
	public CharSequence subSequence(int __a, int __b)
	{
		throw new Error("TODO");
	}
	
	public String substring(int __a)
	{
		throw new Error("TODO");
	}
	
	public String substring(int __a, int __b)
	{
		throw new Error("TODO");
	}
	
	public char[] toCharArray()
	{
		throw new Error("TODO");
	}
	
	public String toLowerCase()
	{
		throw new Error("TODO");
	}
	
	@Override
	public String toString()
	{
		throw new Error("TODO");
	}
	
	public String toUpperCase()
	{
		throw new Error("TODO");
	}
	
	public String trim()
	{
		throw new Error("TODO");
	}
	
	public static String copyValueOf(char[] __a, int __b, int __c)
	{
		throw new Error("TODO");
	}
	
	public static String copyValueOf(char[] __a)
	{
		throw new Error("TODO");
	}
	
	public static String format(String __a, Object... __b)
	{
		throw new Error("TODO");
	}
	
	public static String valueOf(Object __a)
	{
		throw new Error("TODO");
	}
	
	public static String valueOf(char[] __a)
	{
		throw new Error("TODO");
	}
	
	public static String valueOf(char[] __a, int __b, int __c)
	{
		throw new Error("TODO");
	}
	
	public static String valueOf(boolean __a)
	{
		throw new Error("TODO");
	}
	
	public static String valueOf(char __a)
	{
		throw new Error("TODO");
	}
	
	public static String valueOf(int __a)
	{
		throw new Error("TODO");
	}
	
	public static String valueOf(long __a)
	{
		throw new Error("TODO");
	}
	
	public static String valueOf(float __a)
	{
		throw new Error("TODO");
	}
	
	public static String valueOf(double __a)
	{
		throw new Error("TODO");
	}
}

