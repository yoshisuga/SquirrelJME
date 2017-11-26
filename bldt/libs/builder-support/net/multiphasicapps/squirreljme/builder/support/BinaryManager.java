// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirreljme.builder.support;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.multiphasicapps.collections.SortedTreeMap;
import net.multiphasicapps.collections.UnmodifiableCollection;
import net.multiphasicapps.squirreljme.runtime.midlet.DependencySet;
import net.multiphasicapps.squirreljme.runtime.midlet.ManifestedDependency;

/**
 * This class is used to manage binaries which are available for running
 * and/or compilation.
 *
 * @since 2017/10/31
 */
public final class BinaryManager
	implements Iterable<Binary>
{
	/** The output directory where built binaries are to be placed. */
	protected final Path output;
	
	/** Projects which may exist that provide access to source code. */
	protected final SourceManager sources;
	
	/** Projects which have been read by the manager. */
	private final Map<SourceName, Binary> _binaries =
		new SortedTreeMap<>();
	
	/**
	 * Initializes the binary manager.
	 *
	 * @param __out The output directory, this directory is scanned for
	 * binaries as requested.
	 * @param __src The source code where projects may be sourced from, may
	 * be {@code null} if there is no source code.
	 * @throws IOException On read/write errors.
	 * @throws NullPointerException If no output path was specified.
	 * @since 2017/10/31
	 */
	public BinaryManager(Path __out, SourceManager __src)
		throws IOException, NullPointerException
	{
		if (__out == null)
			throw new NullPointerException("NARG");
		
		this.output = __out;
		this.sources = __src;
		
		// Load in binaries from source code first
		Map<SourceName, Binary> binaries = this._binaries;
		for (Source src : __src)
		{
			SourceName name = src.name();
			binaries.put(name,
				new Binary(name, src, __out.resolve(name.toFileName())));
		}
		
		// Go through directory containing binaries and try building binaries
		// that have no source
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(__out))
		{
			for (Path p : ds)
			{
				// Ignore directories and non-binary files
				if (Files.isDirectory(p) || !SourceName.isBinaryPath(p))
					continue;
				
				// Only add a binary if it does not exist, this allows one
				// to add external binaries
				SourceName name = SourceName.ofBinaryPath(p);
				Binary bin = binaries.get(name);
				if (bin == null)
					binaries.put(name, new Binary(name, null, p));
			}
		}
		
		// Ignore these
		catch (NoSuchFileException e)
		{
		}
	}
	
	/**
	 * Returns the dependencies which statisfy the given set.
	 *
	 * @param __set The set of dependencies to get.
	 * @param __opt If {@code true} include optional dependencies.
	 * @return Binaries which statisfy the given dependencies.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/17
	 */
	public final Binary[] dependencies(DependencySet __set, boolean __opt)
		throws NullPointerException
	{
		if (__set == null)
			throw new NullPointerException("NARG");
		
		// The returning set
		Set<Binary> rv = new LinkedHashSet<>();
		
		// Go through the entire set and search for dependencies
		for (ManifestedDependency md : __set)
		{
			// Not wanting an optional dependency
			boolean mdopt = md.isOptional();
			if (mdopt && !__opt)
				continue;
			
			// {@squirreljme.error AU0c Could not locate the binary which
			// statifies the given dependency. (The dependency to look for)}
			Binary bin = this.findDependency(md);
			if (bin == null)
				if (mdopt)
					continue;
				else
					throw new InvalidBinaryException(
						String.format("AU0c %s", md));
			
			// Recursively obtain the dependencies of that dependency
			for (Binary dep : this.dependencies(bin.dependencies(), false))
				rv.add(dep);
			
			// Add the self dependency at the end
			rv.add(bin);
		}
		
		return rv.<Binary>toArray(new Binary[rv.size()]);
	}
	
	/**
	 * Returns the class path for the given binary.
	 *
	 * @param __b The binary to get the classpath for.
	 * @return The binary class path.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/17
	 */
	public final Binary[] classPath(Binary __b)
		throws NullPointerException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		
		// Return value to use run-time
		Set<Binary> rv = new LinkedHashSet<>();
		
		// Make sure all dependencies are used
		for (Binary dep : this.dependencies(__b.dependencies(), false))
			rv.add(dep);
		
		// Include this in the run-time
		rv.add(__b);
		return rv.<Binary>toArray(new Binary[rv.size()]);
	}
	
	/**
	 * Compiles the specified binary and all of their dependencies.
	 *
	 * @param __b The binary to compile.
	 * @return The binaries which were compiled and are part of the class
	 * path.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/17
	 */
	public final Binary[] compile(Binary __b)
		throws NullPointerException
	{
		if (__b == null)
			throw new NullPointerException("NARG");
		
		// Return value to use run-time
		Set<Binary> rv = new LinkedHashSet<>();
		
		// Make sure all dependencies are compiled, this will result in the
		// entire class path being determined aslso for compilation
		for (Binary dep : this.dependencies(__b.dependencies(), false))
			for (Binary c : this.compile(dep))
				rv.add(c);
		
		// Compile this package
		if (true)
		{
			throw new todo.TODO();
		}
		
		// Include this in the run-time
		rv.add(__b);
		return rv.<Binary>toArray(new Binary[rv.size()]);
	}
	
	/**
	 * Finds the binary project which statifies the given dependency.
	 *
	 * @param __dep The dependency to locate.
	 * @return The binary if one was found, otherwise {@code null} if not.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/23
	 */
	public final Binary findDependency(ManifestedDependency __dep)
		throws NullPointerException
	{
		if (__dep == null)
			throw new NullPointerException("NARG");
		
		// Look for the first matching dependency
		for (Binary bin : this)
		{
			ManifestedDependency[] check = bin.matchedDependencies(__dep);
			
			if (check.length > 0)
				return bin;
		}
		
		// None found
		return null;
	}
	
	/**
	 * Obtains the binary which uses the given source name.
	 *
	 * @param __n The name of the project to get.
	 * @return The binary for the given name.
	 * @throws NoSuchBinaryException If no binary with the given name exists.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/11/17
	 */
	public final Binary get(String __n)
		throws NoSuchBinaryException, NullPointerException
	{
		if (__n == null)
			throw new NullPointerException("NARG");
		
		return this.get(new SourceName(__n));
	}
	
	/**
	 * Obtains the binary which uses the given source name.
	 *
	 * @param __n The name of the project to get.
	 * @return The binary for the given name.
	 * @throws NoSuchBinaryException If no binary with the given name exists.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/10/31
	 */
	public final Binary get(SourceName __n)
		throws NoSuchBinaryException, NullPointerException
	{
		if (__n == null)
			throw new NullPointerException("NARG");
		
		// Locate
		Binary rv = this._binaries.get(__n);
		
		// {@squirreljme.error AU0d The specified binary does not exist.
		// (The name of the binary)}
		if (rv == null)
			throw new NoSuchBinaryException(String.format("AU0d %s"));
		return rv;
	}
	
	/**
	 * Creates a virtual binary which is sourced from the given JAR file and
	 * where it has no backing in source code.
	 *
	 * @param __p The path to JAR to be opened as a binary.
	 * @return The binary for the given path.
	 * @throws NoSuchBinaryException If no such binary exists.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/10/31
	 */
	public final Binary getVirtual(Path __p)
		throws NoSuchBinaryException, NullPointerException
	{
		if (__p == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error AU01 Cannot open the specified path as a project
		// because it does not exist. (The path to open as a binary)}
		if (!Files.exists(__p))
			throw new NoSuchBinaryException(String.format("AU01 %s", __p));
		
		// Just create the binary
		return new Binary(SourceName.ofBinaryPath(__p), null, __p);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/11/25
	 */
	@Override
	public final Iterator<Binary> iterator()
	{
		return UnmodifiableCollection.<Binary>of(this._binaries.values()).
			iterator();
	}
}

