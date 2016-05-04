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

    private ArrayList<IVampireVision> visionList = new ArrayList<>();

    /**
     * Return the id of the given vision.
     * -1 if not registered
     *
     * @param vision
     * @return
     */
    public int getIdOfVision(IVampireVision vision) {
        return visionList.indexOf(vision);
    }

    /**
     * Return the vision belonging to the given id.
     * Null if not found
     *
     * @param id
     * @return
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
