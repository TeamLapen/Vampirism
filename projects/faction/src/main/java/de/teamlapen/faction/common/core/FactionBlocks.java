package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.world.blocks.FactionBlockAccess;
import de.teamlapen.faction.common.world.blocks.TotemBaseBlock;
import de.teamlapen.faction.common.world.blocks.TotemTopBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.of;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;

public class FactionBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(REFERENCE.MOD_ID);

    public static final DeferredBlock<TotemBaseBlock> TOTEM_BASE = BLOCKS.registerBlock("totem_base", TotemBaseBlock::new, () -> of().mapColor(MapColor.STONE).strength(40, 2000).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP = BLOCKS.registerBlock(FactionBlockAccess.Keys.TOTEM_TOP.getPath(), props -> new TotemTopBlock(props, false, null), () -> TotemTopBlock.properties(of()));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_CRAFTED = BLOCKS.registerBlock(FactionBlockAccess.Keys.TOTEM_TOP_CRAFTED.getPath(), props -> new TotemTopBlock(props, true, null), props(TOTEM_TOP));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    public static Supplier<BlockBehaviour.Properties> props(Supplier<? extends Block> baseBlock) {
        return () -> ofFullCopy(baseBlock.get());
    }
}
