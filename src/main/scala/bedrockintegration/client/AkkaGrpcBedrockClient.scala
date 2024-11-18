package bedrockintegration.client

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.stream.Materializer
import bedrockintegration.{BedrockServiceClient, PromptRequest}
import scala.concurrent.Future

object AkkaGrpcBedrockClient extends App {
  implicit val system: ActorSystem = ActorSystem("AkkaGrpcClient")
  implicit val materializer: Materializer = Materializer(system)

  val grpcSettings: GrpcClientSettings = GrpcClientSettings
    .connectToServiceAt("your-api-gateway-url", 443) // Replace with API Gateway URL
    .withTls(true)                                   // Ensure TLS is enabled

  val client: BedrockServiceClient = BedrockServiceClient(grpcSettings)

  // Send a request to AWS Lambda (via API Gateway)
  val prompt = "Translate the following text to French: Hello, world!"
  val request = PromptRequest(prompt)

  val response: Future[bedrock.PromptResponse] = client.processPrompt(request)

  response.foreach { res =>
    println(s"Response from Bedrock: ${res.response}")
    system.terminate()
  }
}
