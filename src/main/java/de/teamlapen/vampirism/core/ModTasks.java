package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.time.temporal.ValueRange;
import java.util.Optional;
import java.util.function.Consumer;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;
import static de.teamlapen.vampirism.player.tasks.TaskBuilder.builder;

@SuppressWarnings("unused")
@ObjectHolder(REFERENCE.MODID)
public class ModTasks {
    public static void registerTasks(IForgeRegistry<Task> registry) {

        //general tasks
        registry.register(builder().unlockedBy((playerEntity -> FactionPlayerHandler.getOpt(playerEntity).map(FactionPlayerHandler::getCurrentFactionPlayer).filter(Optional::isPresent).map(Optional::get).map(d -> {
            return d.getLevel() == d.getFaction().getHighestReachableLevel();
        }).orElse(false))).addRequirement(new ItemStack(ModItems.item_garlic)).addReward(playerEntity -> FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> {
            if(factionPlayerHandler.getLordLevel() == 0) {
                factionPlayerHandler.setLordLevel(1);
            }
        })).enableDescription().build("lord"));

        //vampire tasks
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(ModEntities.hunter, 20).addReward(new ItemStack(ModItems.pure_blood_1)).build("hunter_killer"));
        registry.register(builder().withFaction(VReference.VAMPIRE_FACTION).addRequirement(ModEntities.advanced_hunter, 5).addReward(new ItemStack(ModItems.pure_blood_3)).build("advanced_hunter_killer"));

        //hunter tasks
        registry.register(builder().withFaction(VReference.HUNTER_FACTION).addRequirement(ModEntities.vampire, 20).addReward(new ItemStack(ModItems.holy_water_bottle_normal)).build("vampire_killer"));
        registry.register(builder().withFaction(VReference.HUNTER_FACTION).addRequirement(ModEntities.advanced_vampire, 5).addReward(new ItemStack(ModItems.armor_of_swiftness_feet_normal)).build("advanced_vampire_killer"));
    }
}
