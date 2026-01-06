package de.teamlapen.faction.api.factions.village;

import de.teamlapen.faction.api.factions.IFactionBuilder;
import de.teamlapen.faction.api.world.entities.ITaskMasterEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builder for faction village related attributes.
 * <br>
 * used for {@link IFactionBuilder}
 */
public interface IFactionVillageBuilder {

    /**
     * Supply an effect that can trigger faction raids
     *
     * @param badOmenEffect bad omen effect
     * @return this builder
     */
    IFactionVillageBuilder badOmenEffect(Holder<MobEffect> badOmenEffect);

    /**
     * Supply a banner item that is equipped by entities to add a faction bad omen effect to the killer
     *
     * @param bannerItem the banner itemstack
     * @return this builder
     */
    IFactionVillageBuilder banner(Function<HolderLookup.Provider, ItemStack> bannerItem);

    IFactionVillageBuilder guardTypes(TagKey<EntityType<?>> guards);

    /**
     * Supply a taskmaster for this faction
     *
     * @param taskmaster taskmaster entity type
     * @return this builder
     */
    <Z extends Entity & ITaskMasterEntity> IFactionVillageBuilder taskMaster(Supplier<EntityType<Z>> taskmaster);

    /**
     * Supply totem top blocks for this faction
     *
     * @param fragile the totem top for world generation
     * @param crafted the totem top for crafting
     * @return this builder
     */
    IFactionVillageBuilder totem(Supplier<? extends Block> fragile, Supplier<? extends Block> crafted);

    IFactionVillage build();
}
