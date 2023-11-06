/*
 * xemantic-music - a Kotlin library implementing some theory of music and composition
 * Copyright (C) 2023 Kazimierz Pogoda
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

package com.xemantic.music

private val indexesInTheCircleOfFifths = arrayOf(
  0,   // C
  7,   // C#
  2,   // D
  9,   // D# / Eb
  4,   // E
  11,  // F
  6,   // Gb / F#
  1,   // G
  8,   // Ab
  3,   // A
  10,  // Bb
  5    // B
)

val Note.indexInTheCircleOfFifths: Int
  get() = indexesInTheCircleOfFifths[indexInOctave]
