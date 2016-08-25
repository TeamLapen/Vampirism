package de.teamlapen.vampirism.player.skills;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Extension of {@link DefaultSkill} with vampirism default unloc names/descriptions
 */
public abstract class VampirismSkill<T extends ISkillPlayer> extends DefaultSkill<T> {
    private String description = null;


    @Override
    public ResourceLocation getIconLoc() {
        return null;
    }

    @Override
    public String getLocalizedDescription() {
        return description == null ? null : UtilLib.translate(description);
    }

    @Override
    public String getUnlocalizedName() {
        return "text.vampirism.skill." + getID();
    }

    /**
     * Enable description using "text.vampirism.skill."+getID()+".desc" as unloc key
     */
    public void setHasDefaultDescription() {
        description = "text.vampirism.skill." + getID() + ".desc";
    }

    /**
     * Simple hunter skill implementation. Does nothing by itself
     */
    public static class SimpleHunterSkill extends VampirismSkill<IHunterPlayer> {
        private final String id;
        private final int u, v;

        /**
         * @param id   Lowercase id
         * @param desc Enable description using the default unlocalized key
         */
        public SimpleHunterSkill(String id, int u, int v, boolean desc) {
            this.id = id;
            this.u = u;
            this.v = v;
            if (desc) this.setHasDefaultDescription();
        }

        @Override
        public String getID() {
            return id;
        }

        @Override
        public int getMinU() {
            return u;
        }

        @Override
        public int getMinV() {
            return v;
        }
    }


    /**
     * Simple vampire skill implementation. Does nothing by itself
     */
    public static class SimpleVampireSkill extends VampirismSkill<IVampirePlayer> {
        private final String id;
        private final int u, v;

        /**
         * @param id   Lowercase id
         * @param desc Enable description using the default unlocalized key
         */
        public SimpleVampireSkill(String id, int u, int v, boolean desc) {
            this.id = id;
            this.u = u;
            this.v = v;
            if (desc) setHasDefaultDescription();
        }

        @Override
        public String getID() {
            return id;
        }

        @Override
        public int getMinU() {
            return u;
        }

        @Override
        public int getMinV() {
            return v;
        }
    }
}
