package de.teamlapen.faction.api.factions;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.faction.api.FactionAttachments;
import de.teamlapen.faction.api.FactionDataComponents;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.lord.ILordTitleProvider;
import de.teamlapen.faction.api.factions.lord.LordTitles;
import de.teamlapen.faction.api.factions.refinements.IRefinementHandler;
import de.teamlapen.faction.api.factions.tasks.ITaskManager;
import de.teamlapen.faction.api.factions.village.TotemPair;
import de.teamlapen.faction.api.factions.village.VillageBanner;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.api.world.items.RefinementItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FactionProperties {

    private static final DependantName<IFaction<?>, String> DESCRIPTION_ID = (id) -> Util.makeDescriptionId("faction", id.identifier());
    private static final DependantName<IFaction<?>, String> DESCRIPTION_ID_SINGULAR = (id) -> Util.makeDescriptionId("faction", id.identifier().withSuffix("/singular"));
    private static final DependantName<IFaction<?>, String> DESCRIPTION_ID_PLURAL = (id) -> Util.makeDescriptionId("faction", id.identifier().withPrefix("/plural"));
    private DataComponentInitializers.Initializer<IFaction<?>> componentInitializer = (builder, context, id) -> {};
    private @Nullable ResourceKey<IFaction<?>> id;

    private final Map<Class<?>, FactionExtensionType<?>> extensions = new HashMap<>();

    public FactionProperties() {

    }

    public FactionProperties setId(ResourceKey<IFaction<?>> id) {
        this.id = id;
        return this;
    }

    public Map<Class<?>, FactionExtensionType<?>> getExtensions() {
        return ImmutableMap.copyOf(extensions);
    }

    public ResourceKey<IFaction<?>> itemIdOrThrow() {
        return Objects.requireNonNull(this.id, "Faction id not set");
    }

    public String effectiveDescriptionId() {
        return DESCRIPTION_ID.get(this.itemIdOrThrow());
    }

    public String effectiveDescriptionIdSingular() {
        return DESCRIPTION_ID_SINGULAR.get(this.itemIdOrThrow());
    }

    public String effectiveDescriptionIdPlural() {
        return DESCRIPTION_ID_PLURAL.get(this.itemIdOrThrow());
    }

    public FactionProperties color(int color) {
        return component(FactionDataComponents.FACTION_COLOR, color)
                .chatColor(TextColor.fromRgb(color));
    }

    public FactionProperties chatColor(TextColor color) {
        return component(FactionDataComponents.CHAT_COLOR, color);
    }

    public FactionProperties chatColor(ChatFormatting color) {
        return component(FactionDataComponents.CHAT_COLOR, TextColor.fromLegacyFormat(color));
    }

    public FactionProperties maxLevel(int level) {
        return component(FactionDataComponents.MAX_LEVEL, level);
    }

    public FactionProperties lord(int level) {
        return enableLord().component(FactionDataComponents.MAX_LORD_LEVEL, level);
    }

    public FactionProperties enableTasks() {
        return extension(ITaskManager.class, FactionAttachments.TASK_MANAGER);
    }

    public FactionProperties enableRefinements() {
        return extension(IRefinementHandler.class, FactionAttachments.REFINEMENT_HANDLER);
    }

    public FactionProperties enableLord() {
        return extension(ILordPlayer.class, FactionAttachments.LORD_PLAYER);
    }

    public <TInterface> FactionProperties extension(Class<TInterface> interfaceClass, Supplier<? extends AttachmentType<? extends TInterface>> attachment) {
        this.extensions.put(interfaceClass, new FactionExtensionType.Attachment<>(interfaceClass, attachment));
        return this;
    }

    public <TInterface> FactionProperties extension(Class<TInterface> interfaceClass, DataComponentType<TInterface> component, TInterface defaultValue) {
        this.extensions.put(interfaceClass, new FactionExtensionType.Component<>(interfaceClass, component, defaultValue));
        return this;
    }

    public FactionProperties lord(int level, ILordTitleProvider provider) {
        return enableLord().component(FactionDataComponents.MAX_LORD_LEVEL, level)
                .component(FactionDataComponents.LORD_TITLES, LordTitles.provide(level, provider));
    }

    public <TPlayer extends IFactionPlayer<?>> FactionProperties playerAttachment(DeferredHolder<AttachmentType<?>, ? extends AttachmentType<? extends TPlayer>> attachment) {
        return component(FactionDataComponents.PLAYER_CAPABILITY, attachment);
    }

    public FactionProperties refinements(RefinementItems refinementItems) {
        return enableRefinements()
                .component(FactionDataComponents.REFINEMENTS, refinementItems);
    }

    public FactionProperties badOmen(Holder<MobEffect> effect) {
        return component(FactionDataComponents.VILLAGE_BAD_OMEN, effect);
    }

    public FactionProperties villageGuards(TagKey<EntityType<?>> tag) {
        return component(FactionDataComponents.VILLAGE_GUARDS, tag);
    }

    public FactionProperties taskMaster(Holder<EntityType<?>> taskMaster) {
        return enableTasks()
                .component(FactionDataComponents.TASK_MASTER, taskMaster);
    }

    public FactionProperties totem(Holder<Block> fragile, Holder<Block> crafted) {
        return component(FactionDataComponents.VILLAGE_TOTEM, new TotemPair(fragile, crafted));
    }

    public FactionProperties banner(VillageBanner banner) {
        return component(FactionDataComponents.VILLAGE_BANNER, banner);
    }

    public <T> FactionProperties component(Supplier<DataComponentType<T>> type, T value) {
        return component(type.get(), value);
    }

    public <T> FactionProperties component(DataComponentType<T> type, T value) {
        this.componentInitializer = this.componentInitializer.add(type, value);
        return this;
    }

    public DataComponentInitializers.Initializer<IFaction<?>> finalizeInitializer(Component singularName, Component pluralName) {
        return this.componentInitializer
                .andThen((builder, _, key) -> {
                    builder.set(FactionDataComponents.FACTION_NAME_SINGULAR, singularName).set(FactionDataComponents.FACTION_NAME_PLURAL, pluralName);

                    Integer lordLevel = builder.get(FactionDataComponents.MAX_LORD_LEVEL);
                    if (lordLevel != null && lordLevel > 0 && !builder.has(FactionDataComponents.LORD_TITLES)) {
                        builder.set(FactionDataComponents.LORD_TITLES, LordTitles.provideDefault(key.identifier(), lordLevel));
                    }
                });
    }

    public FactionProperties withValidator(Consumer<DataComponentMap> validator) {
        this.componentInitializer = this.componentInitializer.andThen((builder, _, _) -> builder.addValidator(validator));

        return this;
    }
}
