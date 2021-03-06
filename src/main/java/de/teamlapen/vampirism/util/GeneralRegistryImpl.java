package de.teamlapen.vampirism.util;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for several small registries.
 * (Currently only vampire vision)
 */
public class GeneralRegistryImpl implements IVampireVisionRegistry {

    private final ArrayList<IVampireVision> visionList = new ArrayList<>();

    /**
     * @param vision
     * @return Return the id of the given vision, -1 if not registered
     */
    public int getIdOfVision(IVampireVision vision) {
        return visionList.indexOf(vision);
    }

    /**
     * @param id
     * @return the vision belonging to the given id. Null if not found
     */
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
