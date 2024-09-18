package com.xiaohunao.mine_team.common.entity.ai.goal;

import com.xiaohunao.mine_team.common.mixed.MobMixed;
import com.xiaohunao.mine_team.common.mixed.PlayerTeamMixed;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.scores.PlayerTeam;

import java.util.EnumSet;

public class TeamOwnerHurtTargetGoal extends TargetGoal {
    private final Mob tameLivingEntity;
    private LivingEntity ownerLastHurt;
    private long timestamp;
    public TeamOwnerHurtTargetGoal(Mob tameLivingEntity) {
        super(tameLivingEntity, false);
        this.tameLivingEntity = tameLivingEntity;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.tameLivingEntity instanceof MobMixed mobMixed){
            PlayerTeam ownerTeam = mobMixed.getOwnerTeam();
            if (ownerTeam instanceof PlayerTeamMixed playerTeamMixed) {
                this.ownerLastHurt = playerTeamMixed.mineTeam$getLastHurtMob();
                long timestamp = playerTeamMixed.mineTeam$getLastHurtMobTimestamp();
                return timestamp != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) &&
                        mobMixed.wantsToAttack(this.ownerLastHurt, playerTeamMixed.mineTeam$getLastHurtTeam(),ownerTeam);
            }
        }
        return false;
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurt);

        if (this.tameLivingEntity instanceof MobMixed mobMixed){
            PlayerTeam team = mobMixed.getOwnerTeam();
            if (team instanceof PlayerTeamMixed playerTeamMixed) {
                this.timestamp = playerTeamMixed.mineTeam$getLastHurtMobTimestamp();
            }
        }
        super.start();
    }
}
