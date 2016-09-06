// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.jit;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.multiphasicapps.io.data.DataEndianess;
import net.multiphasicapps.io.data.ExtendedDataInputStream;
import net.multiphasicapps.io.region.SizeLimitedInputStream;
import net.multiphasicapps.squirreljme.java.symbols.MethodSymbol;
import net.multiphasicapps.squirreljme.jit.base.JITException;
import net.multiphasicapps.squirreljme.jit.base.JITCPUFloat;
import net.multiphasicapps.squirreljme.jit.base.JITMethodFlags;

/**
 * This class is used to decode the actual code attribute in the method
 * along with any of its attributes.
 *
 * @since 2016/08/19
 */
final class __CodeDecoder__
	extends __HasAttributes__
{
	/** The maximum number of bytes the code attribute can be. */
	private static final int _CODE_SIZE_LIMIT =
		65535;
	
	/** Constant pool. */
	protected final JITConstantPool pool;
	
	/** The owning method decoder. */
	final __MethodDecoder__ _decoder;
	
	/** The class decoder owning this. */
	final __ClassDecoder__ _classdecoder;
	
	/** The logic writer to use. */
	final JITMethodWriter _writer;
	
	/** The method flags. */
	final JITMethodFlags _flags;
	
	/** The method type. */
	final MethodSymbol _type;
	
	/** Is {@code float} in hardware? */
	final boolean _hwfloat;
	
	/** Is {@code double} in hardware? */
	final boolean _hwdouble;
	
	/** The input code attribute data. */
	private final DataInputStream _input;
	
	/** The maximum number of local variables. */
	volatile int _maxlocals;
	
	/** The maximum size of the stack. */
	volatile int _maxstack;
	
	/** The stack map table state. */
	volatile Map<Integer, __SMTState__> _smt;
	
	/**
	 * Add base code decoder class.
	 *
	 * @param __cd The method decoder being used.
	 * @param __dis The input source.
	 * @param __f The method flags.
	 * @param __t The method type.
	 * @param __mlw The logic writer to write code to.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/08/18
	 */
	__CodeDecoder__(__MethodDecoder__ __cd, DataInputStream __dis,
		JITMethodFlags __f, MethodSymbol __t, JITMethodWriter __mlw)
		throws NullPointerException
	{
		// Check
		if (__cd == null || __dis == null || __f == null || __t == null ||
			__mlw == null)
			throw new NullPointerException("NARG");
		
		// Set
		this._decoder = __cd;
		__ClassDecoder__ classdec = __cd._classdecoder;
		this._classdecoder = classdec;
		this._input = __dis;
		this._flags = __f;
		this._type = __t;
		this._writer = __mlw;
		this.pool = __cd._pool;
		
		// Need to get hardware float information
		JITOutputConfig.Immutable config = classdec._config;
		JITCPUFloat ft = config.triplet().floatingPoint();
		this._hwfloat = ft.isHardwareFloat();
		this._hwdouble = ft.isHardwareDouble();
	}
	
	/**
	 * Decodes the code attribute and any of its contained data
	 *
	 * @throws IOException On read/write errors.
	 * @since 2016/08/23
	 */
	void __decode()
		throws IOException
	{
		DataInputStream input = this._input;
		JITConstantPool pool = this.pool;
		JITMethodWriter writer = this._writer;
		
		// Read max stack and locals
		int maxstack = input.readUnsignedShort();
		int maxlocals = input.readUnsignedShort();
		
		// Store for SMT parsing and code usage
		this._maxstack = maxstack;
		this._maxlocals = maxlocals;
		
		// Report this to the writer
		writer.variableCounts(maxstack, maxlocals);
		
		// {@squirreljme.error ED06 The code for a given method exceeds the
		// code size limit, or the size is zero. (The current code length;
		// The code size limit)}
		int codelen = input.readInt();
		if (codelen <= 0 || codelen > _CODE_SIZE_LIMIT)
			throw new JITException(String.format("ED06 %d %d",
				codelen & 0xFFFF_FFFFL, _CODE_SIZE_LIMIT));
		
		// Read code and save it for later after the exception table and
		// possibly the stack map table parse has been parsed
		byte[] code = new byte[codelen];
		input.readFully(code);
		
		// Read the exception table
		int numex = input.readUnsignedShort();
		for (int i = 0; i < numex; i++)
			throw new Error("TODO");
		
		// Read attributes
		int na = input.readUnsignedShort();
		for (int i = 0; i < na; i++)
			__readAttribute(pool, input);
		
		// If no stack map table exists then setup an initial implicit state
		Map<Integer, __SMTState__> smt;
		if ((smt = this._smt) == null)
			this._smt = (smt = __SMTParser__.__initialState(this._flags,
				this._type, maxstack, maxlocals));
		
		// Prime arguments
		__primeArguments();
		
		// Parse the byte code now
		try (ExtendedDataInputStream dis = new ExtendedDataInputStream(
			new ByteArrayInputStream(code)))
		{
			// Mark the stream and determine the jump targets, this information
			// is passed to the method writer so that it is not forced to
			// store state for any position that is not a jump target
			dis.mark(codelen);
			writer.jumpTargets(new __JumpTargetCalc__(dis, codelen).targets());
			
			// Reset and decode operations
			dis.reset();
			new __OpParser__(writer, dis, smt, this._decoder._classflags,
				pool).__decodeAll();
		}
		
		// Done
		throw new Error("TODO");
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/08/27
	 */
	@Override
	void __handleAttribute(String __name, DataInputStream __is)
		throws IOException
	{
		// Check
		if (__name == null || __is == null)
			throw new NullPointerException("NARG");
		
		// Which attribute?
		boolean old = false;
		switch (__name)
		{
				// The stack map table
			case "StackMap":
				old = true;
			case "StackMapTable":
				// {@squirreljme.error ED0t Only a single stack map table is
				// permitted in a code attribute.}
				if (this._smt != null)
					throw new JITException("ED0t");
				
				// Parse and store result
				this._smt = new __SMTParser__(!old, __is, this._flags,
					this._type, this._maxstack, this._maxlocals).result();
				return;
			
				// Unknown
			default:
				break;
		}
	}
	
	/**
	 * Primes the input arguments.
	 *
	 * @since 2016/08/29
	 */
	private void __primeArguments()
	{
		// Get initial state
		__SMTLocals__ locals = this._smt.get(0)._locals;
		
		// Setup output arguments
		List<JITVariableType> args = new ArrayList<>();
		int n = locals.size();
		for (int i = 0; i < n; i++)
		{
			// Get
			__SMTType__ t = locals.get(i);
			
			// No more arguments?
			if (t == __SMTType__.NOTHING)
				break;
			
			// Map
			args.add(__remapType(t.map()));
		}
		
		// Prime it
		this._writer.primeArguments(args.<JITVariableType>toArray(
			new JITVariableType[args.size()]));
	}
	
	/**
	 * Remaps the specified type, if it is a floating point type and hardware
	 * support is not enabled for that specified type then it is translated
	 * into an integer type.
	 *
	 * @param __t The type to remap.
	 * @return {@code __t} or the remapped type.
	 * @throws NullPointerException On null arguments.
	 * @since 2016/09/03
	 */
	JITVariableType __remapType(JITVariableType __t)
		throws NullPointerException
	{
		// Check
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Depends
		switch (__t)
		{
				// If software floating point is used for either type then
				// remap
			case FLOAT: return (this._hwfloat ? __t : JITVariableType.INTEGER);
			case DOUBLE: return (this._hwdouble ? __t : JITVariableType.LONG);
			
				// Keep the same
			default:
				return __t;
		}
	}
}

