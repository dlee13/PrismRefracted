package network.darkhelmet.prism.actions.data;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import net.md_5.bungee.chat.ComponentSerializer;
import network.darkhelmet.prism.Prism;
import network.darkhelmet.prism.actions.typeadapter.ItemNameIgnoreEmptyAdapter;
import network.darkhelmet.prism.api.objects.MaterialState;
import network.darkhelmet.prism.utils.EntityUtils;
import network.darkhelmet.prism.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemStackActionData {
    public int amt;
    public Material material;
    @JsonAdapter(ItemNameIgnoreEmptyAdapter.class)
    public String name;
    public int color;
    public String owner;
    public String[] enchs;
    public String by;
    public String title;
    public BookMeta.Generation generation;
    public String[] lore;
    public String[] content;
    public String slot = "-1";
    public int[] effectColors;
    public int[] fadeColors;
    public boolean hasFlicker;
    public boolean hasTrail;
    public short durability = 0;
    public Map<String, String> bannerMeta;
    public String potionType;
    public boolean hasTrim;
    public NamespacedKey trimMaterial;
    public NamespacedKey trimPattern;
    public Map<Integer, ItemStackActionData> shulkerBoxInv;  // Deprecated
    public Map<Integer, ItemStackActionData> blockInventory;

    public static ItemStackActionData createData(ItemStack item, int quantity, short durability, Map<Enchantment, Integer> enchantments) {

        ItemStackActionData actionData = new ItemStackActionData();

        if (item == null || item.getAmount() <= 0) {
            return null;
        }
        actionData.durability = (short) ItemUtils.getItemDamage(item);

        if (durability >= 0) {
            actionData.durability = durability;
        }

        actionData.amt = quantity;
        actionData.material = item.getType();

        final ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : null;
        if (meta != null) {
            actionData.name = meta.getDisplayName();
        }
        if (meta instanceof LeatherArmorMeta lam) {
            actionData.color = lam.getColor().asRGB();
        } else if (meta instanceof SkullMeta skull) {
            if (skull.hasOwner()) {
                actionData.owner = Objects.requireNonNull(skull.getOwningPlayer()).getUniqueId().toString();
            }
        } else if (meta instanceof PotionMeta potion) {
            actionData.potionType = potion.getBasePotionType().getKey().getKey();
        }

        // Written books
        if (meta instanceof BookMeta bookMeta) {
            actionData.by = bookMeta.getAuthor();
            actionData.title = bookMeta.getTitle();
            actionData.generation = bookMeta.getGeneration();
            if (Prism.isSpigot) {
                actionData.content = bookMeta.spigot().getPages().stream().map(ComponentSerializer::toString).toArray(String[]::new);
            } else {
                actionData.content = bookMeta.getPages().toArray(new String[0]);
            }
        }

        // Lore
        if (meta != null && meta.hasLore()) {
            actionData.lore = Objects.requireNonNull(meta.getLore()).toArray(new String[0]);
        }

        // Enchantments
        if (!enchantments.isEmpty()) {
            final String[] enchs = new String[enchantments.size()];
            int i = 0;
            for (final Map.Entry<Enchantment, Integer> ench : enchantments.entrySet()) {
                // This is silly
                enchs[i] = ench.getKey().getKey().getKey() + ":" + ench.getValue();
                i++;
            }
            actionData.enchs = enchs;
        } else if (meta instanceof EnchantmentStorageMeta bookEnchantments) {
            if (bookEnchantments.hasStoredEnchants()) {
                if (!bookEnchantments.getStoredEnchants().isEmpty()) {
                    final String[] enchs = new String[bookEnchantments.getStoredEnchants().size()];
                    int i = 0;
                    for (final Map.Entry<Enchantment, Integer> ench : bookEnchantments.getStoredEnchants().entrySet()) {
                        // This is absolutely silly
                        enchs[i] = ench.getKey().getKey().getKey() + ":" + ench.getValue();
                        i++;
                    }
                    actionData.enchs = enchs;
                }
            }
        }
        if (meta instanceof FireworkEffectMeta) {
            applyFireWorksMetaToActionData(meta, actionData);
        }
        if (meta instanceof BannerMeta) {
            List<Pattern> patterns = ((BannerMeta) meta).getPatterns();
            Map<String, String> stringyPatterns = new HashMap<>();
            patterns.forEach(
                    pattern -> stringyPatterns.put(pattern.getPattern().getKey().getKey(), pattern.getColor().name()));
            actionData.bannerMeta = stringyPatterns;
        }
        if (meta instanceof BlockStateMeta) {
            BlockState blockState = ((BlockStateMeta) meta).getBlockState();
            if (blockState instanceof BlockInventoryHolder) {
                Inventory inventory = ((BlockInventoryHolder) blockState).getInventory();
                ItemStack[] contents = inventory.getContents();
                actionData.blockInventory = new HashMap<>();
                for (int i = 0; i < contents.length; i++) {
                    ItemStack invItem = contents[i];
                    if (invItem == null) {
                        continue;
                    }
                    actionData.blockInventory.put(i, createData(invItem, invItem.getAmount(), (short) ItemUtils.getItemDamage(invItem), invItem.getEnchantments()));
                }
            }
        }
        if (Prism.getInstance().getServerMajorVersion() >= 20 && meta instanceof ArmorMeta armorMeta) {
            actionData.hasTrim = armorMeta.hasTrim();
            if (actionData.hasTrim) {
                ArmorTrim trim = armorMeta.getTrim();
                actionData.trimMaterial = trim.getMaterial().getKey();
                actionData.trimPattern = trim.getPattern().getKey();
            }
        }
        return actionData;
    }


    private static void applyFireWorksMetaToActionData(ItemMeta meta, ItemStackActionData actionData) {
        final FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) meta;
        if (fireworkMeta.hasEffect()) {
            final FireworkEffect effect = fireworkMeta.getEffect();
            if (effect != null) {
                if (!effect.getColors().isEmpty()) {
                    final int[] effectColors = new int[effect.getColors().size()];
                    int i = 0;
                    for (final Color effectColor : effect.getColors()) {
                        effectColors[i] = effectColor.asRGB();
                        i++;
                    }
                    actionData.effectColors = effectColors;
                }

                if (!effect.getFadeColors().isEmpty()) {
                    final int[] fadeColors = new int[effect.getColors().size()];
                    final int i = 0;
                    for (final Color fadeColor : effect.getFadeColors()) {
                        fadeColors[i] = fadeColor.asRGB();
                    }
                    actionData.fadeColors = fadeColors;
                }
                if (effect.hasFlicker()) {
                    actionData.hasFlicker = true;
                }
                if (effect.hasTrail()) {
                    actionData.hasTrail = true;
                }
            }
        }
    }

    public static ItemStack deserializeFireWorksMeta(ItemStack item, ItemMeta meta, ItemStackActionData actionData) {

        final FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) meta;
        final FireworkEffect.Builder effect = FireworkEffect.builder();

        for (int i = 0; i < actionData.effectColors.length; i++) {
            effect.withColor(Color.fromRGB(actionData.effectColors[i]));
        }
        fireworkMeta.setEffect(effect.build());

        if (actionData.fadeColors != null) {
            for (int i = 0; i < actionData.fadeColors.length; i++) {
                effect.withFade(Color.fromRGB(actionData.fadeColors[i]));
            }
            fireworkMeta.setEffect(effect.build());
        }
        if (actionData.hasFlicker) {
            effect.flicker(true);
        }
        if (actionData.hasTrail) {
            effect.trail(true);
        }
        fireworkMeta.setEffect(effect.build());
        item.setItemMeta(fireworkMeta);
        return item;
    }

    public ItemStack toItem() {
        ItemStack item = new ItemStack(material, amt);

        if (durability > 0) {
            MaterialState.setItemDamage(item, durability);
        }

        // Restore enchantment
        if (enchs != null) {
            for (final String ench : enchs) {
                final String[] enchArgs = ench.split(":");
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchArgs[0]));

                // Restore book enchantment
                if (enchantment != null) {
                    if (item.getType() == Material.ENCHANTED_BOOK) {
                        final EnchantmentStorageMeta bookEnchantments = (EnchantmentStorageMeta) item.getItemMeta();
                        bookEnchantments.addStoredEnchant(enchantment, Integer.parseInt(enchArgs[1]), false);
                        item.setItemMeta(bookEnchantments);
                    } else {
                        item.addUnsafeEnchantment(enchantment, Integer.parseInt(enchArgs[1]));
                    }
                }
            }
        }

        ItemMeta meta = item.getItemMeta();

        // Leather color
        if (meta instanceof LeatherArmorMeta lam && color > 0) {
            lam.setColor(Color.fromRGB(color));
            item.setItemMeta(lam);
        } else if (meta instanceof SkullMeta skull && owner != null) {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(EntityUtils.uuidOf(owner)));
            item.setItemMeta(skull);
        } else if (meta instanceof BookMeta bookMeta) {
            bookMeta.setAuthor(by);
            bookMeta.setTitle(title);
            bookMeta.setGeneration(generation);
            if (content != null) {
                // May be null if a writable book has not been opened
                if (Prism.isSpigot) {
                    try {
                        bookMeta.spigot().setPages(Arrays.stream(content).map(ComponentSerializer::parse).collect(Collectors.toList()));
                    } catch (JsonParseException ex) {
                        // Old Prism version saves plain text
                        bookMeta.setPages(content);
                    }
                } else {
                    bookMeta.setPages(content);
                }
            }
            item.setItemMeta(bookMeta);
        } else if (meta instanceof PotionMeta potionMeta) {
            var namespacedKey = NamespacedKey.fromString(this.potionType);
            if (namespacedKey != null) {
                var potionType = Registry.POTION.get(namespacedKey);
                if (potionType != null) {
                    potionMeta.setBasePotionType(potionType);
                }
            }
        }
        if (meta instanceof FireworkEffectMeta && effectColors != null
                && effectColors.length > 0) {

            item = ItemStackActionData.deserializeFireWorksMeta(item, meta, this);
        }
        if (meta instanceof BannerMeta && bannerMeta != null) {
            Map<String, String> stringStringMap = bannerMeta;
            List<Pattern> patterns = new ArrayList<>();
            stringStringMap.forEach((patternKey, dyeName) -> {
                var namespacedKey = NamespacedKey.fromString(patternKey);
                if (namespacedKey != null) {
                    var bannerPattern = Registry.BANNER_PATTERN.get(namespacedKey);
                    if (bannerPattern != null) {
                        DyeColor color = DyeColor.valueOf(dyeName);
                        Pattern p = new Pattern(color, bannerPattern);
                        patterns.add(p);
                    }
                }
            });
            ((BannerMeta) meta).setPatterns(patterns);
        }
        if (meta instanceof BlockStateMeta) {
            BlockState blockState = ((BlockStateMeta) meta).getBlockState();
            if (blockState instanceof BlockInventoryHolder) {
                if (blockInventory != null) {
                    Inventory inventory = ((BlockInventoryHolder) blockState).getInventory();
                    for (Map.Entry<Integer, ItemStackActionData> entry : blockInventory.entrySet()) {
                        inventory.setItem(entry.getKey(), entry.getValue().toItem());
                    }
                } else if (blockState instanceof ShulkerBox  // else if : before we use blockInventory field
                        // For older version
                        && shulkerBoxInv != null) {
                    Inventory inventory = ((ShulkerBox) blockState).getInventory();
                    for (Map.Entry<Integer, ItemStackActionData> entry : shulkerBoxInv.entrySet()) {
                        inventory.setItem(entry.getKey(), entry.getValue().toItem());
                    }
                }
                ((BlockStateMeta) meta).setBlockState(blockState);
            }
        }
        if (Prism.getInstance().getServerMajorVersion() >= 20 && meta instanceof ArmorMeta) {
            ArmorTrim trim = null;
            if (hasTrim) {
                trim = new ArmorTrim(Registry.TRIM_MATERIAL.get(trimMaterial), Registry.TRIM_PATTERN.get(trimPattern));
            }
            ((ArmorMeta) meta).setTrim(trim);
        }

        if (name != null) {
            if (meta == null) {
                meta = item.getItemMeta();
            }

            if (meta != null) {
                meta.setDisplayName(name);
            }
        }

        if (lore != null) {
            if (meta == null) {
                meta = item.getItemMeta();
            }

            if (meta != null) {
                meta.setLore(Arrays.asList(lore));
            }
        }

        if (meta != null) {
            item.setItemMeta(meta);
        }

        return item;
    }
}
