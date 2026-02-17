package com.nanaios.refined_ammo_box.item;

import com.nanaios.refined_ammo_box.RefinedAmmoBoxLang;
import com.nanaios.refined_ammo_box.util.RSLinkHelper;
import com.nanaios.refined_ammo_box.util.RefinedAmmoBoxMessages;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.item.capabilityprovider.EnergyCapabilityProvider;
import com.refinedmods.refinedstorage.render.Styles;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
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

public class WirelessAmmoBoxItem extends AmmoBoxItem implements ILinkableItem,IEnergyItem,ITimeStamp{
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        // 手に持っているアイテムを取得
        ItemStack stack = player.getItemInHand(hand);

        // スニーク状態で使用した時に弾薬のデータをリセットする
        if (player.isCrouching()) {
            if (!level.isClientSide) {
                clearAmmoData(stack);
                player.displayClientMessage(RefinedAmmoBoxMessages.CLEAR_AMMO_DATA.get(), true);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        // サーバーサイドでのみ動作させる
        if (level.isClientSide()) return;

        // プレイヤーでなければ処理を中断
        if (!(entity instanceof Player player)) return;

        // 弾薬のIDを取得
        ItemStack iGunStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        boolean isUpdate = updateAmmoId(stack, iGunStack);

        // 負荷軽減のため1秒に1回更新する
        if (isWantUpdate(stack) || isUpdate) {
            // 弾薬箱の座標を取得
            setPos(stack, player.blockPosition());
            setLevel(stack, level);

            // タイムスタンプを更新
            setTimeStamp(stack, System.currentTimeMillis());

            // 弾薬数を更新
            ActionResult result = updateAmmoCount(stack);

            switch (result.status()) {
                case DEVICE_NOT_LINKED -> player.displayClientMessage(PlayerMessages.DeviceNotLinked.text(), true);
                case LINKED_NETWORK_NOT_FOUND ->
                        player.displayClientMessage(PlayerMessages.LinkedNetworkNotFound.text(), true);
            }
        }
    }

    /// 弾薬箱が存在するレベルを設定する
    ///
    /// @param stack 弾薬箱のItemStack
    /// @param level レベル
    public void setLevel(ItemStack stack,Level level) {
        stack.getOrCreateTag().putString(NBT_LEVEL_KEY,level.dimension().location().toString());
    }

    /// 弾薬箱が存在するレベルを取得する
    ///
    /// @param stack 弾薬箱のItemStack
    @Nullable
    public Level getLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_LEVEL_KEY)) {
            return ServerLifecycleHooks.getCurrentServer().getLevel(
                    ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString(NBT_LEVEL_KEY)))
            );
        }
        return null;
    }

    /// 弾薬箱が存在する座標を設定する
    ///
    /// @param stack 弾薬箱のItemStack
    /// @param pos   座標
    public void setPos(ItemStack stack, BlockPos pos) {
        stack.getOrCreateTag().putLong(NBT_BLOCK_POS_KEY, pos.asLong());
    }

    /// 弾薬箱が存在する座標を取得する
    ///
    /// @param stack 弾薬箱のItemStack
    @Nullable
    public BlockPos getPos(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_BLOCK_POS_KEY)) {
            return BlockPos.of(tag.getLong(NBT_BLOCK_POS_KEY));
        }
        return null;
    }

    /// 弾薬箱の情報を更新する必要があるかどうかを返す
    ///
    /// @param stack 弾薬箱のItemStack
    public boolean isWantUpdate(ItemStack stack) {
        return (System.currentTimeMillis() - getTimeStamp(stack)) > 1000;
    }

    /// 弾薬IDを銃から取得し更新する
    /// 更新が発生した場合はtrueを返す
    ///
    /// @param ammoBox  弾薬箱のItemStack
    /// @param gunStack 銃のItemStack
    public boolean updateAmmoId(ItemStack ammoBox, ItemStack gunStack) {
        // 銃でなければ処理を中断
        if (!(gunStack.getItem() instanceof IGun gun)) return false;

        // 弾薬のIDを取得
        ResourceLocation ammoId = TimelessAPI.getCommonGunIndex(gun.getGunId(gunStack))
                .map(commonGunIndex -> commonGunIndex.getGunData().getAmmoId())
                .orElse(DefaultAssets.EMPTY_AMMO_ID);

        // 弾薬IDが同じなら処理を中断
        if (ammoId.equals(getAmmoId(ammoBox))) return false;

        // 弾薬IDを更新
        setAmmoId(ammoBox, ammoId);
        return true;
    }

    /// 弾薬数をAE2ネットワークから取得し更新する
    ///
    /// @param stack 弾薬箱のItemStack
    public ActionResult updateAmmoCount(ItemStack stack) {
        // 弾薬の情報を取得
        ItemStack ammo = AmmoItemBuilder.create().setId(getAmmoId(stack)).setCount(1).build();

        // 弾薬箱が存在するレベルと座標を取得
        BlockPos pos = getPos(stack);
        Level level = getLevel(stack);
        if (level == null || pos == null) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

        // 弾薬数を更新
        ActionResult result = AE2LinkHelper.extractionAmmo(level, pos, stack, ammo, Integer.MAX_VALUE, Actionable.SIMULATE);
        // 弾薬箱の弾薬数を直接更新
        super.setAmmoCount(stack, result.count());
        // リンク状態を更新
        this.setLinked(stack, result.status() == ActionResult.Status.SUCCESS);

        return result;
    }

    @Override
    public void setAmmoCount(ItemStack ammoBox, int count) {
        //弾薬が減少している個数を計算
        int oldCount = this.getAmmoCount(ammoBox);
        int diff = oldCount - count;
        if (diff <= 0) return;

        // 弾薬箱が存在するレベルと座標を取得
        BlockPos pos = getPos(ammoBox);
        Level level = getLevel(ammoBox);
        if (level == null || pos == null) return;

        // 弾薬をAE2ネットワークから取り出す
        ItemStack ammo = AmmoItemBuilder.create().setId(getAmmoId(ammoBox)).setCount(1).build();
        // AE2LinkHelper.extractionAmmo(level, pos, ammoBox, ammo, diff, Actionable.MODULATE);

        // エネルギーを消費
        // extractAEPower(ammoBox, AppliedAmmoBoxConfig.AMMO_BOX_USE_POWER_PER_AMMO.get() * diff, Actionable.MODULATE);

        // 弾薬数を再取得して設定
        updateAmmoCount(ammoBox);
    }

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
        return stack.getCapability(ForgeCapabilities.ENERGY).map(
                energy -> {
                    float stored = (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored();
                    return Math.round(stored * 13F);
                }
        ).orElse(0);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY, null).map(
                energy -> Mth.hsvToRgb(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F)
        ).orElse(super.getBarColor(stack));
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

    public void clearAmmoData(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if(tag == null) return;

        // 弾薬IDを削除
        if (tag.contains("AmmoId")) {
            tag.remove("AmmoId");
        }

        // 弾薬数を削除
        if(tag.contains("AmmoCount")) {
            tag.remove("AmmoCount");
        }
    }
}
