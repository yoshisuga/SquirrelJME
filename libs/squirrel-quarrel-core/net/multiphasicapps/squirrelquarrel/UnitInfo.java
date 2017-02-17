// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Steven Gawroriski <steven@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.squirrelquarrel;

import java.io.InputStream;
import java.io.IOException;
import java.util.Objects;
import net.multiphasicapps.squirreljme.java.manifest.JavaManifest;
import net.multiphasicapps.squirreljme.java.manifest.JavaManifestAttributes;
import net.multiphasicapps.squirreljme.java.manifest.JavaManifestKey;

/**
 * This contains a cachable.
 *
 * @since 2017/02/14
 */
public final class UnitInfo
{
	/** Key for hitpoints. */
	public static final JavaManifestKey HP_KEY =
		new JavaManifestKey("hp");
		
	/** Key for shields. */
	public static final JavaManifestKey SHIELDS_KEY =
		new JavaManifestKey("shields");
	
	/** Key for armor. */
	public static final JavaManifestKey ARMOR_KEY =
		new JavaManifestKey("armor");
	
	/** Key for unit size. */
	public static final JavaManifestKey SIZE_KEY =
		new JavaManifestKey("size");
	
	/** Key for unit cost in salt. */
	public static final JavaManifestKey SALT_COST_KEY =
		new JavaManifestKey("salt-cost");
	
	/** Key for unit cost in peppers. */
	public static final JavaManifestKey PEPPER_COST_KEY =
		new JavaManifestKey("pepper-cost");
	
	/** Key for build time in frames. */
	public static final JavaManifestKey BUILD_TIME_KEY =
		new JavaManifestKey("build-time");
	
	/** Key for the amount of supply that is provided. */
	public static final JavaManifestKey SUPPLY_PROVIDED_KEY =
		new JavaManifestKey("supply-provided");
	
	/** Key for the amount of supply that is consumed. */
	public static final JavaManifestKey SUPPLY_COST_KEY =
		new JavaManifestKey("supply-cost");
	
	/** Key for the dimensions of the unit in pixels. */
	public static final JavaManifestKey PIXEL_DIMENSIONS_KEY =
		new JavaManifestKey("pixel-dimensions");
	
	/** Key for the offsets in dimensions (used for buildings). */
	public static final JavaManifestKey OFFSET_DIMENSIONS_KEY =
		new JavaManifestKey("offset-dimensions");
	
	/** The key for sight range. */
	public static final JavaManifestKey SIGHT_RANGE_KEY =
		new JavaManifestKey("sight");
	
	/** Key for the build score of the unit. */
	public static final JavaManifestKey SCORE_BUILD_KEY =
		new JavaManifestKey("score-build");
	
	/** Key for the destroy score of the unit. */
	public static final JavaManifestKey SCORE_DESTROY_KEY =
		new JavaManifestKey("score-destroy");
	
	/** Key for the speed of the unit. */
	public static final JavaManifestKey SPEED_KEY =
		new JavaManifestKey("speed");
	
	/** The type of unit this has info for. */
	protected final UnitType type;
	
	/** Unit hitpoints. */
	protected final int hp;
	
	/** Unit shields. */
	protected final int shields;
	
	/** Armor. */
	protected final int armor;
	
	/** Unit size. */
	protected final UnitSize size;
	
	/** The cost in salt. */
	protected final int salt;
	
	/** The cost in pepper. */
	protected final int pepper;
	
	/** The build time in frames. */
	protected final int buildtime;
	
	/** The supply provided. */
	protected final int supplyprovided;
	
	/** The supply cost. */
	protected final int supplycost;
	
	/** The dimension of the unit in pixels. */
	protected final Dimension pixeldimension;
	
	/** Center point offset for the unit. */
	protected final Point centerpointoffset;
	
	/** The center point offset used for buildings (based on tile grid). */
	protected final Point buildingcenterpointoffset;
	
	/** The unit size in tiles (for buildings). */
	protected final Dimension tiledimension;
	
	/** The sight range. */
	protected final int sight;
	
	/** The score for creating this unit. */
	protected final int scorebuild;
	
	/** The score for destroying this unit. */
	protected final int scoredestroy;
	
	/** The speed of this unit, in 16.16 fixed point. */
	protected final int speed;
	
	/**
	 * Initializes the unit information.
	 *
	 * @param __t The unit to load information for.
	 * @throws NullPointerException On null arguments.
	 * @since 2017/02/15
	 */
	UnitInfo(UnitType __t)
		throws NullPointerException
	{
		// Check
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Set
		this.type = __t;
		
		// Could fail
		String path = "units/" + TerrainType.__lower(__t.name()) + "/info";
		try (InputStream is = UnitInfo.class.getResourceAsStream(path))
		{
			// {@squirreljme.error BE0b No information resource exists for the
			// given unit type. (The unit type; The attempted path)}
			if (is == null)
				throw new IOException(String.format("BE0b %s %s", __t, path));
			
			// Load manifest
			JavaManifest man = new JavaManifest(is);
			JavaManifestAttributes attr = man.getMainAttributes();
			
			// Load these values directly
			this.hp = Integer.parseInt(
				Objects.toString(attr.get(HP_KEY), "0"));
			this.shields = Integer.parseInt(
				Objects.toString(attr.get(SHIELDS_KEY), "0"));
			this.armor = Integer.parseInt(
				Objects.toString(attr.get(ARMOR_KEY), "0"));
			this.salt = Integer.parseInt(
				Objects.toString(attr.get(SALT_COST_KEY), "0"));
			this.pepper = Integer.parseInt(
				Objects.toString(attr.get(PEPPER_COST_KEY), "0"));
			this.buildtime = Integer.parseInt(
				Objects.toString(attr.get(BUILD_TIME_KEY), "0"));
			this.supplyprovided = Integer.parseInt(
				Objects.toString(attr.get(SUPPLY_PROVIDED_KEY), "0"));
			this.supplycost = Integer.parseInt(
				Objects.toString(attr.get(SUPPLY_COST_KEY), "0"));
			this.sight = Integer.parseInt(
				Objects.toString(attr.get(SIGHT_RANGE_KEY), "0"));
			this.scorebuild = Integer.parseInt(
				Objects.toString(attr.get(SCORE_BUILD_KEY), "0"));
			this.scoredestroy = Integer.parseInt(
				Objects.toString(attr.get(SCORE_DESTROY_KEY), "0"));
			this.speed = Integer.parseInt(
				Objects.toString(attr.get(SPEED_KEY), "0"));
			
			// Parse size
			String vsize = Objects.toString(attr.get(SIZE_KEY), "small");
			switch (vsize)
			{
				case "small": this.size = UnitSize.SMALL; break;
				case "medium": this.size = UnitSize.MEDIUM; break;
				case "large": this.size = UnitSize.LARGE; break;
				
					// {@squirreljme.error BE0c Unknown unit size. (Unit size)}
				default:
					throw new IOException(String.format("BE0c %s", vsize));
			}
			
			// Parse unit dimensions and potential offsets
			Dimension pixeldimension = __parseDimension(
				Objects.toString(attr.get(PIXEL_DIMENSIONS_KEY), "0 0"));
			this.pixeldimension = pixeldimension;
			
			// Load offset to calculate building related details
			Point offset = __parsePoint(
				Objects.toString(attr.get(PIXEL_DIMENSIONS_KEY), "0 0"));
			
			// Center point is just half the dimension
			this.centerpointoffset = new Point(pixeldimension.width / 2,
				pixeldimension.height / 2);
			
			// Point buildingcenterpointoffset;
			// Dimension tiledimension;
			
			if (false)
				throw new IOException("OOPS");
			throw new Error("TODO");
		}
		
		// {@squirreljme.error BE0a Failed to load information for the
		// specified unit type. (The unit type)}
		catch (IOException|NumberFormatException e)
		{
			throw new RuntimeException(String.format("BE0a %s", __t), e);
		}
	}
	
	/**
	 * Returns the unit type which this has informaton for.
	 *
	 * @return The unit type this has information for,
	 * @since 2017/02/15
	 */
	public UnitType type()
	{
		return this.type;
	}
	
	/**
	 * Parses a dimension.
	 *
	 * @param __v The dimension to parse.
	 * @return The resulting dimension.
	 * @throws NullPointerException On null arguments.
	 * @throws NumberFormatException If the format of the string is not
	 * correct.
	 * @since 2017/02/17
	 */
	static Dimension __parseDimension(String __v)
		throws NullPointerException, NumberFormatException
	{
		// Check
		if (__v == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error BE0d Missing space between dimensions.}
		__v = __v.trim();
		int spdx = __v.indexOf(' ');
		if (spdx < 0)
			throw new NumberFormatException("BE0d");
		
		// Get both fragments
		String fa = __v.substring(0, spdx).trim(),
			fb = __v.substring(spdx + 1).trim();
		
		// Parse
		return new Dimension(Integer.parseInt(fa), Integer.parseInt(fb));
	}
	
	/**
	 * Parses a point.
	 *
	 * @param __v The point to parse.
	 * @return The resulting point.
	 * @throws NullPointerException On null arguments.
	 * @throws NumberFormatException If the format of the string is not
	 * correct.
	 * @since 2017/02/17
	 */
	static Point __parsePoint(String __v)
		throws NullPointerException, NumberFormatException
	{
		// Check
		if (__v == null)
			throw new NullPointerException("NARG");
		
		// {@squirreljme.error BE0e Missing space between points.}
		__v = __v.trim();
		int spdx = __v.indexOf(' ');
		if (spdx < 0)
			throw new NumberFormatException("BE0e");
		
		// Get both fragments
		String fa = __v.substring(0, spdx).trim(),
			fb = __v.substring(spdx + 1).trim();
		
		// Parse
		return new Point(Integer.parseInt(fa), Integer.parseInt(fb));
	}
}

