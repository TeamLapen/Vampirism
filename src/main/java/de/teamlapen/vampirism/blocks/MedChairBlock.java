package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Block which represents the top and the bottom part of a "Medical Chair" used for injections
 */
public class MedChairBlock extends VampirismSplitBlock {

    private static final @NotNull VoxelShape SHAPE_TOP = box(2, 6, 0, 14, 16, 16);
    private static final @NotNull VoxelShape SHAPE_BOTTOM = box(1, 1, 0, 15, 10, 16);


    public MedChairBlock() {
        super(Properties.of(Material.METAL).strength(1).noOcclusion(), SHAPE_BOTTOM, SHAPE_TOP, false);
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (player.isAlive()) {
            ItemStack stack = player.getItemInHand(hand);
            if (handleInjections(player, world, stack)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.getInventory().removeItem(stack);
                }
            }
        } else if (world.isClientSide) {
            player.displayClientMessage(Component.translatable("text.vampirism.need_item_to_use", Component.translatable((new ItemStack(ModItems.INJECTION_GARLIC.get()).getDescriptionId()))), true);
        }
        return InteractionResult.SUCCESS;
    }

    private boolean handleGarlicInjection(@NotNull Player player, @NotNull Level world, @NotNull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (handler.canJoin(VReference.HUNTER_FACTION)) {
            if (world.isClientSide) {
                VampirismMod.proxy.renderScreenFullColor(4, 30, 0xBBBBBBFF);
            } else {
                handler.joinFaction(VReference.HUNTER_FACTION);
                player.addEffect(new MobEffectInstance(ModEffects.POISON.get(), 200, 1));
            }
            return true;
        } else if (currentFaction != null) {
            if (!world.isClientSide) {
                player.sendSystemMessage(Component.translatable("text.vampirism.med_chair_other_faction", currentFaction.getName()));
            }
        }
        return false;
    }

    private boolean handleInjections(@NotNull Player player, @NotNull Level world, @NotNull ItemStack stack) {
        return FactionPlayerHandler.getOpt(player).map(handler -> {
            IPlayableFaction<?> faction = handler.getCurrentFaction();
            if (stack.getItem().equals(ModItems.INJECTION_GARLIC.get())) {
                return handleGarlicInjection(player, world, handler, faction);
            }
            if (stack.getItem().equals(ModItems.INJECTION_SANGUINARE.get())) {
                return handleSanguinareInjection(player, handler, faction);
            }
            return false;
        }).orElse(false);

    }

    private boolean handleSanguinareInjection(@NotNull Player player, @NotNull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (VReference.VAMPIRE_FACTION.equals(currentFaction)) {
            player.displayClientMessage(Component.translatable("text.vampirism.already_vampire"), false);
            return false;
        }
        if (VReference.HUNTER_FACTION.equals(currentFaction)) {
            if (player.level.isClientSide()) {
                VampirismMod.proxy.displayRevertBackScreen();
            }
            return true;
        }
        if (currentFaction == null) {
            if (handler.canJoin(VReference.VAMPIRE_FACTION)) {
                if (VampirismConfig.SERVER.disableFangInfection.get()) {
                    player.displayClientMessage(Component.translatable("text.vampirism.deactivated_by_serveradmin"), true);
                } else {
                    SanguinareEffect.addRandom(player, true);
                    player.addEffect(new MobEffectInstance(ModEffects.POISON.get(), 60));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }
}
