package de.teamlapen.vampirism.entity.player;

public class PlayerAbilities {
	public final float nightVision;
	
	private PlayerAbilities(float nv){
		nightVision=nv;
	}
	
	public static PlayerAbilities getPlayerAbilities(float level){
		float nv= (level==0.0F ? 0 : 1.0F-(3/level));
		nv=(nv<0.0F?0.0F:nv);
		return new PlayerAbilities(nv);
	}
}
