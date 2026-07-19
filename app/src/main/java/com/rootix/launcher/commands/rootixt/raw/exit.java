package com.rootix.launcher.commands.rootixt.raw;

import android.app.Activity;

import com.rootix.launcher.R;
import com.rootix.launcher.commands.CommandAbstraction;
import com.rootix.launcher.commands.ExecutePack;
import com.rootix.launcher.commands.rootixt.RootixtPack;

/**
 * Created by francescoandreuzzi on 24/01/2017.
 */

public class exit implements CommandAbstraction {

    @Override
    public String exec(ExecutePack info) throws Exception {
        RootixtPack pack = (RootixtPack) info;

        ((Activity) pack.context).finish();
        return null;
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public int helpRes() {
        return R.string.help_rootixt_exit;
    }

    @Override
    public String onArgNotFound(ExecutePack info, int index) {
        return null;
    }

    @Override
    public String onNotArgEnough(ExecutePack info, int nArgs) {
        return null;
    }
}
