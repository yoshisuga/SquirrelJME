// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.lcdui.ui;

/**
 * This represents the base for a form item which may be added to a form as
 * required.
 *
 * @since 2018/04/04
 */
public interface UiFormItem
	extends UiCollectable
{
	/**
	 * Returns the form that this item is a member of.
	 *
	 * @return The form this item is in or {@code null} if it is not in one.
	 * @since 2018/04/04
	 */
	public abstract UiForm getForm();
}

