package com.khorn.terraincontrol.customobjects;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.logging.LogMarker;
import com.khorn.terraincontrol.util.Rotation;
import com.khorn.terraincontrol.util.minecraftTypes.TreeType;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class TreeObject implements CustomObject
{

    private TreeType type;
    private int minHeight = TerrainControl.WORLD_DEPTH;
    private int maxHeight = TerrainControl.WORLD_HEIGHT;

    public TreeObject(TreeType type)
    {
        this.type = type;
    }

    @Override
    public void onEnable(Map<String, CustomObject> otherObjectsInDirectory)
    {
        // Stub method
    }

    public TreeObject(TreeType type, Map<String, String> settings)
    {
        this.type = type;
        for (Entry<String, String> entry : settings.entrySet())
        {
            try
            {
                if (entry.getKey().equalsIgnoreCase("MinHeight"))
                {
                    this.minHeight = Math.max(TerrainControl.WORLD_DEPTH, Math.min(Integer.parseInt(entry.getValue()), TerrainControl.WORLD_HEIGHT));
                }
                if (entry.getKey().equalsIgnoreCase("MaxHeight"))
                {
                    this.maxHeight = Math.max(minHeight, Math.min(Integer.parseInt(entry.getValue()), TerrainControl.WORLD_HEIGHT));
                }
            } catch (NumberFormatException e)
            {
                TerrainControl.log(LogMarker.WARN, "Cannot parse {} of a {} tree: invalid number!", new Object[]
                {
                    entry.getKey(), type
                });
            }

        }
    }

    @Override
    public String getName()
    {
        return type.name();
    }

    @Override
    public boolean canSpawnAsTree()
    {
        return true;
    }

    @Override
    public boolean canSpawnAsObject()
    {
        return false;
    }

    @Override
    public boolean spawnForced(LocalWorld world, Random random, Rotation rotation, int x, int y, int z)
    {
        return world.PlaceTree(type, random, x, y, z);
    }

    @Override
    public boolean spawnAsTree(LocalWorld world, Random random, int x, int z)
    {
        int y = world.getHighestBlockYAt(x, z);
        if (y < minHeight || y > maxHeight)
        {
            return false;
        }
        return world.PlaceTree(type, random, x, y, z);
    }

    @Override
    public boolean process(LocalWorld world, Random random, int chunkX, int chunkZ)
    {
        // A tree has no rarity, so spawn it once in the chunk
        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);
        return spawnAsTree(world, random, x, z);
    }

    @Override
    public CustomObject applySettings(Map<String, String> settings)
    {
        return new TreeObject(type, settings);
    }

    @Override
    public boolean hasPreferenceToSpawnIn(LocalBiome biome)
    {
        return true;
    }

    @Override
    public boolean canSpawnAt(LocalWorld world, Rotation rotation, int x, int y, int z)
    {
        return true; // We can only guess...
    }

    @Override
    public boolean canRotateRandomly()
    {
        // Trees cannot be rotated
        return false;
    }

}
