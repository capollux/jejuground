package xyz.zzulu.jejuground.fragments.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SpotContent {

    /**
     * An array of spot items.
     */
    public static final List<SpotItem> ITEMS = new ArrayList<SpotItem>();

    /**
     * A map of spot items, by ID.
     */
    public static final Map<String, SpotItem> ITEM_MAP = new HashMap<String, SpotItem>();

    public static void addItem(SpotItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(String.valueOf(item.spotId), item);
    }

    /**
     * A spot item representing a piece of content.
     */
    public static class SpotItem {
        public final String region;
        public final long spotId;
        public final String description;
        public final boolean indoor;
        public final double lat;
        public final double lng;

        public boolean visited;

        public SpotItem(String region, long spotId, String description, boolean indoor, double lat, double lng, boolean visited) {
            this.region = region;
            this.spotId = spotId;
            this.description = description;
            this.indoor = indoor;
            this.lat = lat;
            this.lng = lng;
            this.visited = visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        @Override
        public String toString() {
            return region + " / " + description;
        }
    }
}
