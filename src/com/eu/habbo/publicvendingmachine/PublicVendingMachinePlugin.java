package com.eu.habbo.publicvendingmachine;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.publicvendingmachine.events.EventRegister;

/**
 * Created by Scott Stamp <scott@hypermine.com> on 2/17/2017.
 */
public class PublicVendingMachinePlugin extends HabboPlugin {

    public PublicVendingMachinePlugin()
    {
        Emulator.getPluginManager().registerEvents(this, new EventRegister());
    }

    @Override
    public void onEnable() {
        Emulator.getLogging().logStart("Starting Public VendingMachine Interaction Plugin!");
    }

    @Override
    public void onDisable() {
        Emulator.getLogging().logStart("Stopping Public VendingMachine Interaction Plugin!");
    }

    @Override
    public boolean hasPermission(Habbo habbo, String s) {
        return false;
    }
}
