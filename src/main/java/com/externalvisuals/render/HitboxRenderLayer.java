package com.externalvisuals.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public final class HitboxRenderLayer {

    private static final RenderLayer THROUGH_WALLS =
            RenderLayer.getLines();

    private HitboxRenderLayer() {
    }

    public static RenderLayer get() {
        return THROUGH_WALLS;
    }
}