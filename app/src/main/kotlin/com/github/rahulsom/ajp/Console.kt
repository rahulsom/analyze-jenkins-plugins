package com.github.rahulsom.ajp

import org.fusesource.jansi.Ansi.ansi

fun log(message: String) {
  synchronized(System.out) {
    println(ansi().eraseLine().a(message).reset())
  }
}

fun status(prefix: String, count: Int, total: Int) {
  synchronized(System.out) {
    val max = 40
    val equals = count * max / total
    val bar = "".padStart(equals, '=') +
        if (count < total) ">" else "" +
            "".padStart(max - equals, ' ')
    print(ansi().eraseLine()
      .a(prefix.padEnd(31))
      .a("[").a(bar).a("]  ")
      .a(count).a("/").a(total)
      .reset())
    if (count == total) {
      println("")
    }
  }
}
