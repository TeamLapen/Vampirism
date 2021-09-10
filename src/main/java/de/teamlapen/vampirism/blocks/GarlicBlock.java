package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * Garlic Plant
 * Similar to potatoes, the (dropped) item also is the seed.
 * 7 grow states with 4 different icons
 *
 * @author Maxanier
 */
public class GarlicBlock extends CropBlock {

    public static final String regName = "garlic";
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
        setRegistryName(REFERENCE.MODID, regName);
    }

    @Override
    public void entityInside(BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        if (state.getValue(AGE) > 5 && Helper.isVampire(entity)) {
            DamageHandler.affectVampireGarlicDirect(entity instanceof Player ? VReference.VAMPIRE_FACTION.getPlayerCapability((Player) entity).orElseThrow(() -> new IllegalStateException("Capability cannot be null")) : (IVampire) entity, EnumStrength.WEAK);
        }
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return shape[state.getValue(this.getAgeProperty())];
    }

    @Nonnull
    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.item_garlic;
    }
}