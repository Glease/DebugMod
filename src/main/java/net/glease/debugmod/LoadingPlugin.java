package net.glease.debugmod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.Map;

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
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
