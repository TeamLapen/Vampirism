package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IFactionVillage {
    /**
     * @return A faction specific bad omen effect
     */
    @Nullable
    MobEffect getBadOmenEffect();

    @Nonnull
    ItemStack getBanner();

    List<CaptureEntityEntry<?>> getCaptureEntries();

    @Nonnull
    VillagerProfession getFactionVillageProfession();

    @Nonnull
    Class<? extends Mob> getGuardSuperClass();

    @Nullable
    EntityType<? extends ITaskMasterEntity> getTaskMasterEntity();

    @Nonnull
    Block getTotemTopBlock(boolean crafted);

    boolean isBanner(@Nonnull ItemStack stack);

    /**
     * @deprecated internal use only
     */
    @Deprecated
    default IFactionVillage build() {
        return this;
    }
}
