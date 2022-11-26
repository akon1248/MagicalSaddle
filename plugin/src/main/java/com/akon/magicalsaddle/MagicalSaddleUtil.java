package com.akon.magicalsaddle;

import com.akon.magicalsaddle.version.VersionWrapper;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.Arrays;

@UtilityClass
public class MagicalSaddleUtil {

	public final String MAGICAL_SADDLE_NAME = "§r§dMagical Saddle";
	private final DecimalFormat DECIMAL_FORMAT1 = new DecimalFormat("0.##");
	private final DecimalFormat DECIMAL_FORMAT2 = new DecimalFormat("0.####");

	public boolean isMagicalSaddle(ItemStack stack) {
		VersionWrapper versionWrapper = MagicalSaddle.getVersionWrapper();
		return stack != null && stack.getType() == Material.SADDLE && versionWrapper.getBooleanTag(versionWrapper.getTag(stack), "MagicalSaddle");
	}

	public boolean hasHorse(ItemStack stack) {
		Validate.isTrue(isMagicalSaddle(stack), "The item is not a magical saddle");
		VersionWrapper versionWrapper = MagicalSaddle.getVersionWrapper();
		return versionWrapper.hasKeyTypeOf(versionWrapper.getTag(stack), "Horse", 10);
	}

	public ItemStack saveHorseInto(ItemStack stack, AbstractHorse horse) {
		Validate.isTrue(!(horse instanceof Llama), "Llama is not a horse");
		Validate.isTrue(isMagicalSaddle(stack), "The item is not a magical saddle");
		VersionWrapper versionWrapper = MagicalSaddle.getVersionWrapper();
		Object entityNBT = versionWrapper.saveEntityToNBT(horse);
		versionWrapper.removeTag(entityNBT, "Pos");
		versionWrapper.removeTag(entityNBT, "Motion");
		versionWrapper.removeTag(entityNBT, "Rotation");
		versionWrapper.removeTag(entityNBT, "WorldUUID");
		versionWrapper.removeTag(entityNBT, "WorldUUIDLeast");
		versionWrapper.removeTag(entityNBT, "WorldUUIDMost");
		versionWrapper.removeTag(entityNBT, "SaddleItem");
		versionWrapper.setBooleanTag(entityNBT, "Leashed", false);
		versionWrapper.removeTag(entityNBT, "Leash");
		Object compound = versionWrapper.getTag(stack);
		versionWrapper.setCompoundTag(compound, "Horse", entityNBT);
		Object display = versionWrapper.getCompoundTag(compound, "display");
		String customName = versionWrapper.getStringTagNullable(entityNBT, "CustomName");
		if (customName != null) {
			versionWrapper.setStringTag(display, "Name", customName);
		}
		versionWrapper.setCompoundTag(compound, "display", display);
		stack = versionWrapper.setTag(stack, compound);
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(Arrays.asList(
			"§r§7Type: " + versionWrapper.getEntityTypeName(horse.getType()),
			"§r§7Health: " + DECIMAL_FORMAT1.format(horse.getHealth()),
			"§r§7Max Health: " + DECIMAL_FORMAT1.format(horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()),
			"§r§7Speed: " + DECIMAL_FORMAT2.format(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()),
			"§r§7Jump Strength: " + DECIMAL_FORMAT2.format(horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).getBaseValue())
		));
		stack.setItemMeta(meta);
		return stack;
	}

	public void update(AbstractHorse horse) {
		Validate.isTrue(!(horse instanceof Llama), "Llama is not a horse");
		AbstractHorseInventory inventory = horse.getInventory();
		ItemStack stack = inventory.getSaddle();
		Validate.isTrue(isMagicalSaddle(stack), "The item is not a magical saddle");
		inventory.setSaddle(saveHorseInto(stack, horse));
	}

	public ItemStack empty(ItemStack stack) {
		Validate.isTrue(isMagicalSaddle(stack), "The item is not a magical saddle");
		VersionWrapper versionWrapper = MagicalSaddle.getVersionWrapper();
		Object compound = versionWrapper.getTag(stack);
		versionWrapper.removeTag(compound, "Horse");
		versionWrapper.removeTag(compound, "display");
		ItemStack empty = versionWrapper.setTag(stack, compound);
		ItemMeta meta = empty.getItemMeta();
		meta.setDisplayName(MAGICAL_SADDLE_NAME);
		empty.setItemMeta(meta);
		return empty;
	}
}
