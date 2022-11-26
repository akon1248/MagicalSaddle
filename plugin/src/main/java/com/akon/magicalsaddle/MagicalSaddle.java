package com.akon.magicalsaddle;

import com.akon.magicalsaddle.version.VersionWrapper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name = "MagicalSaddle", version = "1.1.2")
@Author("akon")
@Description("This plugin makes it possible to carry horses.")
public class MagicalSaddle extends JavaPlugin {

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
		meta.setDisplayName(MagicalSaddleUtil.MAGICAL_SADDLE_NAME);
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
}
