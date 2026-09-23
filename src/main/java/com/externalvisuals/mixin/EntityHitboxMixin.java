package com.externalvisuals.mixin;

import com.externalvisuals.ExternalVisuals;
import com.externalvisuals.modules.visual.Hitbox;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityHitboxMixin {

    @Inject(
            method = "getBoundingBox",
            at = @At("RETURN"),
            cancellable = true
    )
    private void externalVisuals$expandHitbox(
            CallbackInfoReturnable<Box> cir
    ) {

        if (ExternalVisuals.MODULE_MANAGER == null) {
            return;
        }

        Hitbox hitbox =
                ExternalVisuals.MODULE_MANAGER.get(Hitbox.class);

        if (hitbox == null || !hitbox.isEnabled()) {
            return;
        }

        Entity entity =
                (Entity) (Object) this;

        if (!hitbox.shouldRender(entity)) {
            return;
        }

        double expand =
                hitbox.getExpand();

        if (expand <= 0.0) {
            return;
        }

        Box original =
                cir.getReturnValue();

        if (original == null) {
            return;
        }

        Box expanded =
                new Box(
                        original.minX - expand,
                        original.minY - expand,
                        original.minZ - expand,
                        original.maxX + expand,
                        original.maxY + expand,
                        original.maxZ + expand
                );

        cir.setReturnValue(expanded);
    }
}