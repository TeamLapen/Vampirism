package de.teamlapen.faction.misc.mixin;

import de.teamlapen.faction.misc.extensions.IConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModConfigSpec.ConfigValue.class)
public interface ConfigValueMixin extends IConfigValue {

    @Accessor("spec")
    @Override
    ModConfigSpec factionapi$spec();

}
