package org.arc.efp.compat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class EFPCompatManager {

    public static boolean isEFXLoaded;
    public static boolean isEFNLoaded;
    public static boolean isSkillTreeLoaded;

    public static void setup() {
        isEFXLoaded = ModList.get().isLoaded("epicfightx");
        if (isEFXLoaded) {
            MinecraftForge.EVENT_BUS.register(EFXCompat.class);
        }

        isEFNLoaded = ModList.get().isLoaded("efn");
        if (isEFNLoaded) {
            MinecraftForge.EVENT_BUS.register(EFNCompat.class);
        }

        isSkillTreeLoaded = ModList.get().isLoaded("epicskills");
        if (isSkillTreeLoaded) {
            MinecraftForge.EVENT_BUS.register(EpicSkillsPonderCompat.class);
        }
    }
}