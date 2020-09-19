package de.teamlapen.vampirism.modcompat;

import de.teamlapen.lib.lib.util.IModCompat;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;


public class SpongeModCompat implements IModCompat {
    public static final String MODID = "sponge";

    @Override
    public String getModID() {
        return MODID;
    }

    @Override
    public void onInitStep(Step step, ParallelDispatchEvent event) {

    }
}
