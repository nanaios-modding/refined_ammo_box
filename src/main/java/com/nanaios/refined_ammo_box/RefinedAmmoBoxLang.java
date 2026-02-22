package com.nanaios.refined_ammo_box;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum RefinedAmmoBoxLang {
    CREATIVE_TAB_NAME("itemGroup.refined_ammo_box.creative_tab"),
    AMMO_BOX_NAME("item.refined_ammo_box.ammo_box"),
    ENERGY_STORED("misc.refinedstorage.energy_stored"),
    CLEAR_AMMO_DATA("chat.refined_ammo_box.cleared_ammo_data"),
    NETWORK_NOT_FOUND("misc.refinedstorage.network_item.not_found"),
    NETWORK_TOOL_TIP("misc.refinedstorage.network_item.tooltip"),
    CREATIVE_WIRELESS_AMMO_BOX_NAME("item.refined_ammo_box.creative_ammo_box");

    private final String key;
    RefinedAmmoBoxLang(String key) {
        this.key = key;
    }

    public MutableComponent get(Object... args) {
        return Component.translatable(this.key, args);
    }
}
