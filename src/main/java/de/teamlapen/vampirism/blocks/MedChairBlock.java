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
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Block which represents the top and the bottom part of a "Medical Chair" used for injections
 */
public class MedChairBlock extends VampirismSplitBlock {

    private static final VoxelShape SHAPE_TOP = box(2, 6, 0, 14, 16, 16);
    private static final VoxelShape SHAPE_BOTTOM = box(1, 1, 0, 15, 10, 16);


    public MedChairBlock() {
        super(Properties.of(Material.METAL).strength(1).noOcclusion(), SHAPE_BOTTOM, SHAPE_TOP, false);
    }

    @Override
    @Nonnull
    public ActionResultType use(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        if (player.isAlive()) {
            ItemStack stack = player.getItemInHand(hand);
            if (handleInjections(player, world, stack)) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.inventory.removeItem(stack);
                }
            }
        } else if (world.isClientSide) {
            player.displayClientMessage(new TranslationTextComponent("text.vampirism.need_item_to_use", new TranslationTextComponent((new ItemStack(ModItems.INJECTION_GARLIC.get()).getDescriptionId()))), true);
        }
        return ActionResultType.SUCCESS;
    }

    private boolean handleGarlicInjection(@Nonnull PlayerEntity player, @Nonnull World world, @Nonnull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (handler.canJoin(VReference.HUNTER_FACTION)) {
            if (world.isClientSide) {
                VampirismMod.proxy.renderScreenFullColor(4, 30, 0xBBBBBBFF);
            } else {
                handler.joinFaction(VReference.HUNTER_FACTION);
                player.addEffect(new EffectInstance(ModEffects.POISON.get(), 200, 1));
            }
            return true;
        } else if (currentFaction != null) {
            if (!world.isClientSide) {
                player.sendMessage(new TranslationTextComponent("text.vampirism.med_chair_other_faction", currentFaction.getName()), Util.NIL_UUID);
            }
        }
        return false;
    }

    private boolean handleInjections(PlayerEntity player, World world, ItemStack stack) {
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

    private boolean handleSanguinareInjection(@Nonnull PlayerEntity player, @Nonnull IFactionPlayerHandler handler, @Nullable IPlayableFaction<?> currentFaction) {
        if (VReference.VAMPIRE_FACTION.equals(currentFaction)) {
            player.displayClientMessage(new TranslationTextComponent("text.vampirism.already_vampire"), false);
            return false;
        }
        if (VReference.HUNTER_FACTION.equals(currentFaction)) {
            VampirismMod.proxy.displayRevertBackScreen();
            return true;
        }
        if (currentFaction == null) {
            if (handler.canJoin(VReference.VAMPIRE_FACTION)) {
                if (VampirismConfig.SERVER.disableFangInfection.get()) {
                    player.displayClientMessage(new TranslationTextComponent("text.vampirism.deactivated_by_serveradmin"), true);
                } else {
                    SanguinareEffect.addRandom(player, true);
                    player.addEffect(new EffectInstance(ModEffects.POISON.get(), 60));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }
}
