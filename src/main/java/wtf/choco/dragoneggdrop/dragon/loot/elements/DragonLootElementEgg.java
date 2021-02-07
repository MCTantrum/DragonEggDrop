package wtf.choco.dragoneggdrop.dragon.loot.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import wtf.choco.dragoneggdrop.dragon.DragonTemplate;
import wtf.choco.dragoneggdrop.placeholder.DragonEggDropPlaceholders;

/**
 * An implementation of {@link IDragonLootElement} to represent a dragon egg. This element
 * can be generated as either an item in an inventory or as a block in the world.
 *
 * @author Parker Hawke - Choco
 */
public final class DragonLootElementEgg implements IDragonLootElement {

    private final String name;
    private final List<String> lore;
    private final double chance;
    private final boolean centered;

    /**
     * Create a {@link DragonLootElementEgg}.
     *
     * @param name the item's name
     * @param centered whether the item should be centered in the inventory
     * @param chance the chance that this element will be generated
     * @param lore the item's lore
     */
    public DragonLootElementEgg(String name, List<String> lore, boolean centered, double chance) {
        this.name = name;
        this.lore = new ArrayList<>(lore);
        this.chance = chance;
        this.centered = centered;
    }

    /**
     * Create a {@link DragonLootElementEgg}.
     *
     * @param name the item's name
     * @param chance the chance that this element will be generated
     * @param lore the item's lore
     */
    public DragonLootElementEgg(String name, List<String> lore, double chance) {
        this(name, lore, true, chance);
    }

    /**
     * Create a {@link DragonLootElementEgg}.
     *
     * @param chance the chance that this element will be generated
     */
    public DragonLootElementEgg(double chance) {
        this(null, Collections.EMPTY_LIST, true, chance);
    }

    /**
     * Create a {@link DragonLootElementEgg}.
     */
    public DragonLootElementEgg() {
        this(null, Collections.EMPTY_LIST, true, 100.0);
    }

    /**
     * Get the egg's name.
     *
     * @return the egg name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the egg's lore.
     *
     * @return the egg lore
     */
    public List<String> getLore() {
        return Collections.unmodifiableList(lore);
    }

    /**
     * Get the chance that this element will generate.
     *
     * @return the chance
     */
    public double getChance() {
        return chance;
    }

    /**
     * Check whether this element's item will be generated in the center slot of the
     * chest.
     *
     * @return true if centered in the inventory, false otherwise
     */
    public boolean isCentered() {
        return centered;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>NOTE:</strong> Hardcoded to 0.0. Eggs are not meant to be generated by loot pools
     */
    @Override
    public double getWeight() {
        return 0.0;
    }

    @Override
    public void generate(DragonBattle battle, DragonTemplate template, Player killer, Random random, Chest chest) {
        if (random.nextDouble() * 100 >= chance) {
            return;
        }

        if (chest == null) { // If no chest is present, just set the egg on the portal
            battle.getEndPortalLocation().add(0, 4, 0).getBlock().setType(Material.DRAGON_EGG);
            return;
        }

        ItemStack egg = new ItemStack(Material.DRAGON_EGG);
        ItemMeta eggMeta = egg.getItemMeta();

        if (name != null) {
            eggMeta.setDisplayName(DragonEggDropPlaceholders.inject(killer, name).replace("%dragon%", template.getName()));
        }

        if (lore != null && !lore.isEmpty()) {
            List<String> contextualLore = lore.stream().map(s -> DragonEggDropPlaceholders.inject(killer, s).replace("%dragon%", template.getName())).collect(Collectors.toList());
            eggMeta.setLore(contextualLore);
        }

        egg.setItemMeta(eggMeta);

        Inventory inventory = chest.getInventory();
        if (centered) {
            inventory.setItem(inventory.getSize() / 2, egg);
        }
        else if (inventory.firstEmpty() != -1) {
            boolean success = false;

            do {
                int slot = random.nextInt(inventory.getSize());
                if (inventory.getItem(slot) == null) {
                    inventory.setItem(slot, egg);
                    success = true;
                }
            } while (!success);
        }
    }

}
