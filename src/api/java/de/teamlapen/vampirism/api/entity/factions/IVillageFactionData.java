package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public interface IVillageFactionData {

    IVillageFactionData INSTANCE = new IVillageFactionData() {
        @Nullable
        @Override
        public Effect getBadOmenEffect() {
            return null;
        }

        @Nonnull
        @Override
        public ItemStack getBanner() {
            return new ItemStack(Items.WHITE_BANNER);
        }

        @Override
        public List<CaptureEntityEntry> getCaptureEntries() {
            return Collections.emptyList();
        }

        @Override
        public VillagerProfession getFactionVillageProfession() {
            return VillagerProfession.NONE;
        }

        @Override
        public Class<? extends MobEntity> getGuardSuperClass() {
            return MobEntity.class;
        }

        @Nullable
        @Override
        public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
            return null;
        }

        @Override
        public Pair<Block, Block> getTotemTopBlock() {
            return Pair.of(Blocks.AIR, Blocks.AIR);
        }

        @Override
        public boolean isBanner(@Nonnull ItemStack stack) {
            return ItemStack.matches(getBanner(), stack);
        }
    };

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

    VillagerProfession getFactionVillageProfession();

    Class<? extends MobEntity> getGuardSuperClass();

    @Nullable
    EntityType<? extends ITaskMasterEntity> getTaskMasterEntity();

    /**
     * @return Pair of totem top blocks for faction. Left is the generated (crafted=false), right is the crafted
     */
    Pair<Block, Block> getTotemTopBlock();

    default boolean isBanner(@Nonnull ItemStack stack) {//TODO 1.17 remove default implementation
        return false;
    }

}
