package de.teamlapen.vampirism.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.IEntitySelectorFactory;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Creates and adds custom entity selectors based on Vampirism's properties
 */
public class VampirismEntitySelectors {

    private static final String FACTION = "vampirism:faction";
    private static final String LEVEL = "vampirism:level";
    private static final String MIN_LEVEL = "vampirism:minLevel";
    private static final String MAX_LEVEL = "vampirism:maxLevel";


    private static void addFactionSelector(List<Predicate<Entity>> list, Map<String, String> arguments, ICommandSender sender) {
        String faction = arguments.get(FACTION);

        if (faction != null) {
            final boolean invert = faction.startsWith("!");

            if (invert) {
                faction = faction.substring(1);
            }

            IFaction[] factions = VampirismAPI.factionRegistry().getFactions();
            for (final IFaction f : factions) {
                if (f.name().equalsIgnoreCase(faction)) {
                    list.add(input -> {
                        if (input instanceof IFactionEntity) {
                            boolean flag1 = f.equals(((IFactionEntity) input).getFaction());
                            return invert != flag1;
                        } else if (f instanceof IPlayableFaction && input instanceof EntityPlayer) {
                            boolean flag1 = FactionPlayerHandler.get((EntityPlayer) input).isInFaction((IPlayableFaction) f);
                            return invert != flag1;
                        }
                        return invert;
                    });
                    return;
                }
            }
            //Prevents selection of all entities if mistyped
            list.add(input -> false);
            sender.sendMessage(new TextComponentString("Unknown faction: " + faction));

        }
    }

    private static void addLevelSelector(List<Predicate<Entity>> list, Map<String, String> arguments) {
        String level = arguments.get(LEVEL);
        if (level != null) {
            try {
                final int l = Integer.parseInt(level);
                list.add(input -> input instanceof EntityPlayer && FactionPlayerHandler.get((EntityPlayer) input).getCurrentLevel() == l);
            } catch (NumberFormatException e) {
                VampirismMod.log.w("EntitySelectors", "Failed to parse level (%s)", level);
                list.add(input -> false);
            }

        }

        String minLevel = arguments.get(MIN_LEVEL);
        if (minLevel != null) {
            try {
                final int l = Integer.parseInt(minLevel);
                list.add(input -> input instanceof EntityPlayer && FactionPlayerHandler.get((EntityPlayer) input).getCurrentLevel() >= l);
            } catch (NumberFormatException e) {
                VampirismMod.log.w("EntitySelectors", "Failed to parse level (%s)", level);
                list.add(input -> false);
            }

        }

        String maxLevel = arguments.get(MAX_LEVEL);
        if (maxLevel != null) {
            try {
                final int l = Integer.parseInt(maxLevel);
                list.add(input -> input instanceof EntityPlayer && FactionPlayerHandler.get((EntityPlayer) input).getCurrentLevel() <= l);
            } catch (NumberFormatException e) {
                VampirismMod.log.w("EntitySelectors", "Failed to parse level (%s)", level);
                list.add(input -> false);
            }

        }
    }

    public static void registerSelectors() {
        GameRegistry.registerEntitySelector(new IEntitySelectorFactory() {
            @Nonnull
            @Override
            public List<Predicate<Entity>> createPredicates(Map<String, String> arguments, String mainSelector, ICommandSender sender, Vec3d position) {
                List<Predicate<Entity>> list = Lists.newArrayList();
                addFactionSelector(list, arguments, sender);
                addLevelSelector(list, arguments);
                return list;
            }
        }, FACTION, LEVEL, MIN_LEVEL, MAX_LEVEL);
    }
}
