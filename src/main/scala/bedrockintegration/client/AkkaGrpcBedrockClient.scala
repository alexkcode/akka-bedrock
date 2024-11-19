package bedrockintegration.client

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.stream.Materializer
import bedrock.{BedrockServiceClient, PromptRequest}

import scala.concurrent.{ExecutionContext, Future}

class AkkaGrpcBedrockClient(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {

  // gRPC Client Settings (update host and port as needed)
  private val clientSettings = GrpcClientSettings.connectToServiceAt("localhost", 8080).withTls(false)

  // Instantiate the gRPC client
  private val client: BedrockServiceClient = BedrockServiceClient(clientSettings)

  // Method to send a prompt to Bedrock
  def sendPrompt(prompt: String): Future[String] = {
    val request = PromptRequest(prompt) // Create request message
    client.processPrompt(request).map { response =>
      response.response // Extract response field
    }
  }

  // Clean up resources
  def close(): Unit = client.close()
}

