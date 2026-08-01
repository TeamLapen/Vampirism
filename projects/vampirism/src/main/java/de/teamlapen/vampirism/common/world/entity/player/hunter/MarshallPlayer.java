package de.teamlapen.vampirism.common.world.entity.player.hunter;

import de.teamlapen.faction.api.factions.level.FactionUpdate;
import de.teamlapen.faction.common.factions.FactionExtension;
import de.teamlapen.faction.common.world.entities.IPlayerEventListener;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IMarshallPlayer;
import de.teamlapen.vampirism.common.core.ModAttachments;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;

public class MarshallPlayer extends FactionExtension implements IMarshallPlayer, IPlayerEventListener {

    private boolean isMarshall;
    private int skillPoints;

    public MarshallPlayer(Player player) {
        super(player);
    }

    @Override
    public void onLeaveFaction(Player player) {
        this.isMarshall = false;
        this.skillPoints = 0;
        sync();
    }

    public void awardSkillPoint() {
        this.skillPoints = Math.clamp(this.skillPoints + 1, 0, 2);
    }


    @Override
    protected void registerProperties() {
        super.registerProperties();
        this.registerProperty(VIdentifier.mod("id_dracula")).simple(false, () -> this.isMarshall, b -> this.isMarshall = b);
        this.registerProperty(VIdentifier.mod("skill_points")).simple(0, () -> this.skillPoints, i -> this.skillPoints = Math.clamp(i, 0, 2));
    }

    @Override
    public AttachmentType<?> getType() {
        return ModAttachments.MARSHALL_PLAYER.get();
    }

    @Override
    public boolean isMarshall() {
        return this.isMarshall;
    }

    @Override
    public int getSkillPoints() {
        return this.skillPoints;
    }

    private void makeMarshall() {
        this.isMarshall = true;
        this.awardSkillPoint();
    }

    private void removeMarshall() {
        this.isMarshall = false;
        this.skillPoints = 0;
    }

    @Override
    public void setLevel(FactionUpdate change) {
        if (change.get(IMarshallPlayer.MarshallChange.KEY) != null) {
            makeMarshall();
        } else if (this.isMarshall) {
            boolean droppedLevel = change.getLevel() < getFaction().value().getHighestReachableLevel();
            boolean droppedLordLevel = change.hasLordLevelChange() && change.getLordLevel() < getFaction().value().getHighestLordLevel();
            if (droppedLevel || droppedLordLevel) {
                removeMarshall();
            }
        }
        sync();
    }
}
