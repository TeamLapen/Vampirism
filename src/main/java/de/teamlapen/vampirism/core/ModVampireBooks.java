package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.items.component.VampireBook;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ModVampireBooks {
    public static final ResourceLocation DEFAULT_ITEM_MODEL = VResourceLocation.mod("item/vampire_book");
    public static final ResourceLocation DEFAULT_BACKGROUND_TEXTURE = VResourceLocation.mod("textures/gui/vampire_book.png");

    public static final ResourceKey<IVampireBook> HUNTERS_DIARY = createKey("hunters_diary");
    public static final ResourceKey<IVampireBook> MAIDS_DIARY = createKey("maids_diary");
    public static final ResourceKey<IVampireBook> MY_MOTHER = createKey("my_mother");
    public static final ResourceKey<IVampireBook> MAD_MANS_JOURNAL = createKey("mad_mans_journal");
    public static final ResourceKey<IVampireBook> MY_PRINCE = createKey("my_prince");
    public static final ResourceKey<IVampireBook> PYROMANIACS_DIARY = createKey("pyromaniacs_diary");
    public static final ResourceKey<IVampireBook> ROYAL_RIVALRY = createKey("royal_rivalry");
    public static final ResourceKey<IVampireBook> CENTURY_OF_EVOLUTION = createKey("century_of_evolution");
    public static final ResourceKey<IVampireBook> OBSERVATION_ON_VAMPIRES = createKey("observation_on_vampires");
    public static final ResourceKey<IVampireBook> CASE_STUDY_ONE = createKey("case_study_one");
    public static final ResourceKey<IVampireBook> SINISTER_INTENTIONS = createKey("sinister_intentions");
    public static final ResourceKey<IVampireBook> DEAR_MARTHA = createKey("dear_martha");
    public static final ResourceKey<IVampireBook> VALOROUS_TALE = createKey("valorous_tale");
    public static final ResourceKey<IVampireBook> NOCTURNAL = createKey("nocturnal");
    public static final ResourceKey<IVampireBook> WANTED = createKey("wanted");
    public static final ResourceKey<IVampireBook> CASE_FILE_144 = createKey("case_file_144");

    public static void createVampireBooks(BootstrapContext<IVampireBook> context) {
        register(context, HUNTERS_DIARY);
        register(context, MAIDS_DIARY);
        register(context, MY_MOTHER);
        register(context, MAD_MANS_JOURNAL);
        register(context, MY_PRINCE);
        register(context, PYROMANIACS_DIARY);
        register(context, ROYAL_RIVALRY);
        register(context, CENTURY_OF_EVOLUTION);
        register(context, OBSERVATION_ON_VAMPIRES);
        register(context, CASE_STUDY_ONE);
        register(context, SINISTER_INTENTIONS);
        register(context, DEAR_MARTHA);
        register(context, VALOROUS_TALE);
        register(context, NOCTURNAL);
        register(context, WANTED);
        register(context, CASE_FILE_144);
    }

    private static ResourceKey<IVampireBook> createKey(String name) {
        return ResourceKey.create(VampirismRegistries.Keys.VAMPIRE_BOOK, VResourceLocation.mod(name));
    }

    private static void register(BootstrapContext<IVampireBook> context, ResourceKey<IVampireBook> id, ResourceLocation itemModel, ResourceLocation backgroundTexture) {
        context.register(id, new VampireBook(id.location(), itemModel, backgroundTexture));
    }

    private static void register(BootstrapContext<IVampireBook> context, ResourceKey<IVampireBook> id) {
        register(context, id, DEFAULT_ITEM_MODEL, DEFAULT_BACKGROUND_TEXTURE);
    }
}
