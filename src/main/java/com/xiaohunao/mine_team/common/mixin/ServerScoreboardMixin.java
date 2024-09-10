package com.xiaohunao.mine_team.common.mixin;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import com.xiaohunao.mine_team.common.mixed.MobMixed;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "addPlayerToTeam", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerScoreboard;setDirty()V"))
    private void addPlayerToTeam(String playerName, PlayerTeam team, CallbackInfoReturnable<Boolean> cir) {
        server.levelKeys().forEach(key -> {
            ServerLevel level = server.getLevel(key);
            if (level != null) {
                UUIDUtil.STRING_CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive(playerName)).result().ifPresent(uuid -> {
                    Entity entity = level.getEntity(uuid);
                    if (entity instanceof MobMixed mobMixed) {
                        mobMixed.setTame(team);
                    }
                });
            }
        });
    }
}
