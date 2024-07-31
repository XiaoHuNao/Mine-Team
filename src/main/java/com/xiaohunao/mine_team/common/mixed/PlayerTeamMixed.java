package com.xiaohunao.mine_team.common.mixed;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

public interface PlayerTeamMixed {
    @Nullable
    PlayerTeam getLastHurtTeam();

    @Unique
    void setLastHurtTeam(@Nullable PlayerTeam lastHurtTeam);

    @Nullable
    @Unique
    LivingEntity getLastHurtMob();

    @Unique
    long getLastHurtMobTimestamp();
    @Unique
    void setLastHurtMob(Entity entity);
}
