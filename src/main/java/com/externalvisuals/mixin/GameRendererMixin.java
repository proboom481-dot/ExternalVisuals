package com.externalvisuals.mixin;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.NoHurtCam;
import com.externalvisuals.modules.camera.ScreenStretch;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(
            method = "getBasicProjectionMatrix",
            at = @At("RETURN")
    )
    private void externalVisuals$applyScreenStretch(
            Camera camera,
            float tickDelta,
            boolean changingFov,
            CallbackInfoReturnable<Matrix4f> callback
    ) {
        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        ScreenStretch stretch =
                ExternalVisuals.MODULE_MANAGER.get(ScreenStretch.class);

        if (stretch == null || !stretch.isEnabled()) {
            return;
        }

        Matrix4f projection = callback.getReturnValue();
        if (projection == null) {
            return;
        }

        projection.multiply(
                Matrix4f.scale(
                        stretch.getHorizontalScale(),
                        1.0f,
                        1.0f
                )
        );
    }
}
