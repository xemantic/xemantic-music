/*
 * xemantic-music - a Kotlin library implementing some theory of music and composition
 * Copyright (C) 2022 Kazimierz Pogoda
 *
 * This file is part of xemantic-music.
 *
 * xemantic-music is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * xemantic-music is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with xemantic-music.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.xemantic.music.loop

import org.apache.logging.log4j.LogManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequence
import javax.sound.midi.Sequencer

class JavaMidiLooper(
  private val input: MidiDevice,
  private val output: MidiDevice
) : Looper, AutoCloseable {

  private val logger = LogManager.getLogger()

  private val recordingSequencer = MidiSystem.getSequencer(false)

  init {
    if (!input.isOpen) {
      input.open()
    }
    if (!output.isOpen) {
      output.open()
    }
    recordingSequencer.open()
  }

  private val recordingMap = ConcurrentHashMap<String, Pair<Recording, Sequence>>()

  private val recordingState = AtomicBoolean(false)

  private val startedRecordingRef = AtomicReference<StartedRecording>()

  private val loopMap = ConcurrentHashMap<Int, PlayingLoop>()

  private val loopCounter = AtomicInteger(0)

  override val recordings: List<Recording> get() = recordingMap.values.map { it.first }

  override val playingLoops: List<Loop> get() = loopMap.values.map { it.loop }

  override val recording: Boolean get() = recordingState.get()

  override fun startRecording(name: String) {

    if (recordingState.getAndSet(true)) {
      throw IllegalStateException("Cannot start new recording if already recording")
    }

    if (recordingMap.containsKey(name)) {
      throw IllegalArgumentException("Recording already exists: $name")
    }

    logger.info("startRecording: $name")

    input.transmitter.receiver = recordingSequencer.receiver
    with (recordingSequencer) {
      sequence = Sequence(Sequence.PPQ, 24) // TODO where to set up the resolution?
      val track = sequence.createTrack()
      recordEnable(track, -1)
      tickPosition = recordingSequencer.tickPosition
      startRecording()
    }

    startedRecordingRef.set(
      StartedRecording(
        name = name,
        start = System.currentTimeMillis()
      )
    )

  }

  override fun stopRecording(): Recording {

    if (!recordingState.get()) {
      throw IllegalStateException("Cannot stop recording which was not started")
    }

    val startedRecording = startedRecordingRef.getAndSet(null)

    logger.info("stopRecording: ${startedRecording.name}")

    input.transmitter.receiver = null // TODO is that correct, or rather close?
    //Thread.sleep(100)
    //inputSequencer.receiver.close()
    recordingSequencer.stopRecording()
    //inputSequencer.stop()
    val recording = Recording(
      name = startedRecording.name,
      start = startedRecording.start,
      stop = System.currentTimeMillis()
    )
    recordingMap[startedRecording.name] = Pair(recording, recordingSequencer.sequence)
    recordingState.set(false)
    return recording
  }

  override fun playLoop(
    name: String,
    repetitionCount: Int,
    onLoopStopped: (Int) -> Unit
  ): Int {

    val recordingPair = recordingMap[name]
      ?: throw IllegalArgumentException("Cannot play loop which does not exist: $name")

    val loopId = loopCounter.incrementAndGet()

    // TODO add logging infinity
    logger.info("Playing loop, recording: $name, repetitions: $repetitionCount")

    val sequencer = MidiSystem.getSequencer(false).apply {
      open()
      transmitter.receiver = output.receiver
      sequence = recordingPair.second
      loopCount = repetitionCount
      addMetaEventListener {
        logger.info("metaEvent: $it, type: ${it.type}")
        if (it.type == 47) {
          close()// end of sequence
          loopMap.remove(loopId)
        }
      }
      start()
    }

    loopMap[loopId] = PlayingLoop(
      loop = Loop(
        id = loopId,
        start = System.currentTimeMillis(),
        recording = recordingPair.first
      ),
      sequencer = sequencer
    )

    return loopId
  }

  override fun stopLoop(loopId: Int) {
    logger.info("Stopping loop: $loopId")
    val loop = loopMap.remove(loopId)
      ?: throw IllegalArgumentException("Cannot stop non-existent loop: $loopId")
    loop.sequencer.stop()
  }

  override fun removeRecording(name: String) {
    logger.info("Removing recording: $name")
    if (recordingMap.remove(name) == null) {
      throw IllegalArgumentException("Cannot remove non-existent recording: $name")
    }
  }

  override fun close() {
    logger.info("Closing resources")
    input.transmitter.receiver = null
    recordingSequencer.close()
  }

}

internal data class StartedRecording(
  val name: String,
  val start: Long
)

internal data class PlayingLoop(
  val loop: Loop,
  val sequencer: Sequencer
)
