package com.nanaios.refined_ammo_box.item;

import com.nanaios.refined_ammo_box.RefinedAmmoBoxLang;
import com.nanaios.refined_ammo_box.util.RSLinkHelper;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.item.capabilityprovider.EnergyCapabilityProvider;
import com.refinedmods.refinedstorage.render.Styles;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.EmptyEnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WirelessAmmoBoxItem extends AmmoBoxItem {
    @Override
    public @NotNull Component getName(ItemStack stack) {
        return RefinedAmmoBoxLang.AMMO_BOX_NAME.get();
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack ammoBox, Slot slot, ClickAction action, Player player) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag) {
        return new EnergyCapabilityProvider(stack, RS.SERVER_CONFIG.getWirelessGrid().getCapacity());
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(ForgeCapabilities.ENERGY).orElse(EmptyEnergyStorage.INSTANCE);
        float stored = (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
        return Math.round(stored * 13F);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        IEnergyStorage energy = stack.getCapability(ForgeCapabilities.ENERGY, null).orElse(EmptyEnergyStorage.INSTANCE);
        return Mth.hsvToRgb(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(ctx.getHand());

        INetwork network = NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromBlockEntity(ctx.getLevel().getBlockEntity(ctx.getClickedPos())));
        if (network != null) {
            RSLinkHelper.putNode(
                    stack,
                    network.getPosition().getX(),
                    network.getPosition().getY(),
                    network.getPosition().getZ(),
                    ctx.getLevel().dimension().location().toString()
            );

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        IEnergyStorage energy = stack.getCapability(ForgeCapabilities.ENERGY, null).orElse(EmptyEnergyStorage.INSTANCE);
        tooltip.add(RefinedAmmoBoxLang.ENERGY_STORED.get(energy.getEnergyStored(), energy.getMaxEnergyStored()).setStyle(Styles.GRAY));
        if (RSLinkHelper.isValid(stack)) {
            tooltip.add(RefinedAmmoBoxLang.NETWORK_TOOL_TIP.get(
                    RSLinkHelper.getX(stack),
                    RSLinkHelper.getY(stack),
                    RSLinkHelper.getZ(stack)
            ).setStyle(Styles.GRAY));
        }
    }
}
