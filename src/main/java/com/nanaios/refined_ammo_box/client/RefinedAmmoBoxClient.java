package com.nanaios.refined_ammo_box.client;

import com.nanaios.refined_ammo_box.RefinedAmmoBox;
import com.nanaios.refined_ammo_box.item.WirelessAmmoBoxItem;
import com.nanaios.refined_ammo_box.registries.RefinedAmmoBoxItems;
import com.nanaios.refined_ammo_box.util.RSLinkHelper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = RefinedAmmoBox.MODID)
public class RefinedAmmoBoxClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemProperties.register(
                RefinedAmmoBoxItems.AMMO_BOX.get(),
                ResourceLocation.fromNamespaceAndPath(RefinedAmmoBox.MODID, "linked"),
                (stack, level, entity, seed) -> {
                    if (stack.getItem() instanceof WirelessAmmoBoxItem) {
                        if (RSLinkHelper.isValid(stack)) {
                            return 1.0f;
                        } else {
                            return 0.0f;
                        }
                    }
                    return 1.0f;
                });

    }
}
