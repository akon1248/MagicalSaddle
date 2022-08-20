package com.akon.magicalsaddle;

import com.akon.magicalsaddle.version.VersionWrapper;
import org.bukkit.*;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.Arrays;
import java.util.Optional;

public class InventoryListener implements Listener {

	private static final Material LEAD;

	static {
		LEAD = Arrays.stream(Material.values()).filter(material -> material.name().equals("LEAD") || material.name().equals("LEASH")).findAny().get();
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Inventory inventory = event.getInventory();
		InventoryHolder holder;
		if (!(inventory instanceof AbstractHorseInventory) || !((holder = inventory.getHolder()) instanceof AbstractHorse) || holder instanceof Llama) {
			return;
		}
		new MagicalSaddleUpdater((AbstractHorse)holder, () -> !inventory.getViewers().isEmpty()).start();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		InventoryHolder holder;
		if (!(inventory instanceof AbstractHorseInventory) || !((holder = inventory.getHolder()) instanceof AbstractHorse) || holder instanceof Llama) {
			return;
		}
		AbstractHorseInventory horseInventory = (AbstractHorseInventory)inventory;
		AbstractHorse horse = (AbstractHorse)holder;
		ItemStack stack = horseInventory.getSaddle();
		if (MagicalSaddle.isMagicalSaddle(stack)) {
			if (event.getRawSlot() != 0) {
				return;
			}
			switch (event.getClick()) {
				case RIGHT:
				case LEFT:
				case SHIFT_RIGHT:
				case SHIFT_LEFT:
				case DROP:
				case CONTROL_DROP:
				case NUMBER_KEY:
					break;
				default:
					return;
			}
			event.setCancelled(true);
			stack = MagicalSaddle.saveHorseInto(stack, horse);
			Player player = (Player)event.getWhoClicked();
			VersionWrapper versionWrapper = MagicalSaddle.getVersionWrapper();
			player.getInventory().addItem(stack).forEach((index, item) -> versionWrapper.makeDrop(player, item, false));
			if (horse.setLeashHolder(null)) {
				horse.getWorld().dropItem(horse.getLocation(), new ItemStack(LEAD)).setPickupDelay(10);
			}
			Location loc = horse.getLocation();
			loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc.add(0, 1, 0), 100, 0.5, 0.5, 0.5, 0);
			horse.remove();
		} else {
			getEquippingSaddle(event)
				.filter(MagicalSaddle::isMagicalSaddle)
				.filter(item -> {
					if (MagicalSaddle.hasHorse(item)) {
						event.setCancelled(true);
						return false;
					}
					return true;
				})
				.ifPresent(item -> Bukkit.getScheduler().runTask(MagicalSaddle.getInstance(), () -> {
					ItemStack maybeSaddle = horseInventory.getSaddle();
					if (MagicalSaddle.isMagicalSaddle(maybeSaddle)) {
						horseInventory.setSaddle(MagicalSaddle.saveHorseInto(maybeSaddle, horse));
					}
				}));
		}
	}

	private static Optional<ItemStack> getEquippingSaddle(InventoryClickEvent event) {
		Inventory topInv = event.getView().getTopInventory();
		Inventory bottomInv = event.getView().getBottomInventory();
		InventoryHolder holder;
		AbstractHorseInventory horseInv;
		if (!(topInv instanceof AbstractHorseInventory) || !((holder = topInv.getHolder()) instanceof AbstractHorse) || holder instanceof Llama || (horseInv = (AbstractHorseInventory)topInv).getSaddle() != null) {
			return Optional.empty();
		}
		Optional<ItemStack> itemOpt;
		switch (event.getClick()) {
			case RIGHT:
			case LEFT:
				itemOpt = Optional.ofNullable(event.getCursor()).filter(item -> event.getInventory() == horseInv && event.getSlot() == 0);
				break;
			case SHIFT_RIGHT:
			case SHIFT_LEFT:
				itemOpt = Optional.ofNullable(event.getCurrentItem()).filter(item -> event.getRawSlot() >= (holder instanceof ChestedHorse && ((ChestedHorse)holder).isCarryingChest() ? 17 : 2));
				break;
			case NUMBER_KEY:
				int hotkey = event.getHotbarButton();
				itemOpt = Optional.ofNullable(bottomInv.getItem(hotkey)).filter(item -> event.getInventory() == horseInv && event.getSlot() == 0);
				break;
			default:
				return Optional.empty();
		}
		return itemOpt.filter(item -> item.getType() == Material.SADDLE);
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		Inventory inventory = event.getInventory();
		if (!(inventory instanceof AbstractHorseInventory) || ((AbstractHorseInventory)inventory).getSaddle() != null) {
			return;
		}
		event.setCancelled(event.getRawSlots().contains(0) && MagicalSaddle.isMagicalSaddle(event.getOldCursor()) && MagicalSaddle.hasHorse(event.getOldCursor()));
	}

	@EventHandler
	public void onRename(PrepareAnvilEvent event) {
		ItemStack stack = event.getResult();
		VersionWrapper versionWrapper = MagicalSaddle.getVersionWrapper();
		Object compound = versionWrapper.getTag(stack);
		Object horse = versionWrapper.getCompoundTagNullable(compound, "Horse");
		if (horse == null) {
			return;
		}
		String displayName = versionWrapper.getStringTag(versionWrapper.getCompoundTag(compound, "display"), "Name");
		versionWrapper.setStringTag(horse, "CustomName", displayName);
		event.setResult(versionWrapper.setTag(stack, compound));
	}

	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event) {
		Recipe recipe = event.getRecipe();
		if (!(recipe instanceof ShapelessRecipe) || !((ShapelessRecipe)recipe).getKey().equals(new NamespacedKey(MagicalSaddle.getInstance(), "magical_saddle"))) {
			return;
		}
		if (Arrays.stream(event.getInventory().getMatrix()).anyMatch(MagicalSaddle::isMagicalSaddle)) {
			event.getInventory().setResult(null);
		}
	}
}
