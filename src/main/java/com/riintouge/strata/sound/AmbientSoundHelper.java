package com.riintouge.strata.sound;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly( Side.CLIENT )
public final class AmbientSoundHelper
{
    private static final long MinimumMillisToNextThreshold = 3 * 1000;
    private static final int MaximumAdditionalMillisToNextThreshold = 15 * 1000;
    private static long NextSoundThresholdMillis = System.currentTimeMillis() + MinimumMillisToNextThreshold;

    public static void playForRandomDisplayTick(
        World world,
        BlockPos pos,
        Random random,
        SoundEventTuple soundEventTuple )
    {
        long currentTimeMillis = System.currentTimeMillis();
        if( currentTimeMillis >= NextSoundThresholdMillis )
        {
            world.playSound(
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                soundEventTuple.soundEvent,
                SoundCategory.AMBIENT,
                soundEventTuple.volume,
                soundEventTuple.pitch,
                false );

            NextSoundThresholdMillis = currentTimeMillis
                + MinimumMillisToNextThreshold
                + random.nextInt( MaximumAdditionalMillisToNextThreshold );
        }
    }
}
