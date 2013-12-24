package com.khorn.terraincontrol.generator.resource;

import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.exception.InvalidConfigException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlantGen extends Resource
{
    private PlantType plant;

    private int minAltitude;
    private int maxAltitude;
    private List<Integer> sourceBlocks;

    @Override
    public void spawn(LocalWorld world, Random rand, boolean villageInChunk, int x, int z)
    {
        int y = rand.nextInt(maxAltitude - minAltitude) + minAltitude;

        for (int i = 0; i < 64; i++)
        {
            int j = x + rand.nextInt(8) - rand.nextInt(8);
            int k = y + rand.nextInt(4) - rand.nextInt(4);
            int m = z + rand.nextInt(8) - rand.nextInt(8);
            if ((!world.isEmpty(j, k, m)) || (!sourceBlocks.contains(world.getTypeId(j, k - 1, m))))
                continue;

            plant.spawn(world, j, k, m);
        }
    }

    @Override
    public void load(List<String> args) throws InvalidConfigException
    {
        assureSize(6, args);

        plant = PlantType.getPlant(args.get(0));
        
        // Not used for terrain generation, they are used by the Forge
        // implementation to fire Forge events. We'll probably want to rewrite
        // this in the future to not use block ids
        blockId = plant.getBlockId();
        blockData = plant.getBottomBlockData();

        frequency = readInt(args.get(1), 1, 100);
        rarity = readRarity(args.get(2));
        minAltitude = readInt(args.get(3), TerrainControl.worldDepth, TerrainControl.worldHeight);
        maxAltitude = readInt(args.get(4), minAltitude + 1, TerrainControl.worldHeight);
        sourceBlocks = new ArrayList<Integer>();
        for (int i = 5; i < args.size(); i++)
        {
            sourceBlocks.add(readBlockId(args.get(i)));
        }
    }

    @Override
    public String makeString()
    {
        return "Plant(" + plant.getName() + "," + frequency + "," + rarity + "," + minAltitude + "," + maxAltitude + makeMaterial(sourceBlocks) + ")";
    }

    @Override
    public boolean isAnalogousTo(Resource other)
    {
        return getClass() == other.getClass() && other.blockId == this.blockId && other.blockData == this.blockData;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 71 * hash + super.hashCode();
        hash = 71 * hash + this.minAltitude;
        hash = 71 * hash + this.maxAltitude;
        hash = 71 * hash + (this.sourceBlocks != null ? this.sourceBlocks.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object other)
    {
        if (!super.equals(other))
            return false;
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (getClass() != other.getClass())
            return false;
        final PlantGen compare = (PlantGen) other;
        return this.minAltitude == compare.minAltitude
               && this.maxAltitude == compare.maxAltitude
               && (this.sourceBlocks == null ? this.sourceBlocks == compare.sourceBlocks
                   : this.sourceBlocks.equals(compare.sourceBlocks));
    }

}