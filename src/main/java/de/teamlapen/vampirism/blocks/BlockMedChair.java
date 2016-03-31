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
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

/**
 * Created by max on 19.03.16.
 */
public class BlockMedChair extends VampirismBlock {
    public static final PropertyEnum<EnumPart> PART = PropertyEnum.create("part", EnumPart.class);
    private final static String name = "medChair";

    public BlockMedChair() {
        super(name, Material.iron);
        this.blockState.getBaseState().withProperty(PART, EnumPart.TOP).withProperty(FACING, EnumFacing.NORTH);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PART, EnumPart.fromMeta(meta >> 2)).withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART).getMeta() << 2 | state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING, PART);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, placer.getHorizontalFacing().rotateY().rotateY()).withProperty(PART, EnumPart.fromMeta(placer.getRNG().nextInt(2)));//TODO
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {

        ItemStack stack = playerIn.getHeldItem();
        if (stack != null && stack.getItem().equals(ModItems.injection) && stack.getMetadata() == ItemInjection.META_GARLIC) {
            IFactionPlayerHandler handler = VampirismAPI.getFactionPlayerHandler(playerIn);
            IPlayableFaction faction = handler.getCurrentFaction();
            if (faction == null) {
                if (worldIn.isRemote) {
                    VampirismMod.proxy.renderScreenFullColor(4, 30, 0xBBBBBBFF);
                } else {
                    handler.joinFaction(VReference.HUNTER_FACTION);
                    playerIn.addPotionEffect(new PotionEffect(Potion.poison.id, 200, 1));
                }
            } else {
                if (!worldIn.isRemote) {
                    playerIn.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.med_chair_other_faction", new ChatComponentTranslation(faction.getUnlocalizedName())));
                }

            }
        } else {
            if (worldIn.isRemote)
                playerIn.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.need_item_to_use", new ChatComponentTranslation((new ItemStack(ModItems.injection, 1, ItemInjection.META_GARLIC)).getUnlocalizedName() + ".name")));
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
    }

    public enum EnumPart implements IStringSerializable {
        TOP("top", 0), BOTTOM("bottom", 1);

        public final String name;
        public final int meta;

        EnumPart(String name, int meta) {
            this.name = name;
            this.meta = meta;
        }

        public static EnumPart fromMeta(int meta) {
            if (meta == 1) {
                return BOTTOM;
            }
            return TOP;
        }

        @Override
        public String getName() {
            return name;
        }

        public int getMeta() {
            return meta;
        }
    }
}
