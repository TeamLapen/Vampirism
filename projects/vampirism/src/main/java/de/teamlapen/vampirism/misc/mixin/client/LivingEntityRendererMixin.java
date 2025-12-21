package de.teamlapen.vampirism.misc.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.api.world.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.world.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.client.renderer.entities.ConvertedCreatureRenderer;
import de.teamlapen.vampirism.client.renderer.entities.state.IVampirismRenderState;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.misc.extension.client.IHunterPlayerState;
import de.teamlapen.vampirism.misc.extension.client.IVampirePlayerState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("RETURN"))
    private void applyConvertedRenderState(T entity, S reusedState, float partialTick, CallbackInfo ci) {
        IVampirismRenderState renderState = (IVampirismRenderState) reusedState;
        if (ConvertedCreatureRenderer.renderOverlay) {
            Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())).map(Identifier::toString).map(s -> VampirismApi.services().entityRegistry().getConvertibleOverlay(s)).ifPresent(location -> {
                renderState.vampirism$overlay(location);
            });
        }
        if (entity instanceof IConvertedCreature<?> creature) {
            Optional.ofNullable(creature.getSourceEntityId()).map(s -> VampirismApi.services().entityRegistry().getConvertibleOverlay(s)).ifPresent(location -> {
                renderState.vampirism$convertedOverlay(location);
            });
        }
        if (entity instanceof Player player) {
            if (renderState instanceof IVampirePlayerState vampireState) {
                var vampire = VampirePlayer.get(player);
                vampireState.vampirism$vampire$setDisguised(vampire.isDisguised());
                vampireState.vampirism$vampire$setVampireLevel(vampire.getLevel());
                vampireState.vampirism$vampire$setEyeType(vampire.getEyeType());
                vampireState.vampirism$vampire$setFangType(vampire.getFangType());
                vampireState.vampirism$vampire$setGlowingEyes(vampire.getGlowingEyes());
                vampireState.vampirism$vampire$setInvisible(vampire.getSkillProperties().invisible);

                if (vampire.getSkillProperties().bat) {
                    Bat bat = player.getData(ModAttachments.VAMPIRE_BAT.get());
                    bat.yHeadRot = player.yHeadRot;
                    bat.yBodyRot = player.yBodyRot;
                    bat.yHeadRotO = player.yHeadRotO;
                    bat.yBodyRotO = player.yBodyRotO;
                    vampireState.vampirism$vampire$setBat(bat);
                }
            }

            if (renderState instanceof IHunterPlayerState hunterState) {
                var hunter = HunterPlayer.get(player);
                hunterState.vampirism$hunter$setDisguised(hunter.isDisguised());
                hunterState.vampirism$hunter$setFullHunterCoat(hunter.getSpecialAttributes().fullHunterCoat == IItemWithTier.Tier.ENHANCED || hunter.getSpecialAttributes().fullHunterCoat == IItemWithTier.Tier.ULTIMATE);
            }
        }
        ExtendedCreature.getSafe(entity).ifPresent(creature -> {
            renderState.vampirism$blood(creature.getBlood());
            renderState.vampirism$poisonousBlood(creature.hasPoisonousBlood());
        });
        renderState.vampirism$hunter(entity instanceof IHunterMob);
    }

    @WrapOperation(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;shouldRenderLayers(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)Z"))
    private boolean skipLayersInBloodVision(LivingEntityRenderer<T, S, M> instance, S state, Operation<Boolean> original) {
        if (VampirismModClient.services().bloodVisionRenderer().isInBloodVisionRendering()) {
            return false;
        } else {
            return original.call(instance, state);
        }
    }
}
