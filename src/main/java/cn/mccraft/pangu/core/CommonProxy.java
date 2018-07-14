package cn.mccraft.pangu.core;

import cn.mccraft.pangu.core.loader.AnnotationInjector;
import cn.mccraft.pangu.core.loader.InstanceHolder;
import cn.mccraft.pangu.core.loader.Proxy;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;

/**
 * Common proxy of Pangu Core
 * Pangu Core 公共代理类
 *
 * @author trychen
 * @since 1.0.0.2
 */
public class CommonProxy {

    public CommonProxy() {
    }

    /*
     *  ==============================
     *        FML Initialization
     */

    public void construction(FMLConstructionEvent event) {
        InstanceHolder.storeAllModInstance();
        AnnotationInjector.INSTANCE.startSolveAutoWireds();
        AnnotationInjector.INSTANCE.startSolveInjectors();
        Proxy.INSTANCE.invoke(event, LoaderState.CONSTRUCTING, Side.SERVER);
    }

    public void preInit(FMLPreInitializationEvent event) {
        Proxy.INSTANCE.invoke(event, LoaderState.PREINITIALIZATION, Side.SERVER);
    }

    public void init(FMLInitializationEvent event) {
        Proxy.INSTANCE.invoke(event, LoaderState.INITIALIZATION, Side.SERVER);
    }

    public void postInit(FMLPostInitializationEvent event) {
        Proxy.INSTANCE.invoke(event, LoaderState.POSTINITIALIZATION, Side.SERVER);
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        Proxy.INSTANCE.invoke(event, LoaderState.AVAILABLE, Side.SERVER);
    }

    /*
     *        FML Initialization
     *  ==============================
     */
}
