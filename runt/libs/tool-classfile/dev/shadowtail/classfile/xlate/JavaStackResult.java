// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package dev.shadowtail.classfile.xlate;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.multiphasicapps.classfile.ClassName;
import net.multiphasicapps.classfile.JavaType;

/**
 * This represents the result of operations performed on the Java stack.
 *
 * This class is immutable.
 *
 * @since 2019/03/30
 */
public final class JavaStackResult
{
	/** Input to the zero register. */
	public static final JavaStackResult.Input INPUT_ZERO =
		new JavaStackResult.Input(0, JavaType.NOTHING, true);
	
	/** Output to the zero register. */
	public static final JavaStackResult.Output OUTPUT_ZERO =
		new JavaStackResult.Output(0, JavaType.NOTHING, true);
	
	/** The stack state before. */
	public final JavaStackState before;
	
	/** The stack state after. */
	public final JavaStackState after;
	
	/** Enqueue list. */
	public final JavaStackEnqueueList enqueue;
	
	/** State operations. */
	public final StateOperations ops;
	
	/** Input. */
	private final JavaStackResult.Input[] _in;
	
	/** Output. */
	private final JavaStackResult.Output[] _out;
	
	/** String representation. */
	private Reference<String> _string;
	
	/**
	 * Initializes the result of the operation
	 *
	 * @param __bs The previous stack state.
	 * @param __as The after (the new) stack state.
	 * @param __eq Enqueue list, may be {@code null}.
	 * @param __io Input/output.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/31
	 */
	public JavaStackResult(JavaStackState __bs, JavaStackState __as,
		JavaStackEnqueueList __eq, InputOutput... __io)
		throws NullPointerException
	{
		this(__bs, __as, __eq, (StateOperations)null, __io);
	}
	
	/**
	 * Initializes the result of the operation
	 *
	 * @param __bs The previous stack state.
	 * @param __as The after (the new) stack state.
	 * @param __eq Enqueue list, may be {@code null}.
	 * @param __ops State operations.
	 * @param __io Input/output.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/04/11
	 */
	public JavaStackResult(JavaStackState __bs, JavaStackState __as,
		JavaStackEnqueueList __eq, StateOperations __ops, InputOutput... __io)
		throws NullPointerException
	{
		if (__bs == null || __as == null)
			throw new NullPointerException("NARG");
		
		// Sort through input/output and put into their own pile
		List<Input> in = new ArrayList<>();
		List<Output> out = new ArrayList<>();
		for (InputOutput x : (__io = (__io == null ?
			new InputOutput[0] : __io.clone())))
			if (x == null)
				throw new NullPointerException("NARG");
			else if (x instanceof Input)
				in.add((Input)x);
			else
				out.add((Output)x);
		
		this.before = __bs;
		this.after = __as;
		this.enqueue = (__eq == null ? new JavaStackEnqueueList(0) : __eq);
		this._in = in.<Input>toArray(new Input[in.size()]);
		this._out = out.<Output>toArray(new Output[out.size()]);
		this.ops = (__ops == null ? new StateOperations() : __ops);
		
		// Debug
		if (__Debug__.ENABLED)
		{
			todo.DEBUG.note("*** Stack Result ***");
			todo.DEBUG.note("BEF: %s", __bs);
			todo.DEBUG.note("AFT: %s", __as);
			if (__eq != null && !__eq.isEmpty())
				todo.DEBUG.note("ENQ: %s", __eq);
			todo.DEBUG.note("IN : %s", in);
			todo.DEBUG.note("OUT: %s", out);
			if (__ops != null && !__ops.isEmpty())
				todo.DEBUG.note("OPS: %s", __ops);
			todo.DEBUG.note("********************");
		}
	}
	
	/**
	 * Represents the new state after the operation was performed.
	 *
	 * @return The state that is the result of the operation.
	 * @since 2019/03/30
	 */
	public final JavaStackState after()
	{
		return this.after;
	}
	
	/**
	 * Represents the previous state which this was based off.
	 *
	 * @return The previous state this originated from.
	 * @since 2019/03/30
	 */
	public final JavaStackState before()
	{
		return this.before;
	}
	
	/**
	 * Returns the enqueue list which represents everything that is to be
	 * uncounted after the operation completes.
	 *
	 * @return The enqueue list, will be empty if there is nothing to
	 * enqueue.
	 * @since 2019/03/30
	 */
	public final JavaStackEnqueueList enqueue()
	{
		return this.enqueue;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/30
	 */
	@Override
	public final boolean equals(Object __o)
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/30
	 */
	@Override
	public final int hashCode()
	{
		throw new todo.TODO();
	}
	
	/**
	 * Returns the input.
	 *
	 * @return The input.
	 * @since 2019/04/08
	 */
	public final JavaStackResult.Input[] in()
	{
		return this._in.clone();
	}
	
	/**
	 * Returns the information on the input.
	 *
	 * @param __i The input to get.
	 * @return The information on the input.
	 * @since 2019/03/30
	 */
	public final JavaStackResult.Input in(int __i)
	{
		return this._in[__i];
	}
	
	/**
	 * Returns the number of generated inputs.
	 *
	 * @return The input count.
	 * @since 2019/03/30
	 */
	public final int inCount()
	{
		return this._in.length;
	}
	
	/**
	 * The operations to be performed.
	 *
	 * @return The operations to perform.
	 * @since 2019/04/11
	 */
	public final StateOperations operations()
	{
		return this.ops;
	}
	
	/**
	 * Returns the output.
	 *
	 * @return The output.
	 * @since 2019/04/08
	 */
	public final JavaStackResult.Output[] out()
	{
		return this._out.clone();
	}
	
	/**
	 * Returns the information on the output.
	 *
	 * @param __i The output to get.
	 * @return The information on the output.
	 * @since 2019/03/30
	 */
	public final JavaStackResult.Output out(int __i)
	{
		return this._out[__i];
	}
	
	/**
	 * Returns the number of generated outputs.
	 *
	 * @return The output count.
	 * @since 2019/03/30
	 */
	public final int outCount()
	{
		return this._out.length;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2019/03/30
	 */
	@Override
	public final String toString()
	{
		Reference<String> ref = this._string;
		String rv;
		
		if (ref == null || null == (rv = ref.get()))
			this._string = new WeakReference<>((rv = String.format(
				"Result:{bef=%s, aft=%s, enq=%s, in=%s, out=%s, ops=%s}",
				this.before, this.after, this.enqueue,
				Arrays.asList(this._in), Arrays.asList(this._out),
				this.ops)));
		
		return rv;
	}
	
	/**
	 * Makes an input.
	 *
	 * @param __i The info to base from.
	 * @return The input.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/31
	 */
	public static final Input makeInput(JavaStackState.Info __i)
		throws NullPointerException
	{
		return new Input(__i);
	}
	
	/**
	 * Makes an output.
	 *
	 * @param __i The info to base from.
	 * @return The output.
	 * @throws NullPointerException On null arguments.
	 * @since 2019/03/31
	 */
	public static final Output makeOutput(JavaStackState.Info __i)
		throws NullPointerException
	{
		return new Output(__i);
	}
	
	/**
	 * Input information.
	 *
	 * @since 2019/03/30
	 */
	public static final class Input
		implements InputOutput
	{
		/** The register used for input. */
		public final int register;
		
		/** The type which was read. */
		public final JavaType type;
		
		/** Not counting? */
		public final boolean nocounting;
	
		/** String representation. */
		private Reference<String> _string;
		
		/**
		 * Initializes the input.
		 *
		 * @param __i The info to base off.
		 * @throws NullPointerException On null arguments.
		 * @since 2019/03/31
		 */
		public Input(JavaStackState.Info __i)
			throws NullPointerException
		{
			if (__i == null)
				throw new NullPointerException("NARG");
			
			this.register = __i.value;
			this.type = __i.type;
			this.nocounting = __i.nocounting;
		}
		
		/**
		 * Initializes the input.
		 *
		 * @param __r The register used.
		 * @param __t The type used.
		 * @param __nc Is not counting?
		 * @throws NullPointerException On null arguments.
		 * @since 2019/04/08
		 */
		public Input(int __r, JavaType __t, boolean __nc)
			throws NullPointerException
		{
			if (__t == null)
				throw new NullPointerException("NARG");
			
			this.register = __r;
			this.type = __t;
			this.nocounting = __nc;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2019/03/31
		 */
		@Override
		public final boolean equals(Object __o)
		{
			throw new todo.TODO();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2019/03/31
		 */
		@Override
		public final int hashCode()
		{
			throw new todo.TODO();
		}
		
		/**
		 * Checks if the type is an array.
		 *
		 * @return True if the type is an array.
		 * @since 2019/05/25
		 */
		public final boolean isArray()
		{
			JavaType type = this.type;
			return type.isObject() && type.isArray();
		}
		
		/**
		 * Checks if this type is quickly compatible with the given class.
		 *
		 * @param __cl If this is compatible with the given class.
		 * @return True if this is quickly compatible.
		 * @throws NullPointerException On null arguments.
		 * @since 2019/05/24
		 */
		public final boolean isCompatible(ClassName __cl)
			throws NullPointerException
		{
			if (__cl == null)
				throw new NullPointerException("NARG");
			
			JavaType type = this.type;
			return (type.isObject() || type.isPrimitive()) &&
				__cl.equals(type.className());
		}
		
		/**
		 * Is this an object?
		 *
		 * @return If this is an object.
		 * @since 2019/06/12
		 */
		public final boolean isObject()
		{
			return this.type.isObject();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2019/03/31
		 */
		@Override
		public final String toString()
		{
			Reference<String> ref = this._string;
			String rv;
			
			if (ref == null || null == (rv = ref.get()))
				this._string = new WeakReference<>((rv = String.format(
					"In:{r=r%d, type=%s, flags=%s}",
					this.register, this.type, (this.nocounting ? "NC" : ""))));
			
			return rv;
		}
	}
	
	/**
	 * Used to flag input and output.
	 *
	 * @since 2019/03/31
	 */
	public static interface InputOutput
	{
	}
	
	/**
	 * Output information.
	 *
	 * @since 2019/03/30
	 */
	public static final class Output
		implements InputOutput
	{
		/** The register used for output. */
		public final int register;
		
		/** The output type. */
		public final JavaType type;
		
		/** Not counting? */
		public final boolean nocounting;
	
		/** String representation. */
		private Reference<String> _string;
		
		/**
		 * Initializes the output.
		 *
		 * @param __i The info to base off.
		 * @throws NullPointerException On null arguments.
		 * @since 2019/03/31
		 */
		public Output(JavaStackState.Info __i)
			throws NullPointerException
		{
			if (__i == null)
				throw new NullPointerException("NARG");
			
			this.register = __i.value;
			this.type = __i.type;
			this.nocounting = __i.nocounting;
		}
		
		/**
		 * Initializes the output.
		 *
		 * @param __r The register used.
		 * @param __t The type used.
		 * @param __nc Is not counting?
		 * @throws NullPointerException On null arguments.
		 * @since 2019/04/08
		 */
		public Output(int __r, JavaType __t, boolean __nc)
			throws NullPointerException
		{
			if (__t == null)
				throw new NullPointerException("NARG");
			
			this.register = __r;
			this.type = __t;
			this.nocounting = __nc;
		}
		
		/**
		 * Returns this output as an input.
		 *
		 * @return The input.
		 * @since 2019/04/12
		 */
		public final Input asInput()
		{
			return new Input(this.register, this.type, this.nocounting);
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2019/03/31
		 */
		@Override
		public final boolean equals(Object __o)
		{
			throw new todo.TODO();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2019/03/31
		 */
		@Override
		public final int hashCode()
		{
			throw new todo.TODO();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2019/03/31
		 */
		@Override
		public final String toString()
		{
			Reference<String> ref = this._string;
			String rv;
			
			if (ref == null || null == (rv = ref.get()))
				this._string = new WeakReference<>((rv = String.format(
					"Out:{r=r%d, type=%s, flags=%s}",
					this.register, this.type, (this.nocounting ? "NC" : ""))));
			
			return rv;
		}
	}
}

