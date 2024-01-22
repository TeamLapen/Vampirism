package de.teamlapen.vampirism.blocks.diffuser;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.blockentity.diffuser.DiffuserBlockEntity;
import de.teamlapen.vampirism.blockentity.diffuser.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.world.garlic.GarlicLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GarlicDiffuserBlock extends DiffuserBlock {

    public static final MapCodec<GarlicDiffuserBlock> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    StringRepresentable.fromEnum(Type::values).fieldOf("type").forGetter(p -> p.type),
                    propertiesCodec()
            ).apply(inst, GarlicDiffuserBlock::new)
    );

    private final Type type;

    public GarlicDiffuserBlock(@NotNull Type type) {
        this(type, Properties.of().mapColor(MapColor.STONE).strength(40.0F, 1200.0F).sound(SoundType.STONE).noOcclusion());
    }

    public GarlicDiffuserBlock(@NotNull Type type, @NotNull Properties properties) {
        super(properties, ModTiles.GARLIC_DIFFUSER::get);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    protected @NotNull MapCodec<? extends DiffuserBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable DiffuserBlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        EnumStrength strength;
        int radius;
        switch (type) {
            case WEAK -> {
                strength = EnumStrength.WEAK;
                radius = VampirismConfig.BALANCE.hsGarlicDiffuserWeakDist.get();
            }
            case IMPROVED -> {
                strength = EnumStrength.MEDIUM;
                radius = VampirismConfig.BALANCE.hsGarlicDiffuserEnhancedDist.get();
            }
            case NORMAL -> {
                strength = EnumStrength.MEDIUM;
                radius = VampirismConfig.BALANCE.hsGarlicDiffuserNormalDist.get();
            }
            default -> {
                strength = EnumStrength.WEAK;
                radius = 0;
            }
        }
        return new GarlicDiffuserBlockEntity(pPos, pState, strength, radius);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter world, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        if (type == Type.WEAK || type == Type.IMPROVED) {
            tooltip.add(Component.translatable(getDescriptionId() + "." + type.getSerializedName()).withStyle(ChatFormatting.AQUA));
        }

        tooltip.add(Component.translatable("block.vampirism.garlic_diffuser.tooltip1").withStyle(ChatFormatting.GRAY));
        int c = VampirismConfig.BALANCE.hsGarlicDiffuserEnhancedDist == null /* During game start config is not yet set*/ ? 1 : 1 + 2 * (type == Type.IMPROVED ? VampirismConfig.BALANCE.hsGarlicDiffuserEnhancedDist.get() : (type == Type.WEAK ? VampirismConfig.BALANCE.hsGarlicDiffuserWeakDist.get() : VampirismConfig.BALANCE.hsGarlicDiffuserNormalDist.get()));
        tooltip.add(Component.translatable("block.vampirism.garlic_diffuser.tooltip2", c, c).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "block.vampirism.garlic_diffuser";
    }

    public enum Type implements StringRepresentable {
        NORMAL("normal"),
        IMPROVED("improved"),
        WEAK("weak");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
