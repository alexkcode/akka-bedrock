package bedrockintegration.actor

import akka.actor.{Actor, Props}
import bedrockintegration.client.AkkaGrpcBedrockClient

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

case class PromptRequestActor(prompt: String)
case class PromptResponseActor(response: String)

class BedrockActor(bedrockClient: AkkaGrpcBedrockClient)(implicit ec: ExecutionContext) extends Actor {
  override def receive: Receive = {
    case PromptRequestActor(prompt) =>
      val senderRef = sender()
      bedrockClient.sendPrompt(prompt).onComplete {
        case Success(response) =>
          senderRef ! PromptResponseActor(response)
        case Failure(exception) =>
          senderRef ! akka.actor.Status.Failure(exception)
      }
  }
}

object BedrockActor {
  def props(client: AkkaGrpcBedrockClient)(implicit ec: ExecutionContext): Props =
    Props(new BedrockActor(client))
}
