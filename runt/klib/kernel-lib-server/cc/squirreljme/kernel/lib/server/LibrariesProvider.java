// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.kernel.lib.server;

import cc.squirreljme.kernel.lib.client.LibrariesClient;
import cc.squirreljme.kernel.lib.client.LibrariesClientFactory;
import cc.squirreljme.kernel.lib.DependencyInfo;
import cc.squirreljme.kernel.lib.InstallErrorCodes;
import cc.squirreljme.kernel.lib.Library;
import cc.squirreljme.kernel.lib.LibraryControlKey;
import cc.squirreljme.kernel.lib.LibraryInstallationReport;
import cc.squirreljme.kernel.lib.LibraryType;
import cc.squirreljme.kernel.lib.MatchResult;
import cc.squirreljme.kernel.lib.ProvidedInfo;
import cc.squirreljme.kernel.lib.SuiteIdentifier;
import cc.squirreljme.kernel.lib.SuiteInfo;
import cc.squirreljme.kernel.lib.SuiteName;
import cc.squirreljme.kernel.lib.SuiteType;
import cc.squirreljme.kernel.lib.SuiteVendor;
import cc.squirreljme.kernel.lib.SuiteVersion;
import cc.squirreljme.kernel.service.ClientInstance;
import cc.squirreljme.kernel.service.ServerInstance;
import cc.squirreljme.kernel.service.ServicePacketStream;
import cc.squirreljme.kernel.service.ServiceProvider;
import cc.squirreljme.kernel.trust.client.TrustClient;
import cc.squirreljme.runtime.cldc.SystemCall;
import cc.squirreljme.runtime.cldc.SystemTask;
import cc.squirreljme.runtime.cldc.SystemTrustGroup;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.multiphasicapps.collections.SortedTreeMap;
import net.multiphasicapps.tool.manifest.JavaManifest;
import net.multiphasicapps.zip.blockreader.ZipBlockReader;
import net.multiphasicapps.zip.blockreader.ZipEntryNotFoundException;

/**
 * This is the base class which manages the library of installed programs
 * on the server.
 *
 * The first library must always have an index of zero and be the library
 * which represents the system. The system manifest must be returned by it in
 * order for it to function.
 *
 * @since 2018/01/05
 */
public abstract class LibrariesProvider
	extends ServiceProvider
{
	/** Thread safety lock. */
	protected final Object lock =
		new Object();
	
	/** Libraries which are availble for usage. */
	private final Map<Integer, Library> _libraries =
		new SortedTreeMap<>();
	
	/** The trust client. */
	private volatile TrustClient _trustsclient;
	
	/**
	 * Initializes the base library server.
	 *
	 * @since 2018/01/05
	 */
	public LibrariesProvider()
		throws NullPointerException
	{
		super(LibrariesClient.class, LibrariesClientFactory.class);
	}
	
	/**
	 * Compiles and creates an installation for the given library.
	 *
	 * @param __lci Input for the compiler.
	 * @return The compiled library.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/11
	 */
	protected abstract Library compileLibrary(LibraryCompilerInput __lci)
		throws NullPointerException;
	
	/**
	 * Returns the library indicated by the given index.
	 *
	 * @param __dx The index to get the library for.
	 * @return The library for the given index or {@code null} if it does not
	 * exist.
	 * @since 2018/02/13
	 */
	public final Library byIndex(int __dx)
	{
		Map<Integer, Library> libraries = this._libraries;
		synchronized (this.lock)
		{
			return libraries.get(__dx);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/01/05
	 */
	@Override
	public final ServerInstance createInstance(SystemTask __task,
		ServicePacketStream __sps)
		throws NullPointerException
	{
		if (__task == null || __sps == null)
			throw new NullPointerException("NARG");
		
		return new LibrariesServer(__task, __sps, this);
	}
	
	/**
	 * Installs the specified JAR file.
	 *
	 * @param __b The data for the JAR file.
	 * @param __o The offset to the JAR data.
	 * @param __l The length of the JAR.
	 * @return The installation report.
	 * @throws ArrayIndexOutOfBoundsException If the offset and/or length
	 * are negative or exceed the array bounds.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/13
	 */
	public final LibraryInstallationReport install(byte[] __b, int __o,
		int __l)
		throws ArrayIndexOutOfBoundsException, NullPointerException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		if (__o < 0 || __l < 0 || (__o + __l) > __b.length)
			throw new ArrayIndexOutOfBoundsException("IOOB");
		
		// Need to process the ZIP
		Map<Integer, Library> libraries = this._libraries;
		try (ZipBlockReader zip = new ZipBlockReader(__b, __o, __l))
		{
			// Need to parse the info so the trust group and to see if it is
			// replacing another program
			SuiteInfo info;
			try (InputStream in = zip.open("META-INF/MANIFEST.MF"))
			{
				info = new SuiteInfo(new JavaManifest(in));
			}
			
			// {@squirreljme.error BC02 Cannot install applications which are
			// SquirrelJME APIs, they must be pre-installed by the system.}
			if (info.type() == SuiteType.SQUIRRELJME_API)
				throw new __PlainInstallError__(
					InstallErrorCodes.INVALID_JAR_TYPE, "BC02");
			
			// Lock because we need to go through each library to see if this
			// one is a duplicate.
			// Additionally, dependencies for this suite need to be handled.
			// Then after compilation, etc. it needs to be registered after
			// it is created
			synchronized (this.lock)
			{
				// First check to see if this library is a duplicate
				// {@squirreljme.error BC03 The library to be installed is
				// a duplicate.}
				if (this.__checkInstallDuplicate(info))
					throw new __PlainInstallError__(
						InstallErrorCodes.ALREADY_INSTALLED, "BC03");
				
				// Need to determine the library's index so it does not collide
				int nextdx = 1;
				for (Library otherlib : libraries.values())
				{
					int ldx = otherlib.index();
					if (ldx >= nextdx)
						nextdx = ldx + 1;
				}
				
				// Determine the trust group
				SystemTrustGroup trustgroup = this.__getTrustGroup(zip, info);
				
				// Determine dependencies for this library
				Library[] depends = this.__getDepends(info);
				
				// Compile the library
				Library lib = this.compileLibrary(new LibraryCompilerInput(
					info, zip, depends, nextdx));
				
				// Either an application or library
				lib.controlSet(LibraryControlKey.TYPE,
					Integer.toString((info.type() == SuiteType.MIDLET ?
					LibraryType.APPLICATION : LibraryType.LIBRARY)));
				
				// Record dependencies
				for (int i = 0, n = depends.length; i < n; i++)
					lib.controlSet(LibraryControlKey.DEPENDENCY_PREFIX + i,
						Integer.toString(depends[i].index()));
				
				// The trust group is important
				lib.controlSet(LibraryControlKey.TRUST_GROUP,
					Integer.toString(trustgroup.index()));
				
				// These are pretty much always set
				lib.controlSet(LibraryControlKey.STATE_FLAG_AVAILABLE, "true");
				lib.controlSet(LibraryControlKey.STATE_FLAG_ENABLED, "true");
				
				// Is now fully installed so register it
				lib.controlSet(LibraryControlKey.IS_INSTALLED, "true");
				this.registerLibrary(lib);
				
				// All is well, build a report
				return new LibraryInstallationReport(lib);
			}
		}
		
		// Map the thrown exception to an error code
		catch (Exception t)
		{
			// Print stack trace so it is better known why it failed
			t.printStackTrace();
			
			// Throw it
			return __mapThrowable(t);
		}
	}
	
	/**
	 * Lists the libraries which currently exist.
	 *
	 * @param __mask The mask for the library type.
	 * @return The list of libraries.
	 * @since 2018/01/07
	 */
	public final Library[] list(int __mask)
	{
		List<Library> rv = new ArrayList<>();
		
		Map<Integer, Library> libraries = this._libraries;
		synchronized (this.lock)
		{
			// Only add libraries which match the mask
			for (Library l : libraries.values())
				if ((l.type() & __mask) != 0)
					rv.add(l);
		}
		
		return rv.<Library>toArray(new Library[rv.size()]);
	}
	
	/**
	 * Registers the specified library to the library list.
	 *
	 * @param __l The library to register.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/12
	 */
	protected final void registerLibrary(Library __l)
		throws NullPointerException
	{
		if (__l == null)
			throw new NullPointerException("NARG");
		
		Map<Integer, Library> libraries = this._libraries;
		synchronized (this.lock)
		{
			// {@squirreljme.error BC04 The library with the specified index
			// has already been registered. (The library index)}
			Integer idx = __l.index();
			if (libraries.containsKey(idx))
				throw new IllegalArgumentException(String.format("BC04 %d",
					idx));
			
			// Store it
			libraries.put(idx, __l);
		}
	}
	
	/**
	 * Checks if the specified suite information to see if a library which
	 * is considered a duplicate exists.
	 *
	 * @param __myinfo The library information.
	 * @return {@code true} if it is a duplicate.
	 * @throws NullPointerException On null arguments.
	 * @throws __PlainInstallError__ If the library is duplicated.
	 * @since 2018/01/15
	 */
	private final boolean __checkInstallDuplicate(SuiteInfo __myinfo)
		throws NullPointerException, __PlainInstallError__
	{
		if (__myinfo == null)
			throw new NullPointerException("NARG");
		
		SuiteName myname = __myinfo.name();
		SuiteVendor myvendor = __myinfo.vendor();
		SuiteVersion myversion = __myinfo.version();
		
		// Lock
		Map<Integer, Library> libraries = this._libraries;
		synchronized (this.lock)
		{
			// Go through libraries and check to see if there are any
			// duplicates
			for (Library otherlib : libraries.values())
			{
				SuiteInfo otherinfo = otherlib.suiteInfo();
				
				SuiteName othername = otherinfo.name();
				SuiteVendor othervendor = otherinfo.vendor();
				SuiteVersion otherversion = otherinfo.version();
				
				if (myname.equals(othername) && myvendor.equals(othervendor))
				{
					// {@squirreljme.error BC05 Installing library which has
					// an older version.}
					int comp = myversion.compareTo(otherversion);
					if (comp < 0)
						throw new __PlainInstallError__(
							InstallErrorCodes.VERSION_MISMATCH, "BC05");
					
					// {@squirreljme.error BC06 Installing library which has
					// a newer version.}
					else if (comp > 0)
						throw new __PlainInstallError__(
							InstallErrorCodes.NEW_VERSION, "BC06");
					
					// {@squirreljme.error BC07 The library has already been
					// installed.}
					throw new __PlainInstallError__(
						InstallErrorCodes.ALREADY_INSTALLED, "BC07");
				}
			}
		}
		
		// Is okay
		return false;
	}
	
	/**
	 * Obtains dependencies for the given information.
	 *
	 * @param __info The information.
	 * @return The libraries which are dependencies of this one.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/02/11
	 */
	private final Library[] __getDepends(SuiteInfo __info)
		throws NullPointerException
	{
		if (__info == null)
			throw new NullPointerException("NARG");
		
		Collection<Library> rv = new LinkedHashSet<>();
		
		// Do not consider optional dependencies at all
		DependencyInfo depends = __info.dependencies().noOptionals(),
			left = depends;
		
		Map<Integer, Library> libraries = this._libraries;
		synchronized (this.lock)
		{
			for (Library lib : libraries.values())
			{
				// If this library happens to be in the return list then that
				// means it has been already calculated and their dependencies
				// recursed
				if (rv.contains(lib))
					continue;
				
				// If the library has matching dependencies then add it
				SuiteInfo subinfo = lib.suiteInfo();
				ProvidedInfo provinfo = subinfo.provided();
				MatchResult mr = depends.match(provinfo);
				if (mr.hasMatches())
				{
					// Need to go through the dependencies of that library and
					// make sure they are added before this one is
					// It also has to be recursive because the dependency chain
					// can potentially go deep.
					for (Library dep : this.__getDepends(subinfo))
						rv.add(dep);
					
					// Add the base library to the dependency chain
					rv.add(lib);
					
					// This is used to detect dependencies which have not been
					// matched at all
					left = left.match(provinfo).unmatched();
				}
			}
		}
		
		// {@squirreljme.error BC0b Cannot install the given application
		// because a dependency it requires is missing. (The application
		// attempted to be installed; The missing dependencies)}
		if (!left.isEmpty())
			throw new __PlainInstallError__(
				InstallErrorCodes.APP_INTEGRITY_FAILURE_DEPENDENCY_MISMATCH,
				String.format("BC0b %s %s", __info.suite(), left));
		
		return rv.<Library>toArray(new Library[rv.size()]);
	}
	
	/**
	 * Returns the trust group which is associated with the given program.
	 *
	 * @param __zip The input ZIP for verification, if signed.
	 * @param __info The suite information.
	 * @return The trust group of the application.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/17
	 */
	private final SystemTrustGroup __getTrustGroup(ZipBlockReader __zip,
		SuiteInfo __info)
		throws IOException, NullPointerException
	{
		if (__zip == null || __info == null)
			throw new NullPointerException("NARG");
		
		TrustClient trusts = this.__trusts();
		SystemTrustGroup trust = trusts.untrustedTrust(
			__info.name().toString(), __info.vendor().toString());
		
		return trust;
	}
	
	/**
	 * Returns the trust client.
	 *
	 * @return The trust client.
	 * @since 2018/01/17
	 */
	private final TrustClient __trusts()
	{
		TrustClient rv = this._trustsclient;
		if (rv == null)
			this._trustsclient = (rv =
				SystemCall.<TrustClient>service(TrustClient.class));
		return rv;
	}
	
	/**
	 * Maps the given throwable to a report.
	 *
	 * @param __t The input throwable.
	 * @return The resulting throwable.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/01/14
	 */
	private static final LibraryInstallationReport __mapThrowable(
		Throwable __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Determine error code
		int code;
		if (__t instanceof __PlainInstallError__)
			code = ((__PlainInstallError__)__t).code();
		else if (__t instanceof ZipEntryNotFoundException)
			code = InstallErrorCodes.CORRUPT_JAR;
		else if (__t instanceof IOException)
			code = InstallErrorCodes.IO_FILE_ERROR;
		else
			code = InstallErrorCodes.OTHER_ERROR;
		
		// {@squirreljme.error BC08 No message specified in the throwable.}
		return new LibraryInstallationReport(code,
			Objects.toString(__t.getMessage(), "BC08"));
	}
}

