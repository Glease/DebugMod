package net.glease.debugmod;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.stream.Collectors;

import static net.glease.debugmod.ASMCallhookServer.dump;

public class DebugModContainer extends DummyModContainer {
    private LoadController controller;

    public DebugModContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "debugmod";
        meta.name = "DebugMod";
        meta.version = "1.0.1";
        meta.credits = "";
        meta.authorList = Collections.singletonList("glee8e");
        meta.description = "Find root cause of your CME.";
        meta.url = "";
        meta.updateUrl = "";
        meta.screenshots = new String[0];
        meta.logoFile = "";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        this.controller = controller;
        bus.register(this);
        return true;
    }

    @Subscribe
    public void onConstruction(FMLConstructionEvent e) {
        // I'm not sure what's the ealiest time point when FMLCommonHandler could be initialized, but this should be early
        // enough to capture most if not all CMEs.
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
            @Override
            public String getLabel() {
                return "Thread Dump";
            }

            @Override
            public String call() {
                if (dump == null) {
                    return "Not generated";
                }
                return Arrays.stream(dump).map(ThreadInfo::toString).collect(Collectors.joining("", "\n", ""));
            }
        });
    }

    @Subscribe
    public void onPreInit(FMLPreInitializationEvent e) {
        if (Boolean.getBoolean("glease.triggercme"))
            // to prove this mod is working
            controller.errorOccurred(this, new ConcurrentModificationException("intentional crash to demonstrate DebugMod is working. set system property glease.triggercme to false to remove this."));
    }
}
