package com.akon.magicalsaddle;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BooleanSupplier;

public class MagicalSaddleUpdater extends BukkitRunnable {

	public MagicalSaddleUpdater(AbstractHorse horse, BooleanSupplier predicate) {
		Validate.isTrue(!(horse instanceof Llama), "Llama is not a horse");
		this.horse = horse;
		this.predicate = predicate;
	}

	private final AbstractHorse horse;
	private final BooleanSupplier predicate;

	@Override
	public void run() {
		ItemStack stack = this.horse.getInventory().getSaddle();
		if (!this.predicate.getAsBoolean() || !MagicalSaddleUtil.isMagicalSaddle(stack)) {
			this.cancel();
			return;
		}
		MagicalSaddleUtil.update(this.horse);
	}

	public void start() {
		this.runTaskTimer(MagicalSaddle.getInstance(), 0, 5);
	}

}
