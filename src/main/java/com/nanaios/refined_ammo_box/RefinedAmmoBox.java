package com.nanaios.refined_ammo_box;

import com.nanaios.refined_ammo_box.config.RefinedAmmoBoxConfig;
import com.nanaios.refined_ammo_box.registries.RefinedAmmoBoxCreativeTabs;
import com.nanaios.refined_ammo_box.registries.RefinedAmmoBoxItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RefinedAmmoBox.MODID)
public class RefinedAmmoBox {
    public static final String MODID = "refined_ammo_box";
    public static final Logger LOGGER = LogManager.getLogger();

    public RefinedAmmoBox(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        RefinedAmmoBoxItems.ITEMS.register(modEventBus);
        RefinedAmmoBoxCreativeTabs.TABS.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, RefinedAmmoBoxConfig.init());
    }
}
