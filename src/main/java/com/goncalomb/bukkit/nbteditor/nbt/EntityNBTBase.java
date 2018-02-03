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

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.goncalomb.bukkit.mylib.namemaps.EntityTypeMap;
import com.goncalomb.bukkit.mylib.reflect.NBTTagCompound;
import com.goncalomb.bukkit.mylib.reflect.NBTTagList;
import com.goncalomb.bukkit.mylib.reflect.NBTUtils;

import net.iharder.Base64;

abstract class EntityNBTBase extends BaseNBT {

	private static HashMap<EntityType, Class<? extends EntityNBT>> _entityClasses;
	// XXX: remove this ugly hack
	private static EntityType _initializerEntityTypeIfNull = null;

	// private static final HashMap<String, NBTUnboundVariableContainer> ENTITY_VARIABLES = new HashMap<String, NBTUnboundVariableContainer>();

	static {
		_entityClasses = new HashMap<EntityType, Class<? extends EntityNBT>>();
		// Force static initialization of the EntityNBT class.
		try {
			Class.forName(EntityNBT.class.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	static void registerEntity(EntityType entityType, Class<? extends EntityNBT> entityClass) {
		_entityClasses.put(entityType, entityClass);
	}

	public static boolean isValidType(EntityType entityType) {
		return _entityClasses.containsKey(entityType);
	}

	public static Collection<EntityType> getValidEntityTypes() {
		return _entityClasses.keySet();
	}

	private static EntityNBT newInstance(EntityType entityType, NBTTagCompound data) {
		Class<? extends EntityNBT> entityClass = _entityClasses.get(entityType);
		EntityNBTBase instance;
		if (entityClass != null) {
			_initializerEntityTypeIfNull = entityType;
			try {
				instance = entityClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Error when instantiating " + entityClass.getName() + ".", e);
			}
			_initializerEntityTypeIfNull = null;
			instance._entityType = entityType;
		} else {
			instance = new EntityNBT(entityType);
		}
		if (data != null) {
			instance._data = data;
		} else {
			instance.setDefautData();
		}
		instance._data.setString("id", EntityTypeMap.getName(entityType));
		return (EntityNBT) instance;
	}

	public static EntityNBT fromEntityType(EntityType entityType) {
		if (_entityClasses.containsKey(entityType)) {
			return newInstance(entityType, null);
		}
		return null;
	}

	public static EntityNBT fromEntityData(NBTTagCompound data) {
		EntityType entityType = EntityTypeMap.getByName(data.getString("id"));

		EntityType entityTypeNew = entityType;
		// Backward compatibility with pre-1.11.
		if (entityType == EntityType.GUARDIAN) {
			if (data.hasKey("Elder")) {
				if (data.getByte("Elder") != 0) {
					entityTypeNew = EntityType.ELDER_GUARDIAN;
				}
				data.remove("Elder");
			}
		} else if (entityType == EntityType.SKELETON) {
			if (data.hasKey("SkeletonType")) {
				switch (data.getByte("SkeletonType")) {
				case 1: entityTypeNew = EntityType.WITHER_SKELETON; break;
				case 2: entityTypeNew = EntityType.STRAY; break;
				}
				data.remove("SkeletonType");
			}
		} else if (entityType == EntityType.ZOMBIE) {
			// pre-1.10
			if (data.hasKey("IsVillager")) {
				if (data.getByte("IsVillager") != 0) {
					entityTypeNew = EntityType.ZOMBIE_VILLAGER;
					if (data.hasKey("VillagerProfession")) {
						data.setInt("Profession", data.getInt("VillagerProfession"));
						data.remove("VillagerProfession");
					}
				}
				data.remove("IsVillager");
			}
			// pre-1.11
			if (data.hasKey("ZombieType")) {
				int zombieType = data.getInt("ZombieType");
				if (zombieType == 0) {
					entityTypeNew = EntityType.ZOMBIE;
				} else if (zombieType >= 1 && zombieType <= 5) {
					entityTypeNew = EntityType.ZOMBIE_VILLAGER;
					data.setInt("Profession", zombieType - 1);
				} else if (zombieType == 6) {
					entityTypeNew = EntityType.HUSK;
				}
				data.remove("ZombieType");
			}
		} else if (entityType == EntityType.HORSE) {
			if (data.hasKey("Type")) {
				switch (data.getInt("Type")) {
				case 1: entityTypeNew = EntityType.DONKEY; break;
				case 2: entityTypeNew = EntityType.MULE; break;
				case 3: entityTypeNew = EntityType.ZOMBIE_HORSE; break;
				case 4: entityTypeNew = EntityType.SKELETON_HORSE; break;
				}
				data.remove("Type");
			}
			if (entityTypeNew != EntityType.DONKEY && entityTypeNew != EntityType.MULE) {
				data.remove("ChestedHorse");
			}
		}

		data.setString("id", EntityTypeMap.getName(entityTypeNew));
		entityType = entityTypeNew;

		if (entityType != null) {
			return newInstance(entityType, data);
		}
		return null;
	}

	public static EntityNBT fromEntity(Entity entity) {
		EntityNBT entityNbt = newInstance(entity.getType(), NBTUtils.getEntityNBTData(entity));
		// When cloning, remove the UUID to force all entities to have a unique one.
		entityNbt._data.remove("UUIDMost");
		entityNbt._data.remove("UUIDLeast");
		return entityNbt;
	}

	public static EntityNBT unserialize(String serializedData) {
		try {
			NBTTagCompound data = NBTTagCompound.unserialize(Base64.decode(serializedData));

			// Backward compatibility with pre-1.9.
			// On 1.9 the entities are stacked for bottom to top.
			// This conversion needs to happen before instantiating any class, we cannot use onUnserialize.
			while (data.hasKey("Riding")) {
				NBTTagCompound riding = data.getCompound("Riding");
				data.remove("Riding");
				riding.setList("Passengers", new NBTTagList(data));
				data = riding;
			}

			EntityNBT entityNBT = fromEntityData(data);
			entityNBT.onUnserialize();
			return entityNBT;
		} catch (Throwable e) {
			throw new RuntimeException("Error unserializing EntityNBT.", e);
		}
	}

	private EntityType _entityType;

	protected EntityNBTBase(EntityType entityType) {
		super(new NBTTagCompound(), EntityTypeMap.getName(entityType == null ? _initializerEntityTypeIfNull : entityType));
		_entityType = entityType;
	}

	abstract public void setDefautData();

	public EntityType getEntityType() {
		return _entityType;
	}

	public Entity spawn(Location location) {
		return NBTUtils.spawnEntity(_data, location);
	}

	public String serialize() {
		try {
			return Base64.encodeBytes(_data.serialize(), Base64.GZIP);
		} catch (Throwable e) {
			throw new RuntimeException("Error serializing EntityNBT.", e);
		}
	}

	void onUnserialize() { }

	public EntityNBT clone() {
		return fromEntityData(_data.clone());
	}

	public NBTTagCompound getData() {
		return _data.clone();
	}

	public String getMetadataString() {
		NBTTagCompound data = _data.clone();
		data.remove("id");
		return data.toString();
	}

}
