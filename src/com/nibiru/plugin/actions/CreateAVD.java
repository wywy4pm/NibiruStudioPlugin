package com.nibiru.plugin.actions;

import com.android.repository.Revision;
import com.android.resources.ScreenOrientation;
import com.android.sdklib.AndroidVersion;
import com.android.sdklib.ISystemImage;
import com.android.sdklib.devices.Device;
import com.android.sdklib.devices.DeviceManager;
import com.android.sdklib.repository.IdDisplay;
import com.android.tools.idea.avdmanager.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.nibiru.plugin.utils.Log;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.Map;

public class CreateAVD extends AnAction {
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile file = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
        Module curmodule = ModuleUtil.findModuleForFile(file, anActionEvent.getProject());
        Sdk sdk = ModuleRootManager.getInstance(curmodule).getSdk();
        String sdkpath=sdk.getHomePath();
        if (TextUtils.isEmpty(sdkpath)){
            Log.i("获取sdkpath失败!");
            return;
        }
        DeviceManagerConnection deviceManagerConnection = DeviceManagerConnection.getDeviceManagerConnection(new File(sdkpath));
        Device device = deviceManagerConnection.getDevice("pixel_2", "Google");
        if (device != null) {
            SystemImageDescription systemImageDescription = new SystemImageDescription(new ISystemImage() {
                @Override
                public int compareTo(@NotNull ISystemImage o) {
                    return 0;
                }

                @Override
                public File getLocation() {
                    return new File(sdkpath+"\\system-images\\android-27\\default\\x86_64");
                }

                @Override
                public IdDisplay getTag() {
                    return IdDisplay.create("default", "Default Android System Image");
                }

                @Override
                public IdDisplay getAddonVendor() {
                    return null;
                }

                @Override
                public String getAbiType() {
                    return "x86_64";
                }

                @Override
                public File[] getSkins() {
                    return new File[0];
                }

                @Override
                public Revision getRevision() {
                    Revision revision = new Revision(1);
                    return revision;
                }

                @Override
                public AndroidVersion getAndroidVersion() {
                    AndroidVersion androidVersion = new AndroidVersion(27);
                    return androidVersion;
                }

                @Override
                public boolean obsolete() {
                    return false;
                }

                @Override
                public boolean hasPlayStore() {
                    return false;
                }
            });
            Map<String, String> hardwareProperties = DeviceManager.getHardwareProperties(device);
            hardwareProperties.put("hw.sdCard", "yes");
            hardwareProperties.put("hw.ramSize", "1536");
            hardwareProperties.put("vm.heapSize", "256");
            hardwareProperties.put("skin.dynamic", "yes");
            hardwareProperties.put("hw.keyboard", "yes");
            hardwareProperties.put("fastboot.chosenSnapshotFile", "");
            hardwareProperties.put("runtime.network.speed", "full");
            hardwareProperties.put("skin.path", sdkpath+"\\skins\\pixel_2");
            hardwareProperties.put("hw.initialOrientation", "landscape");
            hardwareProperties.put("hw.cpu.arch", "x86_64");
            hardwareProperties.put("showDeviceFrame", "yes");
            hardwareProperties.put("hw.camera.back", "virtualscene");
            hardwareProperties.put("AvdId", "Pixel_2_API_27");
            hardwareProperties.put("hw.camera.front", "emulated");
            hardwareProperties.put("avd.ini.displayname", "Pixel 2 API 27");
            hardwareProperties.put("fastboot.forceFastBoot", "yes");
            hardwareProperties.put("fastboot.forceChosenSnapshotBoot", "no");
            hardwareProperties.put("fastboot.forceColdBoot", "no");
            hardwareProperties.put("hw.cpu.ncore", "4");
            hardwareProperties.put("runtime.network.latency", "none");
            hardwareProperties.put("disk.dataPartition.size", "800M");
            hardwareProperties.put("hw.gpu.enabled", "yes");

//            创建或者更新avd
            AvdManagerConnection connection = AvdManagerConnection.getDefaultAvdManagerConnection();
            ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                connection.createOrUpdateAvd(null, "Pixel_2_API_27", device, systemImageDescription, ScreenOrientation.LANDSCAPE, false, "512M", new File(sdkpath+"\\skins\\pixel_2"), hardwareProperties, false, true);
            }, "Creating Android Virtual Device", false, (Project) null);
        }
    }
}
