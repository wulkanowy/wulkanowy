package io.github.wulkanowy.ui.modules.about.creators

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class CreatorsXMLParser {
    fun parse(parser: XmlPullParser): List<Creator> {
        parser.next()
        parser.next()
        return readCreators(parser)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCreators(parser: XmlPullParser): List<Creator> {
        val creators = mutableListOf<Creator>()

        parser.require(XmlPullParser.START_TAG, null, "creators")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "creator") {
                creators.add(readCreator(parser))
            } else {
                skip(parser)
            }
        }
        return creators
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCreator(parser: XmlPullParser): Creator {
        parser.require(XmlPullParser.START_TAG, null, "creator")
        var name: String? = null
        var summary: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "name" -> name = readName(parser)
                "summary" -> summary = readSummary(parser)
                else -> skip(parser)
            }
        }
        return Creator(name, summary)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readName(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, "name")
        val name = readText(parser)
        parser.require(XmlPullParser.END_TAG, null, "name")
        return name
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readSummary(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, "summary")
        val summary = readText(parser)
        parser.require(XmlPullParser.END_TAG, null, "summary")
        return summary
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}