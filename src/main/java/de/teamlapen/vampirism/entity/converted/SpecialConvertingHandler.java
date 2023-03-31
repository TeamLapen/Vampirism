package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SpecialConvertingHandler<T extends PathfinderMob, Z extends PathfinderMob & IConvertedCreature<T>> extends DefaultConvertingHandler<T> {

    private final Supplier<EntityType<Z>> convertedType;

    public SpecialConvertingHandler(Supplier<EntityType<Z>> convertedType) {
        super(null);
        this.convertedType = convertedType;
    }

    @Nullable
    @Override
    public IConvertedCreature<T> createFrom(@NotNull T entity) {
        return Helper.createEntity(this.convertedType.get(), entity.getCommandSenderWorld()).map(convertedCreature -> {
            copyImportantStuff(convertedCreature, entity);
            convertedCreature.setUUID(Mth.createInsecureUUID(convertedCreature.getRandom()));
            convertedCreature.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
            return convertedCreature;
        }).orElse(null);
    }

    protected void copyImportantStuff(@NotNull Z converted, @NotNull T entity) {
        CompoundTag nbt = new CompoundTag();
        entity.saveWithoutId(nbt);
        converted.yBodyRot = entity.yBodyRot;
        converted.yHeadRot = entity.yHeadRot;
        converted.load(nbt);
        converted.setHealth(converted.getMaxHealth() / 3 * 2);
    }
}
