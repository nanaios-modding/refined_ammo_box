package com.nanaios.refined_ammo_box.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RSLinkHelper {
    private static final String NBT_NODE_X = "NodeX";
    private static final String NBT_NODE_Y = "NodeY";
    private static final String NBT_NODE_Z = "NodeZ";
    private static final String NBT_DIMENSION = "Dimension";

    @Nullable
    public static ResourceKey<Level> getDimension(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null) return null;

        if (tag.contains(NBT_DIMENSION)) {
            ResourceLocation name = ResourceLocation.tryParse(stack.getTag().getString(NBT_DIMENSION));
            if (name == null) {
                return null;
            }
            return ResourceKey.create(Registries.DIMENSION, name);
        }

        return null;
    }

    public static int getX(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null) return 0;
        return stack.getTag().getInt(NBT_NODE_X);
    }

    public static int getY(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null) return 0;
        return stack.getTag().getInt(NBT_NODE_Y);
    }

    public static int getZ(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null) return 0;
        return stack.getTag().getInt(NBT_NODE_Z);
    }

    public static void putNode(ItemStack stack, int x, int y, int z, String dimensionKey) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(NBT_NODE_X, x);
        tag.putInt(NBT_NODE_Y, y);
        tag.putInt(NBT_NODE_Z, z);
        tag.putString(NBT_DIMENSION, dimensionKey);
    }

    /// 弾薬をAE2ネットワークから取得する
    ///
    /// @param pos     弾薬箱の座標
    /// @param ammoBox 弾薬箱のItemStack
    /// @param ammo    弾薬のItemStack
    /// @param count   更新する弾薬数の上限
    /// @param mode    抽出モード
    public static ActionResult extractionAmmo(Level level, BlockPos pos, ItemStack ammoBox, ItemStack ammo, int count) {
        // 座標を取得
        GlobalPos linkPos = AE2LinkHelper.getLinkedPosition(ammoBox);
        if (linkPos == null) return new ActionResult(ActionResult.Status.DEVICE_NOT_LINKED, 0);

        // グリッドを取得
        IGrid grid = AE2LinkHelper.getGrid(linkPos);
        if (grid == null) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

        // 有効範囲内のアクセスポイントを取得
        IWirelessAccessPoint wap = AE2LinkHelper.getBestWap(grid, level, pos);
        if (wap == null) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

        // グリッドノードを取得
        IGridNode node = wap.getActionableNode();
        if (node == null) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

        // 弾薬のデータを生成
        IActionSource source = new BaseActionSource();
        AEKey key = AEItemKey.of(ammo);
        if (key == null) return new ActionResult(ActionResult.Status.SUCCESS, 0);

        // 弾薬の数を取得
        int ammoCount = (int) StorageHelper.poweredExtraction(new ChannelPowerSrc(node, grid.getEnergyService()), grid.getStorageService().getInventory(), key, count, source, mode);
        // 弾薬数を0以上に補正
        ammoCount = Math.max(0, ammoCount);

        return new ActionResult(ActionResult.Status.SUCCESS, ammoCount);
    }

    public record ActionResult(Status status, int count) {
        public enum Status {
            SUCCESS,
            DEVICE_NOT_LINKED,
            LINKED_NETWORK_NOT_FOUND
        }
    }

    public static boolean isValid(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null) return false;

        return stack.getTag().contains(NBT_NODE_X)
                && stack.getTag().contains(NBT_NODE_Y)
                && stack.getTag().contains(NBT_NODE_Z)
                && stack.getTag().contains(NBT_DIMENSION);
    }
}
