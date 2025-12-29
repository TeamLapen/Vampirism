package de.teamlapen.factions.api.factions.village;

import de.teamlapen.factions.api.world.entities.ITaskMasterEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public interface IFactionVillage {

    /**
     * @return The faction-specific bad omen effect
     */
    @Nullable
    Holder<MobEffect> badOmenEffect();

    /**
     * Creates a banner for the bad omen effect.
     * <p>
     * It's just white when the faction does not support it.
     */
    ItemStack createBanner(HolderLookup.Provider provider);

    @Nullable
    TagKey<EntityType<?>> getVillageGuardTypes();

    /**
     * @return The entity type of the task master entity for this faction
     */
    @Nullable
    EntityType<? extends ITaskMasterEntity> getTaskMasterEntity();

    /**
     * @return The block that represents the fragile or crafted totem top block for this faction
     */
    Block getTotemTopBlock(boolean crafted);
}
