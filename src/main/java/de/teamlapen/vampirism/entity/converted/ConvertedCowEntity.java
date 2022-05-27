package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class ConvertedCowEntity extends ConvertedCreatureEntity<Cow> {
    public ConvertedCowEntity(EntityType<? extends ConvertedCreatureEntity> type, Level world) {
        super(type, world);
    }

    @Nonnull
    public InteractionResult mobInteract(Player player, @Nonnull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.BUCKET && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, itemstack1);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    public static class ConvertingHandler extends DefaultConvertingHandler<Cow> {
        public ConvertingHandler() {
            super(null);
        }

        @Override
        public ConvertedCreatureEntity<Cow> createFrom(Cow entity) {
            return Helper.createEntity(ModEntities.CONVERTED_COW.get(), entity.getCommandSenderWorld()).map(creature -> {
                this.copyImportantStuff(creature, entity);
                return creature;
            }).orElse(null);
        }
    }
}
