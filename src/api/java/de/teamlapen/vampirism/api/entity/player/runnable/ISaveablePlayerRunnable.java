package de.teamlapen.vampirism.api.entity.player.runnable;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface ISaveablePlayerRunnable<T extends IFactionPlayer<T>> extends IPlayerRunnable<T> {

    Map<ResourceLocation, Function<CompoundTag, ISaveablePlayerRunnable<?>>> CONSTRUCTORS = new HashMap<>();

    /**
     * saves the state of the runnable to a {@link net.minecraft.nbt.CompoundTag}
     */
    CompoundTag writeNBT();

    /**
     * @return the unique id of this runnable
     */
    ResourceLocation getID();
}
