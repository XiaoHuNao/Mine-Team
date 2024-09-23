package com.xiaohunao.mine_team;

import com.mojang.logging.LogUtils;
import com.xiaohunao.mine_team.common.config.MineTeamConfig;
import com.xiaohunao.mine_team.common.config.ModJsonConfig;
import com.xiaohunao.mine_team.common.event.RegisterTeamDataEvent;
import com.xiaohunao.mine_team.common.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


@Mod(MineTeam.MOD_ID)
public class MineTeam {
    public static final String MOD_ID = "mine_team";
    public static final Logger LOGGER = LogUtils.getLogger();
    public MineTeam() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
//        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::onFMLCommonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MineTeamConfig.CONFIG, "mine_team.toml");
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    public static String asResourceKey(String path) {
        return MOD_ID + "." + path;
    }

    @SubscribeEvent
    public void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.register();
            ModJsonConfig.INSTANCE.init();
            MinecraftForge.EVENT_BUS.start();
            MinecraftForge.EVENT_BUS.post(new RegisterTeamDataEvent());
        });
    }






}
