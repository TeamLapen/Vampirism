package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.tags.ModEntityTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider holderLookup) {
        tag(ModEntityTags.HUNTER).add(ModEntities.HUNTER.get(), ModEntities.HUNTER_IMOB.get(), ModEntities.ADVANCED_HUNTER.get(), ModEntities.ADVANCED_HUNTER_IMOB.get(), ModEntities.HUNTER_TRAINER.get(), ModEntities.HUNTER_TRAINER.get(), ModEntities.HUNTER_TRAINER_DUMMY.get(), ModEntities.TASK_MASTER_HUNTER.get());
        tag(ModEntityTags.VAMPIRE).addTag(ModEntityTags.CONVERTED_CREATURES).add(ModEntities.VAMPIRE.get(), ModEntities.VAMPIRE_IMOB.get(), ModEntities.ADVANCED_VAMPIRE.get(), ModEntities.ADVANCED_VAMPIRE_IMOB.get(), ModEntities.VAMPIRE_BARON.get(), ModEntities.TASK_MASTER_VAMPIRE.get());
        tag(ModEntityTags.ADVANCED_HUNTER).add(ModEntities.ADVANCED_HUNTER.get(), ModEntities.ADVANCED_HUNTER_IMOB.get());
        tag(ModEntityTags.ADVANCED_VAMPIRE).add(ModEntities.ADVANCED_VAMPIRE.get(), ModEntities.ADVANCED_VAMPIRE_IMOB.get());
        tag(ModEntityTags.ZOMBIES).add(EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE_HORSE);
        tag(ModEntityTags.IGNORE_VAMPIRE_SWORD_FINISHER).add(ModEntities.VULNERABLE_REMAINS_DUMMY.get(), ModEntities.GHOST.get());
        tag(ModEntityTags.CONVERTED_CREATURES).add(ModEntities.CONVERTED_CAMEL.get(), ModEntities.CONVERTED_COW.get(), ModEntities.CONVERTED_CREATURE.get(), ModEntities.CONVERTED_CREATURE_IMOB.get(), ModEntities.CONVERTED_DONKEY.get(), ModEntities.CONVERTED_FOX.get(), ModEntities.CONVERTED_GOAT.get(), ModEntities.CONVERTED_HORSE.get(), ModEntities.CONVERTED_MULE.get(), ModEntities.CONVERTED_SHEEP.get(), ModEntities.VILLAGER_CONVERTED.get(), ModEntities.CONVERTED_CAT.get());
        tag(Tags.EntityTypes.BOATS).add(ModEntities.BOAT.get(), ModEntities.CHEST_BOAT.get());
    }
}
