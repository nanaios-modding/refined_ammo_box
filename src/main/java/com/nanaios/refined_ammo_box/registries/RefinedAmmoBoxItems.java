package com.nanaios.refined_ammo_box.registries;

import com.nanaios.refined_ammo_box.RefinedAmmoBox;
import com.nanaios.refined_ammo_box.item.CreativeWirelessAmmoBoxItem;
import com.nanaios.refined_ammo_box.item.IEnergyItem;
import com.nanaios.refined_ammo_box.item.TabIconItem;
import com.nanaios.refined_ammo_box.item.WirelessAmmoBoxItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RefinedAmmoBoxItems {
    public static final DeferredRegister<Item> WIRELESS_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RefinedAmmoBox.MODID);
    public static final DeferredRegister<Item> FAKE_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,RefinedAmmoBox.MODID);

    /// タブのアイコンアイテム
    public static final RegistryObject<Item> ICON = FAKE_ITEMS.register("tab_icon", TabIconItem::new);

    /// アイテムの登録
    static {
        WIRELESS_ITEMS.register("ammo_box", WirelessAmmoBoxItem::new);
        WIRELESS_ITEMS.register("creative_ammo_box", CreativeWirelessAmmoBoxItem::new);
    }

    public static void registerCreativeTab(CreativeModeTab.Output output) {
        for(RegistryObject<Item> registry : WIRELESS_ITEMS.getEntries()){
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
