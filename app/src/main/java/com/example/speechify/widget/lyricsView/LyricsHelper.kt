package com.example.speechify.ui.player.lyrics.lyricsParser
import android.content.Context
import java.io.*
import java.util.*
import java.util.regex.Pattern


object LyricsHelper {
    private const val CHARSET = "utf-8"

    private const val LINE_REGEX = "((\\[\\d{2}:\\d{2}\\.\\d{2}])+)(.*)"
    private const val TIME_REGEX = "\\[(\\d{2}):(\\d{2})\\.(\\d{2})]"
    fun parseLrcFromAssets(context: Context, fileName: String): List<Lyrics>? {
        try {
            return parseInputStream(context.resources.assets.open(fileName))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun parseLrcFromFile(file: File?): List<Lyrics>? {
        try {
            return parseInputStream(FileInputStream(file))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    private fun parseInputStream(inputStream: InputStream): List<Lyrics> {
        val lrcs: MutableList<Lyrics> = ArrayList()
        var isr: InputStreamReader? = null
        var br: BufferedReader? = null
        try {
            isr = InputStreamReader(inputStream, CHARSET)
            br = BufferedReader(isr)
            var line: String
            while (br.readLine().also { line = it } != null) {
                val lrcList = parseLrc(line)
                if (lrcList != null && lrcList.size != 0) {
                    lrcs.addAll(lrcList)
                }
            }
            sortLrcs(lrcs)
            return lrcs
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                isr?.close()
                br?.close()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }
        return lrcs
    }

    private fun sortLrcs(lrcs: List<Lyrics>) {
        Collections.sort(lrcs) { o1, o2 -> (o1.time - o2.time).toInt() }
    }

    private fun parseLrc(lrcLine: String): List<Lyrics>? {
        if (lrcLine.trim { it <= ' ' }.isEmpty()) {
            return null
        }
        val lrcs: MutableList<Lyrics> = ArrayList()
        val matcher = Pattern.compile(LINE_REGEX).matcher(lrcLine)
        if (!matcher.matches()) {
            return null
        }
        val time = matcher.group(1)
        val content = matcher.group(3)
        val timeMatcher = Pattern.compile(TIME_REGEX).matcher(time)
        while (timeMatcher.find()) {
            val min = timeMatcher.group(1)
            val sec = timeMatcher.group(2)
            val mil = timeMatcher.group(3)
            val lrc = Lyrics()
            if (content != null && content.length != 0) {
                lrc.time = min.toLong() * 60 * 1000 + sec.toLong() * 1000 + mil.toLong() * 10
                lrc.text = content
                lrcs.add(lrc)
            }
        }
        return lrcs
    }

    @JvmStatic
    fun formatTime(time: Long): String {
        val min = (time / 60000).toInt()
        val sec = (time / 1000 % 60).toInt()
        return adjustFormat(min) + ":" + adjustFormat(sec)
    }

    private fun adjustFormat(time: Int): String {
        return if (time < 10) {
            "0$time"
        } else time.toString() + ""
    }
}