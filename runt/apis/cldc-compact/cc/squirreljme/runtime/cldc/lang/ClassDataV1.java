// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.lang;

import cc.squirreljme.runtime.cldc.asm.StaticMethod;

/**
 * Version 1 class data.
 *
 * @since 2018/12/04
 */
public class ClassDataV1
	extends ClassData
{
	/** The super class of this class. */
	private final Class<?> _superclass;
	
	/** The interface classes of this class. */
	private final Class<?>[] _interfaceclasses;
	
	/** The component type. */
	private final Class<?> _component;
	
	/** The binary name of this class. */
	private final String _binaryname;
	
	/** Special class reference index. */
	private final int _specialindex;
	
	/** The number of dimensions. */
	private final int _dimensions;
	
	/** The JAR this class is in. */
	private final String _injar;
	
	/** Class flags. */
	private final int _flags;
	
	/** Default constructor flags. */
	private final int _defaultconflags;
	
	/** Default constructor method. */
	private final StaticMethod _defaultconmethod;
	
	/** Enumeration values. */
	private final StaticMethod _enumvalues;
	
	/**
	 * Version 1 constructor.
	 *
	 * @param __csi Class special index.
	 * @param __bn The binary name of this class.
	 * @param __sc Super classes.
	 * @param __ic Interface classes.
	 * @param __ij The JAR this class is in.
	 * @param __flags Class flags.
	 * @param __dcf Default constructor flag.
	 * @param __dcm Default constructor method.
	 * @param __efm The method used to fill enumerations.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/12/04
	 */
	public ClassDataV1(int __csi, String __bn, Class<?> __sc, Class<?>[] __ic,
		Class<?> __ct, String __ij, int __flags, int __dcf, StaticMethod __dcm,
		StaticMethod __efm)
		throws NullPointerException
	{
		super(1);
		
		if (__bn == null)
			throw new NullPointerException("NARG");
		
		this._specialindex = __csi;
		this._binaryname = __bn;
		this._superclass = __sc;
		this._interfaceclasses = __ic;
		this._component = __ct;
		this._injar = __ij;
		this._flags = __flags;
		this._defaultconflags = __dcf;
		this._defaultconmethod = __dcm;
		this._enumvalues = __efm;
		
		// Count dimensions, used for comparison purposes
		int dims = 0;
		for (; __bn.charAt(dims) == '['; dims++)
			;
		this._dimensions = dims;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/04
	 */
	@Override
	public String binaryName()
	{
		return this._binaryname;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/04
	 */
	@Override
	public Class<?> component()
	{
		return this._component;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/04
	 */
	@Override
	public int defaultConstructorFlags()
	{
		return this._defaultconflags;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/04
	 */
	@Override
	public StaticMethod defaultConstructorMethod()
	{
		return this._defaultconmethod;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/04
	 */
	@Override
	public int dimensions()
	{
		return this._dimensions;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/08
	 */
	@Override
	public StaticMethod enumValues()
	{
		return this._enumvalues;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/04
	 */
	@Override
	public int flags()
	{
		return this._flags;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/04
	 */
	@Override
	public String inJar()
	{
		return this._injar;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/04
	 */
	@Override
	public Class<?>[] interfaceClasses()
	{
		return this._interfaceclasses;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/12/04
	 */
	@Override
	public Class<?> superClass()
	{
		return this._superclass;
	}
}

