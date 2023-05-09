package de.teamlapen.vampirism.entity.converted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.data.reloadlistener.ConvertiblesReloadListener;
import de.teamlapen.vampirism.entity.converted.converter.DefaultConverter;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * {@link IConvertedCreature} for sheep
 * Allows converted sheep to be sheared
 */
public class ConvertedSheepEntity extends ConvertedCreatureEntity<Sheep> implements net.minecraftforge.common.IForgeShearable {
    private final static EntityDataAccessor<Byte> COAT = SynchedEntityData.defineId(ConvertedSheepEntity.class, EntityDataSerializers.BYTE);

    private @Nullable Boolean lastSheared = null;

    public ConvertedSheepEntity(EntityType<? extends ConvertedSheepEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        boolean t = getSheared();
        if (!nil() && (lastSheared == null || lastSheared != t)) {
            lastSheared = t;
            getOldCreature().setSheared(lastSheared);

        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Sheared", this.getSheared());
    }

    public @NotNull DyeColor getFleeceColor() {
        return nil() ? DyeColor.WHITE : this.getOldCreature().getColor();
    }

    public boolean getSheared() {
        return (this.entityData.get(COAT) & 16) != 0;
    }

    public void setSheared(boolean sheared) {
        byte b0 = this.entityData.get(COAT);

        if (sheared) {
            this.entityData.set(COAT, (byte) (b0 | 16));
        } else {
            this.entityData.set(COAT, (byte) (b0 & -17));
        }
    }

    @Override
    public boolean isShearable(@NotNull ItemStack item, Level world, BlockPos pos) {
        return !getSheared() && !isBaby();
    }

    @NotNull
    @Override
    public List<ItemStack> onSheared(@Nullable Player player, @NotNull ItemStack item, @NotNull Level world, BlockPos pos, int fortune) {
        java.util.List<ItemStack> ret = new java.util.ArrayList<>();
        if (!world.isClientSide()) {
            this.setSheared(true);
            int i = 1 + this.random.nextInt(3);

            for (int j = 0; j < i; ++j)
                ret.add(new ItemStack(Sheep.ITEM_BY_DYE.get(this.getFleeceColor())));

            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
        }
        return ret;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(COAT, (byte) 0);
    }

    public static class ConvertingHandler extends DefaultConvertingHandler<Sheep> {
        public ConvertingHandler() {
            super(null);
        }

        public ConvertingHandler(IDefaultHelper helper) {
            super(helper);
        }

        @Override
        public ConvertedCreatureEntity<Sheep> createFrom(@NotNull Sheep entity) {
            return Helper.createEntity(ModEntities.CONVERTED_SHEEP.get(), entity.getCommandSenderWorld()).map(creature -> {
                this.copyImportantStuff(creature, entity);
                creature.setSheared(entity.isSheared());
                return creature;
            }).orElse(null);
        }
    }

    public static class SheepConverter extends DefaultConverter {

        public static final Codec<SheepConverter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ConvertiblesReloadListener.EntityEntry.Attributes.CODEC.optionalFieldOf("attribute_helper", ConvertiblesReloadListener.EntityEntry.Attributes.DEFAULT).forGetter(i -> i.helper)
        ).apply(instance, SheepConverter::new));

        public SheepConverter(ConvertiblesReloadListener.EntityEntry.Attributes helper) {
            super(helper);
        }

        public SheepConverter() {
        }

        @Override
        public IConvertingHandler<?> createHandler() {
            return new ConvertingHandler(new VampirismEntityRegistry.DatapackHelper(this.helper));
        }

        @Override
        public Codec<? extends Converter> codec() {
            return ModEntities.SHEEP_CONVERTER.get();
        }
    }

}
