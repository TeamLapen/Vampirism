package de.teamlapen.vampirism.common.world.entity.converted;

import de.teamlapen.vampirism.client.renderer.entities.wrapper.FixedDonkeyRenderer;
import de.teamlapen.vampirism.client.renderer.entities.wrapper.FixedLlamaRenderer;
import de.teamlapen.vampirism.client.renderer.entities.wrapper.FixedMuleRenderer;
import de.teamlapen.vampirism.client.renderer.entities.wrapper.FixedTraderLlamaRenderer;
import de.teamlapen.vampirism.annotation.AdditionalConverter;
import de.teamlapen.vampirism.annotation.ConvertedCreature;
import de.teamlapen.vampirism.annotation.ModId;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.equine.*;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.villager.Villager;

/**
 * Declares the converted creatures. The {@code ConvertedCreatureProcessor} generates, for each entry:
 * <ul>
 *     <li>an abstract {@code Converted<Name>} base class,</li>
 *     <li>an entry in {@code GeneratedConvertedEntities} (DeferredHolder, attribute + spawn-placement listeners),</li>
 *     <li>an entry in {@code GeneratedConvertedEntitiesData} (entity-converter data map, entity type tag, loot table),</li>
 *     <li>an entry in {@code GeneratedConvertedEntitiesClient} when a {@code renderer} is declared.</li>
 * </ul>
 * <p>
 * Use {@link de.teamlapen.vampirism.annotation.AdditionalConverter} to declare vanilla→converted mappings for
 * entities whose converted type is registered outside this system (e.g. {@code VillagerConvertedEntity}).
 * <p>
 * Entities that need extra behaviour reference a handwritten {@code subclass} which extends the generated base
 * and carries the {@code getAttributeBuilder} / {@code checkSpawnRules} statics.
 * <p>
 * Renderers needing extra constructor arguments (Donkey, Mule, TraderLlama, Llama) stay handwritten in
 * {@code ModEntitiesRender}.
 * <p>
 * This class is source-retention only and never loaded at runtime, so it may safely import both server
 * and client-only types.
 */
@ModId("vampirism")
@ConvertedCreature(value = Cow.class, renderer = CowRenderer.class)
@ConvertedCreature(value = Sheep.class, renderer = SheepRenderer.class)
@ConvertedCreature(value = Pig.class, renderer = PigRenderer.class)
@ConvertedCreature(value = Rabbit.class, renderer = RabbitRenderer.class)
@ConvertedCreature(value = Wolf.class, renderer = WolfRenderer.class)
@ConvertedCreature(value = MushroomCow.class, renderer = MushroomCowRenderer.class, entityType = "MOOSHROOM")
@ConvertedCreature(value = Ocelot.class, renderer = OcelotRenderer.class)
@ConvertedCreature(value = TraderLlama.class, renderer = FixedTraderLlamaRenderer.class)
@ConvertedCreature(value = Turtle.class, renderer = TurtleRenderer.class)
@ConvertedCreature(value = Llama.class, renderer = FixedLlamaRenderer.class)
@ConvertedCreature(value = PolarBear.class, renderer = PolarBearRenderer.class)
@ConvertedCreature(value = Panda.class, renderer = PandaRenderer.class)
@ConvertedCreature(value = Cat.class, renderer = CatRenderer.class)
@ConvertedCreature(value = Goat.class, renderer = GoatRenderer.class, spawnRulesFrom = "checkGoatSpawnRules")
@ConvertedCreature(value = Fox.class, renderer = FoxRenderer.class, subclass = ConvertedFoxEntity.class, immuneToSweetBerryBush = true)
@ConvertedCreature(value = Horse.class, renderer = HorseRenderer.class, subclass = ConvertedHorseEntity.class, attributeMethod = "createBaseHorseAttributes")
@ConvertedCreature(value = Donkey.class, renderer = FixedDonkeyRenderer.class, subclass = ConvertedDonkeyEntity.class, attributeMethod = "createBaseHorseAttributes")
@ConvertedCreature(value = Mule.class, renderer = FixedMuleRenderer.class, subclass = ConvertedMuleEntity.class, attributeMethod = "createBaseHorseAttributes", spawnRulesFrom = "checkMobSpawnRules")
@ConvertedCreature(value = Camel.class, renderer = CamelRenderer.class, subclass = ConvertedCamelEntity.class, attributeMethod = "createBaseHorseAttributes")
@AdditionalConverter(vanilla = Villager.class, convertedField = "de.teamlapen.vampirism.common.core.ModEntities.VILLAGER_CONVERTED")
public final class GenerateConvertedCreatures {
    private GenerateConvertedCreatures() {
    }
}

