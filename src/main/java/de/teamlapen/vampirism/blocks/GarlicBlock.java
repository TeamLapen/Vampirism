package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Garlic Plant
 * Similar to potatoes, the (dropped) item also is the seed.
 * 7 grow states with 4 different icons
 *
 * @author Maxanier
 */
public class GarlicBlock extends CropsBlock {
    private static final VoxelShape[] shape = makeShape();

    private static VoxelShape[] makeShape() {
        return new VoxelShape[]{
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)
        };
    }

    public GarlicBlock() {
        super(Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP));
    }

    @Override
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        if (state.getValue(AGE) > 5 && Helper.isVampire(entity)) {
            if(entity instanceof PlayerEntity){
                VReference.VAMPIRE_FACTION.getPlayerCapability((PlayerEntity) entity).ifPresent( vamp -> DamageHandler.affectVampireGarlicDirect(vamp, EnumStrength.WEAK));
            }
            else if(entity instanceof IVampire){
                DamageHandler.affectVampireGarlicDirect((IVampire) entity, EnumStrength.WEAK);
            }
        }
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape[state.getValue(this.getAgeProperty())];
    }

    @Nonnull
    @Override
    protected IItemProvider getBaseSeedId() {
        return ModItems.ITEM_GARLIC.get();
    }
}