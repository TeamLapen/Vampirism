package de.teamlapen.vampirism.entity.player.skills;

import java.util.ArrayList;

import de.teamlapen.vampirism.util.Logger;

/**
 * Manages the vampire skills and their registration
 * @author maxanier
 *
 */
public class Skills {
	private final static ArrayList<ISkill> skills=new ArrayList<ISkill>();
	
	/**
	 * 
	 * @param s
	 * @return The assigned id
	 */
	public static int registerSkill(ISkill s){
		skills.add(s);
		return skills.size()-1;
	}
	
	public static int getSkillCount(){
		return skills.size();
	}
	
	/**
	 * Reset all activated timers to cooldown
	 * @param timers
	 */
	public static void resetOnDeath(int[] timers){
		for(int i=0;i<timers.length;i++){
			if(timers[i]>0){
				timers[i]=-skills.get(i).getCooldown();
			}
		}
	}
	
	/**
	 * Returns the skill with the given id, might return null if the skill doesn't exist
	 * @param i
	 * @return
	 */
	public static ISkill getSkill(int i){
		try {
			return skills.get(i);
		} catch (IndexOutOfBoundsException e) {
			Logger.e("Skills", "Skill with id "+i+" doesn't exist");
			return null;
		}
	}
	
	/**
	 * Register all default skills
	 */
	public static void registerDefaultSkills(){
		VampireLordSkill.ID=Skills.registerSkill(new VampireLordSkill());
	}
}
