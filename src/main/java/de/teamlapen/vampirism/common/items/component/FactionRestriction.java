package de.teamlapen.vampirism.common.items.component;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.common.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public record FactionRestriction(HolderSet<IFaction<?>> factions, Optional<HolderSet<ISkill<?>>> skills, Optional<Integer> minLevel) {

    public static final FactionRestriction ALL = FactionRestriction.builder(ModFactionTags.ALL_FACTIONS).build();
    public static final Codec<FactionRestriction> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    RegistryCodecs.homogeneousList(VampirismRegistries.Keys.FACTION).fieldOf("factions").forGetter(FactionRestriction::factions),
                    RegistryCodecs.homogeneousList(VampirismRegistries.Keys.SKILL).optionalFieldOf("skills").forGetter(FactionRestriction::skills),
                    Codec.INT.optionalFieldOf("min_level").forGetter(FactionRestriction::minLevel)
            ).apply(inst, FactionRestriction::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, FactionRestriction> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderSet(VampirismRegistries.Keys.FACTION), FactionRestriction::factions,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(VampirismRegistries.Keys.SKILL)), FactionRestriction::skills,
            ByteBufCodecs.optional(ByteBufCodecs.INT), FactionRestriction::minLevel,
            FactionRestriction::new
    );

    public static FactionRestriction get(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FACTION_RESTRICTION, ALL);
    }

    public FactionRestriction(HolderSet<IFaction<?>> factions) {
        this(factions, Optional.empty(), Optional.empty());
    }

    public FactionRestriction(Holder<IFaction<?>> faction) {
        this(HolderSet.direct(faction));
    }

    public static <T extends IFaction<?>, Z extends Holder<T>> boolean matchFaction(ItemStack stack, Z faction) {
        FactionRestriction factionRestriction = stack.get(ModDataComponents.FACTION_RESTRICTION);
        if (factionRestriction != null) {
            return IFaction.contains(factionRestriction.factions(), faction);
        }
        return true;
    }

    public static Builder builder(TagKey<IFaction<?>> tagKey) {
        return new Builder(tagKey);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFaction<?>, Z extends Holder<T>> Builder builder(Z faction) {
        return new Builder((Holder<IFaction<?>>) faction);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFaction<?>, Z extends Holder<T>> Item.Properties apply(Z faction, Item.Properties properties) {
        return new Builder((Holder<IFaction<?>>) faction).apply(properties);
    }

    public static Item.Properties apply(TagKey<IFaction<?>> faction, Item.Properties properties) {
        return new Builder(faction).apply(properties);
    }

    public static boolean canUse(Player player, ItemStack stack, boolean message) {
        var result = canUse(player, stack, message, stack.get(ModDataComponents.FACTION_RESTRICTION));
        if (result && stack.has(ModDataComponents.APPLIED_OIL)) {
            result = canUse(player, stack, message, AppliedOilContent.HUNTER_RESTRICTION);
        }
        return result;
    }

    private static boolean canUse(Player player, ItemStack stack, boolean message, @Nullable FactionRestriction restriction) {
        if (restriction != null) {
            FactionPlayerHandler factionPlayerHandler = FactionPlayerHandler.get(player);
            Result result = restriction.canUse(factionPlayerHandler);
            if (message) {
                result.message().ifPresent(s -> player.displayClientMessage(s, true));
            }
            return result.success();
        }
        return true;
    }

    public static boolean canUse(LivingEntity entity, ItemStack stack, boolean message) {
        if (entity instanceof Player player) {
            return canUse(player, stack, message);
        } else {
            Holder<? extends IFaction<?>> faction = VampirismAPI.factionRegistry().getFaction(entity);
            FactionRestriction restriction = stack.get(ModDataComponents.FACTION_RESTRICTION);
            if (restriction != null && IFaction.contains(restriction.factions(), faction)) {
                return true;
            }
            return stack.has(ModDataComponents.APPLIED_OIL) && IFaction.contains(AppliedOilContent.HUNTER_RESTRICTION.factions(), faction);
        }
    }

    public static void addTooltipIfExist(ItemStack stack, List<Component> tooltip) {
        addTooltipIfExist(VampirismMod.proxy.getClientPlayer(), stack, tooltip);
    }

    public static void addTooltipIfExist(@Nullable Player player, ItemStack stack, List<Component> tooltip) {
        Stream<FactionRestriction> factionRestrictionStream = Stream.of(stack.get(ModDataComponents.FACTION_RESTRICTION));
        if (stack.has(ModDataComponents.APPLIED_OIL)) {
            factionRestrictionStream = Stream.concat(factionRestrictionStream, Stream.of(AppliedOilContent.HUNTER_RESTRICTION));
        }
        List<FactionRestriction> list = factionRestrictionStream.filter(Objects::nonNull).toList();
        if (!list.isEmpty()) {
            addTooltip(tooltip, player == null ? null : FactionPlayerHandler.get(player), list);
        }
    }

    public Result canUse(FactionPlayerHandler player) {
        if (!IFaction.contains(factions, player.getFaction())) {
            return Result.WRONG_FACTION;
        }
        if (skills().isPresent() && player.getSkillHandler().map(s -> !s.areSkillsEnabled(skills().get().stream().toList())).orElse(true)) {
            return Result.MISSING_SKILLS;
        }
        if (minLevel().isPresent() && player.getCurrentLevel() < minLevel().get()) {
            return Result.MISSING_LEVEL;
        }
        return Result.SUCCESS;
    }

    public static void addTooltip(List<Component> tooltips, @Nullable FactionPlayerHandler player, List<FactionRestriction> restrictions) {
        tooltips.add(Component.empty());
        tooltips.add(Component.translatable("text.vampirism.faction_specifics").withStyle(ChatFormatting.GRAY));
        tooltips.addAll(restrictions.stream().map(FactionRestriction::factions).flatMap(HolderSet::stream).distinct().map(faction -> {
            var color = player == null ? ChatFormatting.GRAY : restrictions.stream().allMatch(factions -> IFaction.contains(factions.factions(), player.getFaction())) ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED;
            return faction.value().getName().copy().withStyle(color);
        }).map(x -> Component.literal(" ").append(x)).toList());
        restrictions.stream().map(FactionRestriction::minLevel).flatMap(Optional::stream).mapToInt(s -> s).max().ifPresent(minLevel -> {
            var color = player == null ? ChatFormatting.GRAY : player.getCurrentLevel() >= minLevel ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED;
            tooltips.add(Component.literal(" ").append(Component.translatable("text.vampirism.required_level", String.valueOf(minLevel)).withStyle(color)));
        });
        restrictions.stream().map(FactionRestriction::skills).flatMap(Optional::stream).flatMap(HolderSet::stream).map(skill ->
        {
            var color = player == null ? ChatFormatting.GRAY : player.getSkillHandler().map(s -> s.isSkillEnabled(skill)).orElse(false) ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED;
            return Component.translatable("text.vampirism.required_skill", skill.value().getName()).withStyle(color);
        }).map(s -> Component.literal(" ").append(s)).forEach(tooltips::add);
    }

    public record Result(Optional<Component> message, boolean success) {
        public static final Result SUCCESS = new Result(Optional.empty(), true);
        public static final Result WRONG_FACTION = new Result(Optional.of(Component.translatable("text.vampirism.can_not_be_used_faction")), false);
        public static final Result MISSING_SKILLS = new Result(Optional.of(Component.translatable("text.vampirism.can_not_be_used_skill")), false);
        public static final Result MISSING_LEVEL = new Result(Optional.of(Component.translatable("text.vampirism.can_not_be_used_level")), false);
    }

    public static class Builder {

        private TagKey<IFaction<?>> factionTag;
        private final List<Holder<IFaction<?>>> factionHolder = new ArrayList<>();
        private TagKey<ISkill<?>> skillTag;
        private final List<Holder<ISkill<?>>> skillHolder = new ArrayList<>();
        private Integer minLevel;

        public Builder(TagKey<IFaction<?>> tagKey) {
            this.factionTag = tagKey;
        }

        @SuppressWarnings("unchecked")
        public Builder(Holder<IFaction<?>>... faction) {
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
            this.minLevel = minLevel;
            return this;
        }

        public FactionRestriction build() {
            HolderGetter<IFaction<?>> factions = BuiltInRegistries.acquireBootstrapRegistrationLookup(ModRegistries.FACTIONS);
            HolderGetter<ISkill<?>> skills = BuiltInRegistries.acquireBootstrapRegistrationLookup(ModRegistries.SKILLS);
            Preconditions.checkArgument((this.factionTag != null && this.factionHolder.isEmpty() )|| (this.factionTag == null && !this.factionHolder.isEmpty()), "You need to provide either a faction tag or a list of factions");
            Preconditions.checkArgument(!(this.skillTag != null && !this.skillHolder.isEmpty()), "You can only supply a skill tag or a list of skills or not skills at all");
            return new FactionRestriction(factionTag != null ? factions.getOrThrow(factionTag) : HolderSet.direct(factionHolder), skillTag != null ? Optional.of(skillTag).map(skills::getOrThrow) : !skillHolder.isEmpty() ? Optional.of(HolderSet.direct(skillHolder)) : Optional.empty(), Optional.ofNullable(minLevel));
        }

        public Item.Properties apply(Item.Properties properties) {
            return properties.component(ModDataComponents.FACTION_RESTRICTION, build());
        }
    }
}
