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
 * This contains boot ROM information.
 *
 * @since 2019/06/23
 */
public final class BootRom
{
	/** Boot libraries which have been loaded. */
	public static volatile BootLibrary[] BOOT_LIBRARIES;
	
	/** The offset to the jar count. */
	public static final byte ROM_NUMJARS_OFFSET =
		4;
	
	/** Offset to the table of contents offset. */
	public static final byte ROM_TOCOFFSET_OFFSET =
		8;
		
	/** The index of the JAR which should be the boot point. */
	public static final byte ROM_BOOTJARINDEX_OFFSET =
		12;
	
	/** The offset into the packfile where the boot entry is. */
	public static final byte ROM_BOOTJAROFFSET_OFFSET =
		16;
	
	/** The size of the boot jar. */
	public static final byte ROM_BOOTJARSIZE_OFFSET =
		20;
	
	/** Initial class path library indexes. */
	public static final byte ROM_BOOTICPOFFSET_OFFSET =
		24;
	
	/** Initial class path library index count. */
	public static final byte ROM_BOOTICPSIZE_OFFSET =
		28;
	
	/** Initial main class. */
	public static final byte ROM_BOOTMAINCLASS_OFFSET =
		32;
	
	/** Table of contents size. */
	public static final byte TOC_ENTRY_SIZE =
		20;
	
	/** Table of contents name offset. */
	public static final byte TOC_NAME_OFFSET =
		0;
	
	/** Table of contents JAR data offset. */
	public static final byte TOC_JAR_OFFSET =
		4;
	
	/** Table of contents size of the JAR. */
	public static final byte TOC_JARLEN_OFFSET =
		8;
	
	/** Table of contents manifest offset. */
	public static final byte TOC_MANIFEST_OFFSET =
		12;
	
	/** Table of contents length of manifest. */
	public static final byte TOC_MANIFEST_LENGTH_OFFSET =
		16;
	
	
	/**
	 * Returns the boot libraries which make up the initial classpath.
	 *
	 * @param __rombase The ROM base.
	 * @param __config The configuration system to use.
	 * @return The libraries to set for the initial classpath.
	 * @since 2019/06/20
	 */
	public static final BootLibrary[] initialClasspath(int __rombase,
		ConfigReader __config)
	{
		// Load all libraries
		BootLibrary[] bootlibs = BootRom.bootLibraries(__rombase);
		if (bootlibs == null)
			Assembly.breakpoint();
		int numboot = bootlibs.length;
		
		// The initial class path to use
		BootLibrary[] usecp;
		
		// Use the passed class-path if one was specified.
		String[] usercp = __config.loadStrings(ConfigRomType.CLASS_PATH);
		if (usercp != null)
		{
			// Debug
			todo.DEBUG.note("Using user class path!");
			
			// Scan for libraries
			int n = usercp.length;
			usecp = new BootLibrary[n];
			for (int i = 0; i < n; i++)
			{
				String libname = usercp[i];
				
				// Find library
				for (int j = 0; j < numboot; j++)
				{
					BootLibrary bl = bootlibs[j];
					
					// Is this library?
					if (libname.equals(bl.name))
					{
						usecp[i] = bl;
						break;
					}
				}
			}
		}
		
		// Use class-path built into the ROM
		else
		{
			// Debug
			todo.DEBUG.note("Using firmware class path!");
			
			// Get offset to the table and its length
			int icpoff = __rombase + Assembly.memReadJavaInt(__rombase,
					ROM_BOOTICPOFFSET_OFFSET),
				icpsize = Assembly.memReadJavaInt(__rombase,
					ROM_BOOTICPSIZE_OFFSET);
			
			// Read all of them
			usecp = new BootLibrary[icpsize];
			for (int i = 0; i < icpsize; i++)
				usecp[i] = bootlibs[Assembly.memReadJavaInt(icpoff,
					i * 4)];
		}
		
		// Use them!
		return usecp;
	}
	
	/**
	 * Returns the initial main class.
	 *
	 * @param __rombase The base of the ROM.
	 * @param __config The configuration to use.
	 * @return The initial main class.
	 * @since 2019/06/23
	 */
	public static final String initialMain(int __rombase,
		ConfigReader __config)
	{
		// Use main user class
		String usermain = __config.loadString(ConfigRomType.MAIN_CLASS);
		if (usermain != null)
			return usermain;
		
		// Otherwise read it from the boot ROM
		return JVMFunction.jvmLoadString(__rombase +
			Assembly.memReadJavaInt(__rombase, ROM_BOOTMAINCLASS_OFFSET));
	}
	
	/**
	 * Returns all of the libraries which are available to the bootstrap.
	 *
	 * @param __rombase The ROM base.
	 * @return The available bootstrap libraries.
	 * @since 2019/06/14
	 */
	public static final BootLibrary[] bootLibraries(int __rombase)
	{
		// Already exists?
		BootLibrary[] bootlibs = BOOT_LIBRARIES;
		if (bootlibs != null)
			return bootlibs;
		
		// Number of JARs in the ROM
		int numjars = Assembly.memReadJavaInt(__rombase, ROM_NUMJARS_OFFSET);
		
		// Offset to table of contents
		int tocoff = Assembly.memReadJavaInt(__rombase, ROM_TOCOFFSET_OFFSET);
		
		// Debug
		todo.DEBUG.note("Scanning %d libraries...", numjars);
		
		// Seeker for the table of contents
		int seeker = __rombase + tocoff;
		
		// Load all the JAR informations
		bootlibs = new BootLibrary[numjars];
		for (int i = 0; i < numjars; i++)
		{
			// Manifest address is optional
			int ma = Assembly.memReadJavaInt(seeker,
				TOC_MANIFEST_LENGTH_OFFSET);
			
			// Load library info
			BootLibrary bl = new BootLibrary(JVMFunction.jvmLoadString(
				__rombase + Assembly.memReadJavaInt(seeker, TOC_NAME_OFFSET)),
				__rombase + Assembly.memReadJavaInt(seeker, TOC_JAR_OFFSET),
				Assembly.memReadJavaInt(seeker, TOC_JARLEN_OFFSET),
				(ma == 0 ? 0 : __rombase + ma),
				Assembly.memReadJavaInt(seeker, TOC_MANIFEST_LENGTH_OFFSET));
			
			// Store it
			bootlibs[i] = bl;
			
			// Go to the next entry
			seeker += TOC_ENTRY_SIZE;
		}
		
		// Store for later usage
		BOOT_LIBRARIES = bootlibs;
		
		// Debug
		todo.DEBUG.note("Okay.");
		
		// Return the libraries
		return bootlibs;
	}
}

