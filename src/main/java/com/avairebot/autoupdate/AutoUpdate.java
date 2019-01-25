package com.avairebot.autoupdate;

import com.avairebot.AppInfo;
import com.avairebot.plugin.JavaPlugin;
import com.avairebot.scheduler.ScheduleHandler;

public class AutoUpdate extends JavaPlugin {

    private SemanticVersion avaVersion;

    @Override
    public void onEnable() {
        avaVersion = new SemanticVersion(AppInfo.getAppInfo().version);

        ScheduleHandler.registerJob(
            new CheckNightlyBuildVersionJob(this)
        );
    }

    SemanticVersion getAvaVersion() {
        return avaVersion;
    }
}
