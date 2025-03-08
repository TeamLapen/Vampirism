package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.core.tags.ModVampireBookTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static de.teamlapen.vampirism.core.ModVampireBooks.*;

public class ModVampireBookTagsProvider extends TagsProvider<IVampireBook> {

    protected ModVampireBookTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @SuppressWarnings("removal") @Nullable net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
        super(output, VampirismRegistries.Keys.VAMPIRE_BOOK, lookupProvider, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModVampireBookTags.IS_GENERAL).add(
                MAIDS_DIARY,
                MY_MOTHER,
                MAD_MANS_JOURNAL,
                DEAR_MARTHA,
                WANTED,
                CASE_FILE_144
        );
        this.tag(ModVampireBookTags.IS_VAMPIRE).add(
                MAIDS_DIARY,
                MY_MOTHER,
                MAD_MANS_JOURNAL,
                MY_PRINCE,
                DEAR_MARTHA,
                NOCTURNAL
        );
        this.tag(ModVampireBookTags.IS_HUNTER).add(
                HUNTERS_DIARY,
                ROYAL_RIVALRY,
                CENTURY_OF_EVOLUTION,
                OBSERVATION_ON_VAMPIRES,
                CASE_STUDY_ONE,
                DEAR_MARTHA,
                WANTED,
                CASE_FILE_144
        );
        this.tag(ModVampireBookTags.NON_TREASURE).add(
                PYROMANIACS_DIARY,
                SINISTER_INTENTIONS,
                VALOROUS_TALE
        );
    }
}
