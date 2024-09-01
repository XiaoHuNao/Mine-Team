package com.xiaohunao.mine_team.common.mixin;

import com.xiaohunao.mine_team.common.config.MineTeamConfig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "canHarmPlayer", at = @At("RETURN"), cancellable = true)
    private void mine_team$canHarmPlayer(Player other, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            cir.setReturnValue(MineTeamConfig.allowDamageSelf.get());
        }
    }
}
