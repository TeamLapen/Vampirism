package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.world.blocks.TotemBaseBlock;
import de.teamlapen.faction.common.world.blocks.TotemTopBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class FactionBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(REFERENCE.MOD_ID);

    public static final DeferredBlock<TotemBaseBlock> TOTEM_BASE = registerWithItem("totem_base", TotemBaseBlock::new, () -> basicProperties().mapColor(MapColor.STONE).strength(40, 2000).sound(SoundType.STONE).noOcclusion().pushReaction(PushReaction.BLOCK));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP = registerWithItem("totem_top", props -> new TotemTopBlock(props, false, null), () -> TotemTopBlock.properties(basicProperties()));
    public static final DeferredBlock<TotemTopBlock> TOTEM_TOP_CRAFTED = registerWithItem("totem_top_crafted", props -> new TotemTopBlock(props, true, null), () -> copyProperties(TOTEM_TOP));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    @SuppressWarnings("SameParameterValue")
    private static BlockBehaviour.Properties copyProperties(DeferredBlock<?> block) {
        return copyProperties(block.get());
    }

    private static BlockBehaviour.Properties copyProperties(BlockBehaviour block) {
        return BlockBehaviour.Properties.ofFullCopy(block);
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties,T> supplier, Supplier<BlockBehaviour.Properties> blockProperties) {
        return registerWithItem(name, supplier, blockProperties, props -> props);
    }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Function<BlockBehaviour.Properties, T> supplier, Supplier<BlockBehaviour.Properties> blockProperties, Function<Item.Properties, Item.Properties> properties) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, prop -> supplier.apply(blockProperties.get().setId(ResourceKey.create(Registries.BLOCK, FIdentifier.mod(name)))));
        createItem(name, block, BlockItem::new, properties);
        return block;
    }

    private static <T extends Block, R extends Item> void createItem(String name, Supplier<T> block, BiFunction<T, Item.Properties, R> itemCreator, Function<Item.Properties, Item.Properties> properties) {
        FactionItems.ITEMS.registerItem(name, props -> itemCreator.apply(block.get(), properties.apply(props).overrideDescription(block.get().getDescriptionId())));
    }

    private static BlockBehaviour.Properties basicProperties() {
        return BlockBehaviour.Properties.of();
    }
}
