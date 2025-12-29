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
import de.teamlapen.vampirism.common.util.Helper;
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
        if (ConvertedCreatureRenderer.renderOverlay) {
            Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())).map(Identifier::toString).map(s -> VampirismApi.services().entityRegistry().getConvertibleOverlay(s)).ifPresent(location -> {
                reusedState.setRenderData(IVampirismRenderState.OVERLAY, location);
            });
        }
        if (entity instanceof IConvertedCreature<?> creature) {
            Optional.ofNullable(creature.getSourceEntityId()).map(s -> VampirismApi.services().entityRegistry().getConvertibleOverlay(s)).ifPresent(location -> {
                reusedState.setRenderData(IVampirismRenderState.CONVERTED_OVERLAY, location);
            });
        }
        if (entity instanceof Player player) {
            if (reusedState instanceof IVampirePlayerState vampireState) {
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

            if (reusedState instanceof IHunterPlayerState hunterState) {
                var hunter = HunterPlayer.get(player);
                hunterState.vampirism$hunter$setDisguised(hunter.isDisguised());
                hunterState.vampirism$hunter$setFullHunterCoat(hunter.getSpecialAttributes().fullHunterCoat == IItemWithTier.Tier.ENHANCED || hunter.getSpecialAttributes().fullHunterCoat == IItemWithTier.Tier.ULTIMATE);
            }
        }
    }
}
