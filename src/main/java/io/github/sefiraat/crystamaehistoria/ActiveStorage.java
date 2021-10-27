package io.github.sefiraat.crystamaehistoria;

import io.github.sefiraat.crystamaehistoria.magic.CastInformation;
import io.github.sefiraat.crystamaehistoria.magic.spells.core.MagicProjectile;
import io.github.sefiraat.crystamaehistoria.runnables.spells.SpellTick;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActiveStorage {

    @Getter
    private final Map<MagicProjectile, Pair<CastInformation, Long>> projectileMap = new HashMap<>();
    @Getter
    private final Map<UUID, Pair<CastInformation, Long>> strikeMap = new HashMap<>();
    @Getter
    private final Map<SpellTick, Integer> tickingCastables = new HashMap<>();
    @Getter
    private final Map<BlockPosition, Long> blocksToRemove = new HashMap<>();
    @Getter
    private final Map<Entity, Long> summonedEntities = new HashMap<>();

    public void removeProjectile(MagicProjectile magicProjectile) {
        projectileMap.remove(magicProjectile);
    }

    public void clearAll() {
        // Cancels all outstanding spells being cast
        for (SpellTick spellTick : tickingCastables.keySet()) {
            spellTick.cancel();
        }
        tickingCastables.clear();

        // Clear all projectiles created from spells
        for (MagicProjectile magicProjectile : projectileMap.keySet()) {
            magicProjectile.kill();
        }
        projectileMap.clear();

        // Clear all spawned entities created from spells
        for (Entity entity : summonedEntities.keySet()) {
            entity.remove();
        }
        summonedEntities.clear();

        // Remove all temporary blocks
        removeBlocks(true);
        blocksToRemove.clear();
    }

    public void removeBlocks(boolean forceRemoveAll) {
        long time = System.currentTimeMillis();
        for (Map.Entry<BlockPosition, Long> entry : blocksToRemove.entrySet()) {
            if (forceRemoveAll || entry.getValue() < time) {
                entry.getKey().getBlock().setType(Material.AIR);
            }
        }
    }

    public void stopBlockRemoval(Block block) {
        BlockPosition blockPosition = new BlockPosition(block);
        blocksToRemove.remove(blockPosition);
    }


}