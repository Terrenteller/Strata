package com.riintouge.strata.sound;

import net.minecraft.util.SoundEvent;

public class SoundEventTuple
{
    public final float volume;
    public final float pitch;
    public final SoundEvent soundEvent;

    public SoundEventTuple( SoundEvent soundEvent )
    {
        this( 1.0f , 1.0f , soundEvent );
    }

    public SoundEventTuple( float volume , float pitch , SoundEvent soundEvent )
    {
        this.volume = volume;
        this.pitch = pitch;
        this.soundEvent = soundEvent;
    }
}
