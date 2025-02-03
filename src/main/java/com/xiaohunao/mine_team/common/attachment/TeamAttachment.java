package com.xiaohunao.mine_team.common.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.mine_team.common.team.Team;
import net.minecraft.core.UUIDUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class TeamAttachment {
    public static final Codec<TeamAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.optionalFieldOf("team_uid",UUID.randomUUID()).forGetter(TeamAttachment::getTeamUid),
            Codec.BOOL.optionalFieldOf("can_pvp", true).forGetter(TeamAttachment::isCanPvP)
            ).apply(instance, TeamAttachment::new)
    );

    private UUID teamUid;
    private boolean canPvP;

    public TeamAttachment(UUID teamUid, boolean canPvP) {
        this.teamUid = teamUid;
        this.canPvP = canPvP;
    }

    public UUID getTeamUid() {
        return teamUid;
    }

    public TeamAttachment setTeamUid(UUID teamUid) {
        this.teamUid = teamUid;
        return this;
    }

    public boolean isCanPvP() {
        return canPvP;
    }

    public TeamAttachment setCanPvP(boolean canPvP) {
        this.canPvP = canPvP;
        return this;
    }

    public TeamAttachment sync(TeamAttachment attachment) {
        this.teamUid = attachment.teamUid;
        this.canPvP = attachment.canPvP;
        return this;
    }
}
