package com.xiaohunao.mine_team.common.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xiaohunao.mine_team.common.capability.TeamCapability;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private <E extends Entity> void onRenderEntity(E entity, double x, double y, double z, float rotationYaw, float partialTicks, PoseStack poseStack, MultiBufferSource Buffer, int pPackedLight, CallbackInfo ci) {
        poseStack.pushPose();
        if (entity instanceof LivingEntity livingEntity && !(entity instanceof Player)){
            TeamCapability.get(livingEntity).ifPresent(teamCapability -> {
                if (teamCapability.data.getFranticTime() > 0) {
                    poseStack.mulPose(Axis.YP.rotationDegrees((float) (Math.cos((double)entity.tickCount * 3.25D) * Math.PI * 0.5F)));
                }
            });
        }
        poseStack.popPose();
    }
}
