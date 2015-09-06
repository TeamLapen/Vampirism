package de.teamlapen.vampirism;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ModPotion extends Potion {
	public static Potion sunscreen;
	public static Potion thirst;
	public static Potion saturation;
	public static Potion sanguinare;

	private static void increasePotionArraySize() {
		Potion[] potionTypes = null;

		for (Field f : Potion.class.getDeclaredFields()) {
			f.setAccessible(true);
			try {
				if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
					Field modfield = Field.class.getDeclaredField("modifiers");
					modfield.setAccessible(true);
					modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

					potionTypes = (Potion[]) f.get(null);
					final Potion[] newPotionTypes = new Potion[256];
					System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
					f.set(null, newPotionTypes);
				}
			} catch (Exception e) {
				Logger.e("ModPotion",e, "COULDN'T INCREASE POTION ARRAY SIZE");
			}
		}

	}

	public static void preInit() {
		increasePotionArraySize();
		sunscreen = new ModPotion(Configs.potion_id_sunscreen, false, 345345).setIconIndex(7, 1).setPotionName("potion.vampirism.sunscreen");
		thirst = new ModPotion(Configs.potion_id_thirst, false, 859494).setIconIndex(1, 1).setPotionName("potion.vampirism.thirst");
		saturation = new ModPotion(Configs.potion_id_saturation, false, 850484).setIconIndex(2, 2).setPotionName("potion.vampirism.saturation");
		sanguinare = new ModPotion(Configs.potion_id_sanguinare, false, 0x6A0888).setIconIndex(7, 1).setPotionName("potion.vampirism.sanguinare")
				.func_111184_a(SharedMonsterAttributes.attackDamage, "22663B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, 0);
	}

	public ModPotion(int id, boolean full_effectiv, int color) {
		super(id, full_effectiv, color);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		if (this.id == thirst.id) {
			return true;
		}
		return false;
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		if (this.id == thirst.id) {
			if (entity instanceof EntityPlayer) {
				VampirePlayer.get((EntityPlayer) entity).getBloodStats().addExhaustion(0.010F * (amplifier + 1));
			}
		}
	}
	
	/**
	 * Checks if all potions were successfully registered, otherwise it returns a String which contains the failed ones.
	 * @return Null if everything is OK
	 */
	public static String checkPotions(){
		String r="";
		if(!sunscreen.equals(Potion.potionTypes[sunscreen.id])){
			r+="sunscreen, ";
		}
		if(!thirst.equals(Potion.potionTypes[thirst.id])){
			r+="thirst, ";
		}
		if(!saturation.equals(Potion.potionTypes[saturation.id])){
			r+="saturation, ";
		}
		if(!sanguinare.equals(Potion.potionTypes[sanguinare.id])){
			r+="sanguinare, ";
		}
		if(r.isEmpty())return null;
		return r;
	}

	@Override
	public Potion setIconIndex(int par1, int par2) {
		super.setIconIndex(par1, par2);
		return this;
	}
}
