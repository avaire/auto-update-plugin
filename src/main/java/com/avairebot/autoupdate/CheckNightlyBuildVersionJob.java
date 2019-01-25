package com.avairebot.autoupdate;

import com.avairebot.contracts.scheduler.Job;
import com.avairebot.factories.RequestFactory;
import com.avairebot.requests.Response;
import com.avairebot.scheduler.ScheduleHandler;
import com.avairebot.shared.ExitCodes;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CheckNightlyBuildVersionJob extends Job {

    private static final Logger log = LoggerFactory.getLogger(CheckNightlyBuildVersionJob.class);

    private final AutoUpdate autoUpdate;

    CheckNightlyBuildVersionJob(AutoUpdate autoUpdate) {
        super(autoUpdate.getAvaire(), 0, 90, TimeUnit.MINUTES);

        this.autoUpdate = autoUpdate;
    }

    @Override
    public void run() {
        RequestFactory
            .makeGET("https://avairebot.com/nightly-build")
            .send(this::handleResponse);
    }

    private void handleResponse(Object obj) {
        if (!(obj instanceof Response)) {
            log.warn("Nightly build request returned a non-response object type: {}", obj.getClass().getTypeName());
            return;
        }

        JSONObject json = new JSONObject(obj.toString());

        if (!json.has("version")) {
            log.warn("Malformed nightly build response, missing version.");
            return;
        }

        if (!shouldUpdate(new SemanticVersion(json.getString("version")))) {
            return;
        }

        log.info("A new version on Ava have been found via the nightly build, restarting the application in 10 seconds to update.");

        ScheduleHandler.getScheduler().schedule(() -> {
            autoUpdate.getAvaire().shutdown(ExitCodes.EXIT_CODE_UPDATE);
        }, 10, TimeUnit.SECONDS);
    }

    private boolean shouldUpdate(SemanticVersion nightBuildVersion) {
        return nightBuildVersion.getMajor() > autoUpdate.getAvaVersion().getMajor()
            || nightBuildVersion.getMinor() > autoUpdate.getAvaVersion().getMinor()
            || nightBuildVersion.getPatch() > autoUpdate.getAvaVersion().getPatch();
    }
}
