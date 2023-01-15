package de.teamlapen.vampirism.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Implementation for several small registries.
 * (Currently only vampire vision)
 */
public class VampireVisionRegistry implements IVampireVisionRegistry {

    private final static String LEGACY_NAMESPACE = "vampirismlegacy";
    private final ArrayList<@NotNull IVampireVision> visionList = new ArrayList<>();
    private final BiMap<@NotNull ResourceLocation, @NotNull IVampireVision> visionMap = HashBiMap.create();

    @Deprecated(forRemoval = true)
    @Override
    public int getIdOfVision(IVampireVision vision) {
        return visionList.indexOf(vision);
    }

    @Deprecated(forRemoval = true)
    @Override
    public
    @Nullable
    IVampireVision getVisionOfId(int id) {
        return visionList.get(id);
    }

    @Deprecated(forRemoval = true)
    @Override
    public ResourceLocation getIdForId(int id) {
        return this.visionMap.inverse().get(this.visionList.get(id));
    }

    @Override
    public @NotNull ResourceLocation getVisionId(IVampireVision vision) {
        ResourceLocation location = this.visionMap.inverse().get(vision);
        if (location == null) {
            throw new IllegalArgumentException("The given vision is not registered");
        }
        return location;
    }

    @Override
    public @Nullable IVampireVision getVision(ResourceLocation id) {
        return this.visionMap.get(id);
    }

    @Override
    public @NotNull List<IVampireVision> getVisions() {
        return ImmutableList.copyOf(visionList);
    }

    @Override
    public <T extends IVampireVision> T registerVision(String key, T vision) {
        return this.registerVision(new ResourceLocation(LEGACY_NAMESPACE, key.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_")), vision);
    }

    @Override
    public <T extends IVampireVision> T registerVision(@NotNull ResourceLocation key, @NotNull T vision) {
        if (this.visionMap.containsKey(key)) {
            throw new IllegalArgumentException("The given key is already registered");
        }
        this.visionList.add(vision);
        this.visionMap.put(key, vision);
        return vision;
    }
}
