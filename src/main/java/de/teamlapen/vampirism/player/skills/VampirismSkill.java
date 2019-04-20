package de.teamlapen.vampirism.player.skills;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Extension of {@link DefaultSkill} with vampirism default unloc names/descriptions
 */
public abstract class VampirismSkill<T extends IFactionPlayer> extends DefaultSkill<T> {
    private String description = null;

    public VampirismSkill(IPlayableFaction<T> faction) {
        super(faction);
    }


    @SideOnly(Side.CLIENT)
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
        return "text.vampirism.skill." + getRegistryName().getPath();
    }

    /**
     * Enable description using "text.vampirism.skill."+getID()+".desc" as unloc key
     */
    public void setHasDefaultDescription() {
        description = "text.vampirism.skill." + getRegistryName().getPath() + ".desc";
    }

    /**
     * Simple hunter skill implementation. Does nothing by itself
     */
    public static class SimpleHunterSkill extends VampirismSkill<IHunterPlayer> {
        private final int u, v;

        /**
         * @param id   Registry name
         * @param desc Enable description using the default unlocalized key
         */
        public SimpleHunterSkill(ResourceLocation id, int u, int v, boolean desc) {
            super(VReference.HUNTER_FACTION);
            this.setRegistryName(id);
            this.u = u;
            this.v = v;
            if (desc) this.setHasDefaultDescription();
        }

        @Deprecated
        public SimpleHunterSkill(String id, int u, int v, boolean desc) {
            this(new ResourceLocation("vampirism", id), u, v, desc);
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
        private final int u, v;


        @Deprecated
        public SimpleVampireSkill(String id, int u, int v, boolean desc) {
            this(new ResourceLocation("vampirism", id), u, v, desc);
        }

        /**
         * @param id   Registry name
         * @param desc Enable description using the default unlocalized key
         */
        public SimpleVampireSkill(ResourceLocation id, int u, int v, boolean desc) {
            super(VReference.VAMPIRE_FACTION);
            this.setRegistryName(id);
            this.u = u;
            this.v = v;
            if (desc) setHasDefaultDescription();
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
