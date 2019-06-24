package de.teamlapen.vampirism.entity;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Creates and adds custom entity selectors based on Vampirism's properties
 */
public class VampirismEntitySelectors {//TODO @maxanier

    private final static Logger LOGGER = LogManager.getLogger(VampirismEntitySelectors.class);
    private static final String FACTION = "vampirism:faction";
    private static final String LEVEL = "vampirism:level";
    private static final String MIN_LEVEL = "vampirism:minLevel";
    private static final String MAX_LEVEL = "vampirism:maxLevel";

    private static final DynamicCommandExceptionType FACTION_NOT_FOUND = new DynamicCommandExceptionType((p_208726_0_) -> new TextComponentTranslation("vampirism.argument.entity.options.faction.not_found", p_208726_0_));


    public static void registerSelectors() {
        EntityOptions.register(FACTION, (parser) -> {
            boolean invert = parser.shouldInvertValue();
            String faction = parser.getReader().readString();
            IFaction[] factions = VampirismAPI.factionRegistry().getFactions();
            for (final IFaction f : factions) {
                if (f.name().equalsIgnoreCase(faction)) {
                    parser.addFilter(input -> {
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
            throw FACTION_NOT_FOUND.createWithContext(parser.getReader(), faction);
        }, (parser) -> {
            return true;
        }, new TextComponentTranslation("vampirism.argument.entity.options.faction.desc"));

        EntityOptions.register(LEVEL, (parser) -> {
            StringReader reader = parser.getReader();
            MinMaxBounds.IntBound bound = MinMaxBounds.IntBound.fromReader(reader);
            if ((bound.getMin() == null || bound.getMin() >= 0) && (bound.getMax() == null || bound.getMax() >= 0)) {
                parser.addFilter(input -> {
                    if (input instanceof EntityPlayer) {
                        int level = FactionPlayerHandler.get((EntityPlayer) input).getCurrentLevel();
                        return (bound.getMin() == null || bound.getMin() <= level) && (bound.getMax() == null || bound.getMax() >= level);
                    }
                    return false;
                });
            } else {
                throw EntityOptions.NEGATIVE_LEVEL.createWithContext(reader);
            }

        }, (parser) -> true, new TextComponentTranslation("vampirism.argument.entity.options.level.desc"));

    }
}
