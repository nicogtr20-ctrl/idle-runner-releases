package com.overmind.zero;

import java.util.Arrays;

/**
 * Actual tactile contact is a FRAME-local observation, not a persistent
 * belief. The intentionally decaying touchPulse is separate and is NOT reset.
 * Invoke AFTER cortex.command() has read last frame's true contact but BEFORE
 * this frame's all-world collision solvers produce its actual tactile input.
 */
final class Zero387TactileFrame {
    private Zero387TactileFrame(){}
    static void begin(Morphology.Surface skin){
        if(skin==null)throw new IllegalArgumentException("Missing tissue");
        Arrays.fill(skin.touchSense,0f);
    }
    static void contact(Morphology.Surface skin,int index){
        if(skin==null||index<0||index>=Morphology.POINTS)
            throw new IllegalArgumentException("Invalid real membrane contact");
        skin.touchSense[index]=Math.max(skin.touchSense[index],1f);
        skin.touchPulse[index]=Math.max(skin.touchPulse[index],.9f);
    }
}
