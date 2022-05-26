package de.teamlapen.vampirism.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.data.recipebuilder.FinishedSkillNode;
import de.teamlapen.vampirism.data.recipebuilder.SkillNodeBuilder;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class SkillNodeGenerator implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator generator;

    public SkillNodeGenerator(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    @Override
    public void run(@Nonnull HashCache cache) {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        this.registerSkillNodes((node) -> {
            if (!set.add(node.getID())) {
                throw new IllegalStateException("Duplicate skill node " + node.getID());
            } else {
                this.saveSkillNode(cache, node.getSkillNodeJson(), path.resolve("data/" + node.getID().getNamespace() + "/vampirismskillnodes/" + node.getID().getPath() + ".json"));
            }
        });
    }

    @Nonnull
    @Override
    public String getName() {
        return "Vampirism skillnode generator";
    }

    protected void registerSkillNodes(Consumer<FinishedSkillNode> consumer) {
        //hunter
        {
            ResourceLocation skill2 = SkillNodeBuilder.hunter(modId("hunter"), HunterSkills.stake1.get()).build(consumer, modId("skill2"));
            ResourceLocation skill3 = SkillNodeBuilder.hunter(skill2, HunterSkills.weapon_table.get()).build(consumer, modId("skill3"));
            ResourceLocation skill4 = SkillNodeBuilder.hunter(skill3, HunterSkills.hunter_disguise.get()).build(consumer, modId("skill4"));

            ResourceLocation alchemy1 = SkillNodeBuilder.hunter(skill4, HunterSkills.basic_alchemy.get()).build(consumer, modId("alchemy1"));
            ResourceLocation alchemy2 = SkillNodeBuilder.hunter(alchemy1, HunterSkills.garlic_diffuser.get()).build(consumer, modId("alchemy2"));
            ResourceLocation alchemy3 = SkillNodeBuilder.hunter(alchemy2, HunterSkills.garlic_diffuser.get()).build(consumer, modId("alchemy3"));
            ResourceLocation alchemy4 = SkillNodeBuilder.hunter(alchemy3, HunterSkills.purified_garlic.get(), HunterSkills.holy_water_enhanced.get()).build(consumer, modId("alchemy4"));
            ResourceLocation alchemy5 = SkillNodeBuilder.hunter(alchemy4, HunterSkills.garlic_diffuser_improved.get()).build(consumer, modId("alchemy5"));
            ResourceLocation alchemy6 = SkillNodeBuilder.hunter(alchemy5, HunterSkills.hunter_awareness.get()).build(consumer, modId("alchemy6"));

            ResourceLocation potion1 = SkillNodeBuilder.hunter(skill4, HunterSkills.multitask_brewing.get()).build(consumer, modId("potion1"));
            ResourceLocation potion2 = SkillNodeBuilder.hunter(potion1, HunterSkills.durable_brewing.get(), HunterSkills.concentrated_brewing.get()).build(consumer, modId("potion2"));
            ResourceLocation potion3 = SkillNodeBuilder.hunter(potion2, HunterSkills.swift_brewing.get(), HunterSkills.efficient_brewing.get()).build(consumer, modId("potion3"));
            ResourceLocation potion4 = SkillNodeBuilder.hunter(potion3, HunterSkills.master_brewer.get()).build(consumer, modId("potion4"));
            ResourceLocation potion5 = SkillNodeBuilder.hunter(potion4, HunterSkills.potion_resistance.get()).build(consumer, modId("potion5"));
            ResourceLocation potion6 = SkillNodeBuilder.hunter(potion5, HunterSkills.concentrated_durable_brewing.get()).build(consumer, modId("potion6"));

            ResourceLocation weapon1 = SkillNodeBuilder.hunter(skill4, HunterSkills.hunter_attack_speed.get(), HunterSkills.hunter_attack_damage.get()).build(consumer, modId("weapon1"));
            ResourceLocation weapon2 = SkillNodeBuilder.hunter(weapon1, HunterSkills.double_crossbow.get()).build(consumer, modId("weapon2"));
            ResourceLocation weapon3 = SkillNodeBuilder.hunter(weapon2, HunterSkills.hunter_attack_speed_advanced.get(), HunterSkills.enhanced_weapons.get()).build(consumer, modId("weapon3"));
            ResourceLocation weapon4 = SkillNodeBuilder.hunter(weapon3, HunterSkills.enhanced_armor.get()).build(consumer, modId("weapon4"));
            ResourceLocation weapon5 = SkillNodeBuilder.hunter(weapon4, HunterSkills.tech_weapons.get()).build(consumer, modId("weapon5"));
            ResourceLocation weapon6 = SkillNodeBuilder.hunter(weapon5, HunterSkills.stake2.get()).build(consumer, modId("weapon6"));
        }

        //vampire
        {
            ResourceLocation skill2 = SkillNodeBuilder.vampire(modId("vampire"), VampireSkills.night_vision.get()).build(consumer, modId("skill2"));
            ResourceLocation skill3 = SkillNodeBuilder.vampire(skill2, VampireSkills.vampire_regeneration.get()).build(consumer, modId("skill3"));
            ResourceLocation skill4 = SkillNodeBuilder.vampire(skill3, VampireSkills.bat.get()).build(consumer, modId("skill4"));

            ResourceLocation util1 = SkillNodeBuilder.vampire(skill4, VampireSkills.summon_bats.get()).build(consumer, modId("util1"));
            ResourceLocation util15 = SkillNodeBuilder.vampire(util1, VampireSkills.hissing.get()).build(consumer, modId("util15"));
            ResourceLocation util2 = SkillNodeBuilder.vampire(util1, VampireSkills.less_sundamage.get(), VampireSkills.water_resistance.get()).build(consumer, modId("util2"));
            ResourceLocation util3 = SkillNodeBuilder.vampire(util2, VampireSkills.less_blood_thirst.get()).build(consumer, modId("util3"));
            ResourceLocation util4 = SkillNodeBuilder.vampire(util3, VampireSkills.vampire_disguise.get()).build(consumer, modId("util4"));
            ResourceLocation util5 = SkillNodeBuilder.vampire(util4, VampireSkills.half_invulnerable.get()).build(consumer, modId("util5"));
            ResourceLocation util6 = SkillNodeBuilder.vampire(util5, VampireSkills.vampire_invisibility.get()).build(consumer, modId("util6"));

            ResourceLocation offensive1 = SkillNodeBuilder.vampire(skill4, VampireSkills.vampire_rage.get()).build(consumer, modId("offensive1"));
            ResourceLocation offensive2 = SkillNodeBuilder.vampire(offensive1, VampireSkills.advanced_biter.get()).build(consumer, modId("offensive2"));
            ResourceLocation offensive3 = SkillNodeBuilder.vampire(offensive2, VampireSkills.sword_finisher.get()).build(consumer, modId("offensive3"));
            ResourceLocation offensive4 = SkillNodeBuilder.vampire(offensive3, VampireSkills.dark_blood_projectile.get()).build(consumer, modId("offensive4"));
            ResourceLocation offensive5 = SkillNodeBuilder.vampire(offensive4, VampireSkills.blood_charge.get()).build(consumer, modId("offensive5"));
            ResourceLocation offensive6 = SkillNodeBuilder.vampire(offensive5, VampireSkills.freeze.get()).build(consumer, modId("offensive6"));

            ResourceLocation defensive1 = SkillNodeBuilder.vampire(skill4, VampireSkills.sunscreen.get()).build(consumer, modId("defensive1"));
            ResourceLocation defensive2 = SkillNodeBuilder.vampire(defensive1, VampireSkills.vampire_attack_speed.get(), VampireSkills.vampire_speed.get()).build(consumer, modId("defensive2"));
            ResourceLocation defensive3 = SkillNodeBuilder.vampire(defensive2, VampireSkills.blood_vision.get()).build(consumer, modId("defensive3"));
            ResourceLocation defensive4 = SkillNodeBuilder.vampire(defensive3, VampireSkills.blood_vision_garlic.get()).build(consumer, modId("defensive4"));
            ResourceLocation defensive5 = SkillNodeBuilder.vampire(defensive4, VampireSkills.vampire_attack_damage.get(), VampireSkills.vampire_jump.get()).build(consumer, modId("defensive5"));
            ResourceLocation defensive6 = SkillNodeBuilder.vampire(defensive5, VampireSkills.neonatal_decrease.get(), VampireSkills.dbno_duration.get()).build(consumer, modId("defensive6"));
            ResourceLocation defensive7 = SkillNodeBuilder.vampire(defensive6, VampireSkills.teleport.get()).build(consumer, modId("defensive7"));
        }

    }

    private ResourceLocation modId(String string) {
        return new ResourceLocation(REFERENCE.MODID, string);
    }

    private void saveSkillNode(HashCache cache, JsonObject nodeJson, Path path) {
        try {
            String s = GSON.toJson(nodeJson);
            @SuppressWarnings("UnstableApiUsage")
            String s1 = SHA1.hashUnencodedChars(s).toString();
            if (!Objects.equals(cache.getHash(path), s1) || !Files.exists(path)) {
                Files.createDirectories(path.getParent());

                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
                    bufferedWriter.write(s);
                }
            }
            cache.putNew(path, s1);
        } catch (IOException ioExeption) {
            LOGGER.error("Couldn't save skill node {}", path, ioExeption);
        }
    }


}
