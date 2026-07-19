package com.rootix.launcher.commands.main.raw;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;

import com.rootix.launcher.MainManager;
import com.rootix.launcher.R;
import com.rootix.launcher.UIManager;
import com.rootix.launcher.commands.CommandAbstraction;
import com.rootix.launcher.commands.ExecutePack;
import com.rootix.launcher.commands.main.MainPack;
import com.rootix.launcher.managers.xml.XMLPrefsManager;
import com.rootix.launcher.managers.xml.options.Behavior;
import com.rootix.launcher.tuils.StoppableThread;

/**
 * Created by francescoandreuzzi on 26/07/2017.
 */

public class ctrlc implements CommandAbstraction {

    @Override
    public String exec(final ExecutePack pack) throws Exception {
        new StoppableThread() {
            @Override
            public void run() {
                super.run();

                MainManager.interactive.kill();
                MainManager.interactive.close();
                MainManager.interactive = null;

                MainManager.interactive = ((MainPack) pack).shellHolder.build();

                ((MainPack) pack).currentDirectory = XMLPrefsManager.get(File.class, Behavior.home_path);
                LocalBroadcastManager.getInstance(pack.context.getApplicationContext()).sendBroadcast(new Intent(UIManager.ACTION_UPDATE_HINT));
            }
        }.start();

        LocalBroadcastManager.getInstance(pack.context.getApplicationContext()).sendBroadcast(new Intent(UIManager.ACTION_NOROOT));

        return null;
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public int helpRes() {
        return R.string.help_ctrlc;
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
