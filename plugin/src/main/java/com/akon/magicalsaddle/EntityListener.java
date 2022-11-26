package com.akon.magicalsaddle;

import com.akon.magicalsaddle.version.VersionWrapper;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Llama;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class EntityListener implements Listener {

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof AbstractHorse) || entity instanceof Llama) {
			return;
		}
		List<ItemStack> drops = event.getDrops();
		VersionWrapper versionWrapper = MagicalSaddle.getVersionWrapper();
		for (int i = 0; i < drops.size(); i++) {
			ItemStack stack = drops.get(i);
			if (!MagicalSaddleUtil.isMagicalSaddle(stack)) {
				continue;
			}
			Object compound = versionWrapper.getTag(stack);
			UUID uuid = versionWrapper.getUUIDTag(versionWrapper.getCompoundTag(compound, "Horse"), "UUID");
			if (!entity.getUniqueId().equals(uuid)) {
				continue;
			}
			drops.set(i, MagicalSaddleUtil.empty(stack));
		}
	}
}
