package com.xiaohunao.mine_team.common.network.s2c;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TeamColorSyncS2CPayload(String newTeamColor) {
    public static final Codec<TeamColorSyncS2CPayload> CODEC = Codec.STRING.xmap(TeamColorSyncS2CPayload::new, TeamColorSyncS2CPayload::newTeamColor);

    public static void encode(TeamColorSyncS2CPayload packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(CODEC, packet);
    }

    public static TeamColorSyncS2CPayload decode(FriendlyByteBuf friendlyByteBuf) {
        return friendlyByteBuf.readJsonWithCodec(CODEC);
    }


    public static void handle(TeamColorSyncS2CPayload packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.handle(packet, ctx)));
        context.setPacketHandled(true);
    }

    public static class Client{
        public static void handle(TeamColorSyncS2CPayload packet, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                LocalPlayer localPlayer = Minecraft.getInstance().player;
                localPlayer.getPersistentData().putString("teamColor", packet.newTeamColor());
            });
        }
    }
}
