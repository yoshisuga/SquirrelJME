// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package cc.squirreljme.runtime.cldc.asm;

import cc.squirreljme.runtime.lcdui.event.NonStandardKey;
import java.awt.event.KeyEvent;
import javax.microedition.lcdui.Canvas;

/**
 * Contains key mapping for Swing.
 *
 * @since 2018/12/01
 */
final class __KeyMap__
{
	/**
	 * Gets a character for a key event.
	 *
	 * @param __e The event to look up.
	 * @return The character for the key.
	 * @since 2018/12/02
	 */
	static final char __char(KeyEvent __e)
	{
		char ch = __e.getKeyChar();
		if (ch == KeyEvent.CHAR_UNDEFINED)
			return 0;
		return ch;
	}
	
	/**
	 * Maps the key event to a key.
	 *
	 * @param __e The event to map.
	 * @return The key it should do.
	 * @since 2018/12/01
	 */
	static final int __map(KeyEvent __e)
	{
		int keycode = __e.getExtendedKeyCode();
		switch (keycode)
		{
			case KeyEvent.VK_0:
			case KeyEvent.VK_1:
			case KeyEvent.VK_2:
			case KeyEvent.VK_3:
			case KeyEvent.VK_4:
			case KeyEvent.VK_5:
			case KeyEvent.VK_6:
			case KeyEvent.VK_7:
			case KeyEvent.VK_8:
			case KeyEvent.VK_9:
				return Canvas.KEY_NUM0 + (keycode - KeyEvent.VK_0);
			
			case KeyEvent.VK_NUMPAD0:
			case KeyEvent.VK_NUMPAD1:
			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_NUMPAD3:
			case KeyEvent.VK_NUMPAD4:
			case KeyEvent.VK_NUMPAD5:
			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_NUMPAD7:
			case KeyEvent.VK_NUMPAD8:
			case KeyEvent.VK_NUMPAD9:
				return Canvas.KEY_NUM0 +
					(keycode - KeyEvent.VK_NUMPAD0);
			
			case KeyEvent.VK_F1:
			case KeyEvent.VK_F2:
			case KeyEvent.VK_F3:
			case KeyEvent.VK_F4:
			case KeyEvent.VK_F5:
			case KeyEvent.VK_F6:
			case KeyEvent.VK_F7:
			case KeyEvent.VK_F8:
			case KeyEvent.VK_F9:
			case KeyEvent.VK_F10:
			case KeyEvent.VK_F11:
			case KeyEvent.VK_F12:
			case KeyEvent.VK_F13:
			case KeyEvent.VK_F14:
			case KeyEvent.VK_F15:
			case KeyEvent.VK_F16:
			case KeyEvent.VK_F17:
			case KeyEvent.VK_F18:
			case KeyEvent.VK_F19:
			case KeyEvent.VK_F20:
			case KeyEvent.VK_F21:
			case KeyEvent.VK_F22:
			case KeyEvent.VK_F23:
			case KeyEvent.VK_F24:
				return NonStandardKey.F1 +
					(keycode - KeyEvent.VK_F1);
					
				// Map the keyboard virtually onto a number pad so that
				// those without a number pad (such as myself) can actually
				// use the input in its natural order without
				// [q w e] > [1 2 3]
				// [a s d] > [4 5 6]
				// [z x c] > [7 8 9]
				// [v b n] > [* 0 #]
			case KeyEvent.VK_Q:					return Canvas.KEY_NUM1;
			case KeyEvent.VK_W:					return Canvas.KEY_NUM2;
			case KeyEvent.VK_E:					return Canvas.KEY_NUM3;
			case KeyEvent.VK_A:					return Canvas.KEY_NUM4;
			case KeyEvent.VK_S:					return Canvas.KEY_NUM5;
			case KeyEvent.VK_D:					return Canvas.KEY_NUM6;
			case KeyEvent.VK_Z:					return Canvas.KEY_NUM7;
			case KeyEvent.VK_X:					return Canvas.KEY_NUM8;
			case KeyEvent.VK_C:					return Canvas.KEY_NUM9;
			case KeyEvent.VK_V:					return Canvas.KEY_STAR;
			case KeyEvent.VK_B:					return Canvas.KEY_NUM0;
			case KeyEvent.VK_N:					return Canvas.KEY_POUND;

			case KeyEvent.VK_ADD:				return '+';
			case KeyEvent.VK_AMPERSAND:			return '&';
			case KeyEvent.VK_ASTERISK:			return Canvas.KEY_STAR;
			case KeyEvent.VK_AT:				return '@';
			case KeyEvent.VK_BACK_QUOTE:		return '`';
			case KeyEvent.VK_BACK_SLASH:		return '\\';
			case KeyEvent.VK_BRACELEFT:			return '{';
			case KeyEvent.VK_BRACERIGHT:		return '}';
			case KeyEvent.VK_CIRCUMFLEX:		return '^';
			case KeyEvent.VK_CLOSE_BRACKET:		return ']';
			case KeyEvent.VK_COLON:				return ':';
			case KeyEvent.VK_COMMA:				return ',';
			case KeyEvent.VK_DECIMAL:			return '.';
			case KeyEvent.VK_DIVIDE:			return '/';
			case KeyEvent.VK_DOLLAR:			return '$';
			case KeyEvent.VK_EQUALS:			return '=';
			case KeyEvent.VK_EURO_SIGN:			return 0x20AC;
			case KeyEvent.VK_EXCLAMATION_MARK:	return '!';
			case KeyEvent.VK_GREATER:			return '>';
			case KeyEvent.VK_LEFT_PARENTHESIS:	return '(';
			case KeyEvent.VK_LESS:				return '<';
			case KeyEvent.VK_MINUS:				return '-';
			case KeyEvent.VK_MULTIPLY:			return '*';
			case KeyEvent.VK_NUMBER_SIGN:		return Canvas.KEY_POUND;
			case KeyEvent.VK_OPEN_BRACKET:		return '[';
			case KeyEvent.VK_RIGHT_PARENTHESIS:	return ')';
			case KeyEvent.VK_PERIOD:			return '.';
			case KeyEvent.VK_PLUS:				return '+';
			case KeyEvent.VK_QUOTE:				return '\'';
			case KeyEvent.VK_QUOTEDBL:			return '"';
			case KeyEvent.VK_SEMICOLON:			return ';';
			case KeyEvent.VK_SLASH:				return '/';
			case KeyEvent.VK_SPACE:				return ' ';
			case KeyEvent.VK_SUBTRACT:			return '-';
			case KeyEvent.VK_TAB:				return '\t';
			case KeyEvent.VK_UNDERSCORE:		return '_';

			case KeyEvent.VK_ALT:
				return NonStandardKey.ALT;

			case KeyEvent.VK_BACK_SPACE:
				return Canvas.KEY_BACKSPACE;

			case KeyEvent.VK_CAPS_LOCK:
				return NonStandardKey.CAPSLOCK;

			case KeyEvent.VK_CONTEXT_MENU:
				return NonStandardKey.CONTEXT_MENU;

			case KeyEvent.VK_CONTROL:
				return NonStandardKey.CONTROL;

			case KeyEvent.VK_DELETE:
				return Canvas.KEY_DELETE;

			case KeyEvent.VK_DOWN:
				return Canvas.KEY_DOWN;

			case KeyEvent.VK_END:
				return NonStandardKey.END;

			case KeyEvent.VK_ENTER:
				return Canvas.KEY_ENTER;

			case KeyEvent.VK_ESCAPE:
				return Canvas.KEY_ESCAPE;

			case KeyEvent.VK_HOME:
				return NonStandardKey.HOME;

			case KeyEvent.VK_INSERT:
				return NonStandardKey.INSERT;

			case KeyEvent.VK_KP_DOWN:
				return Canvas.KEY_DOWN;

			case KeyEvent.VK_KP_LEFT:
				return Canvas.KEY_LEFT;

			case KeyEvent.VK_KP_RIGHT:
				return Canvas.KEY_RIGHT;

			case KeyEvent.VK_KP_UP:
				return Canvas.KEY_UP;

			case KeyEvent.VK_LEFT:
				return Canvas.KEY_LEFT;

			case KeyEvent.VK_META:
				return NonStandardKey.META;

			case KeyEvent.VK_NUM_LOCK:
				return NonStandardKey.NUMLOCK;

			case KeyEvent.VK_PAGE_DOWN:
				return NonStandardKey.PAGE_DOWN;

			case KeyEvent.VK_PAGE_UP:
				return NonStandardKey.PAGE_UP;

			case KeyEvent.VK_PAUSE:
				return NonStandardKey.PAUSE;

			case KeyEvent.VK_PRINTSCREEN:
				return NonStandardKey.PRINTSCREEN;

			case KeyEvent.VK_RIGHT:
				return Canvas.KEY_RIGHT;

			case KeyEvent.VK_SCROLL_LOCK:
				return NonStandardKey.SCROLLLOCK;

			case KeyEvent.VK_SHIFT:
				return NonStandardKey.SHIFT;

			case KeyEvent.VK_UP:
				return Canvas.KEY_UP;

			case KeyEvent.VK_WINDOWS:
				return NonStandardKey.LOGO;
			
				// Probably a character
			default:
				// Shift is not handled because these are raw key codes
				if (keycode >= KeyEvent.VK_A && keycode <= KeyEvent.VK_Z)
					return 'A' + (keycode - KeyEvent.VK_A);
				
				// Unknown
				return NonStandardKey.UNKNOWN;
		}
	}
}

