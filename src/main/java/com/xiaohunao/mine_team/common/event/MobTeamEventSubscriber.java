package com.xiaohunao.mine_team.common.event;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.capability.TeamCapability;
import com.xiaohunao.mine_team.common.config.MineTeamConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = MineTeam.MOD_ID)
public class MobTeamEventSubscriber {
    @SubscribeEvent
    public static void onPlayerInteractEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }
        ItemStack itemstack = player.getItemInHand(hand);
        Entity target = event.getTarget();
        Ingredient tamingMaterial = MineTeamConfig.getTamingMaterial(target.getType());
        if (target instanceof LivingEntity livingEntity){
            TeamCapability.get(player).ifPresent(teamCapability -> {
                teamCapability.withMobTeam(player,livingEntity,hand,tamingMaterial);
            });
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity livingEntity){
            TeamCapability.get(livingEntity).ifPresent(teamCapability -> {
                teamCapability.tickMobTeam(livingEntity);
            });
        }
    }
}
