package com.rootix.launcher.commands.main.raw;

import com.rootix.launcher.R;
import com.rootix.launcher.commands.CommandAbstraction;
import com.rootix.launcher.commands.ExecutePack;
import com.rootix.launcher.managers.xml.XMLPrefsManager;
import com.rootix.launcher.managers.xml.options.Ui;
import com.rootix.launcher.tuils.interfaces.Reloadable;

public class username implements CommandAbstraction {

    @Override
    public String exec(ExecutePack pack) {
        String newUser = pack.getString();
        String newDevice = pack.getString();

        if (newUser == null || newDevice == null) {
            return onNotArgEnough(pack, 0);
        }

        XMLPrefsManager.XMLPrefsRoot.UI.write(Ui.username, newUser);
        XMLPrefsManager.XMLPrefsRoot.UI.write(Ui.deviceName, newDevice);

        try {
            if (pack.context instanceof Reloadable) {
                ((Reloadable) pack.context).reload();
            }
        } catch (Exception e) {}

        return "Username and Device updated!";
    }

    @Override
    public int[] argType() {
        return new int[] {CommandAbstraction.NO_SPACE_STRING, CommandAbstraction.NO_SPACE_STRING};
    }

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public int helpRes() {
        return R.string.help_username;
    }

    @Override
    public String onNotArgEnough(ExecutePack pack, int n) {
        return pack.context.getString(R.string.help_username);
    }

    @Override
    public String onArgNotFound(ExecutePack pack, int index) {
        return null;
    }
}
