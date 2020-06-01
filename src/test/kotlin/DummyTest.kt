import com.github.asyncmc.bedrock.dummy.Dummy
import org.junit.jupiter.api.Test

internal class DummyTest {
    @Test
    fun dummyTest() {
        val dummy = Dummy()
        dummy.codecov("i don't know")
    }
}
