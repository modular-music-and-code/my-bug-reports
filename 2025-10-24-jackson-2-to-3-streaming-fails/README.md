# This is a standalone Gradle project.

## Run: >> gradle test

Should print something like:

	> Task :test

	StreamingWithJaskson3Test > test() FAILED
		java.util.NoSuchElementException at StreamingWithJaskson3Test.kt:22

	3 tests completed, 1 failed

	> Task :test FAILED

## Explanation:

* `StreamingWithJaskson2Test.kt` - use-case using Jackson-2 : _works_
* `StreamingWithJaskson3Test.kt` - use-case using Jackson-3 : _fails_
* `StreamingWithJaskson3FixedTest.kt` - added `generator.flush()` : _works again!_

It seems that there is NO problem with `ObjectReader.readValues`, but that Jackson-3 doesn't forward flushing to underlying layers.
