package com.xiaohunao.mine_team.common.event.subscriber;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import com.xiaohunao.mine_team.common.network.TeamAttachmentSyncPayload;
import com.xiaohunao.mine_team.common.team.Team;
import com.xiaohunao.mine_team.common.team.TeamManager;
import com.xiaohunao.mine_team.common.init.MTAttachmentTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MineTeam.MODID)
public class PlayerEventSubscriber {
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        ServerLevel serverLevel = (ServerLevel)player.level();
        TeamManager teamManager = TeamManager.of(serverLevel);

        if (!player.hasData(MTAttachmentTypes.TEAM)) {
            TeamAttachment attachment = player.getData(MTAttachmentTypes.TEAM);
            attachment.setTeamUid(teamManager.getTeam(DyeColor.WHITE).getUid())
                    .setCanPvP(false);
            player.setData(MTAttachmentTypes.TEAM, attachment);
        }
        PacketDistributor.sendToPlayer((ServerPlayer) player, new TeamAttachmentSyncPayload(player.getId(),player.getData(MTAttachmentTypes.TEAM)));
        serverLevel.getDataStorage().save();
    }

//    @SubscribeEvent
//    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
//        Player player = event.getEntity();
//        Player original = event.getOriginal();
//        if (event.isWasDeath() && event.getOriginal().hasData(MTAttachmentTypes.TEAM)) {
//            player.setData(MTAttachmentTypes.TEAM, original.getData(MTAttachmentTypes.TEAM));
//            PacketDistributor.sendToPlayer((ServerPlayer) player, new TeamAttachmentSyncPayload(player.getId(),player.getData(MTAttachmentTypes.TEAM)));
//        }
//    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player && player.hasData(MTAttachmentTypes.TEAM)) {
            PacketDistributor.sendToPlayer((ServerPlayer) player, new TeamAttachmentSyncPayload(player.getId(),player.getData(MTAttachmentTypes.TEAM)));
        }
    }


    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) {
            return;
        }

//        if (player.hasData(MTAttachmentTypes.TEAM)) {
//            TeamAttachment attachment = player.getData(MTAttachmentTypes.TEAM);
//            System.out.println(attachment.getTeamUid());
//            System.out.println(attachment.isCanPvP());
//        }
        TeamManager teamManager = TeamManager.of(level);
        System.out.println(teamManager);
    }



}