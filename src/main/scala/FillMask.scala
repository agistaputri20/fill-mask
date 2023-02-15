import ai.djl.inference.Predictor
import ai.djl.modality.{Input, Output}
import ai.djl.ndarray.NDList
import ai.djl.repository.zoo.Criteria
import org.slf4j.LoggerFactory
import java.nio.file.{Files,Paths}

object FillMask{

  private val logger = LoggerFactory.getLogger(classOf[FillMask])

  def main(args: Array[String]): Unit = {
    val criteria = Criteria.builder
      .setTypes(classOf[Input], classOf[Output])
      .optModelPath(Paths.get("model/model.zip"))
      .optEngine("TensorFlow")
      .build

    try {
      val tf = criteria.loadModel
      val transformer = tf.newPredictor
      try {
        fillMask(transformer)
      } finally {
        if (tf != null) tf.close()
        if (transformer != null) transformer.close()
      }
    }
  }

  private def fillMask(transformer: Predictor[Input, Output]): Unit = {
    val bertCriteria = Criteria.builder
      .setTypes(classOf[NDList], classOf[NDList])
      .optModelPath(Paths.get("model/model.zip"))
      .optEngine("TensorFlow")
      .build

    try {
      val bert = bertCriteria.loadModel
      val predictor = bert.newPredictor
      try {
        val input = new Input
        input.add("1,Q42,[MASK] Adams,[MASK] writer and [MASK],[MASK],United Kingdom,Artist,1952,2001.0,natural causes,49.0")
        input.addProperty("handler", "fillMask_preprocess")
        var output = transformer.predict(input)

        val manager = bert.getNDManager.newSubManager
        val ndList = output.getDataAsNDList(manager)
        val predictions = predictor.predict(ndList)

        val file = Paths.get("build/fillMask_output.ndlist")
        try {
          val os = Files.newOutputStream(file)
          try predictions.encode(os)
          finally if (os != null) os.close()
        }

        val postProcessing = new Input
        postProcessing.add(predictions)
        postProcessing.addProperty("handler", "fillMask_postprocess")
        output = transformer.predict(postProcessing)
        val result = output.getData.getAsString
        logger.info(result)
      } finally {
        if (bert != null) bert.close()
        if (predictor != null) predictor.close()
      }
    }
  }

  case class FillMask()
}