package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.KeyInputEventHandler;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.FakeSkill;
import de.teamlapen.vampirism.entity.player.skills.ILastingSkill;
import de.teamlapen.vampirism.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.player.skills.Skills;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.IPieElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Used to select/activate/deactivate skills.
 * 
 * @author maxanier
 *
 */
@SideOnly(Side.CLIENT)
public class GUISelectSkill extends GUIPieMenu {

	private VampirePlayer player;

	public GUISelectSkill() {
		super(2298478591L, "selectSkill");
	}

	@Override
	protected void afterIconDraw(IPieElement p, int x, int y) {
		// Draw usage indicator
		ISkill s = (ISkill) p;
		int t = player.getSkillTime(s.getId());
		if (t > 0) {

			float h = ((t / (float) ((ILastingSkill) s).getDuration(player.getLevel()))) * IS;
			this.drawGradientRect(x, (int) (y + h), x + IS, y + IS, 0xDDE0E000, 0x88E0E000);
		} else if (t < 0) {

			float h = (1F - (-t / (float) s.getCooldown())) * IS;
			this.drawGradientRect(x, (int) (y + h), x + IS, y + IS, 0x880E0E0E, 0xEE0E0E0E);
		}
	}

	@Override
	protected int getMenuKeyCode() {
		return KeyInputEventHandler.SKILL.getKeyCode();
	}

	@Override
	protected void onElementSelected(int i) {
		int id = ((ISkill) elements.get(i)).getId();
		if (id >= 0) {
			VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.TOGGLESKILL, "" + id));
		}
	}

	@Override
	protected void onGuiInit() {
		player = VampirePlayer.get(this.mc.thePlayer);
		elements.addAll(Skills.getAvailableSkills(player));
		elements.add(new FakeSkill());
	}

}
