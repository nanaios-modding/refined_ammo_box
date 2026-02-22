package com.nanaios.refined_ammo_box.client;

import com.nanaios.refined_ammo_box.RefinedAmmoBox;
import com.nanaios.refined_ammo_box.item.WirelessAmmoBoxItem;
import com.nanaios.refined_ammo_box.registries.RefinedAmmoBoxItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = RefinedAmmoBox.MODID)
public class RefinedAmmoBoxClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        for(RegistryObject<Item> registry : RefinedAmmoBoxItems.WIRELESS_ITEMS.getEntries()) {
            ItemProperties.register(
                    registry.get(),
                    ResourceLocation.fromNamespaceAndPath(RefinedAmmoBox.MODID, "linked"),
                    RefinedAmmoBoxClient::isLighting
            );
        }


    }

    public static float isLighting(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        if (stack.getItem() instanceof WirelessAmmoBoxItem item) {
            if (item.isLinked(stack) && item.getEnergyStored(stack) > 0) {
                return 1.0f;
            } else {
                return 0.0f;
            }
        }
        return 1.0f;
    }
}
