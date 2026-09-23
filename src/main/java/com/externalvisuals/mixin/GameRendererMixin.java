package com.externalvisuals.mixin;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.NoHurtCam;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public final class GameRendererMixin {

    @Inject(
            method = "bobViewWhenHurt",
            at = @At("HEAD"),
            cancellable = true
    )
    private void externalVisuals$disableHurtCam(
            MatrixStack matrices,
            float tickDelta,
            CallbackInfo callbackInfo
    ) {

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        NoHurtCam module =
                ExternalVisuals.MODULE_MANAGER.get(NoHurtCam.class);

        if (module != null && module.isEnabled()) {
            callbackInfo.cancel();
        }
    }
}
