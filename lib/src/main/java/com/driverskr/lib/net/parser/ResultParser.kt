package com.driverskr.lib.net.parser

interface ResultParser<T> {
    fun parse(json: String): T
}