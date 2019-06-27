package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Block which represents the top and the bottom part of a "Medical Chair" used for injections
 */
public class BlockMedChair extends VampirismBlock {
    public static final EnumProperty<EnumPart> PART = EnumProperty.create("part", EnumPart.class);
    private final static String name = "med_chair";
    public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;


    public BlockMedChair() {
        super(name, Properties.create(Material.IRON).hardnessAndResistance(1));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH).with(PART, EnumPart.TOP));

    }

    @Nullable
    @Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }

    @Override
    public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem().equals(ModItems.injection_garlic)) {
            IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(player);
            IPlayableFaction faction = handler.getCurrentFaction();
            if (handler.canJoin(faction)) {
                if (world.isRemote) {
                    VampirismMod.proxy.renderScreenFullColor(4, 30, 0xBBBBBBFF);
                } else {
                    handler.joinFaction(VReference.HUNTER_FACTION);
                    player.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 1));
                }
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.inventory.deleteStack(stack);
                }
            } else if (faction != null) {
                if (!world.isRemote) {
                    player.sendMessage(new TextComponentTranslation("text.vampirism.med_chair_other_faction", new TextComponentTranslation(faction.getTranslationKey())));
                }

            }
        } else {
            if (world.isRemote)
                player.sendMessage(new TextComponentTranslation("text.vampirism.need_item_to_use", new TextComponentTranslation((new ItemStack(ModItems.injection_garlic).getTranslationKey() + ".name"))));
        }

        return true;
    }

    @Override
    public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving) {
        super.onReplaced(state, world, pos, newState, isMoving);
        if (state.getBlock() != newState.getBlock()) {
            EnumFacing dir = state.get(FACING);
            BlockPos other;
            if (state.get(PART) == EnumPart.TOP) {
                other = pos.offset(dir);
                world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(ModItems.item_med_chair, 1)));
            } else {
                other = pos.offset(dir.getOpposite());
            }
            world.removeBlock(other);
        }
    }

    @Override
    public IBlockState rotate(IBlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }


    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }


    public enum EnumPart implements IStringSerializable {
        TOP("top", 0), BOTTOM("bottom", 1);

        public static EnumPart fromMeta(int meta) {
            if (meta == 1) {
                return BOTTOM;
            }
            return TOP;
        }

        public final String name;
        public final int meta;

        EnumPart(String name, int meta) {
            this.name = name;
            this.meta = meta;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
