package de.teamlapen.vampirism.playervampire;

import java.util.ArrayList;
import java.util.List;

import de.teamlapen.vampirism.util.Logger;

import net.minecraft.entity.player.EntityPlayer;

public abstract class PlayerModifiers {

	public static interface Modifier{
		public void apply(int level);
		public void remove();
	}
	
	public static void applyModifiers(VampirePlayer vampire,List<Modifier> modifiers,EntityPlayer p){
		for(Modifier m:modifiers){
			m.remove();
		}
		modifiers.clear();
		modifiers.add(new SpeedModifier(p));
		for(Modifier m:modifiers){
			m.apply(vampire.getLevel());
		}
	}
	
	public static class SpeedModifier implements Modifier{
		private EntityPlayer player;
		private float changedSpeed;

		public SpeedModifier(EntityPlayer p){
			this.player=p;
			this.changedSpeed=0.0F;
		}

		@Override
		public void apply(int level) {
			if(level>5){
				changedSpeed=0.1F;
				player.capabilities.setPlayerWalkSpeed(player.capabilities.getWalkSpeed()+changedSpeed);
			}
			
		}

		@Override
		public void remove() {
			player.capabilities.setPlayerWalkSpeed(player.capabilities.getWalkSpeed()-changedSpeed);
			
		}
		
	}
	
	
}
