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

import org.bukkit.Color;
import org.bukkit.entity.Player;

import com.goncalomb.bukkit.mylib.reflect.NBTTagCompound;

public class ColorVariable extends NBTVariable {
	private final boolean hasAlpha;
	private final boolean canBeUnset;

	public ColorVariable(String key) {
		this(key, false, false);
	}

	public ColorVariable(String key, boolean hasAlpha, boolean canBeUnset) {
		super(key);
		this.hasAlpha = hasAlpha;
		this.canBeUnset = canBeUnset;
	}

	@Override
	public boolean set(String value, Player player) {
		NBTTagCompound data = data();
		if (!value.startsWith("#")) {
			if (canBeUnset && value.equals("-1")) {
				data.setInt(_key, -1);
				return true;
			}
			value = "#" + value;
		}

		// minor datafixing
		if (value.length() == 4) { // short format, #123 -> #112233
			value = value.substring(0, 2) + value.substring(1, 3) + value.substring(2, 4);
		} else if (hasAlpha && value.length() == 5) { // short format but with alpha
			value = value.substring(0, 2) + value.substring(1, 3) + value.substring(2, 4) + value.substring(3, 5);
		} else if (value.length() != 7 && (!hasAlpha || value.length() != 9)) {
			return false;
		}

        try {
			int color = Integer.decode(value);
			data.setInt(_key, color);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public String get() {
		NBTTagCompound data = data();
		Color color = Color.fromRGB(data.getInt(_key));
		String r = Integer.toHexString(color.getRed());
		String g = Integer.toHexString(color.getGreen());
		String b = Integer.toHexString(color.getBlue());
		return "#" + (r.length() == 1 ? "0" + r : r) + (g.length() == 1 ? "0" + g : g) + (b.length() == 1 ? "0" + b : b);
	}

	@Override
	public String getFormat() {
		return "RGB format, #FFFFFF (e.g. #FF0000 for red)."
				+ (hasAlpha ? " Accepts alpha channel in the format ARGB, e.g. #80FF0000 for half-transparent red, " +
				"where FF is fully opaque." : "");
	}

}
