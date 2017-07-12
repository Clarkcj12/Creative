package network.palace.creative.show.handlers;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import lombok.Getter;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Marc on 6/16/16
 */
@Getter
public class PlotArea extends AudioArea {
    private PlotId plotId;
    private CPlayer owner;

    @SuppressWarnings("deprecation")
    public PlotArea(PlotId plotId, CPlayer owner, String soundname, World world) {
        super(owner.getUniqueId().toString(), soundname, 750, 1.0, null, true, false, world);
        this.plotId = plotId;
        this.owner = owner;
        PlotAPI api = new PlotAPI(Creative.getInstance());
        for (CPlayer p : Core.getPlayerManager().getOnlinePlayers()) {
            if (p == null || p.getBukkitPlayer() == null)
                continue;
            Plot pl = api.getPlot(p.getBukkitPlayer());
            if (pl == null) {
                continue;
            }
            if (pl.getId().equals(plotId)) {
                addPlayer(p);
            }
        }
    }

    @Override
    public String getRegionName() {
        return "Plot ID " + plotId.toString();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean locIsInArea(Location loc) {
        PlotAPI api = new PlotAPI(Creative.getInstance());
        Plot plot = api.getPlot(loc);
        return plot != null && plot.getId().equals(plotId);
    }
}
