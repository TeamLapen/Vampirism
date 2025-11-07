package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public interface IFactionVillage {

    /**
     * @return The faction-specific bad omen effect
     */
    @Nullable
    Holder<MobEffect> badOmenEffect();

    ItemStack createBanner(HolderLookup.Provider provider);

    List<Weighted<Supplier<EntityType<? extends Mob>>>> getCaptureEntries();

    ResourceKey<VillagerProfession> getFactionVillageProfession();

    Class<? extends Mob> getGuardSuperClass();

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
