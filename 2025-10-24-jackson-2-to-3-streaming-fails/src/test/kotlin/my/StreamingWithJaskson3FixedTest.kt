package my

import org.apache.commons.io.output.*
import org.junit.jupiter.api.*
import tools.jackson.databind.SerializationFeature.*
import tools.jackson.module.kotlin.*

class StreamingWithJaskson3FixedTest {
	data class MyPOJO(val s: String)

	@Test @Timeout(5) fun test() {
		val out = QueueOutputStream()
		val inp = out.newQueueInputStream()

		val mapper = jacksonMapperBuilder().findAndAddModules().enable(FLUSH_AFTER_WRITE_VALUE).build()
		val generator = mapper.createGenerator(out)
		val objectReader = mapper.reader()
		val parser = objectReader.createParser(inp)
		val readIter = objectReader.readValues(parser, MyPOJO::class.java)

		generator.writePOJO(MyPOJO("1"))
		generator.flush() // this is the fix
		println(readIter.next())

		generator.writePOJO(MyPOJO("2"))
		generator.flush() // this is the fix
		println(readIter.next())

		generator.writePOJO(MyPOJO("3"))
		generator.flush() // this is the fix
		println(readIter.next())
	}
}
