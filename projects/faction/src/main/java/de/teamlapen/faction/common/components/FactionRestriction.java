package de.teamlapen.faction.common.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.Factions;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.api.factions.IFactionHelper;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.world.items.components.IFactionRestriction;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public record FactionRestriction(HolderSet<IFaction<?>> factions, Optional<HolderSet<ISkill<?>>> skills, Optional<Integer> minLevel, Optional<Component> customMessage) implements IFactionRestriction, IFactionRestrictionProvider {

    private static final Component MESSAGE_WRONG_FACTION = Component.translatable("message.factionapi.restriction.faction");
    public static final Component MESSAGE_MISSING_SKILLS = Component.translatable("message.factionapi.restriction.skill");
    public static final Component MESSAGE_MISSING_LEVEL = Component.translatable("message.factionapi.restriction.level");

    public static final FactionRestriction ALL = FactionRestriction.builder(de.teamlapen.faction.api.tags.FactionTags.ALL_FACTIONS).build();
    public static final Codec<FactionRestriction> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    RegistryCodecs.homogeneousList(FactionRegistries.Keys.FACTION).fieldOf("factions").forGetter(FactionRestriction::factions),
                    RegistryCodecs.homogeneousList(FactionRegistries.Keys.SKILL).optionalFieldOf("skills").forGetter(FactionRestriction::skills),
                    Codec.INT.optionalFieldOf("min_level").forGetter(FactionRestriction::minLevel),
                    ComponentSerialization.CODEC.optionalFieldOf("custom_message").forGetter(FactionRestriction::customMessage)
            ).apply(inst, FactionRestriction::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FactionRestriction> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(FactionRegistries.Keys.FACTION), FactionRestriction::factions,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(FactionRegistries.Keys.SKILL)), FactionRestriction::skills,
            ByteBufCodecs.optional(ByteBufCodecs.INT), FactionRestriction::minLevel,
            ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), FactionRestriction::customMessage,
            FactionRestriction::new
    );

    public static FactionRestriction get(ItemStack stack) {
        return stack.getOrDefault(FactionDataComponents.FACTION_RESTRICTION, ALL);
    }

    public FactionRestriction(HolderSet<IFaction<?>> factions) {
        this(factions, Optional.empty(), Optional.empty(), Optional.empty());
    }

    @SuppressWarnings("unchecked")
    public FactionRestriction(Holder<? extends IFaction<?>> faction) {
        this(HolderSet.direct((Holder<IFaction<?>>) faction));
    }

    @Override
    public FactionRestriction getFactionRestriction() {
        return this;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFaction<?>, Z extends Holder<T>> Item.Properties apply(Item.Properties properties, Z faction) {
        return new Builder(faction).apply(properties);
    }

    public static Item.Properties apply(Item.Properties properties, TagKey<IFaction<?>> faction) {
        return new Builder(faction).apply(properties);
    }

    @SuppressWarnings("unchecked")
    public static Item.Properties apply(Item.Properties properties, Holder<? extends IFaction<?>>... factions) {
        return new Builder(factions).apply(properties);
    }

    public static <T extends IFaction<?>, Z extends Holder<T>> boolean matchFaction(ItemStack stack, Z faction) {
        var restrictions = stack.getAllOfType(IFactionRestrictionProvider.class).map(IFactionRestrictionProvider::getFactionRestriction).filter(Objects::nonNull).toList();
        if (restrictions.isEmpty()) return true;

        for (IFactionRestriction restriction : restrictions) {
            if (!IFaction.contains(restriction.factions(), faction)) return false;
        }

        return true;
    }

    /**
     * Total check for all possibilities
     */
    public static boolean canUse(LivingEntity entity, ItemStack stack, boolean message) {
        var restrictions = stack.getAllOfType(IFactionRestrictionProvider.class).map(IFactionRestrictionProvider::getFactionRestriction).filter(Objects::nonNull).toList();
        if (restrictions.isEmpty()) return true;

        if (entity instanceof Player player) {
            return canUse(player, restrictions, message);
        } else {
            return canUse(entity, restrictions);
        }
    }

    /**
     * simple player check
     */
    public static boolean canUse(Player player, IFactionRestriction restrictions, boolean message) {
        return canUse(player, Collections.singletonList(restrictions), message);
    }

    /**
     * simple entity check
     */
    public static boolean canUse(LivingEntity player, IFactionRestriction restriction) {
        return canUse(player, Collections.singletonList(restriction));
    }

    /**
     * collection player check
     */
    public static boolean canUse(Player player, List<IFactionRestriction> restrictions, boolean message) {
        FactionPlayerHandler factionPlayerHandler = FactionPlayerHandler.get(player);

        for (IFactionRestriction restriction : restrictions) {
            Result result = restriction.canUse(factionPlayerHandler);
            if (!result.success()) {
                result.message().filter(x -> message).ifPresent(player::sendOverlayMessage);
                return false;
            }
        }

        return true;
    }

    /**
     * collection entity check
     */
    public static boolean canUse(LivingEntity player, List<IFactionRestriction> restrictions) {
        Holder<? extends IFaction<?>> faction = IFactionHelper.get().getFaction(player);

        for (IFactionRestriction restriction : restrictions) {
            if(!IFaction.contains(restriction.factions(), faction)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Result canUse(IFactionPlayerHandler player) {
        if (!IFaction.contains(factions, player.getFaction())) {
            return new Result(customMessage.or(() -> Optional.of(getFactionRestrictionMessage(factions))), false);
        }
        if (skills().isPresent() && player.getSkillHandler().map(s -> !s.areSkillsEnabled(skills().get().stream().toList())).orElse(true)) {
            return new Result(customMessage.or(() -> Optional.of(MESSAGE_MISSING_SKILLS)), false);
        }
        if (minLevel().isPresent() && player.getCurrentLevel() < minLevel().get()) {
            return new Result(customMessage.or(() -> Optional.of(MESSAGE_MISSING_LEVEL)), false);
        }
        return new Result(Optional.empty(), true);
    }

    public static void addTooltipIfExist(@Nullable Player player, ItemStack stack, Consumer<Component> tooltips) {
        if (player == null) {
            player = FactionsMod.proxy.getClientPlayer();
        }

        List<IFactionRestriction> restrictions = stack.getAllOfType(IFactionRestrictionProvider.class).map(IFactionRestrictionProvider::getFactionRestriction).filter(Objects::nonNull).toList();
        if (restrictions.isEmpty()) return;

        @Nullable
        FactionPlayerHandler factionPlayerHandler = player == null ? null : FactionPlayerHandler.get(player);

        tooltips.accept(Component.empty());
        tooltips.accept(Component.translatable("tooltip.factionapi.faction_specifics").withStyle(ChatFormatting.GRAY));

        var factionsOpt = restrictions.stream().map(s -> s.factions().stream().collect(Collectors.toSet())).reduce(Sets::intersection);
        var minLevelOpt = restrictions.stream().flatMap(x -> x.minLevel().stream()).max(Integer::compareTo);
        var skills = restrictions.stream().flatMap(x -> x.skills().stream()).flatMap(HolderSet::stream).collect(Collectors.toSet());

        factionsOpt.ifPresent(factions -> {
            Holder<? extends IFaction<?>> faction = factionPlayerHandler == null ? null : factionPlayerHandler.getFaction();
            factionsOpt.get().stream().map(x -> Component.literal(" ").append(x.value().getName().withStyle(style -> {
                if (faction == null) return style;
                return IFaction.is(x, faction) ? style.withColor(ChatFormatting.DARK_GREEN) : style.withColor(ChatFormatting.DARK_RED);
            }))).forEach(tooltips);
        });

        minLevelOpt.ifPresent(minLevel -> {
            tooltips.accept(Component.literal(" ").append(Component.translatable("tooltip.factionapi.required_level", String.valueOf(minLevel))).withStyle(style -> {
                if (factionPlayerHandler == null) return style;
                return factionPlayerHandler.getCurrentLevel() >= minLevel ? style.withColor(ChatFormatting.DARK_GREEN) : style.withColor(ChatFormatting.DARK_RED);
            }));
        });

        if (!skills.isEmpty()) {
            var skillHandler = factionPlayerHandler == null ? null : factionPlayerHandler.getSkillHandler().orElse(null);
            skills.stream().map(skill -> Component.translatable("tooltip.factionapi.required_skill", skill.value().getName().withStyle(style -> {
                if (skillHandler == null) return style;
                return skillHandler.isSkillEnabled(skill) ? style.withColor(ChatFormatting.DARK_GREEN) : style.withColor(ChatFormatting.DARK_RED);
            }))).forEach(tooltips);
        }

    }

    public static Component getFactionRestrictionMessage(IFaction<?> faction) {
        return getFactionRestrictionMessage(List.of(faction));
    }

    public static Component getFactionRestrictionMessage(HolderSet<IFaction<?>> factions) {
        return getFactionRestrictionMessage(factions.stream().map(Holder::value).toList());
    }

    public static Component getFactionRestrictionMessage(List<? extends IFaction<?>> factions) {
        IFaction<?> faction = factions.contains(Factions.NEUTRAL.get()) ? Factions.NEUTRAL.get() : factions.getFirst();
        Identifier factionLocation = ModRegistries.FACTIONS.getKey(faction);

        if (factionLocation != null) {
            return Component.translatable(getFactionMessageKey(factionLocation));
        }

        return MESSAGE_WRONG_FACTION;
    }

    public static String getFactionMessageKey(Identifier factionId) {
        return "message." + factionId.getNamespace() + ".restriction.faction." + factionId.getPath();
    }

    public static Builder builder(TagKey<IFaction<?>> tagKey) {
        return new Builder(tagKey);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFaction<?>, Z extends Holder<T>> Builder builder(Z faction) {
        return new Builder(faction);
    }

    public static class Builder {

        @Nullable
        private TagKey<IFaction<?>> factionTag;
        private final List<Holder<? extends IFaction<?>>> factionHolder = new ArrayList<>();
        @Nullable
        private TagKey<ISkill<?>> skillTag;
        private final List<Holder<ISkill<?>>> skillHolder = new ArrayList<>();
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private Optional<Integer> minLevel = Optional.empty();
        private Optional<Component> customMessage = Optional.empty();

        public Builder(TagKey<IFaction<?>> tagKey) {
            this.factionTag = tagKey;
        }

        @SuppressWarnings("unchecked")
        public Builder(Holder<? extends IFaction<?>>... faction) {
            this.factionHolder.addAll(Arrays.asList(faction));
        }

        public Builder skill(TagKey<ISkill<?>> skillTag) {
            this.skillTag = skillTag;
            return this;
        }

        public Builder skill(Holder<ISkill<?>> skill) {
            this.skillHolder.add(skill);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder skill(Holder<ISkill<?>>... skill) {
            this.skillHolder.addAll(Arrays.asList(skill));
            return this;
        }

        public Builder minLevel(int minLevel) {
            this.minLevel = Optional.of(minLevel);
            return this;
        }

        public Builder message(Component message) {
            this.customMessage = Optional.of(message);
            return this;
        }

        public FactionRestriction build() {
            HolderGetter<IFaction<?>> factions = BuiltInRegistries.acquireBootstrapRegistrationLookup(ModRegistries.FACTIONS);
            HolderGetter<ISkill<?>> skills = BuiltInRegistries.acquireBootstrapRegistrationLookup(ModRegistries.SKILLS);
            Preconditions.checkArgument((this.factionTag != null && this.factionHolder.isEmpty() )|| (this.factionTag == null && !this.factionHolder.isEmpty()), "You need to provide either a faction tag or a list of factions");
            Preconditions.checkArgument(!(this.skillTag != null && !this.skillHolder.isEmpty()), "You can only supply a skill tag or a list of skills or not skills at all");
            //noinspection unchecked,RedundantCast
            return new FactionRestriction(factionTag != null ? factions.getOrThrow(factionTag) : HolderSet.direct((List< ? extends Holder<IFaction<?>>>) (Object) factionHolder), skillTag != null ? Optional.of(skillTag).map(skills::getOrThrow) : !skillHolder.isEmpty() ? Optional.of(HolderSet.direct(skillHolder)) : Optional.empty(), minLevel, customMessage);
        }

        public Item.Properties apply(Item.Properties properties) {
            return properties.component(FactionDataComponents.FACTION_RESTRICTION, build());
        }
    }
}
