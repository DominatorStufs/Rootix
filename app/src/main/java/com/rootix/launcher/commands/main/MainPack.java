package com.rootix.launcher.commands.main;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import java.io.File;
import java.lang.reflect.Method;

import com.rootix.launcher.commands.CommandGroup;
import com.rootix.launcher.commands.CommandsPreferences;
import com.rootix.launcher.commands.ExecutePack;
import com.rootix.launcher.managers.AliasManager;
import com.rootix.launcher.managers.AppsManager;
import com.rootix.launcher.managers.ContactManager;
import com.rootix.launcher.managers.RssManager;
import com.rootix.launcher.managers.TerminalManager;
import com.rootix.launcher.managers.flashlight.TorchManager;
import com.rootix.launcher.managers.music.MusicManager2;
import com.rootix.launcher.managers.xml.XMLPrefsManager;
import com.rootix.launcher.managers.xml.options.Behavior;
import com.rootix.launcher.tuils.interfaces.Redirectator;
import com.rootix.launcher.tuils.libsuperuser.ShellHolder;
import okhttp3.OkHttpClient;

/**
 * Created by francescoandreuzzi on 24/01/2017.
 */

public class MainPack extends ExecutePack {

    //	current directory
    public File currentDirectory;

    //	resources references
    public Resources res;

    //	internet
    public WifiManager wifi;

    //	3g/data
    public Method setMobileDataEnabledMethod;
    public ConnectivityManager connectivityMgr;
    public Object connectMgr;

    //	contacts
    public ContactManager contacts;

    //	music
    public MusicManager2 player;

    //	apps & assocs
    public AliasManager aliasManager;
    public AppsManager appsManager;

    public CommandsPreferences cmdPrefs;

    public String lastCommand;

    public Redirectator redirectator;

    public ShellHolder shellHolder;

    public RssManager rssManager;

    public OkHttpClient client;

    public int commandColor = TerminalManager.NO_COLOR;

    public MainPack(Context context, CommandGroup commandGroup, AliasManager alMgr, AppsManager appmgr, MusicManager2 p,
                    ContactManager c, Redirectator redirectator, RssManager rssManager, OkHttpClient client) {
        super(commandGroup);

        this.currentDirectory = XMLPrefsManager.get(File.class, Behavior.home_path);

        this.rssManager = rssManager;

        this.client = client;

        this.res = context.getResources();

        this.context = context;

        this.aliasManager = alMgr;
        this.appsManager = appmgr;

        this.cmdPrefs = new CommandsPreferences();

        this.player = p;
        this.contacts = c;

        this.redirectator = redirectator;
    }

    public void dispose() {
        TorchManager mgr = TorchManager.getInstance();
        if(mgr.isOn()) mgr.turnOff();
    }

    public void destroy() {
        if(player != null) player.destroy();
        appsManager.onDestroy();
        if(rssManager != null) rssManager.dispose();
        contacts.destroy(context);
    }

    @Override
    public void clear() {
        super.clear();

        commandColor = TerminalManager.NO_COLOR;
    }
}
