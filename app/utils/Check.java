/**
 *
 * Copyright (c) 2013, Linagora
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA 
 *
 */
package utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author chamerling
 * 
 */
public class Check {

	public static boolean isInteger(String value) {
		boolean result = true;
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException e) {
			result = false;
		}
		return result;
	}

	public static boolean isURL(String value) {
		boolean result = true;
		try {
			new URL(value);
		} catch (MalformedURLException e) {
			result = false;
		}
		return result;
	}

}
