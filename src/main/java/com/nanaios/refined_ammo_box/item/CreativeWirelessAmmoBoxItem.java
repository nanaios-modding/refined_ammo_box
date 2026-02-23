package com.nanaios.refined_ammo_box.item;

import com.nanaios.refined_ammo_box.RefinedAmmoBoxLang;
import com.nanaios.refined_ammo_box.util.RSLinkHelper;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreativeWirelessAmmoBoxItem extends WirelessAmmoBoxItem {
    @Override
    public int getEnergyStored(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (RSLinkHelper.isValid(stack)) {
            tooltip.add(RefinedAmmoBoxLang.NETWORK_TOOL_TIP.get(
                    RSLinkHelper.getX(stack),
                    RSLinkHelper.getY(stack),
                    RSLinkHelper.getZ(stack)
            ).setStyle(Styles.GRAY));
        }
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return RefinedAmmoBoxLang.CREATIVE_WIRELESS_AMMO_BOX_NAME.get().withStyle(ChatFormatting.DARK_PURPLE);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
        return maxExtract;
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canExtract(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canReceive(ItemStack stack) {
        return false;
    }
}