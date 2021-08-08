package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class CastleSlabBlock extends SlabBlock {

    private static final String regName = "castle_slab";
    private final CastleBricksBlock.EnumVariant variant;

    public CastleSlabBlock(CastleBricksBlock.EnumVariant variant) {
        super(Properties.of(Material.STONE).strength(2, 10).sound(SoundType.STONE));
        this.variant = variant;

        this.setRegistryName(REFERENCE.MODID, regName + "_" + variant.getName());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter player, List<Component> tooltip, TooltipFlag advanced) {
        super.appendHoverText(stack, player, tooltip, advanced);
        tooltip.add(new TranslatableComponent("block.vampirism.castle_block" + (variant == CastleBricksBlock.EnumVariant.DARK_STONE ? ".no_spawn" : ".vampire_spawn")).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));

    }

    public CastleBricksBlock.EnumVariant getVariant() {
        return variant;
    }
}
