package de.teamlapen.vampirism.common.world.blocks.diffuser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.world.blockentity.diffuser.DiffuserBlockEntity;
import de.teamlapen.vampirism.common.world.blockentity.diffuser.GarlicDiffuserBlockEntity;
import de.teamlapen.faction.api.world.IDescriptionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class GarlicDiffuserBlock extends DiffuserBlock implements IDescriptionProvider {

    // Replace the radius field with a config codec if needed in the future
    public static final MapCodec<GarlicDiffuserBlock> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    propertiesCodec(),
                    EnumStrength.CODEC.fieldOf("strength").forGetter(p -> p.strength),
                    Codec.INT.fieldOf("radius").forGetter(p -> p.radius.get())
            ).apply(inst, (properties, strength, radius) -> new GarlicDiffuserBlock(properties, strength, () -> radius))
    );

    private final EnumStrength strength;
    private final Supplier<Integer> radius;

    public GarlicDiffuserBlock(Properties properties, EnumStrength strength, Supplier<Integer> radius) {
        super(properties, ModBlockEntities.GARLIC_DIFFUSER::get);
        this.strength = strength;
        this.radius = radius;
    }

    @Override
    protected MapCodec<? extends DiffuserBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable DiffuserBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GarlicDiffuserBlockEntity(pos, state, strength, radius.get());
    }

    @Override
    public Object[] getDescriptionParameters() {
        int chunks = 1 + 2 * radius.get();
        return new Object[]{chunks, chunks};
    }
}
