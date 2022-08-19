package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.ThreadSafeAPI;
import de.teamlapen.vampirism.api.entity.factions.*;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public class FactionRegistry implements IFactionRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Integer, Predicate<LivingEntity>> predicateMap = new HashMap<>();
    private @Nullable List<Faction<?>> temp = new CopyOnWriteArrayList<>(); //Copy on write is costly, but we only expect very few elements anyway
    private Faction<?>[] allFactions;
    private PlayableFaction<?>[] playableFactions;

    /**
     * Finishes registrations during InterModProcessEvent
     */
    public void finish() {
        allFactions = temp.toArray(new Faction[0]);
        temp = null;
        List<PlayableFaction<?>> temp2 = new ArrayList<>();
        for (Faction<?> allFaction : allFactions) {
            if (allFaction instanceof PlayableFaction) {
                temp2.add((PlayableFaction<?>) allFaction);
            }
        }
        playableFactions = temp2.toArray(new PlayableFaction[0]);
    }

    @Override
    @Nullable
    public IFaction<?> getFaction(Entity entity) {
        if (entity instanceof IFactionEntity) {
            return ((IFactionEntity) entity).getFaction();
        } else if (entity instanceof Player) {
            return VampirismPlayerAttributes.get((Player) entity).faction;
        }
        return null;
    }

    @Nullable
    @Override
    public IFaction<?> getFactionByID(ResourceLocation id) {
        if (allFactions == null) {
            return null;
        }
        for (IFaction<?> f : allFactions) {
            if (f.getID().equals(id)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public Faction<?>[] getFactions() {
        return allFactions;
    }

    @Override
    public PlayableFaction<?>[] getPlayableFactions() {
        return playableFactions;
    }

    @Override
    public Predicate<LivingEntity> getPredicate(@NotNull IFaction<?> thisFaction, boolean ignoreDisguise) {

        return getPredicate(thisFaction, true, true, true, ignoreDisguise, null);
    }

    @Override
    public Predicate<LivingEntity> getPredicate(@NotNull IFaction<?> thisFaction, boolean player, boolean mob, boolean neutralPlayer, boolean ignoreDisguise, @Nullable IFaction<?> otherFaction) {
        int key = 0;
        if (otherFaction != null) {
            int id = otherFaction.hashCode();
            if (id > 63) {
                LOGGER.warn("Faction id over 64, predicates won't work");
            }
            key |= ((id & 63) << 10);
        }
        if (player) {
            key |= (1 << 9);
        }
        if (mob) {
            key |= (1 << 8);
        }
        if (neutralPlayer) {
            key |= (1 << 7);
        }
        if (ignoreDisguise) {
            key |= (1 << 6);
        }
        int id = thisFaction.hashCode();
        if (id > 64) {
            LOGGER.warn("Faction id over 64, predicates won't work");
        }
        key |= id & 63;
        Predicate<LivingEntity> predicate;
        if (predicateMap.containsKey(key)) {
            predicate = predicateMap.get(key);
        } else {
            predicate = new FactionPredicate(thisFaction, player, mob, neutralPlayer, ignoreDisguise, otherFaction);
            predicateMap.put(key, predicate);
        }
        return predicate;
    }

    @ThreadSafeAPI
    private void addFaction(@NotNull Faction<?> faction) {
        if (temp == null) {
            throw new IllegalStateException(String.format("[Vampirism]You have to register factions during InterModEnqueueEvent. (%s)", faction.getID()));
        } else {
            temp.add(faction);
        }
    }

    @Override
    public <T extends IFactionEntity> @NotNull IFactionBuilder<T> createFaction(ResourceLocation id, Class<T> entityInterface) {
        if (!UtilLib.isNonNull(id, entityInterface)) {
            throw new IllegalArgumentException("[Vampirism] Parameter for faction cannot be null");
        }
        return new FactionBuilder<>(id, entityInterface);
    }

    @Override
    public <T extends IFactionPlayer<T>> @NotNull IPlayableFactionBuilder<T> createPlayableFaction(ResourceLocation id, Class<T> entityInterface, NonNullSupplier<Capability<T>> playerCapabilitySupplier) {
        if (!UtilLib.isNonNull(id, entityInterface, playerCapabilitySupplier)) {
            throw new IllegalArgumentException("[Vampirism] Parameters for faction cannot be null");
        }
        return new PlayableFactionBuilder<>(id, entityInterface, playerCapabilitySupplier);
    }

    public class FactionBuilder<T extends IFactionEntity> implements IFactionBuilder<T> {

        protected final ResourceLocation id;
        protected final Class<T> entityInterface;
        protected int color = Color.WHITE.getRGB();
        protected boolean hostileTowardsNeutral;
        protected final FactionVillageBuilder villageFactionData = new FactionVillageBuilder();
        protected @Nullable TextColor chatColor;
        protected String name;
        protected String namePlural;

        FactionBuilder(ResourceLocation id, Class<T> entityInterface) {
            this.id = id;
            this.entityInterface = entityInterface;
        }

        @Override
        public IFactionBuilder<T> color(int color) {
            this.color = color;
            return this;
        }

        @Override
        public IFactionBuilder<T> chatColor(TextColor color) {
            this.chatColor = color;
            return this;
        }

        @Override
        public IFactionBuilder<T> chatColor(@NotNull ChatFormatting color) {
            if (!color.isColor()) {
                throw new IllegalArgumentException("Parameter must be a color");
            }
            this.chatColor = TextColor.fromLegacyFormat(color);
            return this;
        }

        @Override
        public IFactionBuilder<T> hostileTowardsNeutral() {
            this.hostileTowardsNeutral = true;
            return this;
        }

        @Override
        public IFactionBuilder<T> village(@NotNull Consumer<IFactionVillageBuilder> villageBuilder) {
            villageBuilder.accept(this.villageFactionData);
            return this;
        }

        @Override
        public IFactionBuilder<T> name(@NotNull String nameKey) {
            this.name = nameKey;
            return this;
        }

        @Override
        public IFactionBuilder<T> namePlural(@NotNull String namePluralKey) {
            this.namePlural = namePluralKey;
            return this;
        }

        @Override
        public @NotNull IFaction<T> register() {
            Faction<T> faction = new Faction<>(this);
            addFaction(faction);
            return faction;
        }
    }

    public class PlayableFactionBuilder<T extends IFactionPlayer<T>> extends FactionBuilder<T> implements IPlayableFactionBuilder<T> {

        protected final NonNullSupplier<Capability<T>> playerCapabilitySupplier;
        protected int highestLevel = 1;
        protected int highestLordLevel = 0;
        protected BiFunction<Integer, Boolean, Component> lordTitleFunction;
        protected Function<IRefinementItem.AccessorySlotType, IRefinementItem> refinementItemBySlot;
        protected boolean hasLordSkills = false;

        public PlayableFactionBuilder(ResourceLocation id, Class<T> entityInterface, NonNullSupplier<Capability<T>> playerCapabilitySupplier) {
            super(id, entityInterface);
            this.playerCapabilitySupplier = playerCapabilitySupplier;
        }

        @Override
        public IPlayableFactionBuilder<T> color(int color) {
            return (IPlayableFactionBuilder<T>) super.color(color);
        }

        @Override
        public IPlayableFactionBuilder<T> hostileTowardsNeutral() {
            return (IPlayableFactionBuilder<T>) super.hostileTowardsNeutral();
        }

        @Override
        public @NotNull IPlayableFactionBuilder<T> highestLevel(int highestLevel) {
            this.highestLevel = highestLevel;
            return this;
        }

        public @NotNull PlayableFactionBuilder<T> lordLevel(int highestLordLevel) {
            this.highestLordLevel = highestLordLevel;
            return this;
        }

        public @NotNull PlayableFactionBuilder<T> lordTitle(@NotNull BiFunction<Integer, Boolean, Component> lordTitleFunction) {
            this.lordTitleFunction = lordTitleFunction;
            return this;
        }

        public @NotNull PlayableFactionBuilder<T> enableLordSkills(boolean enabled) {
            this.hasLordSkills = enabled;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> village(@NotNull Consumer<IFactionVillageBuilder> villageBuilder) {
            return (IPlayableFactionBuilder<T>) super.village(villageBuilder);
        }

        @Override
        public @NotNull IPlayableFactionBuilder<T> refinementItems(@NotNull Function<IRefinementItem.AccessorySlotType, IRefinementItem> refinementItemBySlot) {
            this.refinementItemBySlot = refinementItemBySlot;
            return this;
        }

        @Override
        public IPlayableFactionBuilder<T> chatColor(@NotNull TextColor color) {
            return (IPlayableFactionBuilder<T>) super.chatColor(color);
        }

        @Override
        public IPlayableFactionBuilder<T> chatColor(@NotNull ChatFormatting color) {
            return (IPlayableFactionBuilder<T>) super.chatColor(color);
        }

        @Override
        public IPlayableFactionBuilder<T> name(@NotNull String nameKey) {
            return (IPlayableFactionBuilder<T>) super.name(nameKey);
        }

        @Override
        public IPlayableFactionBuilder<T> namePlural(@NotNull String namePluralKey) {
            return (IPlayableFactionBuilder<T>) super.namePlural(namePluralKey);
        }

        @Override
        public @NotNull ILordPlayerBuilder<T> lord() {
            return new LordPlayerBuilder<>(this);
        }

        @Override
        public @NotNull IPlayableFaction<T> register() {
            PlayableFaction<T> faction = new PlayableFaction<>(this);
            addFaction(faction);
            return faction;
        }
    }

    public static class LordPlayerBuilder<T extends IFactionPlayer<T>> implements ILordPlayerBuilder<T> {

        protected final PlayableFactionBuilder<T> factionBuilder;
        protected int maxLevel = 0;
        protected BiFunction<Integer, Boolean, Component> lordTitleFunction;
        protected boolean lordSkillsEnabled;

        public LordPlayerBuilder(PlayableFactionBuilder<T> factionBuilder) {
            this.factionBuilder = factionBuilder;
        }

        @Override
        public @NotNull LordPlayerBuilder<T> lordLevel(int level) {
            this.maxLevel = level;
            return this;
        }

        @Override
        public @NotNull LordPlayerBuilder<T> lordTitle(@NotNull BiFunction<Integer, Boolean, Component> lordTitleFunction) {
            this.lordTitleFunction = lordTitleFunction;
            return this;
        }

        @Override
        public @NotNull ILordPlayerBuilder<T> enableLordSkills() {
            this.lordSkillsEnabled = true;
            return this;
        }

        public IPlayableFactionBuilder<T> build() {
            if (this.lordTitleFunction == null) {
                this.lordTitleFunction = (a, b) -> Component.literal("Lord " + a);
            }
            return this.factionBuilder.lordTitle(this.lordTitleFunction).lordLevel(this.maxLevel).enableLordSkills(this.lordSkillsEnabled);
        }
    }

}
