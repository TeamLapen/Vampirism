package de.teamlapen.vampirism.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.data.recipebuilder.FinishedSkillNode;
import de.teamlapen.vampirism.data.recipebuilder.SkillNodeBuilder;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class SkillNodeGenerator implements IDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator generator;

    public SkillNodeGenerator(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        this.registerSkillNodes((node)-> {
            if(!set.add(node.getID())) {
                throw new IllegalStateException("Dublicate skill node " + node.getID());
            } else {
                this.saveSkillNode(cache,node.getSkillNodeJson(),path.resolve("data/"+node.getID().getNamespace() + "/vampirismskillnodes/" + node.getID().getPath() + ".json"));
            }
        });
    }

    private void saveSkillNode(DirectoryCache cache, JsonObject nodeJson, Path path) {
        try {
            String s = GSON.toJson(nodeJson);
            @SuppressWarnings("UnstableApiUsage")
            String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
            if(!Objects.equals(cache.getPreviousHash(path),s1) || !Files.exists(path)) {
                Files.createDirectories(path.getParent());

                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
                    bufferedWriter.write(s);
                }
            }
            cache.recordHash(path,s1);
        }catch (IOException ioExeption) {
            LOGGER.error("Couldn't save skill node {}",path,ioExeption);
        }
    }

    @Override
    public String getName() {
        return "Vampirism skillnode generator";
    }

    protected void registerSkillNodes(Consumer<FinishedSkillNode> consumer){
        //hunter
        {
            ResourceLocation skill2 = SkillNodeBuilder.hunter(modId("hunter"), HunterSkills.stake1).build(consumer, modId("skill2"));
            ResourceLocation skill3 = SkillNodeBuilder.hunter(skill2, HunterSkills.hunter_attack_speed).build(consumer, modId("skill3"));
            ResourceLocation skill4 = SkillNodeBuilder.hunter(skill3, HunterSkills.hunter_disguise).build(consumer, modId("skill4"));

            ResourceLocation alchemy1 = SkillNodeBuilder.hunter(skill4, HunterSkills.basic_alchemy).build(consumer, modId("alchemy1"));
            ResourceLocation alchemy2 = SkillNodeBuilder.hunter(alchemy1, HunterSkills.garlic_beacon).build(consumer, modId("alchemy2"));
            ResourceLocation alchemy3 = SkillNodeBuilder.hunter(alchemy2, HunterSkills.garlic_beacon).build(consumer, modId("alchemy3"));
            ResourceLocation alchemy4 = SkillNodeBuilder.hunter(alchemy3, HunterSkills.purified_garlic, HunterSkills.holy_water_enhanced).build(consumer, modId("alchemy4"));
            ResourceLocation alchemy5 = SkillNodeBuilder.hunter(alchemy4, HunterSkills.garlic_beacon_improved).build(consumer, modId("alchemy5"));
            ResourceLocation alchemy6 = SkillNodeBuilder.hunter(alchemy5, HunterSkills.hunter_awareness).build(consumer, modId("alchemy6"));

            ResourceLocation blood1 = SkillNodeBuilder.hunter(skill4, HunterSkills.blood_potion_table).build(consumer, modId("blood1"));
            ResourceLocation blood2 = SkillNodeBuilder.hunter(blood1, HunterSkills.blood_potion_less_bad, HunterSkills.blood_potion_good_or_bad).build(consumer, modId("blood2"));
            ResourceLocation blood3 = SkillNodeBuilder.hunter(blood2, HunterSkills.blood_potion_faster_crafting, HunterSkills.blood_potion_category_hint).build(consumer, modId("blood3"));
            ResourceLocation blood4 = SkillNodeBuilder.hunter(blood3, HunterSkills.blood_potion_duration).build(consumer, modId("blood4"));
            ResourceLocation blood5 = SkillNodeBuilder.hunter(blood4, HunterSkills.blood_potion_portable_crafting).build(consumer, modId("blood5"));
            ResourceLocation blood6 = SkillNodeBuilder.hunter(blood5, HunterSkills.blood_potion_less_bad_2, HunterSkills.blood_potion_identify_some).build(consumer, modId("blood6"));

            ResourceLocation weapon1 = SkillNodeBuilder.hunter(skill4, HunterSkills.weapon_table).build(consumer, modId("weapon1"));
            ResourceLocation weapon2 = SkillNodeBuilder.hunter(weapon1, HunterSkills.hunter_attack_speed_advanced, HunterSkills.double_crossbow).build(consumer, modId("weapon2"));
            ResourceLocation weapon3 = SkillNodeBuilder.hunter(weapon2, HunterSkills.enhanced_weapons, HunterSkills.enhanced_crossbow).build(consumer, modId("weapon3"));
            ResourceLocation weapon4 = SkillNodeBuilder.hunter(weapon3, HunterSkills.enhanced_armor).build(consumer, modId("weapon4"));
            ResourceLocation weapon5 = SkillNodeBuilder.hunter(weapon4, HunterSkills.tech_weapons).build(consumer, modId("weapon5"));
            ResourceLocation weapon6 = SkillNodeBuilder.hunter(weapon5, HunterSkills.stake2).build(consumer, modId("weapon6"));
        }

        //vampire
        {
            ResourceLocation skill2 = SkillNodeBuilder.vampire(modId("vampire"), VampireSkills.night_vision).build(consumer, modId("skill2"));
            ResourceLocation skill3 = SkillNodeBuilder.vampire(skill2, VampireSkills.vampire_regeneration).build(consumer, modId("skill3"));
            ResourceLocation skill4 = SkillNodeBuilder.vampire(skill3, VampireSkills.bat).build(consumer, modId("skill4"));

            ResourceLocation util1 = SkillNodeBuilder.vampire(skill4,VampireSkills.summon_bats).build(consumer,modId("util1"));
            ResourceLocation util2 = SkillNodeBuilder.vampire(util1,VampireSkills.less_sundamage,VampireSkills.water_resistance).build(consumer,modId("util2"));
            ResourceLocation util3 = SkillNodeBuilder.vampire(util2,VampireSkills.less_blood_thirst).build(consumer,modId("util3"));
            ResourceLocation util4 = SkillNodeBuilder.vampire(util3,VampireSkills.vampire_disguise).build(consumer,modId("util4"));
            ResourceLocation util5 = SkillNodeBuilder.vampire(util4,VampireSkills.half_invulnerable).build(consumer,modId("util5"));
            ResourceLocation util6 = SkillNodeBuilder.vampire(util5,VampireSkills.vampire_invisibility).build(consumer,modId("util6"));

            ResourceLocation offensive1 = SkillNodeBuilder.vampire(skill4,VampireSkills.vampire_rage).build(consumer,modId("offensive1"));
            ResourceLocation offensive2 = SkillNodeBuilder.vampire(offensive1,VampireSkills.advanced_biter).build(consumer,modId("offensive2"));
            ResourceLocation offensive3 = SkillNodeBuilder.vampire(offensive2,VampireSkills.sword_finisher).build(consumer,modId("offensive3"));
            ResourceLocation offensive4 = SkillNodeBuilder.vampire(offensive3,VampireSkills.dark_blood_projectile).build(consumer,modId("offensive4"));
            ResourceLocation offensive5 = SkillNodeBuilder.vampire(offensive4,VampireSkills.blood_charge).build(consumer,modId("offensive5"));
            ResourceLocation offensive6 = SkillNodeBuilder.vampire(offensive5,VampireSkills.freeze).build(consumer,modId("offensive6"));

            ResourceLocation defensive1 = SkillNodeBuilder.vampire(skill4,VampireSkills.sunscreen).build(consumer,modId("defensive1"));
            ResourceLocation defensive2 = SkillNodeBuilder.vampire(defensive1,VampireSkills.vampire_jump,VampireSkills.vampire_speed).build(consumer,modId("defensive2"));
            ResourceLocation defensive3 = SkillNodeBuilder.vampire(defensive2,VampireSkills.blood_vision).build(consumer,modId("defensive3"));
            ResourceLocation defensive4 = SkillNodeBuilder.vampire(defensive3,VampireSkills.creeper_avoided).build(consumer,modId("defensive4"));
            ResourceLocation defensive5 = SkillNodeBuilder.vampire(defensive4,VampireSkills.vampire_forest_fog,VampireSkills.blood_vision_garlic).build(consumer,modId("defensive5"));
            ResourceLocation defensive6 = SkillNodeBuilder.vampire(defensive5,VampireSkills.teleport).build(consumer,modId("defensive6"));

        }

    }

    private ResourceLocation modId(String string) {
        return new ResourceLocation(REFERENCE.MODID, string);
    }


}
