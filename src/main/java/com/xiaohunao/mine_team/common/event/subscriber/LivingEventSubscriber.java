package com.xiaohunao.mine_team.common.event.subscriber;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import com.xiaohunao.mine_team.common.init.MTAttachmentTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = MineTeam.MODID)
public class LivingEventSubscriber {
    @SubscribeEvent
    public static void onLivingPvP(LivingIncomingDamageEvent event) {
        LivingEntity hurtEntity = event.getEntity();
        DamageSource source = event.getSource();
        Entity attackEntity = source.getEntity();
        if (hurtEntity.level() instanceof ServerLevel serverLevel && attackEntity != null){
            if (!hurtEntity.hasData(MTAttachmentTypes.TEAM) || !attackEntity.hasData(MTAttachmentTypes.TEAM)) {
                return;
            }

            TeamAttachment hurtEntityTeam = hurtEntity.getData(MTAttachmentTypes.TEAM);
            TeamAttachment attackEntityTeam = attackEntity.getData(MTAttachmentTypes.TEAM);

            if (!hurtEntityTeam.getTeamUid().equals(attackEntityTeam.getTeamUid())){
                return;
            }
            if (!hurtEntityTeam.isCanPvP() || !attackEntityTeam.isCanPvP()){
                event.setCanceled(true);
            }
        }
    }
}