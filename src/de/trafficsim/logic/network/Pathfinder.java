package de.trafficsim.logic.network;

import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.tracks.Track;
import java.util.ArrayList;
import java.util.List;

import java.util.*;

public class Pathfinder {

    public static Path getPath(Track from, List<Street> intermediate, Track to){
        //If there are intermediate Streets to take in account
        if (intermediate != null && ! intermediate.isEmpty()){
            List<List<Track>> possibleIntermediateTracks = new ArrayList<>();
            for (Street street : intermediate) {
                possibleIntermediateTracks.add(street.getTracks());
            }
            System.out.println("Intermediate =" + Arrays.toString(possibleIntermediateTracks.toArray()));
            //All Possible ordered Paths
            List<Path> allTrackSequences = new ArrayList<>();
            allTrackSequences.add(new Path(from));
            while (possibleIntermediateTracks.size() > 0){
                List<Path> newAllTrackSequences = new ArrayList<>();
                List<Track> possibleNext = possibleIntermediateTracks.get(0);
                for (Path path : allTrackSequences) {
                    for (Track nextTrack : possibleNext) {
                        Path pathCopy = (Path) path.clone();
                        pathCopy.add(nextTrack);
                        newAllTrackSequences.add(pathCopy);
                    }
                }
                //Remove One Step of the possibleIntermediateTracks
                possibleIntermediateTracks = possibleIntermediateTracks.subList(1,possibleIntermediateTracks.size());
                allTrackSequences = newAllTrackSequences;
            }
            for(Path path:allTrackSequences){
                path.add(to);
            }
            List<Path> allPossiblePathes = new ArrayList<>();
            for (Path path : allTrackSequences) {
                // -1 is safe here because there has to be at least one intermediate Track
                Path foundPath = getPathByTrack(path.get(0),path.subList(1,path.size()-1),path.get(path.size()-1));
                if (foundPath!=null){
                    allPossiblePathes.add(foundPath);
                }
            }
            if (allPossiblePathes.isEmpty()){
                return null;
            }
            allPossiblePathes.sort((p1, p2) -> {
                double l1 = (p1 == null ? 0 : p1.distance());
                double l2 = (p2 == null ? 0 : p2.distance());
                return (int) (l1-l2);
            });
            return allPossiblePathes.get(0);
        } else {
            return getPath(from,to);
        }
    }
    private static Path getPathByTrack(Track from, List<Track> intermediate, Track to){
        System.out.println("Calculating Path: From = " + from + " To = " + to + " Via = " + Arrays.toString(intermediate.toArray()));
        if (intermediate == null || intermediate.isEmpty()){
            return getPath(from,to);
        }
        Path fullTrackSeq = new Path(from);
        fullTrackSeq.addAll(intermediate);
        fullTrackSeq.add(to);
        Path returnPath = new Path(from);
        for (int i = 0; i < fullTrackSeq.size() - 1; i++) {
            Path piecewisePath = getPath(fullTrackSeq.get(i),fullTrackSeq.get(i+1));
            if (piecewisePath == null){
                return null;
            }
            returnPath.addAll(piecewisePath.subList(1,piecewisePath.size()));
        }
        return returnPath;
    }

    public static Path getPath(Track from, Track to){
        List<Track> path = new LinkedList<>();
        //Map saving the path to the Track from the Start
        Map<Track,LinkedList<Track>> paths = new HashMap<>();
        List<Track> currentLayer = new ArrayList<>();
        currentLayer.add(from);
        //Add the path to
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
            if (nextLayer.isEmpty()){
                return null;
            }
            currentLayer = nextLayer;
        }
        return new Path(path.toArray(new Track[0]));
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
    return randomPath;
  }

}
