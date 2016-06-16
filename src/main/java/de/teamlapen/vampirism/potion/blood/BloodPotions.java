package de.teamlapen.vampirism.potion.blood;


import com.google.common.collect.Lists;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.IBloodPotionCategory;
import de.teamlapen.vampirism.api.items.IBloodPotionEffect;
import de.teamlapen.vampirism.api.items.IBloodPotionPropertyRandomizer;
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
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BloodPotions {
    public static void register() {
        IBloodPotionRegistry registry = VampirismAPI.bloodPotionRegistry();

        //Positive------------------------------------------------
        //Normal body boosts
        IBloodPotionCategory normalBodyBoost = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_NORMAL_BODY_BOOSTS, false, "text.vampirism.blood_potion.category.normal_body_boost");
        normalBodyBoost.addItems(Items.APPLE, Items.COOKED_BEEF, Items.COOKED_PORKCHOP, Items.BAKED_POTATO, Items.BREAD);
        registry.registerPotionEffect("vampirism:speed", normalBodyBoost, false, MobEffects.SPEED, 30, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 0));
        registry.registerPotionEffect("vampirism:jump", normalBodyBoost, false, MobEffects.JUMP_BOOST, 25, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 5000, 0));
        registry.registerPotionEffect("vampirism:health", normalBodyBoost, false, MobEffects.HEALTH_BOOST, 25, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 5000, 0));
        registry.registerPotionEffect("vampirism:resistance", normalBodyBoost, false, MobEffects.RESISTANCE, 30, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 0));

        //Special body boosts
        IBloodPotionCategory specialBodyBoost = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_SPECIAL_BODY_BOOSTS, false, "text.vampirism.blood_potion.category.special_body_boost");
        specialBodyBoost.addItems(Items.GOLDEN_APPLE, Items.GOLDEN_CARROT);
        registry.registerPotionEffect("vampirism:specialSpeed", specialBodyBoost, false, MobEffects.SPEED, 15, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 3000, 2));
        registry.registerPotionEffect("vampirism:specialJump", specialBodyBoost, false, MobEffects.JUMP_BOOST, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 2500, 2));
        registry.registerPotionEffect("vampirism:specialHealth", specialBodyBoost, false, MobEffects.HEALTH_BOOST, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 2500, 2));
        registry.registerPotionEffect("vampirism:specialResistance", specialBodyBoost, false, MobEffects.RESISTANCE, 15, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 3000, 2));

        //Normal vampire skills
        IBloodPotionCategory normalVampireSkills = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_NORMAL_VAMPIRE_SKILLS, false, "text.vampirism.blood_potion.category.normal_vampire_skills");
        normalVampireSkills.addItems(ModItems.vampireFang, ModItems.bloodBottle, ModItems.itemCoffin);
        registry.registerPotionEffect("vampirism:nightVision", normalVampireSkills, false, MobEffects.NIGHT_VISION, 20, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 0));

        //Special vampire skills
        IBloodPotionCategory specialVampireSkills = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_SPECIAL_VAMPIRE_SKILL, false, "text.vampirism.blood_potion.category.special_vampire_skills");
        specialVampireSkills.addItems(ModItems.pureBlood, Items.DIAMOND);
        registry.registerPotionEffect("vampirism:disguise", specialVampireSkills, false, ModPotions.disguiseAsVampire, 5, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 2000, 0));

        //Negative--------------------------------------------------
        IBloodPotionCategory badOtherEffects = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_OTHERS, true, "text.vampirism.blood_potion.category.bad_others");
        registry.registerPotionEffect("vampirism:weakness", badOtherEffects, true, MobEffects.WEAKNESS, 30, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 1));
        registry.registerPotionEffect("vampirism:shortNausea", badOtherEffects, true, MobEffects.NAUSEA, 20, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 800, 0));
        registry.registerPotionEffect("vampirism:slowness", badOtherEffects, true, MobEffects.SLOWNESS, 30, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 5000, 0));
        registry.registerPotionEffect("vampirism:hunger", badOtherEffects, true, MobEffects.HUNGER, 25, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 5000, 0));
        registry.registerPotionEffect("vampirism:specialWeakness", badOtherEffects, true, MobEffects.WEAKNESS, 15, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 3000, 2));
        registry.registerPotionEffect("vampirism:specialSlowness", badOtherEffects, true, MobEffects.SLOWNESS, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 2500, 2));
        registry.registerPotionEffect("vampirism:longNausea", badOtherEffects, true, MobEffects.NAUSEA, 2, new IBloodPotionPropertyRandomizer.SimpleRandomizer(1000, 2000, 2));

    }

    /**
     * Adds a tooltip to the given blood potion itemstack
     *
     * @param stack
     * @param tooltip
     */
    public static void addTooltip(ItemStack stack, List<String> tooltip) {

        List<ConfiguredEffect> effects = stack.hasTagCompound() ? readEffectsFromNBT(stack.getTagCompound()) : Lists.<ConfiguredEffect>newArrayList();
        for (ConfiguredEffect effect : effects) {
            String text = "uknoasd";
            if (effect.getEffect().isBad()) {
                text = TextFormatting.DARK_RED + text;
            } else {
                text = TextFormatting.WHITE + text;
            }
            tooltip.add(text);
        }
    }


    /**
     * Applies the blood potion's effects on the entity as long as he is an hunter
     *
     * @param stack
     * @param entity
     */
    public static void applyEffects(ItemStack stack, EntityLivingBase entity) {
        if (!stack.hasTagCompound()) return;
        List<ConfiguredEffect> effects = readEffectsFromNBT(stack.getTagCompound());
        if (entity instanceof IHunterMob || entity instanceof EntityPlayer && FactionPlayerHandler.get((EntityPlayer) entity).isInFaction(VReference.HUNTER_FACTION)) {
            for (ConfiguredEffect effect : effects) {
                effect.getEffect().onActivated(entity, effect.getProperties());
            }
        }

    }

    private static
    @Nonnull
    List<ConfiguredEffect> readEffectsFromNBT(NBTTagCompound nbt) {
        List<ConfiguredEffect> effects = Lists.newArrayList();
        NBTTagCompound effectsTag = nbt.getCompoundTag("effects");
        for (String id : effectsTag.getKeySet()) {
            NBTTagCompound properties = effectsTag.getCompoundTag(id);
                IBloodPotionEffect effect = VampirismAPI.bloodPotionRegistry().getEffectFromId(id);
                if (effect == null) {
                    VampirismMod.log.w("BloodPotions", "Cannot find effect with id %s", id);
                } else {
                    effects.add(new ConfiguredEffect(effect, properties));
                }

        }
        return effects;
    }

    /**
     * Selects a random selection to the given potion stack considering the crafters hunter skills as well as the extra item
     */
    public static void chooseAndAddEffects(@Nonnull ItemStack stack, @Nonnull IHunterPlayer crafter, @Nullable ItemStack extraItem) {
        List<ConfiguredEffect> effects = Lists.newArrayList();
        IBloodPotionRegistry registry = VampirismAPI.bloodPotionRegistry();
        Random rng = crafter.getRepresentingPlayer().getRNG();
        IBloodPotionEffect effect = registry.getRandomEffect(extraItem, false, rng);
        effects.add(new ConfiguredEffect(effect, effect.getRandomProperties(rng)));
        addEffects(stack, effects);
    }

    /**
     * Write the given effects to the potion stack's nbt
     */
    private static void addEffects(ItemStack stack, List<ConfiguredEffect> effects) {
        NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        NBTTagCompound effectTag = new NBTTagCompound();
        for (ConfiguredEffect effect : effects) {
            effectTag.setTag(effect.getEffect().getId(), effect.getProperties());
        }
        nbt.setTag("effects", effectTag);
        stack.setTagCompound(nbt);
    }

    /**
     * Simply stores an effect together with it's property nbt tag
     */
    private static class ConfiguredEffect {
        private final IBloodPotionEffect effect;
        private final NBTTagCompound properties;

        private ConfiguredEffect(IBloodPotionEffect effect, NBTTagCompound properties) {
            this.effect = effect;
            this.properties = properties;
        }

        public IBloodPotionEffect getEffect() {
            return effect;
        }

        public NBTTagCompound getProperties() {
            return properties;
        }

    }
}


