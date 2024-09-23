package com.xiaohunao.mine_team.common.network.s2c;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.mine_team.common.capability.TeamCapability;
import com.xiaohunao.mine_team.common.capability.TeamData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public record TeamDataSyncS2CPayload(int entityId, TeamData data) {
    public static final Codec<TeamDataSyncS2CPayload> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(TeamDataSyncS2CPayload::entityId),
            TeamData.CODEC.fieldOf("data").forGetter(TeamDataSyncS2CPayload::data)
    ).apply(instance, TeamDataSyncS2CPayload::new));

    public static void encode(TeamDataSyncS2CPayload packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(CODEC, packet);
    }
    public static TeamDataSyncS2CPayload decode(FriendlyByteBuf friendlyByteBuf) {
        return friendlyByteBuf.readJsonWithCodec(CODEC);
    }

    public static void handle(TeamDataSyncS2CPayload packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.handle(packet, ctx)));
        context.setPacketHandled(true);
    }

    public static class Client{
        public static void handle(TeamDataSyncS2CPayload packet, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId);
                if (entity instanceof LivingEntity livingEntity) {
                    TeamCapability.get(livingEntity).ifPresent(teamCapability -> {
                        Scoreboard scoreboard = livingEntity.level().getScoreboard();
                        if (!Objects.equals(teamCapability.data.getColor(), packet.data.getColor())) {
                            scoreboard.addPlayerToTeam(livingEntity.getScoreboardName(),scoreboard.getPlayerTeam(packet.data.getColor()));
                        }
                        teamCapability.data.copy(packet.data);
                    });
                }
            });
        }
    }
}
