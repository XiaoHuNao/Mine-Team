package com.xiaohunao.mine_team.common.mixin;

import com.xiaohunao.mine_team.common.mixed.PlayerTeamMixed;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(PlayerTeam.class)
public class PlayerTeamMixin implements PlayerTeamMixed {

    @Unique
    @Nullable
    private LivingEntity lastHurtMob;

    @Unique
    @Nullable
    private PlayerTeam lastHurtTeam;

    @Unique
    private long lastHurtMobTimestamp;

    @Nullable
    @Override
    public PlayerTeam getLastHurtTeam() {
        return lastHurtTeam;
    }

    @Unique
    @Override
    public void setLastHurtTeam(@Nullable PlayerTeam lastHurtTeam) {
        this.lastHurtTeam = lastHurtTeam;
    }

    @Nullable
    @Unique
    @Override
    public LivingEntity getLastHurtMob() {
        return lastHurtMob;
    }

    @Unique
    @Override
    public long getLastHurtMobTimestamp() {
        return this.lastHurtMobTimestamp;
    }

    @Unique
    @Override
    public void setLastHurtMob(Entity entity) {
        if (entity instanceof LivingEntity) {
            this.lastHurtMob = (LivingEntity)entity;
        } else {
            this.lastHurtMob = null;
        }

        this.lastHurtMobTimestamp = Minecraft.getInstance().level.getGameTime();
    }

}
