package org.dreeam.expansion.folia;

import io.papermc.paper.threadedregions.RegionizedServer;
import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.TickData;
import io.papermc.paper.threadedregions.TickRegionScheduler;
import io.papermc.paper.threadedregions.TickRegions;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoliaUtils {

    public boolean isFolia = false;

    public void checkFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
    }

    public List<Double> getGlobalTPS() {
        final TickRegionScheduler.RegionScheduleHandle
                regionHandle = RegionizedServer.getGlobalTickData();

        double tps_5s = regionHandle.getTickReport5s(System.nanoTime()).tpsData().segmentAll().average();
        double tps_15s = regionHandle.getTickReport15s(System.nanoTime()).tpsData().segmentAll().average();
        double tps_1m = regionHandle.getTickReport1m(System.nanoTime()).tpsData().segmentAll().average();
        double tps_5m = regionHandle.getTickReport5m(System.nanoTime()).tpsData().segmentAll().average();
        double tps_15m = regionHandle.getTickReport15m(System.nanoTime()).tpsData().segmentAll().average();

        return List.of(tps_5s, tps_15s, tps_1m, tps_5m, tps_15m);
    }

    public List<Double> getGlobalMSPT() {
        final TickRegionScheduler.RegionScheduleHandle
                regionHandle = RegionizedServer.getGlobalTickData();

        double mspt_5s = regionHandle.getTickReport5s(System.nanoTime()).timePerTickData().segmentAll().average() / 1.0E6;
        double mspt_15s = regionHandle.getTickReport15s(System.nanoTime()).timePerTickData().segmentAll().average() / 1.0E6;
        double mspt_1m = regionHandle.getTickReport1m(System.nanoTime()).timePerTickData().segmentAll().average() / 1.0E6;
        double mspt_5m = regionHandle.getTickReport5m(System.nanoTime()).timePerTickData().segmentAll().average() / 1.0E6;
        double mspt_15m = regionHandle.getTickReport15m(System.nanoTime()).timePerTickData().segmentAll().average() / 1.0E6;

        return List.of(mspt_5s, mspt_15s, mspt_1m, mspt_5m, mspt_15m);
    }

    public double getGlobalUtil() {
        final List<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> regions =
                new ArrayList<>();

        for (final World bukkitWorld : Bukkit.getWorlds()) {
            ((Level) bukkitWorld).getWorld().getHandle().regioniser.computeForAllRegions(regions::add); // Avoid to use NMS
        }

        double totalUtil = 0.0;

        final long now = System.nanoTime();
        final TickRegionScheduler.RegionScheduleHandle regionHandle = RegionizedServer.getGlobalTickData();

        for (final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData> region : regions) {
            final TickData.TickReportData report = region.getData().getRegionSchedulingHandle().getTickReport15s(now);
            totalUtil += (report == null ? 0.0 : report.utilisation());
        }

        totalUtil += regionHandle.getTickReport15s(now).utilisation();

        return totalUtil;
    }

    public List<Double> getTPS(Location location) {
        //if (location == null) return getGlobalTPS();

        World world = location.getWorld();
        //if (world == null) return getGlobalTPS();
        // happens faster and on the thread of the region that owns the location.
        // Get the potential separate region that owns the location
        final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>
                currentRegion = TickRegionScheduler.getCurrentRegion();
        // If not happening on a separate region, it must mean we're on the main region
        if (currentRegion == null) {
            //return getGlobalTPS();
        }
        // Get region handle and check if there is already a cached tps for it
        final TickRegionScheduler.RegionScheduleHandle
                regionHandle = currentRegion.getData().getRegionSchedulingHandle();

        double tps_5s = regionHandle.getTickReport5s(System.nanoTime()).tpsData().segmentAll().average();
        double tps_15s = regionHandle.getTickReport15s(System.nanoTime()).tpsData().segmentAll().average();
        double tps_1m = regionHandle.getTickReport1m(System.nanoTime()).tpsData().segmentAll().average();
        double tps_5m = regionHandle.getTickReport5m(System.nanoTime()).tpsData().segmentAll().average();
        double tps_15m = regionHandle.getTickReport15m(System.nanoTime()).tpsData().segmentAll().average();

        return List.of(tps_5s, tps_15s, tps_1m, tps_5m, tps_15m);
    }

    public List<Double> getMSPT(Location location) {
        //if (location == null) return getGlobalMSPT();

        World world = location.getWorld();
        //if (world == null) return getGlobalMSPT();
        // happens faster and on the thread of the region that owns the location.
        // Get the potential separate region that owns the location
        final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>
                currentRegion = TickRegionScheduler.getCurrentRegion();
        // If not happening on a separate region, it must mean we're on the main region
        if (currentRegion == null) {
            //return getGlobalMSPT();
        }
        // Get region handle and check if there is already a cached tps for it
        final TickRegionScheduler.RegionScheduleHandle
                regionHandle = currentRegion.getData().getRegionSchedulingHandle();

        double mspt_5s = regionHandle.getTickReport5s(System.nanoTime()).timePerTickData().segmentAll().average() / 1.0E6;
        double mspt_15s = regionHandle.getTickReport15s(System.nanoTime()).timePerTickData().segmentAll().average() / 1.0E6;
        double mspt_1m = regionHandle.getTickReport1m(System.nanoTime()).timePerTickData().segmentAll().average() / 1.0E6;
        double mspt_5m = regionHandle.getTickReport5m(System.nanoTime()).timePerTickData().segmentAll().average() / 1.0E6;
        double mspt_15m = regionHandle.getTickReport15m(System.nanoTime()).timePerTickData().segmentAll().average() / 1.0E6;

        return List.of(mspt_5s, mspt_15s, mspt_1m, mspt_5m, mspt_15m);
    }

    public List<Double> getUtil(Location location) {
        if (location == null) return Collections.singletonList(getGlobalUtil());

        World world = location.getWorld();
        if (world == null) return Collections.singletonList(getGlobalUtil());
        // happens faster and on the thread of the region that owns the location.
        // Get the potential separate region that owns the location
        final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>
                currentRegion = TickRegionScheduler.getCurrentRegion();
        // If not happening on a separate region, it must mean we're on the main region
        if (currentRegion == null) {
            return Collections.singletonList(getGlobalUtil());
        }
        // Get region handle and check if there is already a cached tps for it
        final TickRegionScheduler.RegionScheduleHandle
                regionHandle = currentRegion.getData().getRegionSchedulingHandle();

        double util_5s = regionHandle.getTickReport5s(System.nanoTime()).utilisation();
        double util_15s = regionHandle.getTickReport15s(System.nanoTime()).utilisation();
        double util_1m = regionHandle.getTickReport1m(System.nanoTime()).utilisation();
        double util_5m = regionHandle.getTickReport5m(System.nanoTime()).utilisation();
        double util_15m = regionHandle.getTickReport15m(System.nanoTime()).utilisation();

        return List.of(util_5s, util_15s, util_1m, util_5m, util_15m);
    }

    public double maxThreadsCount() {
        return TickRegions.getScheduler().getTotalThreadCount();
    }
}
