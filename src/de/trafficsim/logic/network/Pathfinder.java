package de.trafficsim.logic.network;

import de.trafficsim.logic.streets.Street;
import de.trafficsim.logic.streets.tracks.Track;
import java.util.ArrayList;
import java.util.List;

import java.util.*;

/**
 * Class providing methods to find Paths in the StreetNetwork
 */
public class Pathfinder {

    /**
     * Find the Path between two tracks via the given intermediate Streets with the least amount of
     * Tracks
     * @param from - Starting Track
     * @param intermediate - Intermediate Street
     * @param to - End Track
     * @return Path
     */
    public static Path getPath(Track from, List<Street> intermediate, Track to){
        //If there are intermediate Streets to take in account
        if (intermediate != null && ! intermediate.isEmpty()){
            List<List<Track>> possibleIntermediateTracks = new ArrayList<>();
            for (Street street : intermediate) {
                possibleIntermediateTracks.add(street.getTracks());
            }
            //System.out.println("Intermediate =" + Arrays.toString(possibleIntermediateTracks.toArray()));
            //All Possible ordered Paths
            List<Path> allTrackSequences = new ArrayList<>();
            allTrackSequences.add(new Path(from));
            //Build All Possible Combinations of Track Sequences for which a path should be found
            while (possibleIntermediateTracks.size() > 0){
                List<Path> newAllTrackSequences = new ArrayList<>();
                List<Track> possibleNext = possibleIntermediateTracks.get(0);
                //Combine each TrackSequence with each possible next Track
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
            //Find all possible paths
            for (Path path : allTrackSequences) {
                // -1 is safe here because there has to be at least one intermediate Track
                Path foundPath = getPathByTrack(path.get(0),path.subList(1,path.size()-1),path.get(path.size()-1));
                if (foundPath!=null){
                    allPossiblePathes.add(foundPath);
                }
            }
            //If there was no Path found, then return null
            if (allPossiblePathes.isEmpty()){
                return null;
            }
            //If there is more than one path find the shortest of them
            allPossiblePathes.sort((p1, p2) -> {
                double l1 = (p1 == null ? 0 : p1.distance());
                double l2 = (p2 == null ? 0 : p2.distance());
                return (int) (l1-l2);
            });
            return allPossiblePathes.get(0);
        } else {
            //Otherwise just find a path between the two tracks
            return getPath(from,to);
        }
    }

    /**
     * Find a path with intermediate Tracks
     * @param from - Starting Track
     * @param intermediate - Intermediate Tracks
     * @param to - End Track
     * @return Path between them
     */
    private static Path getPathByTrack(Track from, List<Track> intermediate, Track to){
        //System.out.println("Calculating Path: From = " + from + " To = " + to + " Via = " + Arrays.toString(intermediate.toArray()));
        System.out.println("Calculating Path: From = " + from + " To = " + to + " Via = " + Arrays.toString(intermediate.toArray()));
        //If there is no intermediate Track, just calculate a Path from the starting Track to the End Track
        if (intermediate == null || intermediate.isEmpty()){
            return getPath(from,to);
        }
        Path fullTrackSeq = new Path(from);
        fullTrackSeq.addAll(intermediate);
        fullTrackSeq.add(to);
        Path returnPath = new Path(from);
        //Calculate the total Path by combining multiple piecewise Paths
        for (int i = 0; i < fullTrackSeq.size() - 1; i++) {
            Path piecewisePath = getPath(fullTrackSeq.get(i),fullTrackSeq.get(i+1));
            if (piecewisePath == null){
                return null;
            }
            returnPath.addAll(piecewisePath.subList(1,piecewisePath.size()));
        }
        return returnPath;
    }

    /**
     * Calculate the Path between two Tracks with the least amount of Tracks
     * @param from - Starting Track
     * @param to - End Track
     * @return Path between them
     */
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
        //Nodes of the next Layer
        List<Track> nextLayer;
        boolean found = false;
        //If the track was found terminate
        while (!found){
            nextLayer = new LinkedList<>();
            for (int i = 0; i < currentLayer.size() && !found; i++){
                Track former = currentLayer.get(i);
                List<Track> outgoing = new ArrayList<Track>(former.getOutTrackList());
                List<Track> shuffledOutgoing = new ArrayList<>();
                //Shuffle the outgoing tracks to get diverse Paths
                while(!outgoing.isEmpty()){
                    int index = (int)(Math.random()*outgoing.size());
                    shuffledOutgoing.add(outgoing.get(index));
                    outgoing.remove(index);
                }
                //Check each outgoing Track of the node
                for(int j = 0; j < shuffledOutgoing.size() && !found; j++){
                    Track next = shuffledOutgoing.get(j);
                    //If it is already contained in the paths Hashmap, then a shorter Path was already found to this point
                    if (! paths.containsKey(next)){
                        LinkedList<Track> pathToFormer = paths.get(former);
                        LinkedList<Track> pathToNext = (LinkedList<Track>) pathToFormer.clone();
                        //Check whether the Track is the Ending Track if so then return
                        if (next == to){
                            found = true;
                            path = pathToNext;
                        }
                        //Add the Path to the Hashmap and the Track to the next Layer
                        pathToNext.add(next);
                        paths.put(next, pathToNext);
                        nextLayer.add(next);
                    }
                }
            }
            //If there is no more node in the new layer. There is no path to the ending node
            if (nextLayer.isEmpty()){
                return null;
            }
            //Step one layer deeper
            currentLayer = nextLayer;
        }
        return new Path(path.toArray(new Track[0]));
    }

    /**
     * Calculate a random Path from the Starting Track
     * @param startTrack - Starting Track
     * @param length - Total length of the Path
     * @return Path from the Starting Track.
     */
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
