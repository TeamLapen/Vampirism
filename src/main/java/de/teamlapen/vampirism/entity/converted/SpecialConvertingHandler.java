package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public class SpecialConvertingHandler<T extends CreatureEntity, Z extends CreatureEntity & IConvertedCreature<T>> extends DefaultConvertingHandler<T> {

    private final EntityType<Z> convertedType;

    public SpecialConvertingHandler(EntityType<Z> convertedType) {
        super(null);
        this.convertedType = convertedType;
    }

    @Nullable
    @Override
    public IConvertedCreature<T> createFrom(T entity) {
        return Helper.createEntity(this.convertedType, entity.getCommandSenderWorld()).map(convertedCreature -> {
            copyImportantStuff(convertedCreature, entity);
            convertedCreature.setUUID(MathHelper.createInsecureUUID(convertedCreature.getRandom()));
            convertedCreature.addEffect(new EffectInstance(Effects.WEAKNESS, 200, 2));
            return convertedCreature;
        }).orElse(null);
    }

    protected void copyImportantStuff(Z converted, T entity) {
        CompoundNBT nbt = new CompoundNBT();
        entity.saveWithoutId(nbt);
        converted.yBodyRot = entity.yBodyRot;
        converted.yHeadRot = entity.yHeadRot;
        converted.load(nbt);
        converted.setHealth(converted.getMaxHealth() / 3 * 2);
    }
}
