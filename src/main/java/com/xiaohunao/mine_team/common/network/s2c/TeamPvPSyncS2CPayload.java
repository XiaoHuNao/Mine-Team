package com.xiaohunao.mine_team.common.network.s2c;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TeamPvPSyncS2CPayload(boolean friendlyFire){
    public static final Codec<TeamPvPSyncS2CPayload> CODEC = Codec.BOOL.xmap(TeamPvPSyncS2CPayload::new, TeamPvPSyncS2CPayload::friendlyFire);

    public static void encode(TeamPvPSyncS2CPayload packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(CODEC, packet);
    }

    public static TeamPvPSyncS2CPayload decode(FriendlyByteBuf friendlyByteBuf) {
        return friendlyByteBuf.readJsonWithCodec(CODEC);
    }

    public static void handle(TeamPvPSyncS2CPayload packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.handle(packet, ctx)));
        context.setPacketHandled(true);
    }

    public static class Client {
        public static void handle(TeamPvPSyncS2CPayload packet, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                LocalPlayer localPlayer = Minecraft.getInstance().player;
                localPlayer.getPersistentData().putBoolean("teamPvP", packet.friendlyFire);
            });
        }
    }
}
