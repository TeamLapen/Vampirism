package de.teamlapen.vampirism;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class ModPotion extends Potion{
	public static Potion sunscreen;
	public static Potion thirst;
	public static Potion saturation;
	
	public ModPotion(int id, boolean full_effectiv, int color) {
		super(id, full_effectiv, color);
	}
	
	public Potion setIconIndex(int par1, int par2)
	{
		super.setIconIndex(par1, par2);
		return this;
	}

	private static void increasePotionArraySize(){
		Potion[] potionTypes = null;

	    for (Field f : Potion.class.getDeclaredFields()) {
	        f.setAccessible(true);
	        try {
	            if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
	                Field modfield = Field.class.getDeclaredField("modifiers");
	                modfield.setAccessible(true);
	                modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

	                potionTypes = (Potion[])f.get(null);
	                final Potion[] newPotionTypes = new Potion[256];
	                System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
	                f.set(null, newPotionTypes);
	            }
	        } catch (Exception e) {
	        	Logger.e("ModPotion","COULDN'T INCREASE POTION ARRAY SIZE",e);
	        }
	    }

	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier)
    {
		if(this.id==thirst.id){
			if(entity instanceof EntityPlayer){
				VampirePlayer.get((EntityPlayer)entity).getBloodStats().addExhaustion(0.025F*(float)(amplifier+1));
			}
		}
    }
	
	@Override
	public boolean isReady(int duration, int amplifier){
		if(this.id==thirst.id){
			return true;
		}
		return false;
	}
	
	public static void init(){
		increasePotionArraySize();
		sunscreen=new ModPotion(40,false,345345).setIconIndex(7, 1).setPotionName("potion.vampirism:sunscreen");
		thirst=new ModPotion(41,false,859494).setIconIndex(1, 1).setPotionName("potion.vampirism:thirst");
		saturation=new ModPotion(42,false,850484).setIconIndex(2, 2).setPotionName("potion.vampirism:saturation");
	}
}
