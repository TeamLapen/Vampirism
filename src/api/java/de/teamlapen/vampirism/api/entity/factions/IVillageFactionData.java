package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IVillageFactionData { //TODO 1.17 IFactionVillage
    /**
     * TODO 1.17 remove default for methods here (as they are implemented in the default instance)
     *
     * @return A faction specific bad omen effect
     */
    @Nullable
    default Effect getBadOmenEffect() {
        return null;
    }

    @Nonnull
    default ItemStack getBanner() { //TODO 1.17 remove default implementation
        return new ItemStack(Items.WHITE_BANNER);
    }

    List<CaptureEntityEntry> getCaptureEntries();

    @Nonnull
    VillagerProfession getFactionVillageProfession();

    @Nonnull
    Class<? extends MobEntity> getGuardSuperClass();

    @Nullable
    EntityType<? extends ITaskMasterEntity> getTaskMasterEntity();

    /**
     * @return Pair of totem top blocks for faction. Left is the generated (crafted=false), right is the crafted
     *
     * @deprecated use {@link #getTotemTopBlock(boolean)}
     */
    @Deprecated
    @Nonnull
    Pair<Block, Block> getTotemTopBlock(); //TODO 1.17 remove

    @Nonnull
    default Block getTotemTopBlock(boolean crafted) {
        return crafted ? getTotemTopBlock().getRight() : getTotemTopBlock().getLeft();
    }

    default boolean isBanner(@Nonnull ItemStack stack) {//TODO 1.17 remove default implementation
        return false;
    }

    /**
     * @deprecated internal use only
     */
    @Deprecated
    default IVillageFactionData build() {
        return this;
    }
}
