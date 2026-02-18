package com.nanaios.refined_ammo_box.util;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphEntry;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.network.node.WirelessTransmitterNetworkNode;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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

    public static boolean isValid(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null) return false;

        return stack.getTag().contains(NBT_NODE_X)
                && stack.getTag().contains(NBT_NODE_Y)
                && stack.getTag().contains(NBT_NODE_Z)
                && stack.getTag().contains(NBT_DIMENSION);
    }

    public static void putNode(ItemStack stack, int x, int y, int z, String dimensionKey) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(NBT_NODE_X, x);
        tag.putInt(NBT_NODE_Y, y);
        tag.putInt(NBT_NODE_Z, z);
        tag.putString(NBT_DIMENSION, dimensionKey);
    }

    /// RSネットワークを取得する
    ///
    /// @param stack  ネットワークアイテムのItemStack
    /// @param server MinecraftServerインスタンス
    @Nullable
    public static INetwork getNetwork(ItemStack stack, MinecraftServer server) {
        if (!isValid(stack)) return null;

        ResourceKey<Level> dimension = getDimension(stack);
        if (dimension == null) return null;

        Level nodeLevel = server.getLevel(dimension);
        if (nodeLevel == null) return null;

        BlockPos pos = new BlockPos(getX(stack), getY(stack), getZ(stack));
        if (!nodeLevel.isLoaded(pos)) return null;

        return NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromBlockEntity(nodeLevel.getBlockEntity(pos)));
    }

    public static boolean isInnerRange(INetwork network,Level level,BlockPos pos) {
        // ネットワークが動いているかチェック
        if(!network.canRun()) return false;

        for (INetworkNodeGraphEntry entry : network.getNodeGraph().all()) {
            //アクティブなnodeを取得
            INetworkNode node = entry.getNode();
            if(!node.isActive()) continue;

            // Wireless Transmitterかどうかチェック
            if(!(node instanceof WirelessTransmitterNetworkNode transmitter)) continue;

            // 同一dimensionでなければ飛ばす
            if(!transmitter.getDimension().equals(level.dimension())) continue;

            // アクセスポイントの範囲を取得
            double rangeLimit = transmitter.getRange();
            // 距離の二乗を計算し、三平方の定理で使用する
            rangeLimit *= rangeLimit;

            // 三平方の定理で距離の二乗を計算
            int offX = transmitter.getOrigin().getX() - pos.getX();
            int offY = transmitter.getOrigin().getY() - pos.getY();
            int offZ = transmitter.getOrigin().getZ() - pos.getZ();
            double distance = offX * offX + offY * offY + offZ * offZ;

            // 距離が範囲内かチェック
            if (distance < rangeLimit) return true;
        }

        return false;
    }

    /// 弾薬をRSネットワークから取得する
    ///
    /// @param pos     弾薬箱の座標
    /// @param ammoBox 弾薬箱のItemStack
    /// @param ammo    弾薬のItemStack
    /// @param count   更新する弾薬数の上限
    /// @param mode    抽出モード
    public static ActionResult extractionAmmo(Level level, BlockPos pos, ItemStack ammoBox, ItemStack ammo, int count, Action mode) {
        INetwork network = getNetwork(ammoBox, level.getServer());
        if (network == null) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

        // ネットワークの範囲内かチェック
        if (!isInnerRange(network, level, pos)) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

        // ネットワークから弾薬を取り出す
        ItemStack extract = network.extractItem(ammo, count, mode);
        return new ActionResult(ActionResult.Status.SUCCESS, extract.getCount());

    }

    public record ActionResult(Status status, int count) {
        public enum Status {
            SUCCESS,
            LINKED_NETWORK_NOT_FOUND
        }
    }
}
