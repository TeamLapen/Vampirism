package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.items.oil.IArmorOil;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OilUtils {

    @Nonnull
    public static IOil getOil(@Nonnull ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        IOil oil = null;
        if (tag != null && tag.contains("oil")) {
            String oilStr = stack.getTag().getString("oil");
            oil = ModRegistries.OILS.getValue(new ResourceLocation(oilStr));
        }
        return oil != null ? oil : ModOils.EMPTY.get();
    }

    public static ItemStack setOil(@Nonnull ItemStack stack, @Nonnull IOil oil) {
        stack.getOrCreateTag().putString("oil", oil.getRegistryName().toString());
        return stack;
    }

    public static ItemStack createOilItem(IOil oil) {
        return ModItems.OIL_BOTTLE.get().withOil(oil);
    }

    public static Collection<IArmorOil> getEquippedArmorOils(PlayerEntity player) {
        return StreamSupport.stream(player.getArmorSlots().spliterator(), false).map(OilUtils::getAppliedOil).filter(o -> o.isPresent() && o.get() instanceof IArmorOil).map(iApplicableOil -> (IArmorOil)iApplicableOil.get()).collect(Collectors.toList());
    }

    public static Optional<IApplicableOil> getAppliedOil(@Nonnull ItemStack stack) {
        return getAppliedOilStatus(stack).map(Pair::getLeft);
    }

    public static boolean reduceAppliedOilDuration(ItemStack stack) {
        return OilUtils.getAppliedOil(stack).map(oil -> reduceAppliedOilDuration(stack, oil, oil.getDurationReduction())).orElse(true);
    }

    public static boolean reduceAppliedOilDuration(@Nonnull ItemStack stack, IApplicableOil oil, int durationReduction) {
        if(stack.hasTag()) {
            CompoundNBT compound = stack.getOrCreateTag().getCompound("applied_oil");
            if (compound.getString("oil").equals(oil.getRegistryName().toString())) {
                int duration = compound.getInt("duration") - durationReduction;
                if (duration <= 0) {
                    removeAppliedOil(stack);
                    return true;
                } else {
                    compound.putInt("duration", duration);
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasAppliedOil(@Nonnull ItemStack stack) {
        return stack.hasTag() && stack.getOrCreateTag().getCompound("applied_oil").contains("oil");
    }

    public static Optional<Pair<IApplicableOil, Integer>> getAppliedOilStatus(@Nonnull ItemStack stack) {
        if (!stack.hasTag()) {
            return Optional.empty();
        }
        CompoundNBT compound = stack.getOrCreateTag().getCompound("applied_oil");
        if (compound.contains("oil")) {
            IOil oil = ModRegistries.OILS.getValue(new ResourceLocation(compound.getString("oil")));
            int duration = compound.getInt("duration");
            if (oil instanceof IApplicableOil && duration > 0) {
                return Optional.of(Pair.of(((IApplicableOil) oil), duration));
            } else {
                removeAppliedOil(stack);
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public static ItemStack removeAppliedOil(@Nonnull ItemStack stack) {
        if (stack.hasTag()) {
            stack.getOrCreateTag().remove("applied_oil");
        }
        return stack;
    }


    public static ItemStack setAppliedOil(@Nonnull ItemStack stack, IApplicableOil oil) {
        return setAppliedOil(stack, oil, oil.getMaxDuration(stack));
    }

    public static ItemStack setAppliedOil(@Nonnull ItemStack stack, IApplicableOil oil, int duration) {
        if (duration <= 0) {
            return removeAppliedOil(stack);
        }
        CompoundNBT compound = new CompoundNBT();
        compound.putString("oil", oil.getRegistryName().toString());
        compound.putInt("duration", duration);
        stack.getOrCreateTag().put("applied_oil", compound);
        return stack;
    }
}
