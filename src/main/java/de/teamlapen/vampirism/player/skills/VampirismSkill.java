package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Extension of {@link DefaultSkill} with vampirism default unloc names/descriptions
 */
public abstract class VampirismSkill<T extends IFactionPlayer> extends DefaultSkill<T> {
    private Supplier<ITextComponent> description = () -> null;
    private Consumer<T> activate = (T player) -> {
    };
    private Consumer<T> deactivate = (T player) -> {
    };
    private String translationKey = null;

    public VampirismSkill(IPlayableFaction<T> faction) {
        super(faction);
    }

    @Override
    public ITextComponent getDescription() {
        return description.get();
    }

    @Override
    public String getTranslationKey() {
        return translationKey == null ? super.getTranslationKey() : translationKey;
    }

    /**
     * Enable description using "text.vampirism.skill."+getID()+".desc" as unloc key
     */
    public VampirismSkill<T> setHasDefaultDescription() {
        description = () -> new TranslationTextComponent(getTranslationKey() + ".desc");
        return this;
    }


    @Override
    protected void onEnabled(T player) {
        activate.accept(player);
        super.onEnabled(player);
    }

    @Override
    protected void onDisabled(T player) {
        deactivate.accept(player);
        super.onDisabled(player);
    }

    public VampirismSkill<T> setToggleActions(Consumer<T> activateIn, Consumer<T> deactivateIn) {
        this.activate = activateIn;
        this.deactivate = deactivateIn;
        return this;
    }

    public VampirismSkill<T> setTranslationKey(String translationKeyIn) {
        translationKey = translationKeyIn;
        return this;
    }

    public VampirismSkill<T> setDescription(Supplier<ITextComponent> descriptionIn) {
        this.description = descriptionIn;
        return this;
    }

    /**
     * Simple hunter skill implementation. Does nothing by itself
     */
    public static class SimpleHunterSkill extends VampirismSkill<IHunterPlayer> {

        /**
         * @param id   Registry name
         * @param desc Enable description using the default unlocalized key
         */
        public SimpleHunterSkill(ResourceLocation id, boolean desc) {
            super(VReference.HUNTER_FACTION);
            this.setRegistryName(id);
            if (desc) this.setHasDefaultDescription();
        }

        @Deprecated
        public SimpleHunterSkill(String id, boolean desc) {
            this(new ResourceLocation(REFERENCE.MODID, id), desc);
        }
    }


    /**
     * Simple vampire skill implementation. Does nothing by itself
     */
    public static class SimpleVampireSkill extends VampirismSkill<IVampirePlayer> {
        @Deprecated
        public SimpleVampireSkill(String id, boolean desc) {
            this(new ResourceLocation(REFERENCE.MODID, id), desc);
        }

        /**
         * @param id   Registry name
         * @param desc Enable description using the default unlocalized key
         */
        public SimpleVampireSkill(ResourceLocation id, boolean desc) {
            super(VReference.VAMPIRE_FACTION);
            this.setRegistryName(id);
            if (desc) setHasDefaultDescription();
        }
    }
}
