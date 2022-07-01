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
import org.apache.logging.log4j.Logger
import java.awt.FlowLayout
import javax.sound.midi.MidiSystem
import javax.swing.*

val logger: Logger = LogManager.getLogger()

fun main() {

  val midiInfos = MidiSystem.getMidiDeviceInfo()
  logger.info("MIDI devices")
  midiInfos.forEach {
    logger.info(" |-name: '${it.name}', vendor: '${it.vendor}', description: '${it.description}', version: '${it.version}'")
  }
  val midiNameString = "Piano"
  val keyboardOutputInfo = midiInfos.first { it.name.contains(midiNameString) }
  val keyboardInputInfo = midiInfos.last { it.name.contains(midiNameString) }

  val looper = JavaMidiLooper(
    MidiSystem.getMidiDevice(keyboardInputInfo),
    MidiSystem.getMidiDevice(keyboardOutputInfo)
  )

  SwingUtilities.invokeLater {
    var sequenceCounter = 0
    JFrame().apply {
      defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
      contentPane = JPanel(FlowLayout())
      contentPane.add(
        JButton("record: $sequenceCounter").apply {
          addActionListener {
            if (looper.recording) {
              val sequenceName = sequenceCounter.toString()
              looper.stopRecording()
              contentPane.add(JButton("loop: $sequenceName").apply {
                addActionListener {
                  val loopNumber = looper.playLoop(sequenceName)
                  contentPane.add(JButton("stop loop: $sequenceName - $loopNumber ").apply {
                    addActionListener {
                      looper.stopLoop(loopNumber)
                      remove(this)
                      pack()
                    }
                  })
                  pack()
                }
              })
              sequenceCounter++
              text = "record: $sequenceName"
              pack()
            } else {
              val sequenceName = sequenceCounter.toString()
              looper.startRecording(sequenceName)
              text = "stop recording: $sequenceName"
              pack()
            }
          }
        })
      pack()
      isVisible = true
    }
  }

}
