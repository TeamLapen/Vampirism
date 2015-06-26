package de.teamlapen.vampirism.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.KeyInputEventHandler;
import de.teamlapen.vampirism.entity.minions.DefaultMinionCommand;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.MinionHelper;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.DefaultPieElement;
import de.teamlapen.vampirism.util.IPieElement;

public class GUIMinionControl extends GUIPieMenu {

	private class FakeCommand extends DefaultMinionCommand {
		public FakeCommand() {
			super(-1);
		}

		@Override
		public int getMinU() {
			return 16;
		}

		@Override
		public int getMinV() {
			return 0;
		}

		@Override
		public String getUnlocalizedName() {
			return "skill.vampirism.cancel";
		}

		@Override
		public void onActivated() {

		}

		@Override
		public void onDeactivated() {

		}
	}
	VampirePlayer player;
	IMinion minion;
	int active = -1;

	final float[] green = new float[] { 0, 1, 0 };

	public GUIMinionControl() {
		super(2298478591L, "minionControl");
	}

	@Override
	public float[] getColor(IPieElement e) {
		if (minion != null && e.getId() == this.active) {
			return green;
		}
		if (e instanceof DefaultPieElement) {
			return ((DefaultPieElement) e).getColor();
		}
		return super.getColor(e);
	}

	@Override
	protected int getMenuKeyCode() {
		return KeyInputEventHandler.MINION_CONTROL.getKeyCode();
	}

	@Override
	protected void onElementSelected(int i) {
		int id = (elements.get(i)).getId();
		if (id >= 0) {
			VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.MINION_CONTROL, id + (minion == null ? "" : "," + minion.getRepresentingEntity().getEntityId())));
		}
	}

	@Override
	protected void onGuiInit() {
		player = VampirePlayer.get(this.mc.thePlayer);
		MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
		if (mouseOver != null && mouseOver.entityHit != null) {
			minion = MinionHelper.getMinionFromEntity(mouseOver.entityHit);
		}

		if (minion != null) {
			elements.addAll(minion.getAvailableCommands());
			active = minion.getActiveCommandId();
		} else {
			elements.addAll(player.getAvailableMinionCalls());
		}
		elements.add(new FakeCommand());

	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (minion != null && minion.getRepresentingEntity().isDead) {
			this.mc.displayGuiScreen(null);
		}
	}

}
