package de.teamlapen.vampirism.potion.blood;


import com.google.common.collect.Lists;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IBloodPotionCategory;
import de.teamlapen.vampirism.api.items.IBloodPotionEffect;
import de.teamlapen.vampirism.api.items.IBloodPotionPropertyRandomizer;
import de.teamlapen.vampirism.api.items.IBloodPotionRegistry;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
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
        registry.registerPotionEffect("vampirism:specialSpeed", specialBodyBoost, false, MobEffects.SPEED, 15, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 1500, 2));
        registry.registerPotionEffect("vampirism:specialJump", specialBodyBoost, false, MobEffects.JUMP_BOOST, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(400, 1000, 2));
        registry.registerPotionEffect("vampirism:specialHealth", specialBodyBoost, false, MobEffects.HEALTH_BOOST, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(400, 1000, 2));
        registry.registerPotionEffect("vampirism:specialResistance", specialBodyBoost, false, MobEffects.RESISTANCE, 15, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 1500, 2));

        //Normal vampire skills
        IBloodPotionCategory normalVampireSkills = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_NORMAL_VAMPIRE_SKILLS, false, "text.vampirism.blood_potion.category.normal_vampire_skills");
        normalVampireSkills.addItems(ModItems.vampire_fang, ModItems.blood_bottle, ModItems.item_coffin);
        registry.registerPotionEffect("vampirism:nightVision", normalVampireSkills, false, MobEffects.NIGHT_VISION, 20, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 0));

        //Special vampire skills
        IBloodPotionCategory specialVampireSkills = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_SPECIAL_VAMPIRE_SKILL, false, "text.vampirism.blood_potion.category.special_vampire_skills");
        specialVampireSkills.addItems(ModItems.pure_blood);
        registry.registerPotionEffect("vampirism:disguise", specialVampireSkills, false, ModPotions.disguise_as_vampire, 5, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 1500, 0));
        registry.registerPotionEffect("vampirism:specialNightVision", specialVampireSkills, false, MobEffects.NIGHT_VISION, 20, new IBloodPotionPropertyRandomizer.SimpleRandomizer(1000, 24000, 0));


        //Special other effects
        IBloodPotionCategory specialOtherEffects = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_SPECIAL_OTHERS, false, "text.vampirism.blood_potion.category.special_other");
        specialOtherEffects.addItems(Items.DIAMOND);
        registry.registerPotionEffect("vampirism:invisibility", specialOtherEffects, false, MobEffects.INVISIBILITY, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 0));
        //Negative--------------------------------------------------
        IBloodPotionCategory badOtherEffects = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_OTHERS, true, "text.vampirism.blood_potion.category.bad_others");
        registry.registerPotionEffect("vampirism:weakness", badOtherEffects, true, MobEffects.WEAKNESS, 30, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 1));
        registry.registerPotionEffect("vampirism:shortNausea", badOtherEffects, true, MobEffects.NAUSEA, 20, new IBloodPotionPropertyRandomizer.SimpleRandomizer(300, 700, 0));
        registry.registerPotionEffect("vampirism:slowness", badOtherEffects, true, MobEffects.SLOWNESS, 30, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 5000, 0));
        registry.registerPotionEffect("vampirism:hunger", badOtherEffects, true, MobEffects.HUNGER, 25, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 5000, 0));
        registry.registerPotionEffect("vampirism:specialWeakness", badOtherEffects, true, MobEffects.WEAKNESS, 15, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 1500, 2));
        registry.registerPotionEffect("vampirism:specialSlowness", badOtherEffects, true, MobEffects.SLOWNESS, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 1000, 2));
        registry.registerPotionEffect("vampirism:longNausea", badOtherEffects, true, MobEffects.NAUSEA, 2, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 1000, 2));

    }

    /**
     * Adds a tooltip to the given blood potion itemstack
     */
    public static void addTooltip(ItemStack stack, List<String> tooltip, IHunterPlayer player) {

        ISkillHandler<IHunterPlayer> skillHandler = player.getSkillHandler();
        List<ConfiguredEffect> effects = stack.hasTagCompound() ? readEffectsFromNBT(stack.getTagCompound()) : Lists.newArrayList();
        Random identifyRandom = null;
        if (skillHandler.isSkillEnabled(HunterSkills.bloodPotion_identifySome)) {
            NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
            int seed;
            if (nbt.hasKey("ident_seed")) {
                seed = nbt.getInteger("ident_seed");
            } else {
                seed = stack.hashCode();
            }
            identifyRandom = new Random(seed);

        }
        for (ConfiguredEffect effect : effects) {

            String text;
            if (identifyRandom != null && identifyRandom.nextBoolean()) {
                text = effect.getEffect().getLocName(effect.properties);
            } else {
                text = UtilLib.translate("text.vampirism.unknown");

            }
            if (skillHandler.isSkillEnabled(HunterSkills.bloodPotion_goodOrBad)) {
                if (effect.getEffect().isBad()) {
                    text = TextFormatting.DARK_RED + text;
                } else {
                    text = TextFormatting.DARK_GREEN + text;
                }
            }

            tooltip.add(text);
        }
    }


    /**
     * Applies the blood potion's effects on the entity as long as he is an hunter
     */
    public static void applyEffects(ItemStack stack, EntityLivingBase entity) {
        if (!stack.hasTagCompound()) return;
        List<ConfiguredEffect> effects = readEffectsFromNBT(stack.getTagCompound());
        boolean flag = entity instanceof IHunterMob;
        float durationMult = 1;
        if (!flag && entity instanceof EntityPlayer) {
            IHunterPlayer hunterPlayer = HunterPlayer.get((EntityPlayer) entity);
            if (hunterPlayer.getLevel() > 0) {
                flag = true;
                if (hunterPlayer.getSkillHandler().isSkillEnabled(HunterSkills.bloodPotion_increaseDuration)) {
                    durationMult += 0.3;
                }
            }
        }
        if (flag) {
            for (ConfiguredEffect effect : effects) {
                effect.getEffect().onActivated(entity, effect.getProperties(), durationMult);
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
        Random rnd = crafter.getRepresentingPlayer().getRNG();
        ISkillHandler<IHunterPlayer> skillHandler = crafter.getSkillHandler();
        int good = rnd.nextInt(2) + 1;
        if (rnd.nextInt(10) == 0) good = 3;
        int bad;
        int badReductions = 0;
        if (skillHandler.isSkillEnabled(HunterSkills.bloodPotion_lessBad)) badReductions++;
        if (skillHandler.isSkillEnabled(HunterSkills.bloodPotion_lessBad2)) badReductions++;
        if (badReductions == 1) {
            bad = rnd.nextInt(10) == 0 ? 2 : 1;
        } else if (badReductions == 2) {
            bad = rnd.nextInt(2);
        } else {
            bad = (rnd.nextInt(10) == 0) ? 3 : rnd.nextInt(2) + 1;
        }
        int extra = 0;
        for (int i = 0; i < good + bad + extra; i++) {
            IBloodPotionEffect effect = registry.getRandomEffect(extraItem, i >= good + extra, rnd);
            boolean valid = true;
            for (ConfiguredEffect effect1 : effects) {
                if (!effect1.getEffect().canCoexist(effect)) {
                    extra = Math.min(extra + 1, 5);
                    valid = false;
                }
            }
            if (valid) effects.add(new ConfiguredEffect(effect, effect.getRandomProperties(rnd)));
        }

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
        if (!nbt.hasKey("ident_seed")) {
            int seed = stack.hashCode();
            nbt.setInteger("ident_seed", seed);
        }
        stack.setTagCompound(nbt);
    }

    /**
     * @return A localized hint about what the given extra stack might cause
     */
    public static
    @Nonnull
    List<String> getLocalizedCategoryHint(ItemStack extra) {
        IBloodPotionRegistry registry = VampirismAPI.bloodPotionRegistry();
        return registry.getLocCategoryDescForItem(extra);
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

        @Override
        public String toString() {
            return "ConfEffect{" +
                    "effect=" + effect +
                    ", properties=" + properties +
                    '}';
        }
    }
}


