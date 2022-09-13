package com.akon.magicalsaddle;

import com.akon.magicalsaddle.version.VersionWrapper;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.text.DecimalFormat;
import java.util.Arrays;

@Plugin(name = "MagicalSaddle", version = "1.1.1")
@Author("akon")
@Description("This plugin makes it possible to carry horses.")
public class MagicalSaddle extends JavaPlugin {

	public static final String MAGICAL_SADDLE_NAME = "§r§dMagical Saddle";
	private static final DecimalFormat DECIMAL_FORMAT1 = new DecimalFormat("0.##");
	private static final DecimalFormat DECIMAL_FORMAT2 = new DecimalFormat("0.####");

	@Getter
	private static MagicalSaddle instance;
	@Getter
	private static VersionWrapper versionWrapper;

	@Getter(lazy = true)
	private static final NamespacedKey recipeKey = new NamespacedKey(instance, "magical_saddle");

	@Override
	public void onEnable() {
		instance = this;
		boolean disable = false;
		String nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			versionWrapper = (VersionWrapper)Class.forName("com.akon.magicalsaddle.version." + nmsVersion).newInstance();
		} catch (ClassNotFoundException e) {
			this.getLogger().severe("Unsupported version: " + nmsVersion);
			disable = true;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			disable = true;
		}
		if (disable) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		addRecipes();
		Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
		Bukkit.getPluginManager().registerEvents(new InteractionListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);

	}

	private void addRecipes() {
		ItemStack stack = new ItemStack(Material.SADDLE);
		stack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 0);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(MAGICAL_SADDLE_NAME);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		stack.setItemMeta(meta);
		Object compound = versionWrapper.getTag(stack);
		versionWrapper.setBooleanTag(compound, "MagicalSaddle", true);
		Bukkit.addRecipe(
			new ShapelessRecipe(getRecipeKey(), versionWrapper.setTag(stack, compound))
				.addIngredient(Material.SADDLE)
				.addIngredient(Material.ENDER_PEARL)
		);
	}

	public static boolean isMagicalSaddle(ItemStack stack) {
		return stack != null && stack.getType() == Material.SADDLE && versionWrapper.getBooleanTag(versionWrapper.getTag(stack), "MagicalSaddle");
	}

	public static boolean hasHorse(ItemStack stack) {
		Validate.isTrue(isMagicalSaddle(stack), "The item is not a magical saddle");
		return versionWrapper.hasKeyTypeOf(versionWrapper.getTag(stack), "Horse", 10);
	}

	public static ItemStack saveHorseInto(ItemStack stack, AbstractHorse horse) {
		Validate.isTrue(!(horse instanceof Llama), "Llama is not a horse");
		Validate.isTrue(isMagicalSaddle(stack), "The item is not a magical saddle");
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

	public static void update(AbstractHorse horse) {
		Validate.isTrue(!(horse instanceof Llama), "Llama is not a horse");
		AbstractHorseInventory inventory = horse.getInventory();
		ItemStack stack = inventory.getSaddle();
		Validate.isTrue(isMagicalSaddle(stack), "The item is not a magical saddle");
		inventory.setSaddle(saveHorseInto(stack, horse));
	}

	public static ItemStack empty(ItemStack stack) {
		Validate.isTrue(isMagicalSaddle(stack), "The item is not a magical saddle");
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
