package com.eu.habbo.publicvendingmachine.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionVendingMachine;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.RoomUnitGiveHanditem;
import com.eu.habbo.threading.runnables.RoomUnitVendingMachineAction;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Rotation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by scott on 2/17/2017.
 */
public class PublicVendingMachineInteraction extends HabboItem {
    public PublicVendingMachineInteraction(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public PublicVendingMachineInteraction(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);

        if (client != null) {
            RoomTile tile = getSquareInFront(room.getLayout(), this);
            RoomTile tileBehind = getSquareInBack(room.getLayout(), this);
            List<RoomTile> surroundingTiles = PathFinder.getTilesAround(room.getLayout(), this.getX(), this.getY());

            if (surroundingTiles.contains(client.getHabbo().getRoomUnit().getCurrentLocation()))
            {
                if (this.getExtradata().equals("0") || this.getExtradata().length() == 0)
                {
                    if (!client.getHabbo().getRoomUnit().getStatus().containsKey("sit"))
                    {
                        client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[Rotation.Calculate(client.getHabbo().getRoomUnit().getX(), client.getHabbo().getRoomUnit().getY(), this.getX(), this.getY())]);
                        room.sendComposer(new RoomUserStatusComposer(client.getHabbo().getRoomUnit()).compose());
                    }
                    this.setExtradata("1");
                    room.updateItem(this);
                    Emulator.getThreading().run(this, 1000);

                    // this.getBaseItem().getRandomVendingItem() returns null from this context?
                    Emulator.getThreading().run(new RoomUnitGiveHanditem(client.getHabbo().getRoomUnit(), room, 24));
                }
            }
            else
            {
                if (room.getLayout().tileWalkable(tile.x, tile.y)) {
                    client.getHabbo().getRoomUnit().setGoalLocation(tile);
                    Emulator.getLogging().logDebugLine("Tile Walkable! " + tile.x + " - " + tile.y);
                } else {
                    client.getHabbo().getRoomUnit().setGoalLocation(tileBehind);
                    Emulator.getLogging().logDebugLine("Tile Not Walkable! " + tileBehind.x + " - " + tileBehind.y);
                }
                Emulator.getThreading().run(new RoomUnitVendingMachineAction(client.getHabbo(), this, room), client.getHabbo().getRoomUnit().getPathFinder().getPath().size() + 2 * 510);
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void run()
    {
        super.run();
        if(this.getExtradata().equals("1"))
        {
            this.setExtradata("0");
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
            if(room != null)
            {
                room.updateItem(this);
            }
        }
    }


    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt32((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    public RoomTile getSquareInBack(RoomLayout roomLayout, HabboItem item)
    {
        return getSquareInBack(roomLayout, item.getX(), item.getY(), item.getRotation(), (short)1);
    }

    public RoomTile getSquareInBack(RoomLayout roomLayout, short x, short y, int rotation, short offset)
    {
        rotation = rotation % 8;

        if(rotation == 0)
            return roomLayout.getTile(x, (short) (y + offset));
        else if(rotation == 1)
            return roomLayout.getTile((short) (x - offset), (short) (y + offset));
        else if(rotation == 2)
            return roomLayout.getTile((short) (x - offset), y);
        else if(rotation == 3)
            return roomLayout.getTile((short) (x - offset), (short) (y - offset));
        else if(rotation == 4)
            return roomLayout.getTile(x, (short) (y - offset));
        else if(rotation == 5)
            return roomLayout.getTile((short) (x + offset), (short) (y - offset));
        else if(rotation == 6)
            return roomLayout.getTile((short) (x + offset), y);
        else if(rotation == 7)
            return roomLayout.getTile((short) (x + offset), (short) (y + offset));
        else
            return roomLayout.getTile(x, y);
    }
}
