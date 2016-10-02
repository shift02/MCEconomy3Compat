package shift.mceconomy3compat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MCEconomy3Compat.MODID, version = MCEconomy3Compat.VERSION)
public class MCEconomy3Compat {
    public static final String MODID = "mceconomy3compat";
    public static final String VERSION = "1.0";

    public static final Logger log = LogManager.getLogger("MCEconomy3Compat");

    public static FileManager manager;

    @EventHandler
    public void init(FMLPreInitializationEvent event) {

        manager = new FileManager(event.getSourceFile());

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        System.out.println("DIRT BLOCK >> " + Blocks.DIRT.getUnlocalizedName());

    }

    @EventHandler
    public void init(FMLPostInitializationEvent event) {

        manager.loadMP();

    }

}
