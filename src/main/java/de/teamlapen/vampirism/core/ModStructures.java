package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.world.gen.structure.crypt.CryptStructurePieces;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.RandomBlockStateRule;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.RandomStructureProcessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.stream.Collectors;

import static de.teamlapen.vampirism.world.gen.VanillaStructureModifications.singleJigsawPieceFunction;

public class ModStructures {

    public static final ResourceKey<StructureTemplatePool> HUNTER_TRAINER = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(REFERENCE.MODID, "village/entities/hunter_trainer"));
    public static final ResourceKey<StructureProcessorList> TOTEM_FACTION = ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation(REFERENCE.MODID, "totem_faction"));


    public static void createStructurePoolTemplates(BootstapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> holderGetter = context.lookup(Registries.TEMPLATE_POOL);
        HolderGetter<StructureProcessorList> processorList = context.lookup(Registries.PROCESSOR_LIST);

        Holder<StructureTemplatePool> empty = holderGetter.getOrThrow(Pools.EMPTY);

        context.register(HUNTER_TRAINER, new StructureTemplatePool(empty , Lists.newArrayList(Pair.of(singleJigsawPieceFunction(processorList, "village/entities/hunter_trainer"), 1)), StructureTemplatePool.Projection.RIGID));
        CryptStructurePieces.bootstrap(context);
    }

    public static void createStructureProcessorLists(BootstapContext<StructureProcessorList> context) {
        StructureProcessor factionProcessor = new RandomStructureProcessor(ImmutableList.of(new RandomBlockStateRule(new RandomBlockMatchTest(ModBlocks.TOTEM_TOP.get(), (VampirismConfig.COMMON.villageTotemFactionChance.getDefault()).floatValue()), AlwaysTrueTest.INSTANCE, ModBlocks.TOTEM_TOP.get().defaultBlockState(), TotemTopBlock.getBlocks().stream().filter((totemx) -> totemx != ModBlocks.TOTEM_TOP.get() && !totemx.isCrafted()).map(Block::defaultBlockState).collect(Collectors.toList()))));
        StructureProcessor biomeTopBlockProcessor = new BiomeTopBlockProcessor(Blocks.DIRT.defaultBlockState());

        context.register(TOTEM_FACTION, new StructureProcessorList(ImmutableList.of(factionProcessor, biomeTopBlockProcessor)));
    }
}
