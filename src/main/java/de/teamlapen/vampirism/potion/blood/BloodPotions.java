package de.teamlapen.vampirism.potion.blood;


import com.google.common.collect.Lists;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.items.IBloodPotionCategory;
import de.teamlapen.vampirism.api.items.IBloodPotionEffect;
import de.teamlapen.vampirism.api.items.IBloodPotionRegistry;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.List;

public class BloodPotions {
    public static void register() {
        IBloodPotionRegistry registry = VampirismAPI.bloodPotionRegistry();

        //Positive------------------------------------------------
        //Normal body boosts
        IBloodPotionCategory normalBodyBoost = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_NORMAL_BODY_BOOSTS, false, "text.vampirism.blood_potion.category.normal_body_boost");
        normalBodyBoost.addItems(Items.APPLE, Items.COOKED_BEEF, Items.COOKED_PORKCHOP, Items.BAKED_POTATO, Items.BREAD);
        registry.registerPotionEffect("vampirism:speed", normalBodyBoost, false, MobEffects.SPEED, 30, new IBloodPotionEffect.SimpleRandomizer(600, 6000, 0));
        registry.registerPotionEffect("vampirism:jump", normalBodyBoost, false, MobEffects.JUMP_BOOST, 25, new IBloodPotionEffect.SimpleRandomizer(500, 5000, 0));
        registry.registerPotionEffect("vampirism:health", normalBodyBoost, false, MobEffects.HEALTH_BOOST, 25, new IBloodPotionEffect.SimpleRandomizer(500, 5000, 0));
        registry.registerPotionEffect("vampirism:resistance", normalBodyBoost, false, MobEffects.RESISTANCE, 30, new IBloodPotionEffect.SimpleRandomizer(600, 6000, 0));

        //Special body boosts
        IBloodPotionCategory specialBodyBoost = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_SPECIAL_BODY_BOOSTS, false, "text.vampirism.blood_potion.category.special_body_boost");
        specialBodyBoost.addItems(Items.GOLDEN_APPLE, Items.GOLDEN_CARROT);
        registry.registerPotionEffect("vampirism:specialSpeed", specialBodyBoost, false, MobEffects.SPEED, 15, new IBloodPotionEffect.SimpleRandomizer(600, 3000, 2));
        registry.registerPotionEffect("vampirism:specialJump", specialBodyBoost, false, MobEffects.JUMP_BOOST, 10, new IBloodPotionEffect.SimpleRandomizer(500, 2500, 2));
        registry.registerPotionEffect("vampirism:specialHealth", specialBodyBoost, false, MobEffects.HEALTH_BOOST, 10, new IBloodPotionEffect.SimpleRandomizer(500, 2500, 2));
        registry.registerPotionEffect("vampirism:specialResistance", specialBodyBoost, false, MobEffects.RESISTANCE, 15, new IBloodPotionEffect.SimpleRandomizer(600, 3000, 2));

        //Normal vampire skills
        IBloodPotionCategory normalVampireSkills = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_NORMAL_VAMPIRE_SKILLS, false, "text.vampirism.blood_potion.category.normal_vampire_skills");
        normalVampireSkills.addItems(ModItems.vampireFang, ModItems.bloodBottle, ModItems.itemCoffin);
        registry.registerPotionEffect("vampirism:nightVision", normalVampireSkills, false, MobEffects.NIGHT_VISION, 20, new IBloodPotionEffect.SimpleRandomizer(600, 6000, 0));

        //Special vampire skills
        IBloodPotionCategory specialVampireSkills = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_SPECIAL_VAMPIRE_SKILL, false, "text.vampirism.blood_potion.category.special_vampire_skills");
        specialVampireSkills.addItems(ModItems.pureBlood, Items.DIAMOND);
        registry.registerPotionEffect("vampirism:disguise", specialVampireSkills, false, ModPotions.disguiseAsVampire, 5, new IBloodPotionEffect.SimpleRandomizer(500, 2000, 0));

        //Negative--------------------------------------------------
        IBloodPotionCategory badOtherEffects = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_OTHERS, true, "text.vampirism.blood_potion.category.bad_others");
        registry.registerPotionEffect("vampirism:weakness", badOtherEffects, true, MobEffects.WEAKNESS, 30, new IBloodPotionEffect.SimpleRandomizer(600, 6000, 1));
        registry.registerPotionEffect("vampirism:shortNausea", badOtherEffects, true, MobEffects.NAUSEA, 20, new IBloodPotionEffect.SimpleRandomizer(500, 800, 0));
        registry.registerPotionEffect("vampirism:slowness", badOtherEffects, true, MobEffects.SLOWNESS, 30, new IBloodPotionEffect.SimpleRandomizer(600, 5000, 0));
        registry.registerPotionEffect("vampirism:hunger", badOtherEffects, true, MobEffects.HUNGER, 25, new IBloodPotionEffect.SimpleRandomizer(500, 5000, 0));
        registry.registerPotionEffect("vampirism:specialWeakness", badOtherEffects, true, MobEffects.WEAKNESS, 15, new IBloodPotionEffect.SimpleRandomizer(600, 3000, 2));
        registry.registerPotionEffect("vampirism:specialSlowness", badOtherEffects, true, MobEffects.SLOWNESS, 10, new IBloodPotionEffect.SimpleRandomizer(500, 2500, 2));
        registry.registerPotionEffect("vampirism:longNausea", badOtherEffects, true, MobEffects.NAUSEA, 2, new IBloodPotionEffect.SimpleRandomizer(1000, 2000, 2));

    }

    /**
     * Adds a tooltip to the given blood potion itemstack
     *
     * @param stack
     * @param tooltip
     */
    public static void addTooltip(ItemStack stack, List<String> tooltip) {

    }


    /**
     * Applies the blood potion's effects on the entity as long as he is an hunter
     *
     * @param stack
     * @param entity
     */
    public static void applyEffects(ItemStack stack, EntityLivingBase entity) {
        NBTTagCompound effectTag = stack.hasTagCompound() ? stack.getTagCompound().getCompoundTag("effects") : new NBTTagCompound();
        List<IBloodPotionEffect> effects = readEffectsFromNBT(effectTag);
        if (entity instanceof IHunterMob || entity instanceof EntityPlayer && FactionPlayerHandler.get((EntityPlayer) entity).isInFaction(VReference.HUNTER_FACTION)) {
            for (IBloodPotionEffect effect : effects) {
            }
        }

    }

    private static
    @Nonnull
    List<IBloodPotionEffect> readEffectsFromNBT(NBTTagCompound nbt) {
        List<IBloodPotionEffect> effects = Lists.newArrayList();
        for (String id : nbt.getKeySet()) {
            if (nbt.getBoolean(id)) {
                IBloodPotionEffect effect = VampirismAPI.bloodPotionRegistry().getEffectFromId(id);
                if (effect == null) {
                    VampirismMod.log.w("BloodPotions", "Cannot find effect with id %s", id);
                } else {
                    effects.add(effect);
                }
            }
            //TODO duration and amplifier
        }
        return effects;
    }

    private static void addEffects(ItemStack stack, List<IBloodPotionEffect> effects) {
        NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        NBTTagCompound effectTag = new NBTTagCompound();
        for (IBloodPotionEffect effect : effects) {
            effectTag.setBoolean(effect.getId(), true);
        }
        nbt.setTag("effects", effectTag);
        stack.setTagCompound(nbt);
    }
}
