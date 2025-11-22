package com.pomidorka.scheduleaag.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AudioToolbox.AudioServicesPlayAlertSoundWithCompletion
import platform.AudioToolbox.SystemSoundID
import platform.AudioToolbox.kSystemSoundID_Vibrate
import platform.CoreHaptics.CHHapticDynamicParameter
import platform.CoreHaptics.CHHapticEngine
import platform.CoreHaptics.CHHapticEngineFinishedActionStopEngine
import platform.CoreHaptics.CHHapticEngineStoppedReasonApplicationSuspended
import platform.CoreHaptics.CHHapticEngineStoppedReasonAudioSessionInterrupt
import platform.CoreHaptics.CHHapticEngineStoppedReasonIdleTimeout
import platform.CoreHaptics.CHHapticEngineStoppedReasonSystemError
import platform.CoreHaptics.CHHapticEvent
import platform.CoreHaptics.CHHapticEventParameter
import platform.CoreHaptics.CHHapticEventTypeHapticContinuous
import platform.CoreHaptics.CHHapticPattern
import platform.Foundation.NSTimeInterval
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

internal class IOSVibrator : Vibrator {
    private var customHaptic = CustomHaptic()

    private fun Long.toIosDuration(): NSTimeInterval {
        return this.toDouble() / 1000
    }

    override val isVibrateSupported: Boolean
        get() = true

    override fun vibrateClick() {
        if (Vibrator.isEnabled) {
            UIImpactFeedbackGenerator(
                style = HapticStyle.Rigid.style
            ).impactOccurred()
        }
    }

    override fun vibrate(time: Long) {
        if (Vibrator.isEnabled) {
            try {
                customHaptic.playHaptic(
                    listOf(
                        CHHapticEvent(
                            eventType = CHHapticEventTypeHapticContinuous,
                            parameters = emptyList<CHHapticEventParameter>(),
                            relativeTime = 0.1,
                            duration = time.toIosDuration()
                        )
                    )
                )
            } catch (e: Exception) {
                println("vibrate error")
                e.printStackTrace()
            }
        }
    }
}

internal sealed class HapticStyle(val style: UIImpactFeedbackStyle) {
    data object Heavy : HapticStyle(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
    data object Light : HapticStyle(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
    data object Rigid : HapticStyle(UIImpactFeedbackStyle.UIImpactFeedbackStyleRigid)
    data object Soft : HapticStyle(UIImpactFeedbackStyle.UIImpactFeedbackStyleSoft)
    data object Medium : HapticStyle(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
}

internal class CustomHaptic {
    private var engine: CHHapticEngine? = null

    @OptIn(ExperimentalForeignApi::class)
    @Throws(Throwable::class)
    internal fun playHaptic(
        eventPattern: List<CHHapticEvent>
    ) {
        if (engine == null) {
            resetEngine()
        }
        engine?.let { engine ->
            engine.stopWithCompletionHandler {
                try {
                    val pattern = CHHapticPattern(
                        events = eventPattern,
                        parameters = emptyList<CHHapticDynamicParameter>(),
                        error = null
                    )
                    val player = engine.createPlayerWithPattern(pattern = pattern, error = null)
                    engine.notifyWhenPlayersFinished {
                        CHHapticEngineFinishedActionStopEngine
                    }
                    engine.startWithCompletionHandler {
                        player?.startAtTime(0.0, error = null)
                    }
                } catch (e: Exception) {
                    println("playHaptic error")
                    e.printStackTrace()
                }
            }
        }
    }

    internal fun stopHaptic() {
        engine?.stopWithCompletionHandler {

        }
    }


    @OptIn(ExperimentalForeignApi::class)
    private fun resetEngine() {
        try {
            engine = CHHapticEngine(null, null)
            engine?.setStoppedHandler { reason ->
                when (reason) {
                    CHHapticEngineStoppedReasonAudioSessionInterrupt -> {
                        println("REASON: Audio Session Interrupt")
                    }

                    CHHapticEngineStoppedReasonApplicationSuspended -> {
                        println("REASON: Application Suspended")
                    }

                    CHHapticEngineStoppedReasonIdleTimeout -> {
                        println("REASON: Idle Timeout")
                    }

                    CHHapticEngineStoppedReasonSystemError -> {
                        println("REASON: System Error")

                    }
                }
            }
        } catch (e: Exception) {
            println("reset error")
            e.printStackTrace()
        }
    }
}

actual fun getVibrator(): Vibrator = IOSVibrator()