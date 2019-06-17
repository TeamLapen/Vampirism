package de.teamlapen.vampirism.player.skills;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;

/**
 * Simple skill that unlocks one action
 */
public class ActionSkill<T extends IFactionPlayer> extends VampirismSkill<T> {
    private static final ResourceLocation defaultIcons = new ResourceLocation(REFERENCE.MODID, "textures/gui/actions.png");
    private final IAction action;
    private final boolean customDescription;

    @Deprecated
    public ActionSkill(String id, IAction action) {
        this(new ResourceLocation("vampirism", id), action, false);

    }

    @Deprecated
    public ActionSkill(String id, IAction action, boolean customDescription) {
        this(new ResourceLocation("vampirism", id), action, customDescription);
    }

    public ActionSkill(ResourceLocation id, IAction action) {
        this(id, action, false);
    }

    /**
     * @param id                Registry id
     * @param action            The corresponding action
     * @param customDescription If false a generic "unlocks action" string is used
     */
    public ActionSkill(ResourceLocation id, IAction action, boolean customDescription) {
        super(action.getFaction());
        this.action = action;
        this.setRegistryName(id);
        this.customDescription = customDescription;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getIconLoc() {
        return action.getIconLoc() == null ? defaultIcons : action.getIconLoc();
    }

    @Override
    public ITextComponent getDescription() {
        return new TextComponentString(UtilLib.translate(customDescription ? "text.vampirism.skill." + this.getRegistryName().getPath() + ".desc" : "text.vampirism.skill.unlocks_action"));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getMinU() {
        return action.getMinU();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getMinV() {
        return action.getMinV();
    }

    @Override
    public String getTranslationKey() {
        return action.getTranslationKey();
    }

    @Override
    protected void getActions(Collection<IAction> list) {
        list.add(action);
    }
}
