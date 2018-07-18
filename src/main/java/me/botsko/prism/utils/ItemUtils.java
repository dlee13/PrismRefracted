package me.botsko.prism.utils;

import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import me.botsko.prism.Prism;
import me.botsko.prism.utils.EnchantmentUtils;

public class ItemUtils {

	/**
	 * 
	 * @param item_id
	 * @param sub_id
	 * @return
	 */
	private static EnumSet<Material> badWands = EnumSet.of(Material.WATER, Material.LAVA,
			Material.FIRE, Material.FLINT_AND_STEEL, Material.NETHER_PORTAL, Material.END_PORTAL);

	public static boolean isAcceptableWand(Material material) {
		return !badWands.contains(material);
	}

	public static String smallString(ItemStack stack) {
		if (stack != null) {
			String result = stack.getType().name().toLowerCase();

			short durability = stack.getDurability();
			if (durability > 0)
				result += ":" + durability;
			return result;
		}
		return null;
	}

	public static ItemStack itemOf(String smallString) {
		if (smallString != null) {
			String[] parts = smallString.split(":", 2);
			Material mat = Material.matchMaterial(parts[0].toUpperCase());

			if (mat != null) {
				if (parts.length > 1)
					try {
						return new ItemStack(mat, 1, Short.valueOf(parts[1]));
					} catch (NumberFormatException e) {
					}

				return new ItemStack(mat, 1);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param item
	 * @return
	 */
	public static boolean isValidItem(ItemStack item) {
		return (item != null && !item.getType().equals(Material.AIR));
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @param checkDura
	 * @return
	 */
	public static boolean isSameType(ItemStack a, ItemStack b, boolean checkDura) {

		// Initial type check
		if (!a.getType().equals(b.getType()))
			return false;

		// Durability check
		if (checkDura && a.getDurability() != b.getDurability())
			return false;

		return true;

	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(ItemStack a, ItemStack b) {
		return equals(a, b, dataValueUsedForSubitems(a.getType()));
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @param checkDura
	 * @return
	 */
	public static boolean equals(ItemStack a, ItemStack b, boolean checkDura) {

		ItemMeta metaA = a.getItemMeta();
		ItemMeta metaB = b.getItemMeta();

		// Type/dura
		if (!isSameType(a, b, checkDura))
			return false;

		// Display name
		if (metaA.getDisplayName() != null) {
			if (!metaA.getDisplayName().equals(metaB.getDisplayName()))
				return false;
		} else {
			if (metaB.getDisplayName() != null)
				return false;
		}

		// Coloring
		if (metaA instanceof LeatherArmorMeta) {
			if (!(metaB instanceof LeatherArmorMeta))
				return false;
			LeatherArmorMeta colorA = (LeatherArmorMeta) metaA;
			LeatherArmorMeta colorB = (LeatherArmorMeta) metaB;
			if (!colorA.getColor().equals(colorB.getColor()))
				return false;
		}

		// Lore
		if (metaA.getLore() != null && metaA.getLore() != null) {
			for (String lore : metaA.getLore()) {
				if (!metaB.getLore().contains(lore))
					return false;
			}
		} else if (!(metaA.getLore() == null && metaB.getLore() == null))
			return false;

		// Enchants
		if (!enchantsEqual(a.getEnchantments(), b.getEnchantments()))
			return false;

		// Books
		if (metaA instanceof BookMeta) {
			if (!(metaB instanceof BookMeta))
				return false;

			BookMeta bookA = (BookMeta) metaA;
			BookMeta bookB = (BookMeta) metaB;

			// Author
			if (bookA.getAuthor() != null) {
				if (!bookA.getAuthor().equals(bookB.getAuthor()))
					return false;
			}

			if (bookA.getTitle() != null) {
				if (!bookA.getTitle().equals(bookB.getTitle()))
					return false;
			}

			// Pages
			if (bookA.getPageCount() != bookB.getPageCount())
				return false;

			for (int page = 0; page < bookA.getPages().size(); page++) {
				String pageContentA = bookA.getPages().get(page);
				if (pageContentA != null) {
					if (!pageContentA.equals(bookB.getPages().get(page)))
						return false;
				}
			}
		}

		// Enchanted books
		if (metaA instanceof EnchantmentStorageMeta) {

			if (!(metaB instanceof EnchantmentStorageMeta))
				return false;

			EnchantmentStorageMeta enchA = (EnchantmentStorageMeta) metaA;
			EnchantmentStorageMeta enchB = (EnchantmentStorageMeta) metaB;

			if (enchA.hasStoredEnchants() != enchB.hasStoredEnchants())
				return false;

			if (!enchantsEqual(enchA.getStoredEnchants(), enchB.getStoredEnchants()))
				return false;

		}

		// Skulls
		if (metaA instanceof SkullMeta) {
			if (!(metaB instanceof SkullMeta))
				return false;

			SkullMeta skullA = (SkullMeta) metaA;
			SkullMeta skullB = (SkullMeta) metaB;

			if (skullA.hasOwner() != skullB.hasOwner())
				return false;

			return skullA.hasOwner()
					&& skullA.getOwningPlayer().getUniqueId().equals(skullB.getOwningPlayer().getUniqueId());
		}

		// Potions
		if (metaA instanceof PotionMeta) {
			if (!(metaB instanceof PotionMeta))
				return false;

			PotionMeta potA = (PotionMeta) metaA;
			PotionMeta potB = (PotionMeta) metaB;

			for (int c = 0; c < potA.getCustomEffects().size(); c++) {
				PotionEffect e = potA.getCustomEffects().get(c);
				if (!e.equals(potB.getCustomEffects().get(c)))
					return true;
			}
		}

		// Fireworks
		if (metaA instanceof FireworkMeta) {
			if (!(metaB instanceof FireworkMeta))
				return false;

			FireworkMeta fwA = (FireworkMeta) metaA;
			FireworkMeta fwB = (FireworkMeta) metaB;

			if (fwA.getPower() != fwB.getPower())
				return false;

			for (int e = 0; e < fwA.getEffects().size(); e++) {
				if (!fwA.getEffects().get(e).equals(fwB.getEffects().get(e)))
					return false;
			}
		}

		// Firework Effects
		if (metaA instanceof FireworkEffectMeta) {
			if (!(metaB instanceof FireworkEffectMeta))
				return false;

			FireworkEffectMeta fwA = (FireworkEffectMeta) metaA;
			FireworkEffectMeta fwB = (FireworkEffectMeta) metaB;

			FireworkEffect effectA = fwA.getEffect();
			FireworkEffect effectB = fwB.getEffect();

			if (!effectA.getType().equals(effectB.getType()))
				return false;

			if (effectA.getColors().size() != effectB.getColors().size())
				return false;

			// Colors
			for (int c = 0; c < effectA.getColors().size(); c++) {
				if (!effectA.getColors().get(c).equals(effectB.getColors().get(c)))
					return false;
			}

			if (effectA.getFadeColors().size() != effectB.getFadeColors().size())
				return false;

			// Fade colors
			for (int c = 0; c < effectA.getFadeColors().size(); c++) {
				if (!effectA.getFadeColors().get(c).equals(effectB.getFadeColors().get(c)))
					return false;
			}

			if (effectA.hasFlicker() != effectB.hasFlicker())
				return false;
			if (effectA.hasTrail() != effectB.hasTrail())
				return false;

		}

		return true;

	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	protected static boolean enchantsEqual(Map<Enchantment, Integer> a, Map<Enchantment, Integer> b) {

		// Enchants
		if (a.size() != b.size())
			return false;

		// Match enchantments and levels
		for (Entry<Enchantment, Integer> entryA : a.entrySet()) {

			// If enchantment not present
			if (!b.containsKey(entryA.getKey()))
				return false;

			// If levels don't match
			if (!b.get(entryA.getKey()).equals(entryA.getValue()))
				return false;

		}

		return true;

	}

	/**
	 * @todo this is buggy, wth?
	 * @return
	 */
	public static String getUsedDurabilityPercentage(ItemStack item) {

		short dura = item.getDurability();
		short max_dura = item.getType().getMaxDurability();
		if (dura > 0 && max_dura > 0 && dura != max_dura) {
			double diff = ((dura / max_dura) * 100);
			if (diff > 0) {
				return Math.floor(diff) + "%";
			}
		}

		return "";
	}

	/**
	 * Returns the durability remaining
	 * 
	 * @return
	 */
	public static String getDurabilityPercentage(ItemStack item) {

		short dura = item.getDurability();
		short max_dura = item.getType().getMaxDurability();
		if (dura > 0 && max_dura > 0 && dura != max_dura) {
			double diff = max_dura - dura;
			diff = ((diff / max_dura) * 100);
			if (diff > 0) {
				return Math.floor(diff) + "%";
			}
			return "0%";
		}

		return "";
	}

	/**
	 * Returns a proper full name for an item, which includes meta content as well.
	 * 
	 * @return string
	 */
	public static String getItemFullNiceName(ItemStack item) {

		String item_name = "";

		// Leather Coloring
		if (item.getType().name().contains("LEATHER_")) {
			LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
			if (lam.getColor() != null) {
				item_name += "dyed ";
			}
		}

		// Skull Owner
		else if (item.getType() == Material.PLAYER_HEAD || item.getType() == Material.PLAYER_WALL_HEAD) {
			SkullMeta skull = (SkullMeta) item.getItemMeta();
			if (skull.hasOwner()) {
				item_name += skull.getOwningPlayer().getName() + "'s ";
			}
		}

		// Set the base item name
		if (dataValueUsedForSubitems(item.getType())) {
			item_name += Prism.getItems().getAlias(item.getType(), null);
		} else {
			item_name += Prism.getItems().getAlias(item.getType(), null);
		}
		if (item_name.isEmpty()) {
			item_name += item.getType().toString().toLowerCase().replace("_", " ");
		}

		// Anvils
		if (item.getType() == Material.ANVIL) {
			if (item.getDurability() == 1) {
				item_name = "slightly damaged anvil";
			} else if (item.getDurability() == 2) {
				item_name = "very damaged anvil";
			}
		}

		// Written books
		if (item.getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta meta = (BookMeta) item.getItemMeta();
			if (meta != null) {
				item_name += " '" + meta.getTitle() + "' by " + meta.getAuthor();
			}
		}

		// Enchanted books
		else if (item.getType().equals(Material.ENCHANTED_BOOK)) {
			EnchantmentStorageMeta bookEnchantments = (EnchantmentStorageMeta) item.getItemMeta();
			if (bookEnchantments.hasStoredEnchants()) {
				int i = 1;
				Map<Enchantment, Integer> enchs = bookEnchantments.getStoredEnchants();
				if (enchs.size() > 0) {
					item_name += " with";
					for (Map.Entry<Enchantment, Integer> ench : enchs.entrySet()) {
						item_name += " "
								+ EnchantmentUtils.getClientSideEnchantmentName(ench.getKey(), ench.getValue());
						item_name += (i < enchs.size() ? ", " : "");
						i++;
					}
				}
			}
		}

		// Enchantments
		int i = 1;
		Map<Enchantment, Integer> enchs = item.getEnchantments();
		if (enchs.size() > 0) {
			item_name += " with";
			for (Map.Entry<Enchantment, Integer> ench : enchs.entrySet()) {
				item_name += " " + EnchantmentUtils.getClientSideEnchantmentName(ench.getKey(), ench.getValue());
				item_name += (i < enchs.size() ? ", " : "");
				i++;
			}
		}

		// Fireworks
		if (item.getType() == Material.FIREWORK_STAR) {
			FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) item.getItemMeta();
			if (fireworkMeta.hasEffect()) {
				FireworkEffect effect = fireworkMeta.getEffect();
				if (!effect.getColors().isEmpty()) {
					item_name += " " + effect.getColors().size() + " colors";
					// int[] effectColors = new int[ effect.getColors().size() ];
					// for (Color effectColor : effect.getColors()){
					//// item_name += effectColor.
					// }
				}
				if (!effect.getFadeColors().isEmpty()) {
					item_name += " " + effect.getFadeColors().size() + " fade colors";
					// int[] fadeColors = new int[ effect.getColors().size() ];
					// for (Color fadeColor : effect.getFadeColors()){
					//// item_name += fadeColor.asRGB();
					// };
				}
				if (effect.hasFlicker()) {
					item_name += " flickering";
				}
				if (effect.hasTrail()) {
					item_name += " with trail";
				}
			}
		}

		// Custom item names
		ItemMeta im = item.getItemMeta();
		if (im != null) {
			String displayName = im.getDisplayName();
			if (displayName != null) {
				item_name += ", named \"" + displayName + "\"";
			}
		}

		return item_name;

	}

	/**
	 * Returns true if an item uses its damage value for something other than
	 * durability.
	 *
	 * @param id
	 * @return
	 */
	/*private static EnumSet<Material> dataMaterials = EnumSet.of(Material.STONE, Material.DIRT, Material.WOOD,
			Material.SAPLING, Material.SAND, Material.LOG, Material.LEAVES, Material.SPONGE, Material.SANDSTONE,
			Material.DIRT, Material.LONG_GRASS, Material.WOOL, Material.RED_ROSE, Material.DOUBLE_STEP, Material.STEP,
			Material.SNOW, Material.STAINED_GLASS, Material.MONSTER_EGGS, Material.SMOOTH_BRICK, Material.SNOW,
			Material.WOOD_DOUBLE_STEP, Material.WOOD_STEP, Material.COBBLE_WALL, Material.SKULL, Material.ANVIL,
			Material.QUARTZ_BLOCK, Material.HARD_CLAY, Material.STAINED_GLASS_PANE, Material.LEAVES_2, Material.LOG_2,
			Material.PRISMARINE, Material.CARPET, Material.DOUBLE_PLANT, Material.RED_SANDSTONE, Material.CONCRETE,
			Material.CONCRETE_POWDER,

			Material.COAL, Material.GOLDEN_APPLE, Material.RAW_FISH, Material.COOKED_FISH, Material.INK_SACK,
			Material.MAP, Material.POTION, Material.SKULL_ITEM);*/

	// THANK YOU 1.13!
	public static boolean dataValueUsedForSubitems(Material material) {
		return false;
		//return dataMaterials.contains(material);
	}

	/**
	 * Determines if an itemstack can be stacked. Maz stack size, meta data, and
	 * more taken into account.
	 * 
	 * @param item
	 */
	public static boolean canSafelyStack(ItemStack item) {
		// Can't stack
		if (item.getMaxStackSize() == 1) {
			return false;
		}
		// Has meta
		ItemMeta im = item.getItemMeta();
		if (im.hasDisplayName() || im.hasEnchants() || im.hasLore()) {
			return false;
		}
		return true;
	}

	/**
	 * Drop an item at a given location.
	 *
	 * @param location
	 *            The location to drop the item at
	 * @param itemStack
	 *            The item to drop
	 */
	public static void dropItem(Location location, ItemStack itemStack) {
		location.getWorld().dropItemNaturally(location, itemStack);
	}

	/**
	 * Drop items at a given location.
	 *
	 * @param location
	 *            The location to drop the items at
	 * @param is
	 *            The items to drop
	 * @param quantity
	 *            The amount of items to drop
	 */
	public static void dropItem(Location location, ItemStack is, int quantity) {
		for (int i = 0; i < quantity; i++) {
			dropItem(location, is);
		}
	}
}