package com.xiaohunao.mine_team.common.mixin;

import com.xiaohunao.mine_team.common.capability.TeamCapability;
import com.xiaohunao.mine_team.common.capability.TeamData;
import com.xiaohunao.mine_team.common.network.NetworkHandler;
import com.xiaohunao.mine_team.common.network.s2c.TeamDataSyncS2CPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Slime.class)
public class SlimeMixin {
    @Inject(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Slime;setCustomName(Lnet/minecraft/network/chat/Component;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void remove(Entity.RemovalReason pReason, CallbackInfo ci, int i, Component component, boolean flag, float f, int j, int k, int l, float f1, float f2, Slime slime) {
        Slime thisSlime = (Slime) (Object) this;
        Level level = thisSlime.level();
        if (!level.isClientSide){
            ServerLevel serverLevel = (ServerLevel) level;
            ServerScoreboard scoreboard = serverLevel.getServer().getScoreboard();
            PlayerTeam playersTeam = scoreboard.getPlayersTeam(thisSlime.getScoreboardName());
            if (playersTeam != null){
                scoreboard.addPlayerToTeam(slime.getScoreboardName(),playersTeam);
                slime.setGlowingTag(true);
                TeamData teamData = TeamCapability.get(thisSlime).orElse(new TeamCapability()).data;
                NetworkHandler.CHANNEL.send(PacketDistributor.DIMENSION.with(level::dimension),new TeamDataSyncS2CPayload(slime.getId(),teamData));
            }
        }
    }
}
