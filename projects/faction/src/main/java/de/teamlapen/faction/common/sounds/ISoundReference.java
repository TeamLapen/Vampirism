package de.teamlapen.faction.common.sounds;

public interface ISoundReference {

    boolean isPlaying();

    void startPlaying();

    void stopPlaying();

    class NoOp implements ISoundReference {

        @Override
        public boolean isPlaying() { return false; }

        @Override
        public void startPlaying() { }

        @Override
        public void stopPlaying() { }
    }
}
