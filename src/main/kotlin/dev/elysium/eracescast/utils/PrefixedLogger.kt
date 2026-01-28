package dev.elysium.eracescast.utils

import org.apache.logging.log4j.Logger


class PrefixedLogger(private val logger: Logger, private val prefix: String) {
    fun info(msg: String) = logger.info("[$prefix] $msg")
    fun warn(msg: String) = logger.warn("[$prefix] $msg")
    fun error(msg: String) = logger.error("[$prefix] $msg")
}
