// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) 2013-2016 Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) 2013-2016 Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// For more information see license.mkd.
// ---------------------------------------------------------------------------

package net.multiphasicapps.tests.util.sorted;

import java.util.Objects;
import java.util.Random;
import net.multiphasicapps.tests.IndividualTest;
import net.multiphasicapps.tests.InvalidTestException;
import net.multiphasicapps.tests.TestComparison;
import net.multiphasicapps.tests.TestGroupName;
import net.multiphasicapps.tests.TestInvoker;
import net.multiphasicapps.tests.TestFamily;
import net.multiphasicapps.tests.TestFragmentName;
import net.multiphasicapps.util.sorted.SortedTreeSet;

/**
 * Contains tests for the sorted set.
 *
 * @since 2016/09/07
 */
public class SortedTreeSetTests
	implements TestInvoker
{
	/** The default seed. */
	public static final long DEFAULT_SEED =
		0x537175697272656CL;
	
	/** Number of entries to place. */
	public static final int ENTRY_COUNT =
		256;
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/07
	 */
	@Override
	public TestFamily testFamily()
	{
		return new TestFamily(
			"net.multiphasicapps.util.sorted.SortedTreeSet",
			Integer.toString(0xCAFEBABE));
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2016/09/07
	 */
	@Override
	public void runTest(IndividualTest __t)
		throws NullPointerException, Throwable
	{
		// Check
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Create random seems for 
		long seed = Long.parseLong(__t.subName().toString());
		Random rvals = new Random(seed);
		
		// Setup
		SortedTreeSet<Byte> target = new SortedTreeSet<>();
		
		// Add values to the set
		int n = ENTRY_COUNT;
		int totalkeys = 0;
		for (int i = 0; i < n; i++)
			if (target.add((byte)rvals.nextInt()))
				totalkeys++;
		
		// Verify the iteration order
		int last = Integer.MIN_VALUE;
		int count = 0;
		for (int i : target)
			if (i > last)
			{
				count++;
				last = i;
			}
		
		// Check
		__t.result("ordercount").compareInt(TestComparison.EQUALS, count,
			target.size());
		__t.result("note").note(target);
	}
}

