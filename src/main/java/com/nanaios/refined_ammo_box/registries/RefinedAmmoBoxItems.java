package com.nanaios.refined_ammo_box.registries;

import com.nanaios.refined_ammo_box.RefinedAmmoBox;
import com.nanaios.refined_ammo_box.item.IEnergyItem;
import com.nanaios.refined_ammo_box.item.WirelessAmmoBoxItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RefinedAmmoBoxItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RefinedAmmoBox.MODID);

    public static RegistryObject<Item> AMMO_BOX = ITEMS.register("ammo_box", WirelessAmmoBoxItem::new);

    public static void registerCreativeTab(CreativeModeTab.Output output) {
        for(RegistryObject<Item> registry : ITEMS.getEntries()){
            Item item = registry.get();
            output.accept(item);

            // 満充電のアイテムも追加する
            if(item instanceof IEnergyItem energyItem) {
                ItemStack fullEnergyStack = new ItemStack(item);
                energyItem.receiveEnergy(fullEnergyStack, energyItem.getMaxEnergyStored(fullEnergyStack), false);
                output.accept(fullEnergyStack);

            }
        }
    }
}
