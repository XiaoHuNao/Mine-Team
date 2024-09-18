package com.xiaohunao.mine_team.common.mixin;

import com.xiaohunao.mine_team.common.mixed.PlayerTeamMixed;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
@Mixin(PlayerTeam.class)
public class PlayerTeamMixin implements PlayerTeamMixed {

    @Unique @Nullable
    private LivingEntity mineTeam$lastHurtMob;

    @Unique @Nullable
    private PlayerTeam mineTeam$lastHurtTeam;

    @Unique
    private long mineTeam$lastHurtMobTimestamp;

    @Nullable @Override
    public PlayerTeam mineTeam$getLastHurtTeam() {
        return mineTeam$lastHurtTeam;
    }

    @Unique @Override
    public void mineTeam$setLastHurtTeam(@Nullable PlayerTeam lastHurtTeam) {
        this.mineTeam$lastHurtTeam = lastHurtTeam;
    }

    @Nullable @Unique @Override
    public LivingEntity mineTeam$getLastHurtMob() {
        return mineTeam$lastHurtMob;
    }

    @Unique @Override
    public long mineTeam$getLastHurtMobTimestamp() {
        return this.mineTeam$lastHurtMobTimestamp;
    }

    @Unique @Override
    public void mineTeam$setLastHurtMob(LivingEntity entity) {
        this.mineTeam$lastHurtMob = entity;
        this.mineTeam$lastHurtMobTimestamp = entity.level().getGameTime();
    }

}
