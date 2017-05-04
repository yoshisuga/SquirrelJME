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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;
import net.multiphasicapps.util.msd.MultiSetDeque;
import net.multiphasicapps.util.sorted.SortedTreeSet;
import net.multiphasicapps.util.unmodifiable.UnmodifiableList;

/**
 * This represents the currently active cache state which is used to store
 * bindings.
 *
 * @since 2017/02/23
 */
public final class ActiveCacheState
	extends CacheState
{
	/** Comparator for aliased by slots. */
	private static final Comparator<ActiveCacheState.Slot> _SLOT_COMPARATOR =
		new Comparator<ActiveCacheState.Slot>()
		{
			/**
			 * {@inheritDoc}
			 * @since 2017/03/25
			 */
			@Override
			public int compare(ActiveCacheState.Slot __a,
				ActiveCacheState.Slot __b)
			{
				// Area first
				int rv = __a.thisArea().ordinal() - __b.thisArea().ordinal();
				if (rv != 0)
					return rv;
				
				// Then the index
				return __a.thisIndex() - __b.thisIndex();
			}
		};
	
	/** Stack code variables. */
	protected final Tread stack;
	
	/** Local code variables. */
	protected final Tread locals;
	
	/** The working tread. */
	protected final Tread work;
	
	/** Registers available for allocation. */
	protected final MultiSetDeque<Register> foralloc =
		new MultiSetDeque<>();
	
	/** Saved integer registers available for allocation. */
	protected final Deque<Register> savedint =
		this.foralloc.subDeque();
	
	/** Saved float registers available for allocation. */
	protected final Deque<Register> savedfloat =
		this.foralloc.subDeque();
	
	/** Temporary integer registers available for allocation. */
	protected final Deque<Register> tempint =
		this.foralloc.subDeque();
	
	/** Temporary float registers available for allocation. */
	protected final Deque<Register> tempfloat =
		this.foralloc.subDeque();
	
	/** The register dictionary. */
	protected final RegisterDictionary rdict;
	
	/** Saved registers available for allocation. */
	private final Set<Register> _availsaved;
	
	/** Temporary registers available for allocation. */
	private final Set<Register> _availtemp;
	
	/** The size of the stack. */
	private volatile int _stacksize;
	
	/**
	 * Initializes the active cache state which stores the current state
	 * information.
	 *
	 * @param __te The code stream
	 * @param __ms The number of stack entries.
	 * @param __ml The number of local entries.
	 * @param __conf The JIT configuration.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/23
	 */
	ActiveCacheState(__Code__ __cs, int __ms, int __ml,
		JITConfig __conf)
		throws NullPointerException
	{
		super(__cs);
		
		// Check
		if (__conf == null)
			throw new NullPointerException("NARG");
		
		// Setup treads
		this.stack = new Tread(AreaType.STACK, __ms);
		this.locals = new Tread(AreaType.LOCAL, __ml);
		this.work = new Tread(AreaType.WORK, __Code__._WORK_COUNT);
		
		// Initialize dictionary
		RegisterDictionary rdict = __conf.registerDictionary();
		this.rdict = rdict;
		
		// Available register sets
		this._availsaved = rdict.bothAllocationRegisters(true);
		this._availtemp = rdict.bothAllocationRegisters(false);
		
		// Initialize register deque
		__initDeque();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/03/03
	 */
	@Override
	public Slot getSlot(CodeVariable __cv)
		throws NullPointerException
	{
		return (Slot)super.getSlot(__cv);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/03/03
	 */
	@Override
	public Slot getSlot(AreaType __s, int __i)
		throws NullPointerException
	{
		return (Slot)super.getSlot(__s, __i);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/03/31
	 */
	@Override
	public Tread getTread(AreaType __a)
	{
		return (Tread)super.getTread(__a);
	}
	
	/**
	 * Checks whether the given register is free for allocation.
	 *
	 * @param __r The register to check if it is available.
	 * @return {@code true} if the specified register is available for
	 * allocation.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/25
	 */
	public boolean isRegisterAvailable(Register __r)
		throws NullPointerException
	{
		// Check
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// It can be in any queue
		return this.foralloc.contains(__r);
	}
	
	/**
	 * Checks if the specified register list and all of its registers are
	 * available for allocation.
	 *
	 * @param __r The register to check if it is fully available.
	 * @return {@code true} if all registers in the register list are
	 * available.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/25
	 */
	public boolean isRegisterListAvailable(RegisterList __r)
		throws NullPointerException
	{
		// Check
		if (__r == null)
			throw new NullPointerException("NARG");
		
		// Go through each register in the list
		for (int i = 0, n = __r.size(); i < n; i++)
			if (!isRegisterAvailable(__r.get(i)))
				return false;
		
		// Is available
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/02/23
	 */
	@Override
	public ActiveCacheState.Tread locals()
	{
		return this.locals;
	}
	
	/**
	 * Pops the given type from this stack and returns the slot in the base
	 * stack.
	 *
	 * @param __base The input state which is sourced for returning, the return
	 * value will be a slot in this state
	 * @param __t The type of value to pop.
	 * @return The slot at the top of the base stack, the stack this method
	 * is called on will be modified accordingly.
	 * @throws JITException If the type is not at the top of the stack, the
	 * stack underflows, or the base state does not match the current top
	 * element.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/30
	 */
	public CacheState.Slot pop(CacheState __base, JavaType __t)
		throws JITException, NullPointerException
	{
		// Check
		if (__base == null || __t == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
	
	/**
	 * Pushes a copy of another slot value onto the stack, it will become an
	 * alias to that value.
	 *
	 * @param __s The slot to copy the value from.
	 * @return The allocated element at the top of the stack.
	 * @throws JITException If the stack overflows or the slot is not valid
	 * (such as if the source is not a valid copy).
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/30
	 */
	public ActiveCacheState.Slot pushCopy(CacheState.Slot __s)
		throws JITException, NullPointerException
	{
		// Check
		if (__s == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error AQ24 Cannot push the specified slot to the
		// stack because it is not a valid type. (The slot being copied)}
		JavaType t = __s.valueType();
		if (t == null || !t.isValid())
			throw new JITException(String.format("AQ24 %s", __s));
		
		// {@squirreljme.error AQ25 Pushing the specified slot to the stack
		// would cause it to overflow. (The slot being copied; The next stack
		// size; The stack limit)}
		Tread stack = this.stack;
		int stacksize = this._stacksize,
			stacklimit = stack.size(),
			pushcount = (t.isWide() ? 2 : 1),
			nextsize = stacksize + pushcount;
		if (nextsize > stacklimit)
			throw new JITException(String.format("AQ25 %s %d %d", __s,
				nextsize, stacklimit));
		
		// Alias this slot to the other
		Slot rv;
		(rv = stack.get(stacksize)).__aliasTo(__s);
		
		// Use that stack entry
		return rv;
	}
	
	/**
	 * Pushes a new value onto the stack which is not a copy.
	 *
	 * @param __t The type of value to push.
	 * @return The slot for the newly pushed stack entry.
	 * @throws JITException If the stack overflows or the type is not valid.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/04/30
	 */
	public ActiveCacheState.Slot pushNew(JavaType __t)
		throws JITException, NullPointerException
	{
		// Check
		if (__t == null)
			throw new NullPointerException("NARG");
		
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/02/23
	 */
	@Override
	public ActiveCacheState.Tread stack()
	{
		return this.stack;
	}
	
	/**
	 * Returns the current size of the stack.
	 *
	 * @return The current stack size.
	 * @since 2017/04/26
	 */
	@Override
	public int stackSize()
	{
		return this._stacksize;
	}
	
	/**
	 * Switches to the specified cache state.
	 *
	 * @param __cs The state to set.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/23
	 */
	public void switchFrom(CacheState __cs)
		throws NullPointerException
	{
		// Check
		if (__cs == null)
			throw new NullPointerException("NARG");
		
		// Initialize the deques for allocation
		__initDeque();
		
		// Restore state
		Tread stack = this.stack,
			locals = this.locals,
			work = this.work;
		stack.__switchFrom(__cs.stack());
		locals.__switchFrom(__cs.locals());
		work.__switchFrom(__cs.work());
		
		// Correct aliased by
		stack.__fixAliasedBy();
		locals.__fixAliasedBy();
		work.__fixAliasedBy();
		
		// Correct stack top
		this._stacksize = __cs.stackSize();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/03/25
	 */
	@Override
	public String toString()
	{
		// Start with the parent state
		StringBuilder sb = new StringBuilder("{state=");
		sb.append(super.toString());
		
		// Then add the remaining registers
		sb.append(", free=");
		sb.append(this.foralloc);
		
		// Finish
		sb.append('}');
		return sb.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/03/31
	 */
	@Override
	public Tread work()
	{
		return this.work;
	}
	
	/**
	 * Initializes the register dequeues.
	 *
	 * @sine 2017/03/25
	 */
	private void __initDeque()
	{
		// Remove all allocations
		this.foralloc.clear();
		
		// Saved the temporary
		Deque<Register> toint, tofloat;
		for (int z = 0; z < 2; z++)
		{
			// Saved
			Set<Register> source;
			if (z == 0)
			{
				source = this._availsaved;
				toint = this.savedint;
				tofloat = this.savedfloat;
			}
			
			// Temporary
			else
			{
				source = this._availtemp;
				toint = this.tempint;
				tofloat = this.tempfloat;
			}
			
			// Fill
			for (Register r : source)
			{
				if (r.isInteger())
					toint.offerLast(r);
				if (r.isFloat())
					tofloat.offerLast(r);
			}
		}
	}
	
	/**
	 * The slot stores the information on the current element.
	 *
	 * @since 2017/02/23
	 */
	public final class Slot
		extends CacheState.Slot
	{
		/** Slots which alias this slot. */
		private final Set<Slot> _aliasedby =
			new SortedTreeSet<>(_SLOT_COMPARATOR);
		
		/** The type of value stored here. */
		private volatile JavaType _type =
			JavaType.NOTHING;
		
		/** Aliased to the stack?. */
		private volatile AreaType _atalias;
		
		/** Slot this is aliased to. */
		private volatile int _idalias =
			-1;
		
		/** The allocation used for this slot. */
		private volatile TypedAllocation _alloc;
		
		/**
		 * Initializes the slot.
		 *
		 * @param __stack The area this slot is in.
		 * @param __i The index of this slot.
		 * @throws NullPointerException On null arguments.
		 * @since 2017/02/23
		 */
		private Slot(AreaType __a, int __i)
			throws NullPointerException
		{
			super(__a, __i);
		}
		
		/**
		 * Forces the slot to have the given allocation and the given type.
		 *
		 * @param __aa The allocation to use for this slot. If the type is
		 * {@link JavaType#TOP} then this must be {@code null}.
		 * @param __t The Java type to store in this slot, this cannot be null.
		 * would cause an invalid state to be set.
		 * @throws JITException If the allocation cannot be set because it
		 * would not be valid.
		 * @throws NullPointerException The Java type to store in this slot.
		 * @since 2017/04/24
		 */
		public void forceAllocation(TypedAllocation __aa, JavaType __t)
			throws JITException, NullPointerException
		{
			// Check
			if (__t == null)
				throw new NullPointerException("NARG");
				
			RegisterList rl = null;
			MultiSetDeque<Register> ms = ActiveCacheState.this.foralloc;
			
			// The top type has specific conditions
			if (__t == JavaType.TOP)
			{			
				// {@squirreljme.error AQ1x The top type cannot have an
				// allocation specified for it. (The allocation)}
				if (__aa != null)
					throw new JITException(String.format("AQ1x %s", __aa));
				
				// {@squirreljme.error AQ20 Cannot set the top type at the
				// front of the tread. (This slot index)}
				Tread t = thisTread();
				int dx = super.index;
				if (dx <= 0)
					throw new JITException(String.format("AQ20 %d", dx));
				
				// {@squirreljme.error AQ21 Cannot set the top type because the
				// previous entry is not wide. (The previous type)}
				JavaType pt = t.get(dx - 1).thisType();
				if (!pt.isWide())
					throw new JITException(String.format("AQ21 %s", pt));
			}
			
			// {@squirreljme.error AQ1y The specified type cannot be set on
			// a slot. (The type)}
			else if (!__t.isValid())
				throw new JITException(String.format("AQ1y %s", __t));
			
			// {@squirreljme.error AQ1z Cannot force the slot to have the
			// given allocation because the registers it uses are already
			// allocated. (The allocation)}
			else if (__aa != null && __aa.hasRegisters() &&
				!isRegisterListAvailable((rl = __aa.registers())))
				throw new JITException(String.format("AQ1z %s %s", __aa, ms));
			
			// Destroy the value following if this is wide
			if (__t.isWide())
			{
				// {@squirreljme.error AQ22 Cannot set the specified type to
				// wide because the top overflows the tread. (The type to set;
				// The current slot index)}
				Tread t = thisTread();
				int dx = super.index;
				if (dx + 1 >= t.size())
					throw new JITException(String.format("AQ22 %s %d", __t,
						dx));
			}
			
			// Remove the previous allocation (give back registers)
			TypedAllocation was = this._alloc;
			if (was != null)
				throw new todo.TODO();
				
			// Is OK set it, also this is not aliased at all
			this._type = __t;
			this._alloc = __aa;
			this._atalias = null;
			this._idalias = -1;
			
			// Actually do it
			if (__t.isWide())
				thisTread().get(super.index + 1).forceAllocation(null,
					JavaType.TOP);
			
			// Remove all registers the allocation uses
			if (rl == null)
				for (int i = 0, n = rl.size(); i < n; i++)
					ms.remove(rl.get(i));
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/03/31
		 */
		@Override
		protected AreaType thisAliasedArea()
		{
			return this._atalias;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/03/31
		 */
		@Override
		protected int thisAliasedIndex()
		{
			return this._idalias;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/04/25
		 */
		@Override
		protected TypedAllocation thisAllocation()
		{
			return this._alloc;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/03/07
		 */
		@Override
		public Tread thisTread()
		{
			return (Tread)super.thisTread();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/02/23
		 */
		@Override
		public JavaType thisType()
		{
			return this._type;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/03/25
		 */
		@Override
		public String toString()
		{
			// If aliased by nothing, do not print that information
			Set<Slot> aliasedby = this._aliasedby;
			if (aliasedby.isEmpty())
				return super.toString();
			
			// Otherwise do build it
			StringBuilder sb = new StringBuilder(super.toString());
			sb.append("<~[");
			
			// Go through aliases and just print their IDs
			boolean comma = false;
			for (Slot s : aliasedby)
			{
				// Space?
				if (!comma)
					comma = true;
				else
					sb.append(", ");
				
				// Print info
				sb.append(s.thisArea());
				sb.append('#');
				sb.append(s.thisIndex());
			}
			
			// Finish
			sb.append("]");
			return sb.toString();
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/03/06
		 */
		@Override
		public Slot value()
		{
			return (Slot)super.value();
		}
		
		/**
		 * Aliases this slot to the specified slot.
		 *
		 * @param __s The slot to alias to.
		 * @throws JITException If the alias is not valid.
		 * @throws NullPointerException On null arguments.
		 * @since 2017/05/02
		 */
		private void __aliasTo(CacheState.Slot __s)
			throws JITException, NullPointerException
		{
			// Check
			if (__s == null)
				throw new NullPointerException("NARG");
			
			// Get the actual slot so that there are no aliases of aliases
			__s = __s.value();
			AreaType ta = __s.thisArea();
			int ti = __s.thisIndex();
			ActiveCacheState.Tread tt = thisTread();
			
			// {@squirreljme.error AQ26 Cannot alias to the target slot because
			// it is not valid. (The slot to alias)}
			JavaType tojt = __s.thisType();
			if (!tojt.isValid())
				throw new JITException(String.format("AQ26 %s", __s));
			
			// {@squirreljme.error AQ27 Cannot alias to the target because it
			// overflows the target tread. (The slot to alias; The target area;
			// The target index)}
			boolean wide;
			if ((wide = tojt.isWide()))
				if (ti + 1 >= tt.size())
					throw new JITException(String.format("AQ27 %s %s %d", __s,
						ta, ti));
			
			// Return any used registers for this slot
			__deallocate();
			
			// Deallocate the following slot if this type is wide because this
			// could be replacing for example: int int
			if (wide)
				tt.get(thisIndex() + 1).__deallocate();
			
			// Set target
			this._type = tojt;
			this._atalias = ta;
			this._idalias = ti;
			
			// No allocation is used (the target is used)
			this._alloc = null;
			
			// Add this slot to the aliased by to the target
			getSlot(ta, ti)._aliasedby.add(this);
		}
		
		/**
		 * De-allocates this slot so that it no longer has an allocated
		 * assignment.
		 *
		 * @since 2017/05/02
		 */
		private void __deallocate()
		{
			// Check if nothing needs to be done
			if (true)
				throw new todo.TODO();
			
			// Check if a de-alias operation has to be performed on these
			if (true)
				throw new todo.TODO();
			
			// Clear values/dealias and any TOP values for long/double
			if (true)
				throw new todo.TODO();
			
			throw new todo.TODO();
		}
		
		/**
		 * Switches to the specified state.
		 *
		 * @param __t The slot to copy from.
		 * @throws NullPointerException On null arguments.
		 * @since 2017/03/01
		 */
		private void __switchFrom(CacheState.Slot __s)
			throws NullPointerException
		{
			// Check
			if (__s == null)
				throw new NullPointerException("NARG");
			
			// The aliased by is set later
			this._aliasedby.clear();
			
			// Copy state
			CacheState.Slot value = __s.value();
			this._type = value.thisType();
			this._alloc = __s.thisAllocation();
			
			// Aliased?
			if (value != __s)
			{
				this._atalias = value.thisArea();
				this._idalias = value.thisIndex();
			}
			
			// Not aliased
			else
			{
				this._atalias = null;
				this._idalias = -1;
			}
		}
	}
	
	/**
	 * This is used to represent an active tread.
	 *
	 * @since 2017/02/23
	 */
	public final class Tread
		extends CacheState.Tread
	{
		/** Slots. */
		private final Slot[] _slots;
		
		/**
		 * Initializes the tread.
		 *
		 * @param __a The area type.
		 * @param __n The number of slots.
		 * @throws NullPointerException On null arguments.
		 * @since 2017/02/23
		 */
		private Tread(AreaType __a, int __n)
			throws NullPointerException
		{
			super(__a);
			
			// Initialize slots
			Slot[] slots = new Slot[__n];
			for (int i = 0; i < __n; i++)
				slots[i] = new Slot(__a, i);
			this._slots = slots;
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/02/23
		 */
		@Override
		public Slot get(int __i)
		{
			return this._slots[__i];
		}
		
		/**
		 * {@inheritDoc}
		 * @since 2017/02/23
		 */
		@Override
		public int size()
		{
			return this._slots.length;
		}
		
		/**
		 * {@Override}
		 * @since 2017/03/07
		 */
		@Override
		public ActiveCacheState state()
		{
			return ActiveCacheState.this;
		}
		
		/**
		 * Fixes the aliased by set for the slots.
		 *
		 * @since 2017/03/25
		 */
		private void __fixAliasedBy()
		{
			Slot[] slots = this._slots;
			for (int i = 0, n = slots.length; i < n; i++)
			{
				// This slot and its value
				Slot self = slots[i],
					value = self.value();
				
				// If this slot aliases another then point to it
				if (self != value)
					value._aliasedby.add(self);
			}
		}
		
		/**
		 * Switches to the specified state.
		 *
		 * @param __t The tread to copy from.
		 * @throws NullPointerException On null arguments.
		 * @since 2017/03/01
		 */
		private void __switchFrom(CacheState.Tread __t)
			throws NullPointerException
		{
			// Check
			if (__t == null)
				throw new NullPointerException("NARG");
			
			// Go through all variables
			Slot[] slots = this._slots;
			for (int i = 0, n = slots.length; i < n; i++)
				slots[i].__switchFrom(__t.get(i));
		}
	}
}

