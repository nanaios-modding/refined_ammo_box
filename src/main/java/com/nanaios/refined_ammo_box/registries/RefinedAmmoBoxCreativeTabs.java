package com.nanaios.refined_ammo_box.registries;

import com.nanaios.refined_ammo_box.RefinedAmmoBox;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RefinedAmmoBoxCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RefinedAmmoBox.MODID);

    static {
        TABS.register("applied_ammo_box_tab",() -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + RefinedAmmoBox.MODID + ".creative_tab"))
                .icon(() -> new ItemStack(RefinedAmmoBoxItems.AMMO_BOX.get()))
                .displayItems((params, output) -> {
                    for(RegistryObject<Item> registry : RefinedAmmoBoxItems.ITEMS.getEntries()){
                        Item item = registry.get();
                        output.accept(item);
                    }
                }).build()
        );
    }
}
