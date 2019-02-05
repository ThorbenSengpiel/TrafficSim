package de.trafficsim.logic.network;

import de.trafficsim.logic.streets.tracks.Track;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class representing a Path
 */
public class Path extends ArrayList<Track> {

    public Path(Track... tracks) {
        addAll(Arrays.asList(tracks));
    }

    /**
     * Total length of a Path
     * @return double - total length
     */
    public double distance(){
        double acc = 0;
        for (Track track : this) {
            acc+=track.getLength();
        }
        return acc;
    }
}
