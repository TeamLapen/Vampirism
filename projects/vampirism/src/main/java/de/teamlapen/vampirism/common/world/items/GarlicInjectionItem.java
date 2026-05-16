package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GarlicInjectionItem extends InjectionItem {

    public GarlicInjectionItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean handleInjection(Level level, BlockPos pos, Player player, IFactionPlayerHandler handler, @Nullable Holder<? extends IPlayableFaction<?>> currentFaction) {
        if (handler.canJoin(ModFactions.HUNTER)) {
            if (level.isClientSide()) {
                VampirismModClient.services().fullScreenOverlay().start(level, 4, 30, 0xBBBBBBFF);
            } else {
                handler.joinFaction(ModFactions.HUNTER);
                player.addEffect(new MobEffectInstance(ModEffects.TOXICANT, 200, 1));
            }
            return true;
        } else if (currentFaction != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.vampirism.injection_chair.already_non_hunter", currentFaction.value().getName()));
            }
        }
        return false;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        return ExtendedCreature.getSafe(interactionTarget).map(target -> {
            if (!player.level().isClientSide()) {
                target.setPoisonousBlood(ExtendedCreature.POISONOUS_BLOOD_DOSE_DURATION);

                if (!player.isCreative()) {
                    player.setItemInHand(usedHand, Helper.shrinkItemStack(stack, player));
                }

                // TODO: Find some other sound for vaccinating mobs
                player.level().playSound(null, player.blockPosition(), ModSounds.VAMPIRE_BITE.get(), SoundSource.PLAYERS, 1.0f,  1.0f);
            }
            
            return (InteractionResult) InteractionResult.SUCCESS;
        }).orElse(InteractionResult.PASS);
    }
}
