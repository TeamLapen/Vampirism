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
    public void run(DirectoryCache cache) {
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

    @Override
    public String getName() {
        return "Vampirism skillnode generator";
    }

    protected void registerSkillNodes(Consumer<FinishedSkillNode> consumer) {
        //hunter
        {
            ResourceLocation skill2 = SkillNodeBuilder.hunter(modId("hunter"), HunterSkills.STAKE1.get()).build(consumer, modId("skill2"));
            ResourceLocation skill3 = SkillNodeBuilder.hunter(skill2, HunterSkills.WEAPON_TABLE.get()).build(consumer, modId("skill3"));
            ResourceLocation skill4 = SkillNodeBuilder.hunter(skill3, HunterSkills.HUNTER_DISGUISE.get()).build(consumer, modId("skill4"));

            ResourceLocation alchemy1 = SkillNodeBuilder.hunter(skill4, HunterSkills.BASIC_ALCHEMY.get()).build(consumer, modId("alchemy1"));
            ResourceLocation alchemy2 = SkillNodeBuilder.hunter(alchemy1, HunterSkills.GARLIC_BEACON.get()).build(consumer, modId("alchemy2"));
            ResourceLocation alchemy3 = SkillNodeBuilder.hunter(alchemy2, HunterSkills.CRUCIFIX_WIELDER.get()).build(consumer, modId("alchemy3"));
            ResourceLocation alchemy4 = SkillNodeBuilder.hunter(alchemy3, HunterSkills.PURIFIED_GARLIC.get(), HunterSkills.ENHANCED_BLESSING.get()).build(consumer, modId("alchemy4"));
            ResourceLocation alchemy5 = SkillNodeBuilder.hunter(alchemy4, HunterSkills.GARLIC_BEACON_IMPROVED.get(), HunterSkills.ULTIMATE_CRUCIFIX.get()).build(consumer, modId("alchemy5"));
            ResourceLocation alchemy6 = SkillNodeBuilder.hunter(alchemy5, HunterSkills.HUNTER_AWARENESS.get()).build(consumer, modId("alchemy6"));

            ResourceLocation potion1 = SkillNodeBuilder.hunter(skill4, HunterSkills.MULTITASK_BREWING.get()).build(consumer, modId("potion1"));
            ResourceLocation potion2 = SkillNodeBuilder.hunter(potion1, HunterSkills.DURABLE_BREWING.get(), HunterSkills.CONCENTRATED_BREWING.get()).build(consumer, modId("potion2"));
            ResourceLocation potion3 = SkillNodeBuilder.hunter(potion2, HunterSkills.SWIFT_BREWING.get(), HunterSkills.EFFICIENT_BREWING.get()).build(consumer, modId("potion3"));
            ResourceLocation potion4 = SkillNodeBuilder.hunter(potion3, HunterSkills.MASTER_BREWER.get()).build(consumer, modId("potion4"));
            ResourceLocation potion5 = SkillNodeBuilder.hunter(potion4, HunterSkills.POTION_RESISTANCE.get()).build(consumer, modId("potion5"));
            ResourceLocation potion6 = SkillNodeBuilder.hunter(potion5, HunterSkills.CONCENTRATED_DURABLE_BREWING.get()).build(consumer, modId("potion6"));

            ResourceLocation weapon1 = SkillNodeBuilder.hunter(skill4, HunterSkills.HUNTER_ATTACK_SPEED.get(), HunterSkills.HUNTER_ATTACK_DAMAGE.get()).build(consumer, modId("weapon1"));
            ResourceLocation weapon2 = SkillNodeBuilder.hunter(weapon1, HunterSkills.DOUBLE_CROSSBOW.get()).build(consumer, modId("weapon2"));
            ResourceLocation weapon3 = SkillNodeBuilder.hunter(weapon2, HunterSkills.HUNTER_ATTACK_SPEED_ADVANCED.get(), HunterSkills.ENHANCED_WEAPONS.get()).build(consumer, modId("weapon3"));
            ResourceLocation weapon4 = SkillNodeBuilder.hunter(weapon3, HunterSkills.ENHANCED_ARMOR.get()).build(consumer, modId("weapon4"));
            ResourceLocation weapon5 = SkillNodeBuilder.hunter(weapon4, HunterSkills.TECH_WEAPONS.get()).build(consumer, modId("weapon5"));
            ResourceLocation weapon6 = SkillNodeBuilder.hunter(weapon5, HunterSkills.STAKE2.get()).build(consumer, modId("weapon6"));

            ResourceLocation lord_2 = SkillNodeBuilder.hunter(modId("hunter_lord"), HunterSkills.HUNTER_MINION_STATS_INCREASE.get()).build(consumer, modId("lord_2"));
            ResourceLocation lord_3 = SkillNodeBuilder.hunter(modId("hunter_lord"), HunterSkills.HUNTER_LORD_SPEED.get(), HunterSkills.HUNTER_LORD_ATTACK_SPEED.get()).build(consumer, modId("lord_3"));
            ResourceLocation lord_4 = SkillNodeBuilder.hunter(modId("hunter_lord"), HunterSkills.HUNTER_MINION_COLLECT.get()).build(consumer, modId("lord_4"));
            ResourceLocation lord_5 = SkillNodeBuilder.hunter(modId("hunter_lord"), HunterSkills.HUNTER_MINION_RECOVERY.get()).build(consumer, modId("lord_5"));
        }

        //vampire
        {
            ResourceLocation skill2 = SkillNodeBuilder.vampire(modId("vampire"), VampireSkills.NIGHT_VISION.get()).build(consumer, modId("skill2"));
            ResourceLocation skill3 = SkillNodeBuilder.vampire(skill2, VampireSkills.VAMPIRE_REGENERATION.get()).build(consumer, modId("skill3"));
            ResourceLocation skill4 = SkillNodeBuilder.vampire(skill3, VampireSkills.FLEDGLING.get()).build(consumer, modId("skill4"));

            ResourceLocation util1 = SkillNodeBuilder.vampire(skill4, VampireSkills.SUMMON_BATS.get()).build(consumer, modId("util1"));
            ResourceLocation util15 = SkillNodeBuilder.vampire(util1, VampireSkills.HISSING.get()).build(consumer, modId("util15"));
            ResourceLocation util2 = SkillNodeBuilder.vampire(util1, VampireSkills.LESS_SUNDAMAGE.get(), VampireSkills.WATER_RESISTANCE.get()).build(consumer, modId("util2"));
            ResourceLocation util3 = SkillNodeBuilder.vampire(util2, VampireSkills.LESS_BLOOD_THIRST.get()).build(consumer, modId("util3"));
            ResourceLocation util4 = SkillNodeBuilder.vampire(util3, VampireSkills.VAMPIRE_DISGUISE.get()).build(consumer, modId("util4"));
            ResourceLocation util5 = SkillNodeBuilder.vampire(util4, VampireSkills.HALF_INVULNERABLE.get()).build(consumer, modId("util5"));
            ResourceLocation util6 = SkillNodeBuilder.vampire(util5, VampireSkills.VAMPIRE_INVISIBILITY.get()).build(consumer, modId("util6"));

            ResourceLocation offensive1 = SkillNodeBuilder.vampire(skill4, VampireSkills.VAMPIRE_RAGE.get()).build(consumer, modId("offensive1"));
            ResourceLocation offensive2 = SkillNodeBuilder.vampire(offensive1, VampireSkills.ADVANCED_BITER.get()).build(consumer, modId("offensive2"));
            ResourceLocation offensive3 = SkillNodeBuilder.vampire(offensive2, VampireSkills.SWORD_FINISHER.get()).build(consumer, modId("offensive3"));
            ResourceLocation offensive4 = SkillNodeBuilder.vampire(offensive3, VampireSkills.DARK_BLOOD_PROJECTILE.get()).build(consumer, modId("offensive4"));
            ResourceLocation offensive5 = SkillNodeBuilder.vampire(offensive4, VampireSkills.BLOOD_CHARGE.get()).build(consumer, modId("offensive5"));
            ResourceLocation offensive6 = SkillNodeBuilder.vampire(offensive5, VampireSkills.FREEZE.get()).build(consumer, modId("offensive6"));

            ResourceLocation defensive1 = SkillNodeBuilder.vampire(skill4, VampireSkills.SUNSCREEN.get()).build(consumer, modId("defensive1"));
            ResourceLocation defensive2 = SkillNodeBuilder.vampire(defensive1, VampireSkills.VAMPIRE_ATTACK_SPEED.get(), VampireSkills.VAMPIRE_SPEED.get()).build(consumer, modId("defensive2"));
            ResourceLocation defensive3 = SkillNodeBuilder.vampire(defensive2, VampireSkills.BLOOD_VISION.get()).build(consumer, modId("defensive3"));
            ResourceLocation defensive4_1 = SkillNodeBuilder.vampire(defensive3, VampireSkills.VAMPIRE_ATTACK_DAMAGE.get(), VampireSkills.VAMPIRE_JUMP.get()).build(consumer, modId("defensive5"));
            ResourceLocation defensive4_2 = SkillNodeBuilder.vampire(defensive3, VampireSkills.BLOOD_VISION_GARLIC.get()).build(consumer, modId("defensive4"));
            ResourceLocation defensive5 = SkillNodeBuilder.vampire(defensive4_1, VampireSkills.NEONATAL_DECREASE.get(), VampireSkills.DBNO_DURATION.get()).build(consumer, modId("defensive6"));
            ResourceLocation defensive6 = SkillNodeBuilder.vampire(defensive5, VampireSkills.TELEPORT.get()).build(consumer, modId("defensive7"));

            ResourceLocation lord_2 = SkillNodeBuilder.vampire(modId("vampire_lord"), VampireSkills.VAMPIRE_MINION_STATS_INCREASE.get()).build(consumer, modId("lord_2"));
            ResourceLocation lord_3 = SkillNodeBuilder.vampire(modId("vampire_lord"), VampireSkills.VAMPIRE_LORD_SPEED.get(), VampireSkills.VAMPIRE_LORD_ATTACK_SPEED.get()).build(consumer, modId("lord_3"));
            ResourceLocation lord_4 = SkillNodeBuilder.vampire(modId("vampire_lord"), VampireSkills.VAMPIRE_MINION_COLLECT.get()).build(consumer, modId("lord_4"));
            ResourceLocation lord_5 = SkillNodeBuilder.vampire(modId("vampire_lord"), VampireSkills.VAMPIRE_MINION_RECOVERY.get()).build(consumer, modId("lord_5"));

        }

    }

    private ResourceLocation modId(String string) {
        return new ResourceLocation(REFERENCE.MODID, string);
    }

    private void saveSkillNode(DirectoryCache cache, JsonObject nodeJson, Path path) {
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
