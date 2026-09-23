/*
 * Copyright (C) 2013-2018 Gonçalo Baltazar <me@goncalomb.com>
 *
 * This file is part of NBTEditor.
 *
 * NBTEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NBTEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NBTEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.goncalomb.bukkit.nbteditor.nbt.variables;

import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import com.goncalomb.bukkit.mylib.reflect.NBTTagCompound;

public class ColorVariable extends NBTVariable {
	private final boolean hasAlpha; // optional alpha channel, can be omitted, minecraft uses ARGB everywhere

	public ColorVariable(String key) {
		this(key, false);
	}

	public ColorVariable(String key, boolean hasAlpha) {
		super(key);
		this.hasAlpha = hasAlpha;
	}

	@Override
	public boolean set(String value, Player player) {
		NBTTagCompound data = data();
		if (!value.startsWith("#")) {
			value = "#" + value;
		}

		// minor datafixing
		if (value.length() == 4) { // short format, #123 -> #112233
			value = value.substring(0, 2) + value.substring(1, 3) + value.substring(2, 4) + value.substring(3, 4);
		} else if (hasAlpha && value.length() == 5) { // short format but with alpha
			value = value.substring(0, 2) + value.substring(1, 3) + value.substring(2, 4) + value.substring(3, 5) + value.substring(4, 5);
		} else if (value.length() != 7 && (!hasAlpha || value.length() != 9)) {
			return false;
		}
		if (hasAlpha && value.length() == 7) {
			value = "#FF" + value.substring(1); // full opacity
		}

        try {
			// java doesn't have unsigned ints but this value might use all 32 bits if it has alpha
			// value will be in [0, 2^32-1], subtract 2^32 from values >= 2^31 to fit in range [-2^31, 2^31-1]
			long color = Long.decode(value);
			if (color >= 1L << 31) {
				color -= 1L << 32;
			}
			int colorint = Math.toIntExact(color);
			data.setInt(_key, colorint);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public String get() {
		NBTTagCompound data = data();
		long color = data.getInt(_key);
		// java doesn't have unsigned ints but this value covers the full 32 bits if it has alpha, add 2^32 to get correct value
		color = color < 0 ? color + (1L << 32) : color;
		StringBuilder colorstr = new StringBuilder(Long.toString(color, 16).toUpperCase(Locale.ROOT));
		while (colorstr.length() < 8) {
			colorstr.insert(0, "0");
		}
		// hide alpha channel if it's fully opaque
		if (!hasAlpha || colorstr.substring(0, 2).equals("FF")) {
            colorstr.replace(0, 2, "");
		}
		return "#" + colorstr;
	}

	@Override
	public String getFormat() {
		return "RGB format, #FFFFFF (e.g. #FF0000 for red)."
				+ (hasAlpha ? " Accepts alpha channel in the format ARGB, e.g. #80FF0000 for half-transparent red, " +
				"where FF is fully opaque." : "");
	}

}
