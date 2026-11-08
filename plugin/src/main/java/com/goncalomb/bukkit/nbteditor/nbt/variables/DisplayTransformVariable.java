package com.goncalomb.bukkit.nbteditor.nbt.variables;

import com.goncalomb.bukkit.mylib.reflect.NBTTagCompound;
import java.util.Arrays;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class DisplayTransformVariable extends NBTVariable {
	public DisplayTransformVariable(String key) {
		super(key);
	}

	@Override
	public boolean set(String value, Player player) {
		String[] split = value.split(" ");
		if (split.length != 14) {
			return false;
		}
		Float[] floats;
		try {
			floats = Arrays.stream(split).map(Float::parseFloat).toArray(Float[]::new);
		} catch (Exception exception) {
			return false;
		}
		NBTTagCompound data = data();
		NBTTagCompound transformation = new NBTTagCompound();
		transformation.setList("right_rotation", floats[0], floats[1], floats[2], floats[3]);
		transformation.setList("scale", floats[4], floats[5], floats[6]);
		transformation.setList("left_rotation", floats[7], floats[8], floats[9], floats[10]);
		transformation.setList("translation", floats[11], floats[12], floats[13]);
		data.setCompound(_key, transformation);
		return true;
	}

	@Override
	public String get() {
		NBTTagCompound data = data();
		if (!data.hasKey(_key)) {
			return null;
		}
		NBTTagCompound transformation = data.getCompound(_key);
		StringBuilder sb = new StringBuilder();
		for (Object rr : Objects.requireNonNull(transformation.getListAsArray("right_rotation"))) {
			sb.append(rr).append(" ");
		}
		for (Object rr : Objects.requireNonNull(transformation.getListAsArray("scale"))) {
			sb.append(rr).append(" ");
		}
		for (Object rr : Objects.requireNonNull(transformation.getListAsArray("left_rotation"))) {
			sb.append(rr).append(" ");
		}
		for (Object rr : Objects.requireNonNull(transformation.getListAsArray("translation"))) {
			sb.append(rr).append(" ");
		}
		return sb.substring(0, sb.length() - 1);
	}

	@Override
	public String getFormat() {
		return "";
	}

	@Override
	public Component getFormatComponent() {
		return Component.text("A display entity transformation in decomposed (compound-tag) form, entered as " +
							  "14 decimal numbers separated by spaces: 4 for right-rotation, 3 for scale, 4 for " +
							  "left-rotation, 3 for translation.");
	}
}
