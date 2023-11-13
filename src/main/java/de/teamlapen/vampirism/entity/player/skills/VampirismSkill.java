package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Extension of {@link DefaultSkill} with vampirism default unloc names/descriptions
 */
public abstract class VampirismSkill<T extends IFactionPlayer<T>> extends DefaultSkill<T> {
    private Supplier<Component> description = () -> null;
    private Consumer<T> activate = (T player) -> {
    };
    private Consumer<T> deactivate = (T player) -> {
    };

    public VampirismSkill() {
        super(2);
    }

    public VampirismSkill(boolean hasDescription) {
        this(2, hasDescription);
    }

    public VampirismSkill(int skillPointCost, boolean hasDescription) {
        super(skillPointCost);
        if (hasDescription) {
            this.setHasDefaultDescription();
        }
    }

    @Override
    public Component getDescription() {
        return description.get();
    }

    public @NotNull VampirismSkill<T> setDescription(Supplier<Component> descriptionIn) {
        this.description = descriptionIn;
        return this;
    }

    /**
     * Enable description using "text.vampirism.skill."+getID()+".desc" as unloc key
     */
    public @NotNull VampirismSkill<T> setHasDefaultDescription() {
        description = () -> Component.translatable(getTranslationKey() + ".desc");
        return this;
    }

    public @NotNull VampirismSkill<T> setToggleActions(Consumer<T> activateIn, Consumer<T> deactivateIn) {
        this.activate = activateIn;
        this.deactivate = deactivateIn;
        return this;
    }

    @Override
    protected void onDisabled(T player) {
        deactivate.accept(player);
        super.onDisabled(player);
    }

    @Override
    protected void onEnabled(T player) {
        activate.accept(player);
        super.onEnabled(player);
    }

    /**
     * Simple hunter skill implementation. Does nothing by itself
     */
    public static class SimpleHunterSkill extends VampirismSkill<IHunterPlayer> {

        /**
         * @param desc Enable description using the default unlocalized key
         */
        public SimpleHunterSkill(int skillPointCost, boolean desc) {
            super(skillPointCost, desc);
        }

        @NotNull
        @Override
        public Optional<IPlayableFaction<?>> getFaction() {
            return Optional.of(VReference.HUNTER_FACTION);
        }

        @Override
        public @NotNull ISkillType getType() {
            return SkillType.LEVEL;
        }
    }

    public static class LordHunterSkill extends SimpleHunterSkill {

        public LordHunterSkill(int skillPointCost, boolean desc) {
            super(skillPointCost, desc);
        }

        @Override
        public @NotNull ISkillType getType() {
            return SkillType.LORD;
        }
    }

    /**
     * Simple vampire skill implementation. Does nothing by itself
     */
    public static class SimpleVampireSkill extends VampirismSkill<IVampirePlayer> {

        public SimpleVampireSkill(int skillPointCost, boolean desc) {
            super(skillPointCost, desc);
        }

        @NotNull
        @Override
        public Optional<IPlayableFaction<?>> getFaction() {
            return Optional.of(VReference.VAMPIRE_FACTION);
        }

        @Override
        public @NotNull ISkillType getType() {
            return SkillType.LEVEL;
        }
    }

    public static class LordVampireSkill extends SimpleVampireSkill {


        public LordVampireSkill(int skillPointCost, boolean desc) {
            super(skillPointCost, desc);
        }

        @Override
        public @NotNull ISkillType getType() {
            return SkillType.LORD;
        }
    }
}
