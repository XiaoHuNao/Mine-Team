package com.xiaohunao.mine_team.common.network.c2s;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.mine_team.common.capability.TeamCapability;
import com.xiaohunao.mine_team.common.capability.TeamData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TeamDataSyncC2SPayload(int entityId,TeamData data) {
    public static final Codec<TeamDataSyncC2SPayload> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(TeamDataSyncC2SPayload::entityId),
            TeamData.CODEC.fieldOf("data").forGetter(TeamDataSyncC2SPayload::data)
    ).apply(instance, TeamDataSyncC2SPayload::new));
    public static void encode(TeamDataSyncC2SPayload packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(CODEC, packet);
    }
    public static TeamDataSyncC2SPayload decode(FriendlyByteBuf friendlyByteBuf) {
        return friendlyByteBuf.readJsonWithCodec(CODEC);
    }

    public static void handle(TeamDataSyncC2SPayload packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            Level level = context.getSender().level();
            Entity entity = level.getEntity(packet.entityId);
            if (entity instanceof LivingEntity livingEntity) {
                TeamCapability.get(livingEntity).ifPresent(teamCapability -> {
                    teamCapability.setData(packet.data);
                });
            }
        });
        context.setPacketHandled(true);
    }

}