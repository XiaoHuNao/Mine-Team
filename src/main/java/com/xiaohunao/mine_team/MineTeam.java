package com.xiaohunao.mine_team;

import com.mojang.logging.LogUtils;
import com.xiaohunao.mine_team.common.config.MineTeamConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mod(MineTeam.MOD_ID)
public class MineTeam {
    public static final String MOD_ID = "mine_team";
    public static final Logger LOGGER = LogUtils.getLogger();
    public MineTeam(IEventBus modEventBus, ModContainer modContainer) {
//        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::onFMLCommonSetup);
        modContainer.registerConfig(ModConfig.Type.COMMON, MineTeamConfig.CONFIG, "mine_team.toml");
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
    public static String asResourceKey(String path) {
        return MOD_ID + "." + path;
    }

    @SubscribeEvent
    public void onFMLCommonSetup(FMLCommonSetupEvent event) {
        MineTeamConfig.loadTamingMaterials();
    }

    public static void extracted(CallbackInfoReturnable<Boolean> cir) {
        Boolean returnValue = cir.getReturnValue();
        if (!returnValue){
            cir.setReturnValue(MineTeamConfig.allowDamageSelf.get());
        }
    }


    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
