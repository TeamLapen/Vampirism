package de.teamlapen.vampirism.common.entity.player.vampire;

import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

public class ModVampireVisions {

    public static final Pair<ResourceLocation, IVampireVision> NIGHT_VISION = Pair.of(VResourceLocation.mod("night_vision"), new NightVision());
    public static final Pair<ResourceLocation, IVampireVision> BLOOD_VISION = Pair.of(VResourceLocation.mod("blood_vision"), new BloodVision());

    public static void registerVisions(IVampireVisionRegistry registry) {
        registry.registerVision(NIGHT_VISION.getLeft(), NIGHT_VISION.getRight());
        registry.registerVision(BLOOD_VISION.getLeft(), BLOOD_VISION.getRight());
    }
}
