package de.teamlapen.vampirism.entity;

import com.google.common.base.Predicate;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Creates and adds custom entity selectors based on Vampirism's properties
 */
public class VampirismEntitySelectors {


    private static void addFactionSelector(List<Predicate<Entity>> list, Map<String, String> arguments, ICommandSender sender) {
        String faction = arguments.get("vampirism:faction");

        if (faction != null) {
            final boolean invert = faction.startsWith("!");

            if (invert) {
                faction = faction.substring(1);
            }

            IFaction[] factions = VampirismAPI.factionRegistry().getFactions();
            for (final IFaction f : factions) {
                if (f.name().equalsIgnoreCase(faction)) {
                    list.add(new Predicate<Entity>() {
                        @Override
                        public boolean apply(@Nullable Entity input) {
                            if (input instanceof IFactionEntity) {
                                boolean flag1 = f.equals(((IFactionEntity) input).getFaction());
                                return invert != flag1;
                            } else if (f instanceof IPlayableFaction && input instanceof EntityPlayer) {
                                boolean flag1 = FactionPlayerHandler.get((EntityPlayer) input).isInFaction((IPlayableFaction) f);
                                return invert != flag1;
                            }
                            return invert;
                        }
                    });
                    return;
                }
            }
            //Prevents selection of all entities if mistyped
            list.add(new Predicate<Entity>() {
                @Override
                public boolean apply(@Nullable Entity input) {
                    return false;
                }
            });
            sender.addChatMessage(new TextComponentString("Unknown faction: " + faction));

        }
    }

    private static void addLevelSelector(List<Predicate<Entity>> list, Map<String, String> arguments) {
        String level = arguments.get("vampirism:level");
        if (level != null) {
            try {
                final int l = Integer.parseInt(level);
                list.add(new Predicate<Entity>() {
                    @Override
                    public boolean apply(@Nullable Entity input) {
                        if (input instanceof EntityPlayer) {
                            return FactionPlayerHandler.get((EntityPlayer) input).getCurrentLevel() == l;
                        }
                        return false;
                    }
                });
            } catch (NumberFormatException e) {
                VampirismMod.log.w("EntitySelectors", "Failed to parse level (%s)", level);
            }

        }

        String minLevel = arguments.get("vampirism:minLevel");
        if (minLevel != null) {
            try {
                final int l = Integer.parseInt(minLevel);
                list.add(new Predicate<Entity>() {
                    @Override
                    public boolean apply(@Nullable Entity input) {
                        if (input instanceof EntityPlayer) {
                            return FactionPlayerHandler.get((EntityPlayer) input).getCurrentLevel() >= l;
                        }
                        return false;
                    }
                });
            } catch (NumberFormatException e) {
                VampirismMod.log.w("EntitySelectors", "Failed to parse level (%s)", level);
            }

        }

        String maxLevel = arguments.get("vampirism:maxLevel");
        if (maxLevel != null) {
            try {
                final int l = Integer.parseInt(maxLevel);
                list.add(new Predicate<Entity>() {
                    @Override
                    public boolean apply(@Nullable Entity input) {
                        if (input instanceof EntityPlayer) {
                            return FactionPlayerHandler.get((EntityPlayer) input).getCurrentLevel() <= l;
                        }
                        return false;
                    }
                });
            } catch (NumberFormatException e) {
                VampirismMod.log.w("EntitySelectors", "Failed to parse level (%s)", level);
            }

        }
    }


    public static void gatherEntitySelectors(List<Predicate<Entity>> list, Map<String, String> map, String mainSelector, ICommandSender sender, Vec3d position) {
        addFactionSelector(list, map, sender);
        addLevelSelector(list, map);
    }
}
