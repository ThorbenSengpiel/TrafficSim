package de.trafficsim.logic.network;

import de.trafficsim.logic.streets.tracks.Track;
import java.util.ArrayList;
import java.util.List;

import java.util.*;

public class Pathfinder {


    public static List<Track> getPath(Track from, Track to){
        System.out.println("From :" + from + "To :" + to);
        List<Track> path = new LinkedList<Track>();
        Set<Track> visited = new HashSet<>();
        Map<Track,LinkedList<Track>> paths = new HashMap<>();
        List<Track> currentLayer = new ArrayList<>();
        currentLayer.add(from);
        LinkedList<Track> toFirst = new LinkedList<>();
        toFirst.add(from);
        paths.put(from,toFirst);
        List<Track> nextLayer;
        boolean found = false;
        while (!found){
            nextLayer = new LinkedList<>();
            for (int i = 0; i < currentLayer.size() && !found; i++){
                Track former = currentLayer.get(i);
                List<Track> outgoing = former.getOutTrackList();

                for(int j = 0; j < outgoing.size() && !found; j++){
                    Track next = outgoing.get(j);
                    if (! paths.containsKey(next)){
                        LinkedList<Track> pathToFormer = paths.get(former);
                        LinkedList<Track> pathToNext = (LinkedList<Track>) pathToFormer.clone();
                        if (next == to){
                            found = true;
                            path = pathToNext;
                        }
                        pathToNext.add(next);
                        paths.put(next, pathToNext);
                        nextLayer.add(next);
                    }
                }
            }
            currentLayer = nextLayer;
        }
        System.out.println(Arrays.toString(path.toArray()));
        return path;
    }
  public static List<Track> getRandomPath(Track startTrack, int length){
    List<Track> randomPath = new ArrayList<>();
    randomPath.add(startTrack);
    Track currentTrack = startTrack;
    for (int i =0; i<length;i++) {
        if (currentTrack.getOutTrackList().size() <= 0) {
            break;
        }
      Track nextTrack = currentTrack.getOutTrackList().get((int) (Math.random() * currentTrack.getOutTrackList().size()));
      randomPath.add(nextTrack);
      currentTrack = nextTrack;
    }
    System.out.println(randomPath);
    return randomPath;
  }

}
