package com.nanaios.refined_ammo_box;

import com.nanaios.refined_ammo_box.config.RefinedAmmoBoxConfig;
import com.nanaios.refined_ammo_box.registries.RefinedAmmoBoxCreativeTabs;
import com.nanaios.refined_ammo_box.registries.RefinedAmmoBoxItems;
import com.tacz.guns.GunMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RefinedAmmoBox.MODID)
@Mod.EventBusSubscriber(modid = RefinedAmmoBox.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RefinedAmmoBox {
    public static final String MODID = "refined_ammo_box";
    public static final Logger LOGGER = LogManager.getLogger();

    public RefinedAmmoBox(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // クリエイティブタブとアイテムの登録
        RefinedAmmoBoxItems.ITEMS.register(modEventBus);
        RefinedAmmoBoxCreativeTabs.TABS.register(modEventBus);

        // コンフィグの登録
        context.registerConfig(ModConfig.Type.COMMON, RefinedAmmoBoxConfig.init());
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        ResourceLocation targetTabLocation = ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID,"other");
        if(!event.getTabKey().location().equals(targetTabLocation)) return;

        LOGGER.info("Registering Refined Ammo Box items to other creative tab...");
        RefinedAmmoBoxItems.registerCreativeTab(event);
    }
}
