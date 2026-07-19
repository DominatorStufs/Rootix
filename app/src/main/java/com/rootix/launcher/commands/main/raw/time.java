package com.rootix.launcher.commands.main.raw;

import com.rootix.launcher.R;
import com.rootix.launcher.commands.CommandAbstraction;
import com.rootix.launcher.commands.ExecutePack;
import com.rootix.launcher.managers.TimeManager;

/**
 * Created by andre on 03/12/15.
 */
public class time implements CommandAbstraction {
    @Override
    public String exec(ExecutePack pack) {
        int index = pack.getInt();
        return TimeManager.instance.replace("%t" + index).toString();
    }

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public int[] argType() {
        return new int[] {CommandAbstraction.INT};
    }

    @Override
    public int helpRes() {
        return R.string.help_time;
    }

    @Override
    public String onArgNotFound(ExecutePack info, int index) {
        return info.context.getString(R.string.invalid_integer);
    }

    @Override
    public String onNotArgEnough(ExecutePack info, int nArgs) {
        return TimeManager.instance.replace("%t0").toString();
    }
}
