package com.akon.magicalsaddle.version;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface VersionWrapper {

	@NotNull
	Object createNBTTagCompound();

	@NotNull
	Object getTag(ItemStack stack);

	@Contract("null, _ -> null")
	ItemStack setTag(ItemStack stack, Object compound);

	boolean hasKeyTypeOf(Object compound, String key, int type);

	boolean hasKey(Object compound, String key);

	void removeTag(Object compound, String key);

	@NotNull
	Object getCompoundTag(Object compound, String key);

	@Nullable
	Object getCompoundTagNullable(Object compound, String key);

	void setCompoundTag(Object compound, String key, Object value);

	@NotNull
	String getStringTag(Object compound, String key);

	@Nullable
	String getStringTagNullable(Object compound, String key);

	void setStringTag(Object compound, String key, String value);

	boolean getBooleanTag(Object compound, String key);

	@Nullable
	Boolean getBooleanTagNullable(Object compound, String key);

	void setBooleanTag(Object compound, String key, boolean value);

	@Nullable
	UUID getUUIDTag(Object compound, String key);

	void setUUIDTag(Object compound, String key, UUID uuid);

	@Contract("null, _ -> null")
	Entity loadEntityFromNBT(Object compound, World world);

	@Contract("null -> null")
	Object saveEntityToNBT(Entity entity);

	boolean addEntity(Entity entity, Location location, CreatureSpawnEvent.SpawnReason reason);

	default boolean addEntity(Entity entity, Location location) {
		return addEntity(entity, location, CreatureSpawnEvent.SpawnReason.DEFAULT);
	}

	void swingHand(Player player, boolean offhand);

	void makeDrop(Player player, ItemStack stack, boolean setThrower);

	String getEntityTypeName(EntityType entityType);
}
