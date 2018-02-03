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

package com.goncalomb.bukkit.nbteditor.nbt;

import java.util.Arrays;

import com.goncalomb.bukkit.mylib.reflect.NBTTagList;
import com.goncalomb.bukkit.nbteditor.nbt.attributes.AttributeContainer;

public class MobNBT extends EquippableNBT {

	public AttributeContainer getAttributes() {
		if (_data.hasKey("Attributes")) {
			return AttributeContainer.fromNBT(_data.getList("Attributes"));
		}
		return new AttributeContainer();
	}

	public void setAttributes(AttributeContainer container) {
		if (container == null || container.size() == 0) {
			_data.remove("Attributes");
		} else {
			_data.setList("Attributes", container.toNBT());
		}
	}

	@Override
	void onUnserialize() {
		super.onUnserialize();
		// Backward compatibility with pre-1.9.
		if (_data.hasKey("HealF")) {
			_data.setFloat("Health", _data.getFloat("HealF"));
			_data.remove("HealF");
		}
		if (_data.hasKey("DropChances")) {
			Object[] drop = _data.getListAsArray("DropChances");
			_data.setList("HandDropChances", new NBTTagList(drop[0], Float.valueOf(0f)));
			_data.setList("ArmorDropChances", new NBTTagList(Arrays.copyOfRange(drop, 1, 5)));
			_data.remove("DropChances");
		}
	}

}
