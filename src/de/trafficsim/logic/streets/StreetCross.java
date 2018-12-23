package de.trafficsim.logic.streets;

import de.trafficsim.gui.views.StreetCrossView;
import de.trafficsim.gui.views.StreetView;
import de.trafficsim.logic.streets.tracks.Track;
import de.trafficsim.logic.streets.tracks.TrackAndPosition;
import de.trafficsim.logic.streets.tracks.TrackStraight;
import de.trafficsim.logic.streets.tracks.TrafficPriorityChecker;
import de.trafficsim.util.geometry.Position;

public class StreetCross extends Street {

    public StreetCross() {
        this(Position.ZERO);
    }

    public StreetCross(Position position) {
        super(position, StreetType.CROSS);

        Track[] inTracks = new Track[4];
        Track[] outTracks = new Track[4];

        //create in- and outgoing tracks
        inTracks[0] = addInTrack(new TrackStraight(new Position(-2.5, -25), new Position(-2.5, -12.5), this));
        inTracks[1] = addInTrack(new TrackStraight(new Position(25, -2.5), new Position(12.5, -2.5), this));
        inTracks[2] = addInTrack(new TrackStraight(new Position(2.5, 25), new Position(2.5, 12.5), this));
        inTracks[3] = addInTrack(new TrackStraight(new Position(-25, 2.5), new Position(-12.5, 2.5), this));

        outTracks[0] = addOutTrack(new TrackStraight(new Position(2.5, -12.5), new Position(2.5, -25), this));
        outTracks[1] = addOutTrack(new TrackStraight(new Position(12.5, 2.5), new Position(25, 2.5), this));
        outTracks[2] = addOutTrack(new TrackStraight(new Position(-2.5, 12.5), new Position(-2.5, 25), this));
        outTracks[3] = addOutTrack(new TrackStraight(new Position(-12.5, -2.5), new Position(-25, -2.5), this));

        Track[][] betweenTracks = new Track[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i != j) {
                    betweenTracks[i][j] = addTrackBetween(inTracks[i], outTracks[j]);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            Track track = betweenTracks[i][(i + 2) % 4];
            track.setPriorityStopPoint(new TrafficPriorityChecker(track, 5, new TrackAndPosition(betweenTracks[(i+3) % 4][i], 11), new TrackAndPosition(betweenTracks[(i+3) % 4][(i+1) % 4], 10)));

            track = betweenTracks[i][(i + 1) % 4];
            track.setPriorityStopPoint(new TrafficPriorityChecker(track, 5, new TrackAndPosition(betweenTracks[(i+3) % 4][i], 14.75), new TrackAndPosition(betweenTracks[(i+2) % 4][i], 13.75)));
        }

        stoppedCountForDeadLock = 4;

    }

    @Override
    public StreetView createView() {
        return new StreetCrossView(this);
    }
}
