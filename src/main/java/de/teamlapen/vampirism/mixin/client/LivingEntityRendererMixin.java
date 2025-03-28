package de.teamlapen.vampirism.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.client.renderer.entity.ConvertedCreatureRenderer;
import de.teamlapen.vampirism.client.renderer.entity.state.IVampirismRenderState;
import de.teamlapen.vampirism.core.ModAttachments;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
    private void applyConvertedRenderState(T entity, S state, float p_361157_, CallbackInfo ci) {
        IVampirismRenderState renderState = (IVampirismRenderState) state;
        if (ConvertedCreatureRenderer.renderOverlay) {
            Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())).map(ResourceLocation::toString).map(s -> VampirismAPI.entityRegistry().getConvertibleOverlay(s)).ifPresent(location -> {
                renderState.vampirism$overlay(location);
            });
        }
        if (entity instanceof IConvertedCreature<?> creature) {
            Optional.ofNullable(creature.getSourceEntityId()).map(s -> VampirismAPI.entityRegistry().getConvertibleOverlay(s)).ifPresent(location -> {
                renderState.vampirism$convertedOverlay(location);
            });
        }
        if (entity instanceof Player player) {
            renderState.vampirism$attributes(VampirismPlayerAttributes.get(player));
            Bat bat = player.getData(ModAttachments.VAMPIRE_BAT.get());
            bat.yHeadRot = player.yHeadRot;
            bat.yBodyRot = player.yBodyRot;
            bat.yHeadRotO = player.yHeadRotO;
            bat.yBodyRotO = player.yBodyRotO;
            renderState.vampirism$bat(bat);
        }
        ExtendedCreature.getSafe(entity).ifPresent(creature -> {
            renderState.vampirism$blood(creature.getBlood());
            renderState.vampirism$poisonousBlood(creature.hasPoisonousBlood());
        });
        renderState.vampirism$hunter(entity instanceof IHunterMob);
        renderState.sleeping$inCoffin(entity.getSleepingPos().map(s -> entity.level().getBlockState(s)).filter(s -> s.getBlock() instanceof CoffinBlock).isPresent());
    }

    @WrapOperation(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;shouldRenderLayers(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)Z"))
    private boolean skipLayersInBloodVision(LivingEntityRenderer<T, S, M> instance, S state, Operation<Boolean> original) {
        if (VampirismModClient.getINSTANCE().getBloodVisionRenderer().isInBloodVisionRendering()) {
            return false;
        } else {
            return original.call(instance, state);
        }
    }
}
