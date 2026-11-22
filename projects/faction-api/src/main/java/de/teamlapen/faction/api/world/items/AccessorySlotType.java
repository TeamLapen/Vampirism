package de.teamlapen.faction.api.world.items;

import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.tags.FactionRefinementSetTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;

/**
 * Slots types for {@link de.teamlapen.faction.api.world.items.IRefinementItem}
 * <br>
 * (like {@link net.minecraft.world.entity.EquipmentSlot} for other items)
 */
public enum AccessorySlotType implements StringRepresentable {
    AMULET("amulet", 0, FactionRefinementSetTags.AMULET),
    RING("ring", 1, FactionRefinementSetTags.RING),
    OBI_BELT("obi_belt", 2, FactionRefinementSetTags.OBI_BELT);

    private final String name;
    /**
     * slot index
     */
    private final int slot;
    public final TagKey<IRefinementSet> tag;

    AccessorySlotType(String name, int slot, TagKey<IRefinementSet> tag) {
        this.name = name;
        this.slot = slot;
        this.tag = tag;
    }

    /**
     * @return slot index
     */
    public int getSlot() {
        return slot;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
