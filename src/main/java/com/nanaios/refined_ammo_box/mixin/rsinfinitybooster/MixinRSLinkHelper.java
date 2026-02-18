package com.nanaios.refined_ammo_box.mixin.rsinfinitybooster;

import com.llamalad7.mixinextras.sugar.Local;
import com.nanaios.refined_ammo_box.util.RSLinkHelper;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.WirelessTransmitterNetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.hexeption.rsinfinitybooster.utils.CardUtil;

@Mixin(value = RSLinkHelper.class,remap = false)
public class MixinRSLinkHelper {
    @Inject(method = "isInnerRange",at = @At(value = "INVOKE", target = "com/refinedmods/refinedstorage/apiimpl/network/node/WirelessTransmitterNetworkNode.getDimension()Lnet/minecraft/resources/ResourceKey;"), cancellable = true)
    private static void refined_ammo_box$isInnerRange$addDimensionCardRange(INetwork network, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(name = "transmitter") WirelessTransmitterNetworkNode transmitter) {
        BaseItemHandler handler = transmitter.getUpgrades();
        if(CardUtil.isDimensionCard(handler)){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isInnerRange",at = @At(value = "INVOKE", target = "com/refinedmods/refinedstorage/apiimpl/network/node/WirelessTransmitterNetworkNode.getRange()I"), cancellable = true)
    private static void refined_ammo_box$isInnerRange$addInfinityCardRange(INetwork network, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(name = "transmitter") WirelessTransmitterNetworkNode transmitter) {
        BaseItemHandler handler = transmitter.getUpgrades();
        if(CardUtil.isInfinityCard(handler)){
            cir.setReturnValue(true);
        }
    }
}
