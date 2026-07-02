package de.teamlapen.vampirism.processor.generator;

import de.teamlapen.vampirism.processor.AdditionalConverterModel;
import de.teamlapen.vampirism.processor.CreatureModel;
import de.teamlapen.vampirism.processor.ProcessorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates {@code Generated<ModId>ConvertedEntitiesData}: an {@code @EventBusSubscriber} that self-registers
 * three datagen providers via {@code GatherDataEvent.Client}:
 * <ul>
 *     <li>{@code GeneratedConverterProvider extends DataMapProvider} — entity-converter data map,</li>
 *     <li>{@code GeneratedLootDataProvider} — empty entity loot tables,</li>
 *     <li>{@code GeneratedConverterEntityTypeTagsProvider extends EntityTypeTagsProvider} — {@code CONVERTED_CREATURES} tag.</li>
 * </ul>
 */
public final class DatagenGenerator {

    private DatagenGenerator() {}

    public static String className(String modId) {
        return "Generated" + ProcessorUtil.modIdToClassPart(modId) + "ConvertedEntitiesData";
    }

    public static String generate(String packageName, String registrarPackage, String modId, List<CreatureModel> models, List<AdditionalConverterModel> additionalModels) {
        String className = className(modId);
        String registrarClass = RegistrarGenerator.className(modId);

        StringBuilder im = new StringBuilder();
        List<String> imps = new ArrayList<>(List.of(
                registrarPackage + "." + registrarClass,
                "de.teamlapen.vampirism.common.core.ModDataMaps",
                "de.teamlapen.vampirism.common.datamaps.ConverterEntry",
                "de.teamlapen.vampirism.common.tags.ModEntityTags",
                "de.teamlapen.vampirism.common.world.entity.converted.converter.SpecialConverter",
                "net.minecraft.core.Holder",
                "net.minecraft.core.HolderLookup",
                "net.minecraft.core.registries.BuiltInRegistries",
                "net.minecraft.data.CachedOutput",
                "net.minecraft.data.DataProvider",
                "net.minecraft.data.PackOutput",
                "net.minecraft.data.loot.EntityLootSubProvider",
                "net.minecraft.data.loot.LootTableProvider",
                "net.minecraft.data.tags.EntityTypeTagsProvider",
                "net.minecraft.tags.TagKey",
                "net.minecraft.world.entity.EntityType",
                "net.minecraft.world.flag.FeatureFlags",
                "net.minecraft.world.level.storage.loot.LootTable",
                "net.minecraft.world.level.storage.loot.parameters.LootContextParamSets",
                "net.neoforged.bus.api.SubscribeEvent",
                "net.neoforged.fml.common.EventBusSubscriber",
                "net.neoforged.neoforge.common.data.DataMapProvider",
                "net.neoforged.neoforge.data.event.GatherDataEvent",
                "java.util.List",
                "java.util.Set",
                "java.util.concurrent.CompletableFuture",
                "java.util.function.Function",
                "java.util.stream.Stream"
        ));
        for (AdditionalConverterModel m : additionalModels) {
            imps.add(m.converterClassFqn());
        }
        ProcessorUtil.imports(im, imps.stream().distinct().toArray(String[]::new));

        // Entity-converter data-map entries (generated creatures + additional)
        StringBuilder converterEntries = new StringBuilder();
        for (CreatureModel m : models) {
            converterEntries.append("            builder.add(holder.apply(EntityType.%1$s), new ConverterEntry(new SpecialConverter<>(%2$s.%3$s)), false);\n"
                    .formatted(m.entityType(), registrarClass, m.holderField()));
        }
        for (AdditionalConverterModel m : additionalModels) {
            converterEntries.append("            builder.add(holder.apply(EntityType.%1$s), new ConverterEntry(new SpecialConverter<>(%2$s.%3$s)), false);\n"
                    .formatted(m.entityType(), m.converterSimple(), m.converterField()));
        }

        // Empty loot-table entries + getKnownEntityTypes stream (generated creatures only)
        StringBuilder lootEntries = new StringBuilder();
        StringBuilder knownEntries = new StringBuilder();
        for (int i = 0; i < models.size(); i++) {
            CreatureModel m = models.get(i);
            lootEntries.append("            this.add(%1$s.%2$s.get(), LootTable.lootTable());\n"
                    .formatted(registrarClass, m.holderField()));
            knownEntries.append("                    %1$s.%2$s.get()%3$s\n"
                    .formatted(registrarClass, m.holderField(), i < models.size() - 1 ? "," : ""));
        }

        // Entity-type tag entries for GeneratedConverterEntityTypeTagsProvider (generated creatures only)
        StringBuilder tagProviderEntries = new StringBuilder();
        for (CreatureModel m : models) {
            tagProviderEntries.append("            this.tag(CONVERTED_CREATURES).add(%1$s.%2$s.get());\n"
                    .formatted(registrarClass, m.holderField()));
        }

        String pkg = packageName.isEmpty() ? "" : "package " + packageName + ";\n\n";
        return pkg + im + "\n" + """
                /**
                 * Generated datagen providers for converted entities. Registers three providers via
                 * {@link GatherDataEvent.Client}: entity-converter data map, empty entity loot tables,
                 * and the {@code CONVERTED_CREATURES} entity-type tag.
                 */
                @EventBusSubscriber(modid = "%6$s")
                public final class %1$s {

                    private %1$s() {}

                    @SubscribeEvent
                    public static void onGatherData(GatherDataEvent.Client event) {
                        event.getGenerator().addProvider(true,
                                new GeneratedConverterProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
                        event.getGenerator().addProvider(true,
                                new GeneratedLootDataProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
                        event.getGenerator().addProvider(true,
                                new GeneratedConverterEntityTypeTagsProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), "%6$s"));
                    }

                    private static final class GeneratedConverterProvider extends DataMapProvider {
                        protected GeneratedConverterProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
                            super(output, lookupProvider);
                        }

                        @Override
                        public String getName() {
                            return super.getName() + " (Generated Convertibles)";
                        }

                        @Override
                        protected void gather(HolderLookup.Provider provider) {
                            var builder = this.builder(ModDataMaps.ENTITY_CONVERTER_MAP);
                            Function<EntityType<?>, Holder<EntityType<?>>> holder = BuiltInRegistries.ENTITY_TYPE::wrapAsHolder;
                %2$s        }
                    }

                    /** Wraps {@link LootTableProvider} to provide a unique name (its {@code getName()} is final). */
                    private static final class GeneratedLootDataProvider implements DataProvider {
                        private final LootTableProvider delegate;

                        GeneratedLootDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
                            this.delegate = new LootTableProvider(output, Set.of(),
                                    List.of(new LootTableProvider.SubProviderEntry(GeneratedConverterLootProvider::new, LootContextParamSets.ENTITY)),
                                    lookupProvider);
                        }

                        @Override
                        public CompletableFuture<?> run(CachedOutput output) {
                            return delegate.run(output);
                        }

                        @Override
                        public String getName() {
                            return "Loot Tables (Generated Convertibles)";
                        }
                    }

                    private static final class GeneratedConverterLootProvider extends EntityLootSubProvider {
                        protected GeneratedConverterLootProvider(HolderLookup.Provider lookupProvider) {
                            super(FeatureFlags.REGISTRY.allFlags(), lookupProvider);
                        }

                        @Override
                        public void generate() {
                %3$s        }

                        @Override
                        protected Stream<EntityType<?>> getKnownEntityTypes() {
                            return Stream.of(
                %4$s            );
                        }
                    }

                    private static final class GeneratedConverterEntityTypeTagsProvider extends EntityTypeTagsProvider {

                        private final TagKey<EntityType<?>> CONVERTED_CREATURES = ModEntityTags.CONVERTED_CREATURES;

                        public GeneratedConverterEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
                            super(output, lookupProvider, modId);
                        }

                        @Override
                        public String getName() {
                            return super.getName() + " (Generated Convertibles)";
                        }

                        @Override
                        protected void addTags(HolderLookup.Provider registries) {
                %5$s        }
                    }
                }
                """.formatted(className, converterEntries.toString(), lootEntries.toString(), knownEntries.toString(), tagProviderEntries.toString(), modId);
    }
}
