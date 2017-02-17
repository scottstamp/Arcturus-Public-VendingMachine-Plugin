package com.eu.habbo.publicvendingmachine.events;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.ItemInteraction;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadItemsManagerEvent;
import com.eu.habbo.publicvendingmachine.items.PublicVendingMachineInteraction;

/**
 * Created by scott on 2/17/2017.
 */
public class EventRegister implements EventListener {

    @EventHandler
    public static void onItemsLoading(EmulatorLoadItemsManagerEvent event)
    {
        Emulator.getGameEnvironment().getItemManager().addItemInteraction(new ItemInteraction("public_vendingmachine", PublicVendingMachineInteraction.class));
    }
}
