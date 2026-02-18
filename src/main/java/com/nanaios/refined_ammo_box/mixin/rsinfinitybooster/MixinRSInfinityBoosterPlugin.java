package com.nanaios.refined_ammo_box.mixin.rsinfinitybooster;

import com.nanaios.refined_ammo_box.RefinedAmmoBox;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinRSInfinityBoosterPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        boolean canApply = LoadingModList.get().getModFileById("rsinfinitybooster") != null;
        if (canApply) {
            RefinedAmmoBox.LOGGER.info("Applying Mixin to RS Infinity Booster");
        } else {
            RefinedAmmoBox.LOGGER.info("RS Infinity Booster is not loaded, skipping Mixin");
        }
        return canApply;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
