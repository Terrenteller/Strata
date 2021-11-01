package com.riintouge.strata.sound;

import com.riintouge.strata.Strata;
import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public final class SoundTypeHelper
{
    public static final SoundTypeHelper INSTANCE = new SoundTypeHelper();

    private final Map< String , SoundEvent > soundEventMap = new HashMap<>();

    public SoundType create(
        float volume,
        float pitch,
        String breakResource,
        String fallResource,
        String hitResource,
        String placeResource,
        String stepResource )
    {
        SoundEvent breakEvent = INSTANCE.soundEventMap.computeIfAbsent( breakResource , resource -> createSoundEvent( breakResource ) );
        SoundEvent fallEvent = INSTANCE.soundEventMap.computeIfAbsent( fallResource , resource -> createSoundEvent( fallResource ) );
        SoundEvent hitEvent = INSTANCE.soundEventMap.computeIfAbsent( hitResource , resource -> createSoundEvent( hitResource ) );
        SoundEvent placeEvent = INSTANCE.soundEventMap.computeIfAbsent( placeResource , resource -> createSoundEvent( placeResource ) );
        SoundEvent stepEvent = INSTANCE.soundEventMap.computeIfAbsent( stepResource , resource -> createSoundEvent( stepResource ) );

        return new SoundType( volume , pitch , breakEvent , stepEvent , placeEvent , hitEvent , fallEvent );
    }

    private SoundEvent createSoundEvent( String resource )
    {
        ResourceLocation resourceLocation = new ResourceLocation( resource );
        return new SoundEvent( resourceLocation ).setRegistryName( resourceLocation );
    }

    // Statics

    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void registerSoundEvents( RegistryEvent.Register< SoundEvent > event )
    {
        for( SoundEvent soundEvent : INSTANCE.soundEventMap.values() )
            if( soundEvent.getRegistryName().getResourceDomain().equals( Strata.modid ) )
                event.getRegistry().register( soundEvent );
    }
}
