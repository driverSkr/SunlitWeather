package com.driverskr.lib.net.exception

import java.lang.Exception

class RequestException(val msg: String, var code: String = "") : Exception(msg)