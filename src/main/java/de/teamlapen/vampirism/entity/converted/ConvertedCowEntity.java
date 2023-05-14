package de.teamlapen.vampirism.entity.converted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.data.reloadlistener.ConvertiblesReloadListener;
import de.teamlapen.vampirism.entity.converted.converter.DefaultConverter;
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
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConvertedCowEntity extends ConvertedCreatureEntity<Cow> {
    public ConvertedCowEntity(EntityType<? extends ConvertedCreatureEntity> type, Level world) {
        super(type, world);
    }

    @NotNull
    public InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.BUCKET && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, itemstack1);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    public static class ConvertingHandler extends DefaultConvertingHandler<Cow> {
        public ConvertingHandler() {
            super(null);
        }

        public ConvertingHandler(IDefaultHelper helper) {
            super(helper);
        }

        @Override
        public ConvertedCreatureEntity<Cow> createFrom(@NotNull Cow entity) {
            return Helper.createEntity(ModEntities.CONVERTED_COW.get(), entity.getCommandSenderWorld()).map(creature -> {
                this.copyImportantStuff(creature, entity);
                return creature;
            }).orElse(null);
        }
    }

    public static class CowConverter extends DefaultConverter {

        public static final Codec<CowConverter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ConvertiblesReloadListener.EntityEntry.Attributes.CODEC.optionalFieldOf("attribute_helper").forGetter(i -> Optional.ofNullable(i.helper))
        ).apply(instance, CowConverter::new));

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public CowConverter(Optional<ConvertiblesReloadListener.EntityEntry.Attributes> helper) {
            super(helper);
        }

        public CowConverter() {
        }

        @Override
        public IConvertingHandler<?> createHandler() {
            return new ConvertingHandler(new VampirismEntityRegistry.DatapackHelper(this.helper));
        }

        @Override
        public Codec<? extends Converter> codec() {
            return ModEntities.COW_CONVERTER.get();
        }
    }
}
