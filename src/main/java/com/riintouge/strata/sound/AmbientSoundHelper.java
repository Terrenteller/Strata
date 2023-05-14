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
    // These numbers were derived from experimentation and not from a definitive resource
    private static final long MINIMUM_MILLIS_TO_NEXT_THRESHOLD = 3 * 1000;
    private static final int MAXIMUM_ADDITIONAL_MILLIS_TO_NEXT_THRESHOLD = 15 * 1000;
    private static long nextSoundThresholdMillis = System.currentTimeMillis() + MINIMUM_MILLIS_TO_NEXT_THRESHOLD;

    public static void playForRandomDisplayTick(
        World world,
        BlockPos pos,
        Random random,
        SoundEventTuple soundEventTuple )
    {
        long currentTimeMillis = System.currentTimeMillis();
        if( currentTimeMillis >= nextSoundThresholdMillis )
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

            nextSoundThresholdMillis = currentTimeMillis
                + MINIMUM_MILLIS_TO_NEXT_THRESHOLD
                + random.nextInt( MAXIMUM_ADDITIONAL_MILLIS_TO_NEXT_THRESHOLD );
        }
    }
}
