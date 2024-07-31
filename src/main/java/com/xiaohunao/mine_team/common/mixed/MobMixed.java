package com.xiaohunao.mine_team.common.mixed;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.PlayerTeam;

public interface MobMixed {
    void setTame(PlayerTeam tame);
    PlayerTeam getOwnerTeam();
    boolean wantsToAttack(LivingEntity ownerLastHurt, PlayerTeam lastHurtTeam, PlayerTeam team);
}
