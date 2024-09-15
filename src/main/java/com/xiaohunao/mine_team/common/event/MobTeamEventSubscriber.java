package com.xiaohunao.mine_team.common.event;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.config.MineTeamConfig;
import com.xiaohunao.mine_team.common.network.s2c.MobTamingS2CPayload;
import com.xiaohunao.mine_team.common.network.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;


@Mod.EventBusSubscriber(modid = MineTeam.MOD_ID)
public class MobTeamEventSubscriber {
    @SubscribeEvent
    public static void onPlayerInteractEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) {
            return;
        }
        ItemStack itemstack = player.getItemInHand(hand);
        Entity target = event.getTarget();
        Ingredient tamingMaterial = MineTeamConfig.getTamingMaterial(target.getType());
        CompoundTag tag = target.getPersistentData();
        if (tamingMaterial.test(itemstack) && target instanceof LivingEntity livingEntity && !tag.contains("teamTamingTime")) {
            ServerLevel serverLevel = (ServerLevel) level;
            PlayerTeam playersTeam = serverLevel.getServer().getScoreboard().getPlayersTeam(target.getScoreboardName());
            if (livingEntity.hasEffect(MobEffects.WEAKNESS) && playersTeam == null) {
                itemstack.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(hand));
                livingEntity.getPersistentData().putInt("teamTamingTime",livingEntity.level().random.nextInt(2401) + 3600);
                tag.putString("teamTamingColor",player.getPersistentData().getString("teamColor"));
                livingEntity.removeEffect(MobEffects.WEAKNESS);
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Math.min(livingEntity.level().getDifficulty().getId() - 1, 0)));
                livingEntity.setGlowingTag(true);
                NetworkHandler.CHANNEL.sendToServer(new MobTamingS2CPayload(target.getId(),target.blockPosition()));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide){
            CompoundTag tag = livingEntity.getPersistentData();
            if (!tag.contains("teamTamingTime")) {
                return;
            }
            int time = tag.getInt("teamTamingTime");
            if (time <= 0){
                String teamColor = tag.getString("teamTamingColor");
                ServerLevel serverLevel = (ServerLevel) livingEntity.level();
                ServerScoreboard scoreboard = serverLevel.getServer().getScoreboard();
                scoreboard.addPlayerToTeam(livingEntity.getScoreboardName(), scoreboard.getPlayerTeam(teamColor));
                livingEntity.setGlowingTag(true);
                tag.remove("teamTamingTime");
                tag.remove("teamTamingColor");
            }else {
                tag.putInt("teamTamingTime",time - 1);
            }
        }
    }
}
