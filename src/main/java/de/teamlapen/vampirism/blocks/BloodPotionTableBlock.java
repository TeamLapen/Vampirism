package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.BloodPotionTableContainer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;


public class BloodPotionTableBlock extends VampirismBlock {

    protected static final VoxelShape tableShape = Block.makeCuboidShape(0, 0, 0, 16, 11, 16);
    protected static final VoxelShape shape = makeShape();
    private final static String regName = "blood_potion_table";
    private static final ITextComponent name = new TranslationTextComponent("container.crafting");

    private static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(0, 0, 0, 16, 1, 16);
        VoxelShape b = Block.makeCuboidShape(1, 1, 1, 15, 2, 15);
        VoxelShape c = Block.makeCuboidShape(2, 2, 2, 14, 9, 14);
        VoxelShape d = Block.makeCuboidShape(0, 9, 0, 16, 11, 16);
        return VoxelShapes.or(a, b, c, d);
    }

    public BloodPotionTableBlock() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(1f));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            if (canUse(player) && player instanceof ServerPlayerEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((id, playerInventory, playerIn) -> new BloodPotionTableContainer(id, playerInventory, IWorldPosCallable.of(playerIn.world, pos)), new TranslationTextComponent("container.crafting")), pos);

            } else {
                player.sendMessage(new TranslationTextComponent("text.vampirism.blood_potion_table.cannot_use"));
            }
        }

        return true;
    }

    private boolean canUse(PlayerEntity player) {
        IPlayableFaction faction = FactionPlayerHandler.get(player).getCurrentFaction();
        if (faction != null && faction.equals(VReference.HUNTER_FACTION)) {
            return faction.getPlayerCapability(player).getSkillHandler().isSkillEnabled(HunterSkills.blood_potion_table);
        }
        return false;
    }
}
