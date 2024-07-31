package com.xiaohunao.mine_team.common.mixin;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.mixed.MobMixed;
import com.xiaohunao.mine_team.common.mixed.PlayerTeamMixed;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "addPlayerToTeam", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerScoreboard;setDirty()V"))
    private void addPlayerToTeam(String playerName, PlayerTeam team, CallbackInfoReturnable<Boolean> cir) {
        server.levelKeys().forEach(key -> {
            ServerLevel level = server.getLevel(key);
            if (level != null) {
                Scoreboard scoreboard = level.getScoreboard();
                try {
                    UUID uuid = UUID.fromString(playerName);
                    Entity entity = level.getEntity(uuid);
                    if (entity instanceof MobMixed mobMixed) {
                        mobMixed.setTame(team);
                    }
                }catch (IllegalArgumentException e) {
                    MineTeam.LOGGER.error("Failed to parse UUID: {}", playerName);
                }
            }
        });
    }
}
