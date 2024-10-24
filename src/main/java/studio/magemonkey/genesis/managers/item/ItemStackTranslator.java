package studio.magemonkey.genesis.managers.item;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import studio.magemonkey.genesis.core.GenesisBuy;
import studio.magemonkey.genesis.core.GenesisShop;
import studio.magemonkey.genesis.core.GenesisShopHolder;
import studio.magemonkey.genesis.managers.ClassManager;
import studio.magemonkey.genesis.managers.misc.StringManager;

import java.util.ArrayList;
import java.util.List;


public class ItemStackTranslator {


    public ItemStack translateItemStack(GenesisBuy buy,
                                        GenesisShop shop,
                                        GenesisShopHolder holder,
                                        ItemStack item,
                                        Player target,
                                        boolean finalVersion) {
        if (item != null) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();

                // Normal itemdata
                if (meta.hasDisplayName()) {
                    meta.setDisplayName(ClassManager.manager.getStringManager()
                            .transform(meta.getDisplayName(), buy, shop, holder, target));
                }

                if (meta.hasLore()) {
                    List<String> lore = meta.getLore();
                    for (int i = 0; i < lore.size(); i++) {
                        lore.set(i,
                                ClassManager.manager.getStringManager()
                                        .transform(lore.get(i), buy, shop, holder, target));
                    }
                    meta.setLore(splitLore(lore, ClassManager.manager.getSettings().getMaxLineLength(), finalVersion));
                }


                //Skull itemdata
                if (meta instanceof SkullMeta) {
                    SkullMeta skullmeta = (SkullMeta) meta;
                    NamespacedKey key =
                            new NamespacedKey(ClassManager.manager.getPlugin(), "skullOwnerPlaceholder");
                    CustomItemTagContainer tagContainer = meta.getCustomTagContainer();
                    if (tagContainer.hasCustomTag(key, ItemTagType.STRING)) {
                        String placeholder = tagContainer.getCustomTag(key, ItemTagType.STRING);
                        if (placeholder != null) {
                            String pName =
                                    ClassManager.manager.getStringManager().transform(placeholder, target);
                            OfflinePlayer transformedPlayer = Bukkit.getOfflinePlayer(pName);
                            if (transformedPlayer != null) {
                                skullmeta.setOwningPlayer(transformedPlayer);
                            } else {
                                skullmeta.setOwner(pName);
                            }
                        }
                    }
                }

                item.setItemMeta(meta);


                if (meta instanceof SkullMeta) {
                    transformCustomSkull(buy, shop, item, holder, target);
                }
            }


        }
        return item;
    }


    private void transformCustomSkull(GenesisBuy buy,
                                      GenesisShop shop,
                                      ItemStack item,
                                      GenesisShopHolder holder,
                                      Player target) {
        String skullTexture = ItemDataPartCustomSkull.readSkullTexture(item);
        if (skullTexture != null) {
            if (ClassManager.manager.getStringManager().checkStringForFeatures(shop, buy, item, skullTexture)) {
                item = ItemDataPartCustomSkull.transformSkull(item,
                        ClassManager.manager.getStringManager().transform(skullTexture, buy, shop, holder, target));
            }
        }
    }

    private List<String> splitLore(List<String> lore, int maxLineLength, boolean finalVersion) {
        if (maxLineLength > 0 && finalVersion) {
            List<String> goal = new ArrayList<>();
            for (String line : lore) {

                String[] words   = line.split(" ");
                String   current = null;

                for (String word : words) {
                    if (current == null) {
                        current = word;
                        continue;
                    }
                    String next = current + " " + word;
                    if (ChatColor.stripColor(next).length() > maxLineLength) {
                        goal.add(current);
                        String lastColors = current == null ? "" : ChatColor.getLastColors(current);
                        current = lastColors + word;
                    } else {
                        current = next;
                    }
                }
                goal.add(current);

            }

            return goal;
        } else {
            return lore;
        }
    }


    public String getFriendlyText(List<ItemStack> items) {
        if (items != null) {
            String msg = "";
            int    x   = 0;
            for (ItemStack i : items) {
                x++;
                msg += readItemStack(i) + (x < items.size() ? ", " : "");
            }
            return msg;
        }
        return null;
    }

    public String readItemStack(ItemStack i) {
        if (ClassManager.manager.getLanguageManager() != null) {
            return i.getAmount() + " " + ClassManager.manager.getLanguageManager().getDisplayNameItem(i);
        }
        String material = readMaterial(i);
        return i.getAmount() + " " + material;
    }

    public String readEnchantment(Enchantment e) {
        if (ClassManager.manager.getLanguageManager() != null) {
            return ClassManager.manager.getLanguageManager().getDisplayNameEnchantment(e);
        }
        return e.getName().toLowerCase().replace("_", "");
    }


    public boolean checkItemStackForFeatures(GenesisShop shop,
                                             GenesisBuy buy,
                                             ItemStack item) { // Returns true if this would make a shop customizable
        boolean b = false;
        if (item != null) {
            if (item.hasItemMeta()) {
                StringManager s    = ClassManager.manager.getStringManager();
                ItemMeta      meta = item.getItemMeta();

                // Normal ItemData
                if (meta.hasDisplayName()) {
                    if (s.checkStringForFeatures(shop, buy, item, meta.getDisplayName())) {
                        b = true;
                    }
                }

                if (meta.hasLore()) {
                    List<String> lore = meta.getLore();
                    for (String string : lore) {
                        if (s.checkStringForFeatures(shop, buy, item, string)) {
                            b = true;
                        }
                    }
                }

                // Skull ItemData
                if (meta instanceof SkullMeta) {
                    SkullMeta skullmeta = (SkullMeta) meta;
                    if (skullmeta.hasOwner()
                            && skullmeta.getOwningPlayer() != null
                            && skullmeta.getOwningPlayer().getName() != null) {
                        if (s.checkStringForFeatures(shop, buy, item,
                                skullmeta.getOwningPlayer().getName())) {
                            b = true;
                        }
                    }
                }
            }
        }
        return b;
    }

    public String readItemName(ItemStack item) {
        if (item != null) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasDisplayName()) {
                    return meta.getDisplayName();
                }
            }
            return readItemStack(item);
        }
        return null;
    }

    public String readMaterial(ItemStack item) {
        if (ClassManager.manager.getLanguageManager() != null) {
            ItemStack i = new ItemStack(item.getType());
            return ClassManager.manager.getLanguageManager().getDisplayNameItem(i);
        }
        String material = item.getType().name().toLowerCase().replace("_", " ");
        material = material.replaceFirst(material.substring(0, 1), material.substring(0, 1).toUpperCase());
        return material;
    }

    public void copyTexts(ItemStack receiver, ItemStack source) {
        if (source.hasItemMeta()) {
            ItemMeta metaSource   = source.getItemMeta();
            ItemMeta metaReceiver = receiver.getItemMeta();

            if (metaSource.hasDisplayName()) {
                metaReceiver.setDisplayName(metaSource.getDisplayName());
            }
            if (metaSource.hasLore()) {
                metaReceiver.setLore(metaSource.getLore());
            }

            if (metaSource instanceof SkullMeta && metaReceiver instanceof SkullMeta) {
                SkullMeta skullMetaSource   = (SkullMeta) metaSource;
                SkullMeta skullMetaReceiver = (SkullMeta) metaReceiver;

                if (skullMetaSource.hasOwner()) {
                    skullMetaReceiver.setOwner(skullMetaSource.getOwner());
                }
            }

            receiver.setItemMeta(metaReceiver);
        }
    }


    public boolean isItemList(Object o) {
        if (o instanceof List<?>) {
            return isItemList((List<?>) o);
        }
        return false;
    }

    public boolean isItemList(List<?> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }

        Object first = list.get(0);
        return first instanceof ItemStack;
    }
}
