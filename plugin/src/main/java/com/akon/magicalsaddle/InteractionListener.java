package com.akon.magicalsaddle;

import com.akon.magicalsaddle.version.VersionWrapper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InteractionListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onUseSaddle(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (MetadataHelper.get(player, "JustClicked", Boolean.class).orElse(false)) {
			return;
		}
		MetadataHelper.set(player, "JustClicked", true);
		Bukkit.getScheduler().runTaskLater(MagicalSaddle.getInstance(), () -> MetadataHelper.remove(player, "JustClicked"), 1);
		ItemStack stack = event.getItem();
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !MagicalSaddleUtil.isMagicalSaddle(stack)) {
			return;
		}
		VersionWrapper versionWrapper = MagicalSaddle.getVersionWrapper();
		Object compound = versionWrapper.getTag(stack);
		Object horseNBT = versionWrapper.getCompoundTag(compound, "Horse");
		versionWrapper.removeTag(horseNBT, "UUID");
		versionWrapper.removeTag(horseNBT, "UUIDLeast");
		versionWrapper.removeTag(horseNBT, "UUIDMost");
		Entity maybeHorse = versionWrapper.loadEntityFromNBT(horseNBT, player.getWorld());
		if (!(maybeHorse instanceof AbstractHorse) || maybeHorse instanceof Llama) {
			return;
		}
		Location loc = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().add(0.5, 0, 0.5);
		loc.setYaw((float)Math.random() * 360);
		boolean sneaking = player.isSneaking();
		if (!sneaking) {
			ItemStack clone = stack.clone();
			clone.setAmount(1);
			((AbstractHorse)maybeHorse).getInventory().setSaddle(MagicalSaddleUtil.saveHorseInto(clone, (AbstractHorse)maybeHorse));
		}
		if (!versionWrapper.addEntity(maybeHorse, loc, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) {
			return;
		}
		loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc.add(0, 1, 0), 100, 0.5, 0.5, 0.5, 0);
		EquipmentSlot hand = event.getHand();
		versionWrapper.swingHand(player, hand == EquipmentSlot.OFF_HAND);
		PlayerInventory inventory = player.getInventory();
		if (sneaking) {
			ItemStack empty = MagicalSaddleUtil.empty(stack);
			empty.setAmount(1);
			if (stack.getAmount() == 1) {
				if (hand == EquipmentSlot.HAND) {
					inventory.setItemInMainHand(empty);
				} else {
					inventory.setItemInOffHand(empty);
				}
			} else {
				inventory.addItem(empty).values().forEach(item -> versionWrapper.makeDrop(player, item, false));
			}
			return;
		}
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		stack.setAmount(stack.getAmount() - 1);
		if (hand == EquipmentSlot.HAND) {
			inventory.setItemInMainHand(stack);
		} else {
			inventory.setItemInOffHand(stack);
		}
	}
}
