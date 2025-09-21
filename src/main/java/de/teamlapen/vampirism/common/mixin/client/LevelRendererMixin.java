package de.teamlapen.vampirism.common.mixin.client;


import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

//    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z"))
//    private boolean vampireGlowing(Minecraft instance, Entity pEntity, Operation<Boolean> original, @Share("renderVampireColor") LocalBooleanRef color) {
//        if (Helper.isHunter(instance.player) && pEntity.distanceToSqr(instance.player) < 256 && Helper.appearsAsVampire(instance.player, pEntity) && HunterPlayer.get(instance.player).getActionHandler().isActionActive(HunterActions.AWARENESS_HUNTER)) {
//            if (instance.player.hasLineOfSight(pEntity)) {
//                color.set(true);
//                return true;
//            }
//        }
//        color.set(false);
//        return original.call(instance, pEntity);
//    }

//    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"))
//    private int color(Entity instance, Operation<Integer> original, @Share("renderVampireColor") LocalBooleanRef color) {
//        if (color.get()) {
//            return Color.RED.getRGB();
//        }
//        return original.call(instance);
//    }
}
