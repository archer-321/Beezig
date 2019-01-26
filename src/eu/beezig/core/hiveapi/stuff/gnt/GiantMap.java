package eu.beezig.core.hiveapi.stuff.gnt;

public enum GiantMap {

    //GNT Maps

    FORTRESS("Fortress", false),
    LOST("Lost", false),
    ELYSIUM("Elysium", false),
    TROPICAL_TROUBLE("Tropical Trouble", false),
    LUMBER("Lumber", false),
    ORC("Orc", false),
    MAGIKOS("Magikos", false),
    ODYSSEY("Odyssey", false),
    GATEWAY("Gateway", false),
    AIRSHIP("Airship", false),
    RADIOACTIVE("Radioactive", false),
    DRAGONS("Dragons", false),
    ADVENUS("Advenus", false),
    MOONLIGHT("Moonlight", false),
    ANCHORED("Anchored", false),
    ANUBIS("Anubis", false),
    SKY_KINGDOM("Sky Kingdom", false),
    STRONGHOLD("Stronghold", false),
    SNOWFIGHT("Snow Fight", false),


    //GNTM Maps

    SANCTUM("Sanctum", true),
    M_FORTRESS("Fortress", true),
    OASIS("Oasis", true),
    EVERGREEN("Evergreen", true),
    BABYLON("Babylon", true),
    URBAN("Urban", true),
    RED("Red", true),
    SPRUCE_SPRINGSTEEN("Spruce Springsteen", true),
    BLOSSOM("Blossom", true),
    TOWN_HALL("Town Hall", true),
    MUFFIN("Muffin", true),
    AQUARIUS("Aquarius", true),
    GWOLITH("Gwolith", true),
    ROCKET("Rocket", true),
    WITCHSLAIR("Witch's Lair", true);


    private String display;
    private boolean mini;

    GiantMap(String display, boolean mini) {

        this.display = display;
        this.mini = mini;

    }

    public static GiantMap get(String display, boolean mini) {
        for (GiantMap map : values()) {
            if (map.isMini() == mini) {
                if (map.getDisplay().equalsIgnoreCase(display)) return map;
            }
        }
        return null;
    }

    public String getDisplay() {
        return display;
    }

    public boolean isMini() {
        return mini;
    }


}