# Unofficial Minecraft 26.3 (Fabric) port of Ponder + Catnip

Branch `mc26.3/dev` is an **unofficial** port of [Ponder](https://github.com/Creators-of-Create/Ponder) (and the bundled
Catnip library) by simibubi, Pepper, zelophed and the Creators of Create contributors, based on their `mc26.1/dev`
branch. It exists so Create can be brought to 26.3; please report problems here, not upstream.

Only the Fabric side is built (NeoForge is excluded). Highlights of the port:
- `MultiBufferSource` was removed from 26.3. Catnip now ships a compatible `net.createmod.catnip.compat.render.MultiBufferSource`
  (records vertices; `submitting(collector)` turns them into custom-geometry submits, `BufferSource#endBatch` draws immediately).
- Picture-in-picture GUI renderers (Ponder scenes, GUI block models/BEs/fluids) use 26.3's submit API; the Ponder scene
  transform is carried by the model-view matrix so particles get it too.
- Ponder schematics are data-fixed on load (26.3 renamed palette keys `Name`/`Properties` → `id`/`properties`).
- Fabric config backend implemented on Forge Config API Port (was "NYI"); config values fall back to defaults until loaded.
- Catnip command argument types registered on Fabric; virtual (Ponder/schematic) entities get ids (26.3 requires them).
- `FriendlyByteBufCompat` re-adds FriendlyByteBuf helpers 26.3 removed; GLFW → SDL key constants; many API renames.

Verified with a client gametest that opens the debug Ponder scenes (blocks, fluids, entities, particles).
