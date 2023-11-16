package com.driverskr.lib.net.exception

/**
 * @Author: driverSkr
 * @Time: 2023/11/16 11:13
 * @Description: $
 */
class RequestException(val msg: String, var code: String = ""): Exception(msg)