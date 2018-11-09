package de.trafficsim.logic.network;

import de.trafficsim.logic.streets.tracks.Track;
import java.util.ArrayList;
import java.util.List;

public class Pathfinder {

  public static List<Track> getRandomPath(Track startTrack, int length){
    List<Track> randomPath = new ArrayList<>();
    randomPath.add(startTrack);
    Track currentTrack = startTrack;
    for (int i =0; i<length;i++) {
      Track nextTrack = currentTrack.getOutTrackList().get(0);
      //(int) (Math.random() * currentTrack.getOutTrackList().size())
      randomPath.add(nextTrack);
      currentTrack = nextTrack;
    }
    System.out.println(randomPath);
    return randomPath;
  }

}
