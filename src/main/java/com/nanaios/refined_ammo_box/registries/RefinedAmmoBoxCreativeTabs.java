package com.nanaios.refined_ammo_box.registries;

import com.nanaios.refined_ammo_box.RefinedAmmoBox;
import com.nanaios.refined_ammo_box.RefinedAmmoBoxLang;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;

public class RefinedAmmoBoxCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RefinedAmmoBox.MODID);

    static {
        TABS.register("refined_ammo_box_tab",() -> CreativeModeTab.builder()
                .title(RefinedAmmoBoxLang.CREATIVE_TAB_NAME.get())
                .icon(() -> new ItemStack(RefinedAmmoBoxItems.AMMO_BOX.get()))
                .displayItems((params, output) -> RefinedAmmoBoxItems.registerCreativeTab(output))
                .build()
        );
    }
}
