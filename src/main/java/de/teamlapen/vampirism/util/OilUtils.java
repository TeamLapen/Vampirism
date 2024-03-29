package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.items.oil.IArmorOil;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModOils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OilUtils {

    @NotNull
    public static IOil getOil(@NotNull ItemStack stack) {
        CompoundTag tag = stack.getTag();
        IOil oil = null;
        if (tag != null && tag.contains("oil")) {
            String oilStr = stack.getTag().getString("oil");
            oil = RegUtil.getOil(new ResourceLocation(oilStr));
        }
        return oil != null ? oil : ModOils.EMPTY.get();
    }

    public static @NotNull ItemStack setOil(@NotNull ItemStack stack, @NotNull IOil oil) {
        stack.getOrCreateTag().putString("oil", RegUtil.id(oil).toString());
        return stack;
    }

    public static ItemStack createOilItem(IOil oil) {
        return ModItems.OIL_BOTTLE.get().withOil(oil);
    }

    public static Collection<IArmorOil> getEquippedArmorOils(Player player) {
        return StreamSupport.stream(player.getArmorSlots().spliterator(), false).map(OilUtils::getAppliedOil).filter(o -> o.isPresent() && o.get() instanceof IArmorOil).map(iApplicableOil -> (IArmorOil)iApplicableOil.get()).collect(Collectors.toList());
    }

    public static @NotNull Optional<IApplicableOil> getAppliedOil(@NotNull ItemStack stack) {
        return getAppliedOilStatus(stack).map(Pair::getLeft);
    }

    public static boolean reduceAppliedOilDuration(@NotNull ItemStack stack) {
        return OilUtils.getAppliedOil(stack).map(oil -> reduceAppliedOilDuration(stack, oil, oil.getDurationReduction())).orElse(true);
    }

    public static boolean reduceAppliedOilDuration(@NotNull ItemStack stack, IApplicableOil oil, int durationReduction) {
        if (stack.hasTag()) {
            CompoundTag compound = stack.getTag().getCompound("applied_oil");
            if (compound.getString("oil").equals(RegUtil.id(oil).toString())) {
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

    public static boolean hasAppliedOil(@NotNull ItemStack stack) {
        return stack.hasTag() && stack.getTag().getCompound("applied_oil").contains("oil");
    }

    public static @NotNull Optional<Pair<IApplicableOil, Integer>> getAppliedOilStatus(@NotNull ItemStack stack) {
        if (!stack.hasTag()) {
            return Optional.empty();
        }
        CompoundTag compound = stack.getTag().getCompound("applied_oil");
        if (compound.contains("oil")) {
            IOil oil = RegUtil.getOil(new ResourceLocation(compound.getString("oil")));
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

    public static @NotNull ItemStack removeAppliedOil(@NotNull ItemStack stack) {
        if (stack.hasTag()) {
            stack.getTag().remove("applied_oil");
        }
        return stack;
    }


    public static @NotNull ItemStack setAppliedOil(@NotNull ItemStack stack, @NotNull IApplicableOil oil) {
        return setAppliedOil(stack, oil, oil.getMaxDuration(stack));
    }

    public static @NotNull ItemStack setAppliedOil(@NotNull ItemStack stack, IApplicableOil oil, int duration) {
        if (duration <= 0) {
            return removeAppliedOil(stack);
        }
        CompoundTag compound = new CompoundTag();
        compound.putString("oil", RegUtil.id(oil).toString());
        compound.putInt("duration", duration);
        stack.getOrCreateTag().put("applied_oil", compound);
        return stack;
    }
}
