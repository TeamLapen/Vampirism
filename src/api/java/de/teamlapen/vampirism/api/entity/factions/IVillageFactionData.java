package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public interface IVillageFactionData {

    IVillageFactionData INSTANCE = new IVillageFactionData() {
        @Nullable
        @Override
        public MobEffect getBadOmenEffect() {
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
        public Class<? extends Mob> getGuardSuperClass() {
            return Mob.class;
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

    /***
     * @return A faction specific bad omen effect
     */
    @Nullable
    MobEffect getBadOmenEffect();

    @Nonnull
    ItemStack getBanner();

    List<CaptureEntityEntry> getCaptureEntries();

    VillagerProfession getFactionVillageProfession();

    Class<? extends Mob> getGuardSuperClass();

    @Nullable
    EntityType<? extends ITaskMasterEntity> getTaskMasterEntity();

    /**
     * @return Pair of totem top blocks for faction. Left is the generated (crafted=false), right is the crafted
     */
    Pair<Block, Block> getTotemTopBlock();

    boolean isBanner(@Nonnull ItemStack stack);

}
