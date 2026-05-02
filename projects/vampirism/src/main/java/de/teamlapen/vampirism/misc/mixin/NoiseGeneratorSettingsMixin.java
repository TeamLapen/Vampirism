package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.world.biomes.OverworldModifications;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin {

    @Inject(method = "surfaceRule", at = @At("RETURN"), cancellable = true)
    private void addVampirismOverworldSurfaceRules(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (!VampirismMod.integrations().isUsingTerraBlender()) { //When TerraBlender is installed, it adds the surface rules appropriately. This is likely called a few times before terrablender is activated, but that should not be an issue
            cir.setReturnValue(SurfaceRules.sequence(OverworldModifications.buildOverworldSurfaceRules(), cir.getReturnValue()));
        }
    }
}
