package bedrockintegration.actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import bedrockintegration.client.AkkaGrpcBedrockClient
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace

class BedrockActorSpec
  extends TestKit(ActorSystem("BedrockActorSpec"))
    with ImplicitSender
    with AnyWordSpecLike
    with BeforeAndAfterAll
    with ScalaFutures {

  implicit val ec: ExecutionContext = system.dispatcher

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "BedrockActor" should {
    "respond with PromptResponseActor on successful prompt processing" in {
      // Stubbed client
      val mockClient = new AkkaGrpcBedrockClient {
        override def sendPrompt(prompt: String): Future[String] =
          Future.successful("This is the response")
      }

      // Create actor
      val actorRef = TestActorRef(new BedrockActor(mockClient))

      // Send request
      actorRef ! PromptRequestActor("Test prompt")

      // Expect a response
      expectMsg(PromptResponseActor("This is the response"))
    }

    "respond with akka.actor.Status.Failure on client failure" in {
      // Stubbed client with failure
      val mockClient = new AkkaGrpcBedrockClient {
        override def sendPrompt(prompt: String): Future[String] =
          Future.failed(new RuntimeException("Client error") with NoStackTrace)
      }

      // Create actor
      val actorRef = TestActorRef(new BedrockActor(mockClient))

      // Send request
      actorRef ! PromptRequestActor("Test prompt")

      // Expect a failure
      expectMsgPF() {
        case akka.actor.Status.Failure(ex) =>
          assert(ex.getMessage == "Client error")
      }
    }
  }
}
