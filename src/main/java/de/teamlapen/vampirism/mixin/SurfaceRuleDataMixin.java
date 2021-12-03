package de.teamlapen.vampirism.mixin;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.core.ModBiomes;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SurfaceRuleData.class)
public abstract class SurfaceRuleDataMixin {

    @ModifyVariable(method = "overworldLike(ZZZ)Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;", at = @At("STORE"), ordinal = 0)
    private static ImmutableList.Builder<SurfaceRules.RuleSource> addVampirismOverworldSurfaceRules(ImmutableList.Builder<SurfaceRules.RuleSource> builder){
        ModBiomes.registerOverworldSurfaceRules(builder);
        return builder;
    }
}
