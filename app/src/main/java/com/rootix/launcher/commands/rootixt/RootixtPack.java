package com.rootix.launcher.commands.rootixt;

import android.content.Context;
import android.content.res.Resources;
import android.widget.EditText;

import java.io.File;

import com.rootix.launcher.commands.CommandGroup;
import com.rootix.launcher.commands.ExecutePack;

/**
 * Created by francescoandreuzzi on 25/01/2017.
 */

public class RootixtPack extends ExecutePack {

    public File editFile;
    public EditText editText;

    public Resources resources;

    public RootixtPack(CommandGroup group, File file, Context context, EditText editText) {
        super(group);

        this.editText = editText;
        editFile = file;
        this.context = context;
        this.resources = context.getResources();
    }
}
