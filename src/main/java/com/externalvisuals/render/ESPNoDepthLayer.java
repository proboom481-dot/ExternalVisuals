package com.externalvisuals.render;

import com.externalvisuals.mixin.RenderPhaseAccessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;

public final class ESPNoDepthLayer {

    private ESPNoDepthLayer() {
    }

    public static final RenderLayer LINES =
            RenderLayer.of(
                    "externalvisuals_esp_no_depth",
                    VertexFormats.POSITION_COLOR,
                    1,
                    256,
                    false,
                    false,
                    RenderLayer.MultiPhaseParameters.builder()
                            .depthTest(
                                    RenderPhaseAccessor.getAlwaysDepthTest()
                            )
                            .build(false)
            );
}