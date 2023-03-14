package com.stefang.app.core.testing.logger

import com.stefang.app.core.data.log.Logger

class TestLogger: Logger {

    override fun error(e: Exception, message: String) {}

    override fun debug(message: String) {}
}
