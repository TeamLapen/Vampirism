package de.teamlapen.vampirism.potion.blood;


import com.google.common.collect.Lists;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.items.IBloodPotionEffect;
import de.teamlapen.vampirism.api.items.IBloodPotionPropertyRandomizer;
import de.teamlapen.vampirism.api.items.IBloodPotionRegistry;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BloodPotions {
    private static final Logger LOGGER = LogManager.getLogger(BloodPotions.class);
    public static void register() {
        IBloodPotionRegistry registry = VampirismAPI.bloodPotionRegistry();

        //Positive------------------------------------------------
        //Normal body boosts
        //IBloodPotionCategory normalBodyBoost = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_NORMAL_BODY_BOOSTS, false, "text.vampirism.blood_potion.category.normal_body_boost");
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "speed"), IBloodPotionRegistry.CATEGORY_NORMAL_BODY_BOOSTS, false, MobEffects.SPEED, 30, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 0));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "jump"), IBloodPotionRegistry.CATEGORY_NORMAL_BODY_BOOSTS, false, MobEffects.JUMP_BOOST, 25, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 5000, 0));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "health"), IBloodPotionRegistry.CATEGORY_NORMAL_BODY_BOOSTS, false, MobEffects.HEALTH_BOOST, 25, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 5000, 0));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "resistance"), IBloodPotionRegistry.CATEGORY_NORMAL_BODY_BOOSTS, false, MobEffects.RESISTANCE, 30, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 0));
        registry.addItemsToCategory(false, IBloodPotionRegistry.CATEGORY_NORMAL_BODY_BOOSTS, Items.APPLE, Items.COOKED_BEEF, Items.COOKED_PORKCHOP, Items.BAKED_POTATO, Items.BREAD);

        //Special body boosts
        //IBloodPotionCategory specialBodyBoost = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_SPECIAL_BODY_BOOSTS, false, "text.vampirism.blood_potion.category.special_body_boost");
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "special_speed"), IBloodPotionRegistry.CATEGORY_SPECIAL_BODY_BOOSTS, false, MobEffects.SPEED, 15, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 1500, 2));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "special_jump"), IBloodPotionRegistry.CATEGORY_SPECIAL_BODY_BOOSTS, false, MobEffects.JUMP_BOOST, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(400, 1000, 2));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "special_health"), IBloodPotionRegistry.CATEGORY_SPECIAL_BODY_BOOSTS, false, MobEffects.HEALTH_BOOST, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(400, 1000, 2));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "special_resistance"), IBloodPotionRegistry.CATEGORY_SPECIAL_BODY_BOOSTS, false, MobEffects.RESISTANCE, 15, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 1500, 2));
        registry.addItemsToCategory(false, IBloodPotionRegistry.CATEGORY_SPECIAL_BODY_BOOSTS, Items.GOLDEN_APPLE, Items.GOLDEN_CARROT);

        //Normal vampire skills
        //IBloodPotionCategory normalVampireSkills = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_NORMAL_VAMPIRE_SKILLS, false, "text.vampirism.blood_potion.category.normal_vampire_skills");
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "night_vision"), IBloodPotionRegistry.CATEGORY_NORMAL_VAMPIRE_SKILLS, false, MobEffects.NIGHT_VISION, 20, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 0));
        registry.addItemsToCategory(false, IBloodPotionRegistry.CATEGORY_NORMAL_VAMPIRE_SKILLS, ModItems.vampire_fang, ModItems.blood_bottle, ModItems.item_coffin);
        //Special vampire skills
        //IBloodPotionCategory specialVampireSkills = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_SPECIAL_VAMPIRE_SKILL, false, "text.vampirism.blood_potion.category.special_vampire_skills");
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "disguise"), IBloodPotionRegistry.CATEGORY_SPECIAL_VAMPIRE_SKILL, false, ModPotions.disguise_as_vampire, 5, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 1500, 0));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "special_night_vision"), IBloodPotionRegistry.CATEGORY_SPECIAL_VAMPIRE_SKILL, false, MobEffects.NIGHT_VISION, 20, new IBloodPotionPropertyRandomizer.SimpleRandomizer(1000, 24000, 0));
        registry.addItemsToCategory(false, IBloodPotionRegistry.CATEGORY_SPECIAL_VAMPIRE_SKILL, ModItems.pure_blood_0, ModItems.pure_blood_1, ModItems.pure_blood_2, ModItems.pure_blood_3, ModItems.pure_blood_4);

        //Special other effects
        //IBloodPotionCategory specialOtherEffects = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_SPECIAL_OTHERS, false, "text.vampirism.blood_potion.category.special_other");
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "invisibility"), IBloodPotionRegistry.CATEGORY_SPECIAL_OTHERS, false, MobEffects.INVISIBILITY, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 0));
        registry.addItemsToCategory(false, IBloodPotionRegistry.CATEGORY_SPECIAL_OTHERS, Items.DIAMOND);
        //Negative--------------------------------------------------
        //IBloodPotionCategory badOtherEffects = registry.getOrCreateCategory(IBloodPotionRegistry.CATEGORY_OTHERS, true, "text.vampirism.blood_potion.category.bad_others");
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "weakness"), IBloodPotionRegistry.CATEGORY_OTHERS, true, MobEffects.WEAKNESS, 30, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 6000, 1));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "short_nausea"), IBloodPotionRegistry.CATEGORY_OTHERS, true, MobEffects.NAUSEA, 20, new IBloodPotionPropertyRandomizer.SimpleRandomizer(300, 700, 0));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "slowness"), IBloodPotionRegistry.CATEGORY_OTHERS, true, MobEffects.SLOWNESS, 30, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 5000, 0));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "hunger"), IBloodPotionRegistry.CATEGORY_OTHERS, true, MobEffects.HUNGER, 25, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 5000, 0));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "special_weakness"), IBloodPotionRegistry.CATEGORY_OTHERS, true, MobEffects.WEAKNESS, 15, new IBloodPotionPropertyRandomizer.SimpleRandomizer(600, 1500, 2));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "special_slowness"), IBloodPotionRegistry.CATEGORY_OTHERS, true, MobEffects.SLOWNESS, 10, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 1000, 2));
        registry.registerPotionEffect(new ResourceLocation(REFERENCE.MODID, "long_nausea"), IBloodPotionRegistry.CATEGORY_OTHERS, true, MobEffects.NAUSEA, 2, new IBloodPotionPropertyRandomizer.SimpleRandomizer(500, 1000, 2));

    }

    /**
     * Adds a tooltip to the given blood potion itemstack
     */
    public static void addTooltip(ItemStack stack, List<ITextComponent> tooltip, IHunterPlayer player) {

        ISkillHandler<IHunterPlayer> skillHandler = player.getSkillHandler();
        List<ConfiguredEffect> effects = stack.hasTag() ? readEffectsFromNBT(stack.getTag()) : Lists.newArrayList();
        Random identifyRandom = null;
        if (skillHandler.isSkillEnabled(HunterSkills.blood_potion_identify_some)) {
            NBTTagCompound nbt = stack.hasTag() ? stack.getTag() : new NBTTagCompound();
            int seed;
            if (nbt.contains("ident_seed")) {
                seed = nbt.getInt("ident_seed");
            } else {
                seed = stack.hashCode();
            }
            identifyRandom = new Random(seed);

        }
        for (ConfiguredEffect effect : effects) {

            ITextComponent text;
            if (identifyRandom != null && identifyRandom.nextBoolean()) {
                text = effect.getEffect().getLocName(effect.properties);
            } else {
                text = new TextComponentTranslation("text.vampirism.unknown");

            }
            if (skillHandler.isSkillEnabled(HunterSkills.blood_potion_good_or_bad)) {
                if (effect.getEffect().isBad()) {
                    text = text.applyTextStyle(TextFormatting.DARK_RED);
                } else {
                    text = text.applyTextStyle(TextFormatting.DARK_GREEN);
                }
            }

            tooltip.add(text);
        }
    }


    /**
     * Applies the blood potion's effects on the entity as long as he is an hunter
     */
    public static void applyEffects(ItemStack stack, EntityLivingBase entity) {
        if (!stack.hasTag()) return;
        List<ConfiguredEffect> effects = readEffectsFromNBT(stack.getTag());
        boolean flag = entity instanceof IHunterMob;
        float durationMult = 1;
        if (!flag && entity instanceof EntityPlayer) {
            IHunterPlayer hunterPlayer = HunterPlayer.get((EntityPlayer) entity);
            if (hunterPlayer.getLevel() > 0) {
                flag = true;
                if (hunterPlayer.getSkillHandler().isSkillEnabled(HunterSkills.blood_potion_duration)) {
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
        NBTTagCompound effectsTag = nbt.getCompound("effects");
        for (String id : effectsTag.keySet()) {
            NBTTagCompound properties = effectsTag.getCompound(id);
            IBloodPotionEffect effect = VampirismAPI.bloodPotionRegistry().getEffectFromId(new ResourceLocation((id)));
            if (effect == null) {
                LOGGER.warn("Cannot find effect with id %s", id);
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
        if (skillHandler.isSkillEnabled(HunterSkills.blood_potion_less_bad)) badReductions++;
        if (skillHandler.isSkillEnabled(HunterSkills.blood_potion_less_bad_2)) badReductions++;
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
        NBTTagCompound nbt = stack.hasTag() ? stack.getTag() : new NBTTagCompound();
        NBTTagCompound effectTag = new NBTTagCompound();
        for (ConfiguredEffect effect : effects) {
            effectTag.put(effect.getEffect().getId().toString(), effect.getProperties());
        }
        nbt.put("effects", effectTag);
        if (!nbt.contains("ident_seed")) {
            int seed = stack.hashCode();
            nbt.putInt("ident_seed", seed);
        }
        stack.setTag(nbt);
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


