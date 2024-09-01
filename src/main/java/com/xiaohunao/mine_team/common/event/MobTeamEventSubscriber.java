package com.xiaohunao.mine_team.common.event;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.config.MineTeamConfig;
import com.xiaohunao.mine_team.common.network.MobTamingS2CPayload;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MineTeam.MOD_ID)
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
                itemstack.consume(1, player);
//                livingEntity.getPersistentData().putInt("TeamConversionTime",livingEntity.level().random.nextInt(2401) + 3600);
                tag.putInt("teamTamingTime",livingEntity.level().random.nextInt(80));
                tag.putString("teamTamingColor",player.getPersistentData().getString("teamColor"));
                livingEntity.removeEffect(MobEffects.WEAKNESS);
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Math.min(livingEntity.level().getDifficulty().getId() - 1, 0)));
                PacketDistributor.sendToPlayersInDimension(serverLevel, new MobTamingS2CPayload(target.getId(),target.blockPosition()));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
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
