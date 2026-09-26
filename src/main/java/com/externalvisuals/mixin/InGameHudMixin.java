package com.externalvisuals.mixin;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.CustomCrosshair;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(
            method = "renderCrosshair",
            at = @At("HEAD"),
            cancellable = true
    )
    private void externalVisuals$hideVanillaCrosshair(
            MatrixStack matrices,
            CallbackInfo callbackInfo
    ) {

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        CustomCrosshair crosshair =
                ExternalVisuals.MODULE_MANAGER
                        .get(CustomCrosshair.class);

        if (crosshair != null && crosshair.isEnabled()) {
            callbackInfo.cancel();
        }
    }
}