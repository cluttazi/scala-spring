import com.chrisluttazi.sescalamovie.WebApplication
import com.chrisluttazi.sescalamovie.config.WebSecurityConfig
import com.chrisluttazi.sescalamovie.model.User
import com.chrisluttazi.sescalamovie.repository.{AuthorityRepository, UserRepository}
import com.github.scalaspring.scalatest.TestContextManagement
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.test.context.ContextConfiguration

/**
  *
  */
@RunWith(classOf[JUnitRunner])
@SpringBootTest
@ContextConfiguration(classes = Array(classOf[WebApplication], classOf[WebSecurityConfig]))
class ModelSuite extends FlatSpec with TestContextManagement with Matchers {
  @Autowired val app: WebApplication = null
  @Autowired val userRepository: UserRepository = null
  @Autowired val authorityRepository: AuthorityRepository = null

  "A UserRepository" should "not accept null values" in {
    val user = new User
    assertThrows[AuthenticationCredentialsNotFoundException](userRepository save user)
  }

}
