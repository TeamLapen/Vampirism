package de.teamlapen.vampirism.entity.minion.management;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;


public class MinionTask implements INBTSerializable<CompoundNBT> {

    private static final Map<ResourceLocation, Supplier<? extends MinionTask>> constructors = new Object2ObjectArrayMap<>();
    private static boolean init = false;

    /**
     * Used for static initializers
     */
    public static void init() {
        if (init) return;
        constructors.put(Type.DEFEND_AREA.ID, DefendArea::new);
        constructors.put(Type.STAY.ID, () -> new MinionTask(Type.STAY));
        constructors.put(Type.FOLLOW.ID, () -> new MinionTask(Type.FOLLOW));
        init = true;
    }

    @Nullable
    public static MinionTask createFromNBT(CompoundNBT nbt) {
        ResourceLocation id = new ResourceLocation(nbt.getString("id"));
        assert constructors.size() > 0 : "Minion task init has not been called";
        if (constructors.containsKey(id)) {
            MinionTask t = constructors.get(id).get();
            t.fromNBT(nbt);
            return t;
        }
        return null;
    }

    public final Type type;

    public MinionTask(Type type) {
        this.type = type;
    }

    @Override
    public final void deserializeNBT(CompoundNBT nbt) {
        fromNBT(nbt);
    }

    @Override
    public final CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("id", type.ID.toString());
        toNBT(nbt);
        return nbt;
    }

    protected void fromNBT(CompoundNBT tag) {

    }

    protected void toNBT(CompoundNBT tag) {

    }

    public static ITextComponent getNameOfTask(Type t) {
        return new TranslationTextComponent("minioncommand." + t.ID.getNamespace() + "." + t.ID.getPath());
    }

    public enum Type {
        FOLLOW(new ResourceLocation("vampirism", "follow")), STAY(new ResourceLocation("vampirism", "stay")), DEFEND_AREA(new ResourceLocation("vampirism", "defend_area"));
        ResourceLocation ID;

        Type(ResourceLocation id) {
            this.ID = id;
        }
    }

    public static class DefendArea extends MinionTask {
        @Nullable
        private BlockPos center;
        private int radius;

        private DefendArea() {
            super(Type.DEFEND_AREA);
        }

        public DefendArea(int radius) {
            super(Type.DEFEND_AREA);
            this.radius = radius;
        }

        @Nullable
        public BlockPos getCenter() {
            return center;
        }

        public void setCenter(@Nullable BlockPos center) {
            this.center = center;
        }

        public int getRadius() {
            return radius;
        }

        @Override
        protected void fromNBT(CompoundNBT tag) {
            tag.putInt("radius", this.radius);
            if (center != null) {
                NBTUtil.writeBlockPos(this.center);
            }
        }

        @Override
        protected void toNBT(CompoundNBT tag) {
            this.radius = tag.getInt("radius");
            if (tag.contains("x")) {
                this.center = NBTUtil.readBlockPos(tag);
            } else {
                this.center = null;
            }
        }
    }


}
