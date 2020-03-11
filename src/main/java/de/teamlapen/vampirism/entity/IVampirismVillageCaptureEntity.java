package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.world.IVillageAttributes;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IVampirismVillageCaptureEntity extends IVillageCaptureEntity {

    @Nonnull
    default LazyOptional<IVillageAttributes> getAttributes(@Nullable BlockPos totemPos, @Nonnull LazyOptional<LazyOptional<IVillageAttributes>> villageAttributes, Dimension dim){
        if(totemPos == null) return LazyOptional.empty();
        if(!villageAttributes.isPresent()){
            setTotemPos(null);
            return LazyOptional.empty();
        }
        return villageAttributes.orElseGet(() -> {
            LazyOptional<LazyOptional<IVillageAttributes>> attributes = TotemTileEntity.getVillageOpt(dim, totemPos);
            setVillageAttributes(attributes);
            return attributes.orElse(LazyOptional.empty());
        });
    }

    void setVillageAttributes(@Nonnull LazyOptional<LazyOptional<IVillageAttributes>> opt);
}
