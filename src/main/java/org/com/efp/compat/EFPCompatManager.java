package org.com.efp.compat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class EFPCompatManager {

    public static boolean isEFXLoaded;

    public static void setup() {
        isEFXLoaded = ModList.get().isLoaded("epicfightx");
        if (isEFXLoaded) {
            MinecraftForge.EVENT_BUS.register(EFXCompat.class);
        }
    }
}