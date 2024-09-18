package com.xiaohunao.mine_team.common.event;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.capability.TeamCapability;
import com.xiaohunao.mine_team.common.network.NetworkHandler;
import com.xiaohunao.mine_team.common.network.s2c.TeamDataSyncS2CPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;


@Mod.EventBusSubscriber(modid = MineTeam.MOD_ID)
public class LivingEventSubscriber {
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        TeamCapability.get(player).ifPresent(teamCapability -> {
            teamCapability.init(player);
        });
    }

    @SubscribeEvent
    public static void onLivingPvP(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attackEntity){
            TeamCapability.get(attackEntity).ifPresent(teamCapability -> {
                event.setCanceled(!teamCapability.canAttack(attackEntity, event.getEntity()));
            });
        }
    }
    @SubscribeEvent
    public static void onSetTeamLastHurtMob(LivingAttackEvent event) {
        LivingEntity hurtEntity = event.getEntity();
        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();
        if (sourceEntity instanceof LivingEntity attackEntity){
            TeamCapability.get(attackEntity).ifPresent(teamCapability -> {
                teamCapability.setLastHurtMob(attackEntity,hurtEntity);
            });
        }
    }


    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        Player original = event.getOriginal();
        if (event.isWasDeath() && !player.level().isClientSide()) {
            TeamCapability.get(player).ifPresent(teamCapability -> {
                TeamCapability.get(original).ifPresent(originalTeamCapability -> {
                    teamCapability.data = originalTeamCapability.data;
                    NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),new TeamDataSyncS2CPayload(player.getId(), originalTeamCapability.data));
                });
            });
        }
    }
}