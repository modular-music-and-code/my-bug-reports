package my

import java.nio.file.*
import java.nio.file.StandardOpenOption.*
import java.util.concurrent.*
import kotlin.concurrent.*
import kotlin.io.path.*
import org.assertj.core.api.Assertions.assertThat as t
import org.junit.jupiter.api.*
import org.junit.jupiter.api.io.*
import tools.jackson.databind.*
import tools.jackson.module.kotlin.*

class StreamingWithJaskson3Test {
	data class MyPOJO(val s: String)

	@TempDir private lateinit var dir: Path

	private fun mapper(): ObjectMapper = jacksonMapperBuilder().findAndAddModules().build()

	@Test fun test() {
		val file = dir / "file.io"

		val events = CopyOnWriteArrayList<String>()
		val sema = Semaphore(0)

		val prod = thread(name = "producer") {
			val m = mapper()
			val fileWriter = Files.newBufferedWriter(file, CREATE_NEW)
			val g = m.createGenerator(fileWriter)

			repeat(5) { counter ->
				val pojo = MyPOJO((1 + counter).toString())
				events += "write: $pojo"
				g.writePOJO(pojo)
				fileWriter.flush()
				sema.release()
				Thread.sleep(50)
			}
		}

		val cons = thread(name = "consumer") {
			sema.acquire() // wait for file to exist at all

			val m = mapper()
			val r = m.reader()
			val fileReader = Files.newBufferedReader(file)
			val p = r.createParser(fileReader)

			val iter = m.reader().readValues(p, MyPOJO::class.java)

			while (true) {
				val pojo = iter.next()
				events += " read: $pojo"

				try {
					sema.acquire()
				} catch (_: InterruptedException) {
					break
				}
			}
		}

		prod.join()
		cons.interrupt()

		t(events).containsExactly(
			"write: MyPOJO(s=1)",
			" read: MyPOJO(s=1)",
			"write: MyPOJO(s=2)",
			" read: MyPOJO(s=2)",
			"write: MyPOJO(s=3)",
			" read: MyPOJO(s=3)",
			"write: MyPOJO(s=4)",
			" read: MyPOJO(s=4)",
			"write: MyPOJO(s=5)",
			" read: MyPOJO(s=5)",
		)

		t(file).hasContent("""
			{"s":"1"} {"s":"2"} {"s":"3"} {"s":"4"} {"s":"5"}
		""".trim())
	}
}
