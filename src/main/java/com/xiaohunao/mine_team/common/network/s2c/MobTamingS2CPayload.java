package com.xiaohunao.mine_team.common.network.s2c;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record MobTamingS2CPayload(int entityId,BlockPos pos){
    public static final Codec<MobTamingS2CPayload> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("entityId").forGetter(MobTamingS2CPayload::entityId),
            BlockPos.CODEC.fieldOf("pos").forGetter(MobTamingS2CPayload::pos)
    ).apply(instance, MobTamingS2CPayload::new));

    public static void encode(MobTamingS2CPayload packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(CODEC, packet);
    }

    public static MobTamingS2CPayload decode(FriendlyByteBuf friendlyByteBuf) {
        return friendlyByteBuf.readJsonWithCodec(CODEC);
    }


    public static void handle(MobTamingS2CPayload packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.handle(packet, ctx)));
        context.setPacketHandled(true);
    }

    public static class Client{
        public static void handle(MobTamingS2CPayload packet, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId());
                if (entity != null) {
                    entity.level().playLocalSound(packet.pos().getX(), packet.pos().getY(), packet.pos().getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, entity.getSoundSource(), 1.0F + entity.level().random.nextFloat(), entity.level().random.nextFloat() * 0.7F + 0.3F, false);
                }
            });
        }
    }
}
