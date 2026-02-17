package com.nanaios.refined_ammo_box.util;

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

    public static boolean isValid(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null) return false;

        return stack.getTag().contains(NBT_NODE_X)
                && stack.getTag().contains(NBT_NODE_Y)
                && stack.getTag().contains(NBT_NODE_Z)
                && stack.getTag().contains(NBT_DIMENSION);
    }
}
