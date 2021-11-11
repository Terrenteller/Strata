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

public final class SoundEventRegistry
{
    public static final SoundEventRegistry INSTANCE = new SoundEventRegistry();

    private final Map< String , SoundEvent > soundEventMap = new HashMap<>();

    public SoundEvent register( String soundResource )
    {
        return INSTANCE.soundEventMap.computeIfAbsent( soundResource , resource ->
        {
            ResourceLocation resourceLocation = new ResourceLocation( resource );
            return new SoundEvent( resourceLocation ).setRegistryName( resourceLocation );
        } );
    }

    public SoundType registerAndCreate(
        float volume,
        float pitch,
        String breakResource,
        String stepResource,
        String placeResource,
        String hitResource,
        String fallResource )
    {
        return new SoundType(
            volume,
            pitch,
            register( breakResource ),
            register( stepResource ),
            register( placeResource ),
            register( hitResource ),
            register( fallResource ) );
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
