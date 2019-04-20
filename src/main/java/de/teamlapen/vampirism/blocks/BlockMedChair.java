package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ItemInjection;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * Block which represents the top and the bottom part of a "Medical Chair" used for injections
 */
public class BlockMedChair extends VampirismBlock {
    public static final PropertyEnum<EnumPart> PART = PropertyEnum.create("part", EnumPart.class);
    private final static String name = "med_chair";

    public BlockMedChair() {
        super(name, Material.IRON);
        this.blockState.getBaseState().withProperty(PART, EnumPart.TOP).withProperty(FACING, EnumFacing.NORTH);
        this.setHasFacing();
        setHardness(0.5F);

    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        EnumFacing dir = state.getValue(FACING);
        BlockPos other;
        if (state.getValue(PART) == EnumPart.TOP) {
            other = pos.offset(dir);
            worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(ModItems.item_med_chair, 1)));
        } else {
            other = pos.offset(dir.getOpposite());
        }
        worldIn.setBlockToAir(other);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART).getMeta() << 2 | state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, placer.getHorizontalFacing().rotateY().rotateY()).withProperty(PART, EnumPart.fromMeta(placer.getRNG().nextInt(2)));//TODO
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PART, EnumPart.fromMeta(meta >> 2)).withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

        ItemStack stack = playerIn.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem().equals(ModItems.injection) && stack.getMetadata() == ItemInjection.META_GARLIC) {
            IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(playerIn);
            IPlayableFaction faction = handler.getCurrentFaction();
            if (handler.canJoin(faction)) {
                if (worldIn.isRemote) {
                    VampirismMod.proxy.renderScreenFullColor(4, 30, 0xBBBBBBFF);
                } else {
                    handler.joinFaction(VReference.HUNTER_FACTION);
                    playerIn.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 1));
                }
                stack.shrink(1);
                if (stack.isEmpty()) {
                    playerIn.inventory.deleteStack(stack);
                }
            } else if (faction != null) {
                if (!worldIn.isRemote) {
                    playerIn.sendMessage(new TextComponentTranslation("text.vampirism.med_chair_other_faction", new TextComponentTranslation(faction.getUnlocalizedName())));
                }

            }
        } else {
            if (worldIn.isRemote)
                playerIn.sendMessage(new TextComponentTranslation("text.vampirism.need_item_to_use", new TextComponentTranslation((new ItemStack(ModItems.injection, 1, ItemInjection.META_GARLIC)).getTranslationKey() + ".name")));
        }

        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, PART);
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

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
