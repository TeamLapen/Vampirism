package de.teamlapen.vampirism.util;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for several small registries.
 * (Currently only vampire vision)
 */
public class GeneralRegistryImpl implements IVampireVisionRegistry {

    private final ArrayList<IVampireVision> visionList = new ArrayList<>();

    @Override
    public int getIdOfVision(IVampireVision vision) {
        return visionList.indexOf(vision);
    }

    @Override
    public
    @Nullable
    IVampireVision getVisionOfId(int id) {
        return visionList.get(id);
    }

    @Override
    public List<IVampireVision> getVisions() {
        return ImmutableList.copyOf(visionList);
    }

    @Override
    public <T extends IVampireVision> T registerVision(String key, T vision) {
        visionList.add(vision);
        return vision;
    }
}
