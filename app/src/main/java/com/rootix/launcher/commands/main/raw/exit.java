package com.rootix.launcher.commands.main.raw;

import com.rootix.launcher.R;
import com.rootix.launcher.commands.CommandAbstraction;
import com.rootix.launcher.commands.ExecutePack;
import com.rootix.launcher.tuils.Tuils;

/**
 * Created by francescoandreuzzi on 21/05/2017.
 */

public class exit implements CommandAbstraction {
    @Override
    public String exec(ExecutePack pack) throws Exception {
        Tuils.resetPreferredLauncherAndOpenChooser(pack.context);
        return null;
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public int helpRes() {
        return R.string.help_exit;
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int indexNotFound) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int nArgs) {
        return null;
    }
}
