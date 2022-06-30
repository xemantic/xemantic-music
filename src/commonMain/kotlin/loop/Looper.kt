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

/**
 * A looper pedal-like API. It's a general interface but focused on MIDI devices.
 */
interface Looper {

  /**
   * The list of recordings sorted by the start time.
   */
  val recordings: List<Recording>

  /**
   * The list of loops being currently played, sorted by the start time.
   */
  val playingLoops: List<Loop>

  /**
   * Indicates if something is being currently recorded.
   */
  val recording: Boolean

  /**
   * Starts recording under a given `name`.
   * The [Recording] will be created only if [stopRecording] is being called afterwards.
   *
   * @param name the name of the recording.
   * @throws IllegalArgumentException if the recording of given name already exists.
   * @throws IllegalStateException if the looper is currently recording.
   */
  fun startRecording(name: String)

  /**
   * Stops the recording initiated with [startRecording].
   *
   * @return the [Recording] instance describing the finished recording.
   * @throws IllegalStateException if nothing is being recorded. See [recording] flog.
   */
  fun stopRecording(): Recording

  /**
   * Plays the loop.
   *
   * @param name the name of the loop.
   * @param repetitionCount how many times it should be repeated, `-1` by default which
   *          implies endless playback (at least until [stopLoop] is called.
   * @param onLoopStopped an optional callback called when the loop is stopped. Either
   *          triggered after reaching `repetitionCount` or when [stopLoop] is being called.
   * @return the loop id.
   */
  fun playLoop(
    name: String,
    repetitionCount: Int = -1,
    onLoopStopped: (Int) -> Unit = {}
  ): Int

  /**
   * Stops the loop of given id.
   * Note: it will cause the loop to be removed from [playingLoops].
   *
   * @param loopId the loop id.
   * @throws IllegalArgumentException if the loop does not exists.
   */
  fun stopLoop(loopId: Int)

  /**
   * Removes recording of given `name`.
   *
   * @param name the name of the recording to remove.
   * @throws IllegalArgumentException if the recording of given name does not exists.
   */
  fun removeRecording(name: String)

}

data class Recording(
  val name: String,
  val start: Long,
  val stop: Long
)

data class Loop(
  val id: Int,
  val start: Long,
  val recording: Recording
)
