package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.gui.client.GuiPieMenu;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.vampire.*;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Gui which is used to select vampire skills
 */
@SideOnly(Side.CLIENT)
public class GuiSelectSkill extends GuiPieMenu<IVampireSkill> {
    private final static ResourceLocation defaultIcons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/skills.png");
    private ISkillHandler skillHandler;
    /**
     * Fake skill which represents the cancel button
     */
    private IVampireSkill fakeSkill = new DefaultSkill(null) {
        @Override
        public int getMinLevel() {
            return 0;
        }

        @Override
        public int getCooldown() {
            return 0;
        }

        @Override
        public String getUnlocalizedName() {
            return "skill.vampirism.cancel";
        }

        @Override
        public boolean onActivated(IVampirePlayer vampire) {
            return true;
        }

        @Override
        public int getMinU() {
            return 16;
        }

        @Override
        public int getMinV() {
            return 0;
        }

    };

    public GuiSelectSkill() {
        super(2298478591L, "selectSkill");
    }

    @Override
    protected ResourceLocation getIconLoc(IVampireSkill item) {
        return item.getIconLoc() == null ? defaultIcons : item.getIconLoc();
    }

    @Override
    protected int getMinU(IVampireSkill item) {
        return item.getMinU();
    }

    @Override
    protected int getMinV(IVampireSkill item) {
        return item.getMinV();
    }

    @Override
    protected String getUnlocalizedName(IVampireSkill item) {
        return item.getUnlocalizedName();
    }

    @Override
    protected int getMenuKeyCode() {
        return ModKeys.getKeyCode(ModKeys.KEY.SKILL);
    }

    @Override
    protected void onElementSelected(IVampireSkill skill) {
        if (skill != fakeSkill) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.TOGGLESKILL, "" + SkillRegistry.getIdFromSkill(skill)));
        }
    }

    @Override
    protected void onGuiInit() {
        skillHandler = VampirePlayer.get(this.mc.thePlayer).getSkillHandler();
        elements.addAll(skillHandler.getAvailableSkills());
        elements.add(fakeSkill);
    }

    @Override
    protected void afterIconDraw(IVampireSkill p, int x, int y) {
        if (p == fakeSkill) return;
        // Draw usage indicator

        float active = skillHandler.getPercentageForSkill(p);
        if (active > 0) {

            float h = active * IS;
            this.drawGradientRect(x, (int) (y + h), x + IS, y + IS, 0xDDE0E000, 0x88E0E000);
        } else if (active < 0) {

            float h = (1F + (active)) * IS;
            this.drawGradientRect(x, (int) (y + h), x + IS, y + IS, 0x880E0E0E, 0xEE0E0E0E);
        }
    }
}
