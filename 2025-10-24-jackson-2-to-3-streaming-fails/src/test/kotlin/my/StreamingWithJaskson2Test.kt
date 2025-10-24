package my

import com.fasterxml.jackson.databind.SerializationFeature.*
import com.fasterxml.jackson.module.kotlin.*
import org.apache.commons.io.output.*
import org.junit.jupiter.api.*

class StreamingWithJaskson2Test {
	data class MyPOJO(val s: String)

	@Test @Timeout(5) fun test() {
		val out = QueueOutputStream()
		val inp = out.newQueueInputStream()

		val mapper = jacksonObjectMapper().findAndRegisterModules().enable(FLUSH_AFTER_WRITE_VALUE)
		val generator = mapper.createGenerator(out)
		val objectReader = mapper.reader()
		val parser = objectReader.createParser(inp)
		val readIter = objectReader.readValues(parser, MyPOJO::class.java)

		generator.writePOJO(MyPOJO("1"))
		println(readIter.next())

		generator.writePOJO(MyPOJO("2"))
		println(readIter.next())

		generator.writePOJO(MyPOJO("3"))
		println(readIter.next())
	}
}
