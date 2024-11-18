package bedrockintegration.actors

import akka.actor.{Actor, Props}
import bedrockintegration.client.AkkaGrpcBedrockClient
import scala.concurrent.ExecutionContext

case class PromptRequest(prompt: String)
case class PromptResponse(response: String)

class PromptActor(bedrockClient: AkkaGrpcBedrockClient)(implicit ec: ExecutionContext) extends Actor {
  override def receive: Receive = {
    case PromptRequest(prompt) =>
      val senderRef = sender()
      bedrockClient.processPrompt(prompt).map(response =>
        senderRef ! PromptResponse(response)
      )
  }
}

object PromptActor {
  def props(bedrockClient: AkkaGrpcBedrockClient)(implicit ec: ExecutionContext): Props =
    Props(new PromptActor(bedrockClient))
}
