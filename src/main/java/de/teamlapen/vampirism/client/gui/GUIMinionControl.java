package de.teamlapen.vampirism.client.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.KeyInputEventHandler;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.minions.DefaultMinionCommand;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.IMinionCommand;
import de.teamlapen.vampirism.entity.minions.MinionHelper;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.entity.player.skills.ISkill;
import de.teamlapen.vampirism.network.InputEventPacket;

public class GUIMinionControl extends GUIPieMenu {
	
	VampirePlayer player;
	IMinion minion;

	public GUIMinionControl() {
		super(2298478591L, "minionControl");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int getMenuKeyCode() {
		return KeyInputEventHandler.MINION_CONTROL.getKeyCode();
	}
	
	@Override
	protected void onElementSelected(int i) {
		int id = ((IMinionCommand) elements.get(i)).getId();
		if (id >= 0) {
			VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.MINION_CONTROL, id+(minion==null?"":","+minion.getRepresentingEntity().getEntityId())));
		}
	}

	@Override
	protected void onGuiInit() {
		player=VampirePlayer.get(this.mc.thePlayer);
		MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;
		if(mouseOver!=null&&mouseOver.entityHit!=null){
			minion=MinionHelper.getMinionFromEntity(mouseOver.entityHit);
		}
		
		if(minion!=null){
			elements.addAll(minion.getAvailableCommands());
			elements.add(new FakeCommand());
		}
		else{
			//TODO add general commands
		}
		

	}
	
	@Override
	public void updateScreen(){
		super.updateScreen();
		if(minion!=null&&minion.getRepresentingEntity().isDead){
			this.mc.displayGuiScreen(null);
		}
	}
	
	private class FakeCommand extends DefaultMinionCommand{
		public FakeCommand() {
			super(-1);
		}

		@Override
		public String getUnlocalizedName() {
			return "skill.vampirism.cancel";
		}

		@Override
		public int getMinU() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getMinV() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void onActivated() {
			
		}

		@Override
		public void onDeactivated() {
			
		}		
	}

}
