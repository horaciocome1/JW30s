package me.horacioco.jw30s

import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

class IOSGameFeedback : GameFeedback {

    override fun vibrate() {
        val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
        generator.prepare()
        generator.impactOccurred()
        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
    }

    override fun playTimerEndSound() {
        AudioServicesPlaySystemSound(1007u)
    }
}
