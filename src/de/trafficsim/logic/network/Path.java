package de.trafficsim.logic.network;

import de.trafficsim.logic.streets.tracks.Track;

import java.util.ArrayList;
import java.util.Arrays;

public class Path extends ArrayList<Track> {

    public Path(Track... tracks) {
        addAll(Arrays.asList(tracks));
    }

    public double distance(){
        double acc = 0;
        for (Track track : this) {
            acc+=track.getLength();
        }
        return acc;
    }
}
