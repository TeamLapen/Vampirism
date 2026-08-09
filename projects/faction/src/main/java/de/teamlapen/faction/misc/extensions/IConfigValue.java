package de.teamlapen.faction.misc.extensions;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface IConfigValue {

    ModConfigSpec factionapi$spec();
}
