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

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CircleOfFifthsTest {

  @Test
  fun shouldReturnProperIndexInTheCircleOfFifths() {
    Note.C.indexInTheCircleOfFifths shouldBe 0
    Note.Cs.indexInTheCircleOfFifths shouldBe 7
    Note.D.indexInTheCircleOfFifths shouldBe 2
    Note.Ds.indexInTheCircleOfFifths shouldBe 9
    Note.E.indexInTheCircleOfFifths shouldBe 4
    Note.F.indexInTheCircleOfFifths shouldBe 11
    Note.Fs.indexInTheCircleOfFifths shouldBe 6
    Note.G.indexInTheCircleOfFifths shouldBe 1
    Note.Gs.indexInTheCircleOfFifths shouldBe 8
    Note.A.indexInTheCircleOfFifths shouldBe 3
    Note.As.indexInTheCircleOfFifths shouldBe 10
    Note.B.indexInTheCircleOfFifths shouldBe 5
  }

}
