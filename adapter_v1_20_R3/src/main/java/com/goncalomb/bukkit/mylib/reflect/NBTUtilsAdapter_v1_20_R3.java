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

package com.goncalomb.bukkit.mylib.reflect;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class NBTUtilsAdapter_v1_20_R3 implements NBTUtilsAdapter {
	public NBTUtilsAdapter_v1_20_R3() {
		// No setup needed but this constructor must exist
	}

	@Override
	public ItemStack itemStackFromNBTData(NBTTagCompound data) {
		return net.minecraft.world.item.ItemStack.of((CompoundTag) data._handle).asBukkitMirror();
	}

	@Override
	public NBTTagCompound itemStackToNBTData(ItemStack stack) {
		NBTTagCompound data = new NBTTagCompound();
		((CraftItemStack) stack).handle.save((CompoundTag) (data._handle));
		return data;
	}

	@Override
	public Entity spawnEntity(NBTTagCompound data, Location location) {
		ServerLevel world = ((CraftWorld) location.getWorld()).getHandle();

		net.minecraft.world.entity.Entity entity = EntityType.loadEntityRecursive((CompoundTag) (data._handle), world, (spawnedEntity) -> {
			spawnedEntity.setPos(location.getX(), location.getY(), location.getZ());
			return spawnedEntity;
		});
		entity.getSelfAndPassengers().forEach((e) -> world.addFreshEntity(e));
		return entity.getBukkitEntity();
	}

	@Override
	public NBTTagCompound getEntityNBTData(Entity entity) {
		NBTTagCompound data = new NBTTagCompound();
		((CraftEntity) entity).getHandle().save((CompoundTag) (data._handle));
		return data;
	}

	private BlockEntity getTileEntity(Block block) {
		return ((CraftWorld) block.getWorld()).getHandle().getBlockEntity(new BlockPos(block.getX(), block.getY(), block.getZ()));
	}

	@Override
	public NBTTagCompound getTileEntityNBTData(Block block) {
		BlockEntity tileEntity = getTileEntity(block);
		if (tileEntity != null) {
			return new NBTTagCompound(tileEntity.saveWithId());
		}
		return null;
	}

	@Override
	public void setTileEntityNBTData(Block block, NBTTagCompound data) {
		BlockEntity tileEntity = getTileEntity(block);
		if (tileEntity != null) {
			tileEntity.load((CompoundTag) data._handle);
		}
	}

	@Override
	public NBTTagCompound getItemStackTag(ItemStack item) {
		Object tag = ((CraftItemStack) item).handle.getTag();
		return (tag == null ? new NBTTagCompound() : new NBTTagCompound(tag));
	}

	@Override
	public void setItemStackTag(ItemStack item, NBTTagCompound tag) {
		((CraftItemStack) item).handle.setTag((CompoundTag) (tag._handle));
	}

	@Override
	public ItemStack itemStackToCraftItemStack(ItemStack item) {
		if (!(item instanceof CraftItemStack)) {
			return CraftItemStack.asCraftCopy(item);
		}
		return item;
	}
}
