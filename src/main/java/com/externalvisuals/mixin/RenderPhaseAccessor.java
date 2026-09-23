package com.externalvisuals.mixin;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPhase.class)
public interface RenderPhaseAccessor {

    @Accessor("ALWAYS_DEPTH_TEST")
    static RenderPhase.DepthTest getAlwaysDepthTest() {
        throw new AssertionError();
    }
}