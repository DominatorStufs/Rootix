package com.rootix.launcher.commands.main.raw;

import android.content.Intent;

import com.rootix.launcher.R;
import com.rootix.launcher.commands.CommandAbstraction;
import com.rootix.launcher.commands.ExecutePack;
import com.rootix.launcher.tuils.Tuils;

/**
 * Created by francescoandreuzzi on 10/07/2017.
 */

public class tutorial implements CommandAbstraction {

    final String url = "https://github.com/DominatorStufs/Rootix/wiki";

    @Override
    public String exec(ExecutePack pack) throws Exception {
        Intent intent = Tuils.webPage(url);
        if(intent != null) pack.context.startActivity(intent);
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
        return R.string.help_tutorial;
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
