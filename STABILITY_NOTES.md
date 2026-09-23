# ExternalVisuals 1.4.4 stability notes

Minecraft 1.16.5 Fabric world rendering is built around the shared
`WorldRenderContext.consumers()` provider.

World render callbacks append vertices/text to the shared provider and do not
call `draw()` themselves. Vanilla flushes the provider at the appropriate
render stage.

ESP uses a dedicated no-depth line RenderLayer so ESP geometry can remain
visible through blocks without modifying entity collision boxes.

The artifact is not artificially padded to 100 MB. Large file size is not a
quality metric; padding would only waste storage and download time.
