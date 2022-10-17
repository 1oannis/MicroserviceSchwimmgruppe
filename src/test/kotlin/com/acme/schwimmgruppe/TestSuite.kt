package com.acme.schwimmgruppe

import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.IncludeTags
import org.junit.platform.suite.api.Suite

@Suite
@IncludeEngines("junit-jupiter")
@IncludeTags("rest", "service")
class TestSuite
