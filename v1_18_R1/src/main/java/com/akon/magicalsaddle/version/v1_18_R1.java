package com.akon.magicalsaddle.version;

import net.minecraft.SystemUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class v1_18_R1 implements VersionWrapper {

	@NotNull
	@Override
	public Object createNBTTagCompound() {
		return new NBTTagCompound();
	}

	@NotNull
	@Override
	public Object getTag(ItemStack stack) {
		return CraftItemStack.asNMSCopy(stack).t();
	}

	@Override
	public ItemStack setTag(ItemStack stack, Object compound) {
		net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
		nmsStack.c((NBTTagCompound)compound);
		return CraftItemStack.asCraftMirror(nmsStack);
	}

	@Override
	public boolean hasKeyTypeOf(Object compound, String key, int type) {
		return ((NBTTagCompound)compound).b(key, type);
	}

	@Override
	public boolean hasKey(Object compound, String key) {
		return ((NBTTagCompound)compound).e(key);
	}

	@Override
	public void removeTag(Object compound, String key) {
		((NBTTagCompound)compound).r(key);
	}

	@NotNull
	@Override
	public Object getCompoundTag(Object compound, String key) {
		return ((NBTTagCompound)compound).p(key);
	}

	@Nullable
	@Override
	public Object getCompoundTagNullable(Object compound, String key) {
		if (((NBTTagCompound)compound).b(key, 10)) {
			return ((NBTTagCompound)compound).p(key);
		}
		return null;
	}

	@Override
	public void setCompoundTag(Object compound, String key, Object value) {
		((NBTTagCompound)compound).a(key, (NBTTagCompound)value);
	}

	@NotNull
	@Override
	public String getStringTag(Object compound, String key) {
		return ((NBTTagCompound)compound).l(key);
	}

	@Nullable
	@Override
	public String getStringTagNullable(Object compound, String key) {
		if (((NBTTagCompound)compound).b(key, 8)) {
			return ((NBTTagCompound)compound).l(key);
		}
		return null;
	}

	@Override
	public void setStringTag(Object compound, String key, String value) {
		((NBTTagCompound)compound).a(key, value);
	}

	@Override
	public boolean getBooleanTag(Object compound, String key) {
		return ((NBTTagCompound)compound).q(key);
	}

	@Nullable
	@Override
	public Boolean getBooleanTagNullable(Object compound, String key) {
		if (((NBTTagCompound)compound).b(key, 1)) {
			return ((NBTTagCompound)compound).q(key);
		}
		return null;
	}

	@Override
	public void setBooleanTag(Object compound, String key, boolean value) {
		((NBTTagCompound)compound).a(key, value);
	}

	@Nullable
	@Override
	public UUID getUUIDTag(Object compound, String key) {
		return ((NBTTagCompound)compound).a(key);
	}

	@Override
	public void setUUIDTag(Object compound, String key, UUID uuid) {
		((NBTTagCompound)compound).a(key, uuid);
	}

	@Contract("null, _ -> null")
	@Override
	public Entity loadEntityFromNBT(Object compound, World world) {
		if (compound == null) {
			return null;
		}
		WorldServer nmsWorld = ((CraftWorld)world).getHandle();
		return EntityTypes.a((NBTTagCompound)compound, nmsWorld).map(net.minecraft.world.entity.Entity::getBukkitEntity).orElse(null);
	}

	@Contract("null -> null")
	@Override
	public Object saveEntityToNBT(Entity entity) {
		if (entity == null) {
			return null;
		}
		net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity)entity).getHandle();
		NBTTagCompound compound = new NBTTagCompound();
		compound.a("id", nmsEntity.bk());
		nmsEntity.f(compound);
		return compound;
	}

	@Override
	public boolean addEntity(Entity entity, Location location, CreatureSpawnEvent.SpawnReason reason) {
		WorldServer nmsWorld = ((CraftWorld)location.getWorld()).getHandle();
		net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity)entity).getHandle();
		nmsEntity.b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		return nmsWorld.addFreshEntity(nmsEntity, reason);
	}

	@Override
	public void swingHand(Player player, boolean offhand) {
		if (offhand) {
			player.swingOffHand();
		} else {
			player.swingMainHand();
		}
	}

	@Override
	public void makeDrop(Player player, ItemStack stack, boolean setThrower) {
		((CraftPlayer)player).getHandle().a(CraftItemStack.asNMSCopy(stack), setThrower);
	}

	@Override
	public String getEntityTypeName(EntityType entityType) {
		NamespacedKey key = entityType.getKey();
		return new ChatMessage(SystemUtils.a("entity", new MinecraftKey(key.getNamespace(), key.getKey()))).getString();
	}
}
