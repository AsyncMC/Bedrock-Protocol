import dummy.Dummy
import dummy.Dummy2
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Dummy2Test {
    @Test
    fun dummyTest() {
        val dummy = Dummy2()
        dummy.codecovNeedsMoreFiles(areYouWorkingNow = "i don't know")
    }
}
