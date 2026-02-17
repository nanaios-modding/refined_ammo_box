package com.nanaios.refined_ammo_box.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

public interface IEnergyItem {
    default int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(cap -> cap.receiveEnergy(maxReceive, simulate)).orElse(0);
    }
    default int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(cap -> cap.extractEnergy(maxExtract, simulate)).orElse(0);
    }

    default int getEnergyStored(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }
    default int getMaxEnergyStored(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }
    default boolean canExtract(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::canExtract).orElse(false);
    }
     default boolean canReceive(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::canReceive).orElse(false);
    }
}
