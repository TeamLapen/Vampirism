package de.teamlapen.vampirism.world.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.api.entity.VampireBookLootProvider;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.core.tags.ModVampireBookTags;
import de.teamlapen.vampirism.items.component.VampireBook;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SetVampireBookFunction extends LootItemConditionalFunction {

    public static final MapCodec<SetVampireBookFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance)
                    .and(TagKey.codec(VampirismRegistries.Keys.VAMPIRE_BOOK).fieldOf("tag").forGetter(SetBookFunction -> SetBookFunction.tag))
                    .apply(instance, SetVampireBookFunction::new)
    );

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Used for special books dropped from advanced entities. Tag isn't really used here, but it is required so that it doesn't crush, it cannot be null.
     */
    public static LootItemConditionalFunction.Builder<?> special() {
        return simpleBuilder(conditions -> new SetVampireBookFunction(conditions, ModVampireBookTags.NON_TREASURE));
    }

    public static LootItemConditionalFunction.Builder<?> randomTagged(TagKey<IVampireBook> tag) {
        return simpleBuilder(conditions -> new SetVampireBookFunction(conditions, tag));
    }

    private final TagKey<IVampireBook> tag;

    public SetVampireBookFunction(List<LootItemCondition> predicates, TagKey<IVampireBook> tag) {
        super(predicates);
        this.tag = tag;
    }

    @Override
    public @NotNull LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return ModLoot.SET_VAMPIRE_BOOK.get();
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack stack, @NotNull LootContext lootContext) {
        RegistryAccess registryAccess = lootContext.getLevel().registryAccess();
        IVampireBook vampireBook = VampireBook.getRandomBook(tag, lootContext);

        Entity entity = lootContext.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity instanceof VampireBookLootProvider provider) {
            if (provider.getBookLootId().isPresent()) {
                ResourceLocation id = ResourceLocation.parse(provider.getBookLootId().get());
                Optional<IVampireBook> possibleBook = registryAccess.lookupOrThrow(VampirismRegistries.Keys.VAMPIRE_BOOK).getOptional(id);

                if (possibleBook.isPresent()) {
                    vampireBook = possibleBook.get();
                } else {
                    LOGGER.warn("Vampire Book \"{}\" does not exist, cannot add it to a loot table", id.getPath());
                }
            } else {
                TagKey<IVampireBook> factionTag = Optional.of(VampirismAPI.factionRegistry().getFaction(entity)).map(Holder::value).flatMap(s -> s.getTag(VampirismRegistries.Keys.VAMPIRE_BOOK)).orElse(ModVampireBookTags.IS_GENERAL);
                vampireBook = VampireBook.getRandomBook(factionTag, lootContext);
            }
        }

        VampireBook.addToStack(stack, vampireBook);
        return stack;
    }
}
