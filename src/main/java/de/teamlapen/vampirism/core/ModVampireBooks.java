package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.items.component.VampireBook;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

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
    public static final ResourceKey<IVampireBook> PYROMANIACS_DIARY = createKey("pyromaniacs_diary");

    public static void createVampireBooks(BootstrapContext<IVampireBook> context) {
        register(context, MAIDS_DIARY);
        register(context, MY_MOTHER);
        register(context, MAD_MANS_JOURNAL, true);
        register(context, MY_PRINCE);
        register(context, DEAR_MARTHA);
        register(context, NOCTURNAL, true);

        register(context, HUNTERS_DIARY);
        register(context, ROYAL_RIVALRY, true);
        register(context, CENTURY_OF_EVOLUTION, "Matheo");
        register(context, OBSERVATION_ON_VAMPIRES);
        register(context, INFUSION_BREAKTHROUGH, true);
        register(context, CASE_STUDY_ONE);
        register(context, WANTED, "Kae din Saarin");
        register(context, CASE_FILE_144, true);

        register(context, SINISTER_INTENTIONS, "Sinister Solace");
        register(context, VALOROUS_TALE);
        register(context, PYROMANIACS_DIARY, "Pyromaniac Pik");
    }

    private static ResourceKey<IVampireBook> createKey(String name) {
        return ResourceKey.create(VampirismRegistries.Keys.VAMPIRE_BOOK, VResourceLocation.mod(name));
    }

    private static void register(BootstrapContext<IVampireBook> context, ResourceKey<IVampireBook> id, Component author) {
        context.register(id, new VampireBook(id.location(), author));
    }

    private static void register(BootstrapContext<IVampireBook> context, ResourceKey<IVampireBook> id, boolean hasCustomAuthor) {
        register(context, id, hasCustomAuthor ? Component.translatable("vampire_book." + id.location().toLanguageKey() + ".author") : VampireBook.DEFAULT_AUTHOR);
    }

    private static void register(BootstrapContext<IVampireBook> context, ResourceKey<IVampireBook> id) {
        register(context, id, false);
    }

    private static void register(BootstrapContext<IVampireBook> context, ResourceKey<IVampireBook> id, String author) {
        register(context, id, Component.literal(author));
    }
}
