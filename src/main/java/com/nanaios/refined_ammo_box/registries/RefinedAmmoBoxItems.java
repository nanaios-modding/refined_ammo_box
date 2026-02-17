package com.nanaios.refined_ammo_box.registries;

import com.nanaios.refined_ammo_box.RefinedAmmoBox;
import com.nanaios.refined_ammo_box.item.WirelessAmmoBoxItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RefinedAmmoBoxItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RefinedAmmoBox.MODID);

    public static RegistryObject<Item> AMMO_BOX = ITEMS.register("ammo_box", WirelessAmmoBoxItem::new);
}
