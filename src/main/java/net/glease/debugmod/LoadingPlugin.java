package net.glease.debugmod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static net.glease.debugmod.ASMCallhookServer.dump;

@IFMLLoadingPlugin.Name("DebugMod")
public class LoadingPlugin implements IFMLLoadingPlugin {
    static File debugOutputDir;

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                "net.glease.debugmod.Transformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        debugOutputDir = new File((File) data.get("mcLocation"), ".asm");
        //noinspection ResultOfMethodCallIgnored
        debugOutputDir.mkdir();
        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
            @Override
            public String getLabel() {
                return "Thread dump";
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

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
