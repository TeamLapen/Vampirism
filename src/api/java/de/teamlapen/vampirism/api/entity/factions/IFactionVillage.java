package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IFactionVillage {
    /**
     * @return A faction specific bad omen effect
     */
    @Nullable
    MobEffect getBadOmenEffect();

    @NotNull
    ItemStack getBanner();

    List<CaptureEntityEntry<?>> getCaptureEntries();

    @NotNull
    VillagerProfession getFactionVillageProfession();

    @NotNull
    Class<? extends Mob> getGuardSuperClass();

    @Nullable
    EntityType<? extends ITaskMasterEntity> getTaskMasterEntity();

    @NotNull
    Block getTotemTopBlock(boolean crafted);

    boolean isBanner(@NotNull ItemStack stack);

    /**
     * @deprecated internal use only
     */
    @Deprecated
    default @NotNull IFactionVillage build() {
        return this;
    }
}
