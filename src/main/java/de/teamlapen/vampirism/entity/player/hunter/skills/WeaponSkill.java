package de.teamlapen.vampirism.entity.player.hunter.skills;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;


public class WeaponSkill extends DefaultSkill<IHunterPlayer> {
    private final String id;
    private final int u, v;
    private final String unloc;
    private String unlocDesc = null;

    /**
     * @param id          Lowercase
     * @param description If a description should be rendered
     * @param unloc       Unlocalized name
     */
    public WeaponSkill(String id, int u, int v, boolean description, String unloc) {
        this.id = id;
        this.u = u;
        this.v = v;
        this.unloc = unloc;
        if (description) {
            this.setUnlocDesc(unloc + ".desc");
        }
    }

    /**
     * Generates the unlocalized name from the id
     *
     * @param id          Lowercase
     * @param description If a description should be rendered
     */
    public WeaponSkill(String id, int u, int v, boolean description) {
        this(id, u, v, description, "text.vampirism.skill." + id);

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

    @Override
    public String getUnlocDescription() {
        return unlocDesc;
    }

    @Override
    public String getUnlocalizedName() {
        return unloc;
    }

    public void setUnlocDesc(String unlocDesc) {
        this.unlocDesc = unlocDesc;
    }
}
