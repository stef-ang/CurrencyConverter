package com.stefang.app.core.testing.logger

import com.stefang.app.core.data.log.Logger

class TestLogger: Logger {

    var onErrorCalled = false

    var onDebugCalled = false

    override fun error(e: Exception, message: String) { onErrorCalled = true }

    override fun debug(message: String) { onDebugCalled = true }
}
