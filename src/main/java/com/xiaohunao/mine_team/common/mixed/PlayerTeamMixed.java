package com.xiaohunao.mine_team.common.mixed;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

public interface PlayerTeamMixed {
    @Nullable
    PlayerTeam mineTeam$getLastHurtTeam();

    @Unique
    void mineTeam$setLastHurtTeam(@Nullable PlayerTeam lastHurtTeam);

    @Nullable
    @Unique
    LivingEntity mineTeam$getLastHurtMob();

    @Unique
    long mineTeam$getLastHurtMobTimestamp();
    @Unique
    void mineTeam$setLastHurtMob(LivingEntity entity);
}
