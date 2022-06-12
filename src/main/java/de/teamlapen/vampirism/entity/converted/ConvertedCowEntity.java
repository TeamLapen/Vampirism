package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ConvertedCowEntity extends ConvertedCreatureEntity<CowEntity> {
    public ConvertedCowEntity(EntityType<? extends ConvertedCreatureEntity> type, World world) {
        super(type, world);
    }

    @Nonnull
    public ActionResultType mobInteract(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.BUCKET && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack itemstack1 = DrinkHelper.createFilledResult(itemstack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, itemstack1);
            return ActionResultType.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    public static class ConvertingHandler extends DefaultConvertingHandler<CowEntity> {
        public ConvertingHandler() {
            super(null);
        }

        @Override
        public ConvertedCreatureEntity<CowEntity> createFrom(CowEntity entity) {
            return Helper.createEntity(ModEntities.CONVERTED_COW.get(), entity.getCommandSenderWorld()).map(creature -> {
                this.copyImportantStuff(creature, entity);
                return creature;
            }).orElse(null);
        }
    }
}
