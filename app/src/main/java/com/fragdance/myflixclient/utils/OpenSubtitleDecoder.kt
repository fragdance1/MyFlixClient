/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.text.subrip

import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import com.fragdance.myflixclient.Settings
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder
import com.google.android.exoplayer2.text.Subtitle
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.ParsableByteArray
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

/** A [SimpleSubtitleDecoder] for SubRip.  */
class OpenSubtitleDecoder : SimpleSubtitleDecoder("SubtitleDecoder") {
    private val textBuilder: StringBuilder = StringBuilder()
    private val tags: ArrayList<String> = ArrayList()

    fun my_decode(bytes: ByteArray, length: Int, reset: Boolean): Subtitle {
        val cues = ArrayList<Cue>()
        val cueTimesUs = arrayListOf<Long>()

        val subripData = ParsableByteArray(bytes, bytes.size)
        var currentLine: String?
        Timber.tag(Settings.TAG).d("Allocate space for subtitle "+bytes.size)
        while (subripData.readLine().also { currentLine = it } != null) {

            if (currentLine!!.isEmpty()) {
                // Skip blank lines.
                continue
            }

            // Parse and check the index line.
            try {
                currentLine!!.toInt()
            } catch (e: NumberFormatException) {
                Log.w(
                    TAG,
                    "Skipping invalid index: $currentLine"
                )
                continue
            }

            // Read and parse the timing line.
            currentLine = subripData.readLine()
            if (currentLine == null) {
                Log.w(TAG, "Unexpected end")
                break
            }
            val matcher = SUBRIP_TIMING_LINE.matcher(currentLine)
            if (matcher.matches()) {
                cueTimesUs.add(parseTimecode(matcher,  /* groupOffset= */1))
                cueTimesUs.add(parseTimecode(matcher,  /* groupOffset= */6))
            } else {
                Log.w(
                    TAG,
                    "Skipping invalid timing: $currentLine"
                )
                continue
            }

            // Read and parse the text and tags.
            textBuilder.setLength(0)

            tags.clear()
            currentLine = subripData.readLine()
            while (!TextUtils.isEmpty(currentLine)) {
                if (textBuilder.length > 0) {
                    textBuilder.append("<br>")
                }
                textBuilder.append(processLine(currentLine, tags))
                currentLine = subripData.readLine()
            }
            val text = Html.fromHtml(textBuilder.toString())
            var alignmentTag: String? = null
            for (i in tags.indices) {
                val tag = tags[i]
                if (tag.matches(SUBRIP_ALIGNMENT_TAG)) {
                    alignmentTag = tag
                    // Subsequent alignment tags should be ignored.
                    break
                }
            }
            cues.add(buildCue(text, alignmentTag))
            cues.add(Cue.EMPTY)
        }
        val cuesArray = cues.toTypedArray()
        val cueTimesUsArray: LongArray = cueTimesUs.toLongArray()//.toArray()
        return SubripSubtitle(cuesArray, cueTimesUsArray)
    }

    /**
     * Trims and removes tags from the given line. The removed tags are added to `tags`.
     *
     * @param line The line to process.
     * @param tags A list to which removed tags will be added.
     * @return The processed line.
     */
    private fun processLine(line: String?, tags: ArrayList<String>): String {
        var line = line
        line = line!!.trim { it <= ' ' }
        var removedCharacterCount = 0
        val processedLine = StringBuilder(line)
        val matcher = SUBRIP_TAG_PATTERN.matcher(line)
        while (matcher.find()) {
            val tag = matcher.group()
            tags.add(tag)
            val start = matcher.start() - removedCharacterCount
            val tagLength = tag.length
            processedLine.replace(start,  /* end= */start + tagLength,  /* str= */"")
            removedCharacterCount += tagLength
        }
        return processedLine.toString()
    }

    /**
     * Build a [Cue] based on the given text and alignment tag.
     *
     * @param text The text.
     * @param alignmentTag The alignment tag, or `null` if no alignment tag is available.
     * @return Built cue
     */
    private fun buildCue(text: Spanned, alignmentTag: String?): Cue {
        val cue = Cue.Builder().setText(text)
        if (alignmentTag == null) {
            return cue.build()
        }
        when (alignmentTag) {
            ALIGN_BOTTOM_LEFT, ALIGN_MID_LEFT, ALIGN_TOP_LEFT -> cue.positionAnchor =
                Cue.ANCHOR_TYPE_START
            ALIGN_BOTTOM_RIGHT, ALIGN_MID_RIGHT, ALIGN_TOP_RIGHT -> cue.positionAnchor =
                Cue.ANCHOR_TYPE_END
            ALIGN_BOTTOM_MID, ALIGN_MID_MID, ALIGN_TOP_MID -> cue.positionAnchor =
                Cue.ANCHOR_TYPE_MIDDLE
            else -> cue.positionAnchor = Cue.ANCHOR_TYPE_MIDDLE
        }
        when (alignmentTag) {
            ALIGN_BOTTOM_LEFT, ALIGN_BOTTOM_MID, ALIGN_BOTTOM_RIGHT -> cue.lineAnchor =
                Cue.ANCHOR_TYPE_END
            ALIGN_TOP_LEFT, ALIGN_TOP_MID, ALIGN_TOP_RIGHT -> cue.lineAnchor =
                Cue.ANCHOR_TYPE_START
            ALIGN_MID_LEFT, ALIGN_MID_MID, ALIGN_MID_RIGHT -> cue.lineAnchor =
                Cue.ANCHOR_TYPE_MIDDLE
            else -> cue.lineAnchor = Cue.ANCHOR_TYPE_MIDDLE
        }
        return cue.setPosition(getFractionalPositionForAnchorType(cue.positionAnchor))
            .setLine(getFractionalPositionForAnchorType(cue.lineAnchor), Cue.LINE_TYPE_FRACTION)
            .build()
    }

    companion object {
        // Fractional positions for use when alignment tags are present.
        private const val START_FRACTION = 0.08f
        private const val END_FRACTION = 1 - START_FRACTION
        private const val MID_FRACTION = 0.5f
        private const val TAG = "SubripDecoder"

        // Some SRT files don't include hours or milliseconds in the timecode, so we use optional groups.
        private const val SUBRIP_TIMECODE = "(?:(\\d+):)?(\\d+):(\\d+)(?:,(\\d+))?"
        private val SUBRIP_TIMING_LINE =
            Pattern.compile("\\s*(" + SUBRIP_TIMECODE + ")\\s*-->\\s*(" + SUBRIP_TIMECODE + ")\\s*")

        // NOTE: Android Studio's suggestion to simplify '\\}' is incorrect [internal: b/144480183].
        private val SUBRIP_TAG_PATTERN = Pattern.compile("\\{\\\\.*?\\}")
        private val SUBRIP_ALIGNMENT_TAG:Regex = "\\{\\\\an[1-9]\\}".toRegex()

        // Alignment tags for SSA V4+.
        private const val ALIGN_BOTTOM_LEFT = "{\\an1}"
        private const val ALIGN_BOTTOM_MID = "{\\an2}"
        private const val ALIGN_BOTTOM_RIGHT = "{\\an3}"
        private const val ALIGN_MID_LEFT = "{\\an4}"
        private const val ALIGN_MID_MID = "{\\an5}"
        private const val ALIGN_MID_RIGHT = "{\\an6}"
        private const val ALIGN_TOP_LEFT = "{\\an7}"
        private const val ALIGN_TOP_MID = "{\\an8}"
        private const val ALIGN_TOP_RIGHT = "{\\an9}"
        private fun parseTimecode(matcher: Matcher, groupOffset: Int): Long {
            val hours = matcher.group(groupOffset + 1)
            var timestampMs = if (hours != null) hours.toLong() * 60 * 60 * 1000 else 0
            timestampMs += Assertions.checkNotNull(matcher.group(groupOffset + 2))
                .toLong() * 60 * 1000
            timestampMs += Assertions.checkNotNull(matcher.group(groupOffset + 3)).toLong() * 1000
            val millis = matcher.group(groupOffset + 4)
            if (millis != null) {
                timestampMs += millis.toLong()
            }
            return timestampMs * 1000
        }

        /* package */
        fun getFractionalPositionForAnchorType(anchorType: @Cue.AnchorType Int): Float {
            return when (anchorType) {
                Cue.ANCHOR_TYPE_START -> START_FRACTION
                Cue.ANCHOR_TYPE_MIDDLE -> MID_FRACTION
                Cue.ANCHOR_TYPE_END -> END_FRACTION
                Cue.TYPE_UNSET -> throw IllegalArgumentException()
                else -> throw IllegalArgumentException()
            }
        }
    }

    override fun decode(data: ByteArray, size: Int, reset: Boolean): Subtitle {
        TODO("Not yet implemented")
    }

}