package de.teamlapen.vampirism.player.skills;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Extension of {@link DefaultSkill} with vampirism default unloc names/descriptions
 */
public abstract class VampirismSkill<T extends IFactionPlayer> extends DefaultSkill<T> {
    private String description = null;
    private String translationKey;

    public VampirismSkill(IPlayableFaction<T> faction) {
        super(faction);
    }

    @Override
    public ITextComponent getDescription() {
        return description == null ? null : new TranslationTextComponent(description);
    }

//   @Override
//    public String getTranslationKey() {
//        if (this.translationKey == null) {
//            this.translationKey = Util.makeTranslationKey("skill", getRegistryName());
//        }
//        return this.translationKey;
//    }

    /**
     * Enable description using "text.vampirism.skill."+getID()+".desc" as unloc key
     */
    public void setHasDefaultDescription() {
        description = getTranslationKey() + ".desc";
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
            this(new ResourceLocation("vampirism", id), desc);
        }
    }


    /**
     * Simple vampire skill implementation. Does nothing by itself
     */
    public static class SimpleVampireSkill extends VampirismSkill<IVampirePlayer> {
        @Deprecated
        public SimpleVampireSkill(String id, boolean desc) {
            this(new ResourceLocation("vampirism", id), desc);
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
