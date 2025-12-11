package de.teamlapen.vampirism.common.entity.player.vampire;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.common.entity.player.vampire.vision.BloodVision;
import de.teamlapen.vampirism.common.entity.player.vampire.vision.NightVision;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class VampirismVampireVisions {

    public static final DeferredRegister<IVampireVision> VAMPIRE_VISIONS = DeferredRegister.create(VampirismRegistries.Keys.VAMPIRE_VISION, REFERENCE.MODID);

    public static final DeferredHolder<IVampireVision, NightVision> NIGHT_VISION = VAMPIRE_VISIONS.register("night_vision", NightVision::new);
    public static final DeferredHolder<IVampireVision, BloodVision> BLOOD_VISION = VAMPIRE_VISIONS.register("blood_vision", BloodVision::new);

    public static void register(IEventBus bus) {
        VAMPIRE_VISIONS.register(bus);
    }
}
