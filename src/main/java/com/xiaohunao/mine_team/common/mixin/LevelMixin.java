package com.xiaohunao.mine_team.common.mixin;

import com.xiaohunao.mine_team.common.mixed.TeamManagerContainer;
import com.xiaohunao.mine_team.common.team.TeamManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin implements TeamManagerContainer {
    @Unique
    private TeamManager heaven_destiny_moment$momentManager;

    @Override
    public TeamManager mine_team$getTeamManager() {
        return heaven_destiny_moment$momentManager;
    }

    @Override
    public void mine_team$setTeamManager(TeamManager teamManager) {
        this.heaven_destiny_moment$momentManager = teamManager;
    }

    @Inject(at = @At("HEAD"), method = "close")
    private void heaven_destiny_moment$onClose(CallbackInfo ci) {
        heaven_destiny_moment$momentManager = null;
    }
}