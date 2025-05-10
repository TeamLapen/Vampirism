package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.items.component.VampireBook;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import static de.teamlapen.vampirism.items.component.VampireBook.builder;

public class ModVampireBooks {

    public static final ResourceKey<IVampireBook> MAIDS_DIARY = createKey("maids_diary");
    public static final ResourceKey<IVampireBook> MY_MOTHER = createKey("my_mother");
    public static final ResourceKey<IVampireBook> MAD_MANS_JOURNAL = createKey("mad_mans_journal");
    public static final ResourceKey<IVampireBook> MY_PRINCE = createKey("my_prince");
    public static final ResourceKey<IVampireBook> DEAR_MARTHA = createKey("dear_martha");
    public static final ResourceKey<IVampireBook> NOCTURNAL = createKey("nocturnal");

    public static final ResourceKey<IVampireBook> HUNTERS_DIARY = createKey("hunters_diary");
    public static final ResourceKey<IVampireBook> ROYAL_RIVALRY = createKey("royal_rivalry");
    public static final ResourceKey<IVampireBook> CENTURY_OF_EVOLUTION = createKey("century_of_evolution");
    public static final ResourceKey<IVampireBook> OBSERVATION_ON_VAMPIRES = createKey("observation_on_vampires");
    public static final ResourceKey<IVampireBook> INFUSION_BREAKTHROUGH = createKey("infusion_breakthrough");
    public static final ResourceKey<IVampireBook> CASE_STUDY_ONE = createKey("case_study_one");
    public static final ResourceKey<IVampireBook> WANTED = createKey("wanted");
    public static final ResourceKey<IVampireBook> CASE_FILE_144 = createKey("case_file_144");

    public static final ResourceKey<IVampireBook> SINISTER_INTENTIONS = createKey("sinister_intentions");
    public static final ResourceKey<IVampireBook> VALOROUS_TALE = createKey("valorous_tale");
    public static final ResourceKey<IVampireBook> ASHES_OF_PAST_DAWNS = createKey("ashes_of_past_dawns");
    public static final ResourceKey<IVampireBook> PYROMANIACS_DIARY = createKey("pyromaniacs_diary");

    public static void createVampireBooks(BootstrapContext<IVampireBook> context) {
        register(context, builder(MAIDS_DIARY));
        register(context, builder(MY_MOTHER));
        register(context, builder(MAD_MANS_JOURNAL).customAuthor());
        register(context, builder(MY_PRINCE));
        register(context, builder(DEAR_MARTHA));
        register(context, builder(NOCTURNAL).customAuthor());

        register(context, builder(HUNTERS_DIARY));
        register(context, builder(ROYAL_RIVALRY).customAuthor());
        register(context, builder(CENTURY_OF_EVOLUTION).author("Matheo"));
        register(context, builder(OBSERVATION_ON_VAMPIRES));
        register(context, builder(INFUSION_BREAKTHROUGH).customAuthor());
        register(context, builder(CASE_STUDY_ONE));
        register(context, builder(WANTED).author("Kae din Saarin"));
        register(context, builder(CASE_FILE_144).customAuthor());

        register(context, builder(SINISTER_INTENTIONS).author("Sinister Solace"));
        register(context, builder(VALOROUS_TALE));
        register(context, builder(ASHES_OF_PAST_DAWNS).customAuthor());
        register(context, builder(PYROMANIACS_DIARY).author("Pyromaniac Pik"));
    }

    private static ResourceKey<IVampireBook> createKey(String name) {
        return ResourceKey.create(VampirismRegistries.Keys.VAMPIRE_BOOK, VResourceLocation.mod(name));
    }

    private static void register(BootstrapContext<IVampireBook> context, VampireBook.Builder builder) {
        context.register(builder.id, builder.build());
    }
}
