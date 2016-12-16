package fr.gplassard.dynamodbeventparsing

import cats.data.Validated.{Invalid, Valid}
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import fr.gplassard.dynamodbeventparsing.Model.Person
import jto.validation.{From, GenericRules, IdxPathNode, KeyPathNode, ParsingRules, Path, Rule, RuleLike, ValidationError}

trait Rules extends GenericRules with ParsingRules {

  implicit val strRule: Rule[AttributeValue, String] = Rule.fromMapping { attribute =>
    if (attribute.getS != null) Valid(attribute.getS)
    else Invalid(Seq(ValidationError("error.invalid", s"Expected String, got $attribute")))
  }

  implicit val intRule: Rule[AttributeValue, Int] = Rule.fromMapping[AttributeValue, String] { attribute =>
    if (attribute.getN != null) Valid(attribute.getN)
    else Invalid(Seq(ValidationError("error.invalid", s"Expected Number, got $attribute")))
  } andThen intR

  implicit val booleanRule: Rule[AttributeValue, Boolean] = Rule.fromMapping { attribute =>
    if (attribute.getBOOL != null) Valid(attribute.getBOOL)
    else Invalid(Seq(ValidationError("error.invalid", s"Expected Boolean, got $attribute")))
  }

  type DynamoDocument = java.util.Map[String, AttributeValue]

  def dynamoDocumentToAttributeValue: Rule[DynamoDocument, AttributeValue] = Rule.fromMapping(map => Valid(new AttributeValue().withM(map)))

  implicit val personRule: Rule[DynamoDocument, Person] = dynamoDocumentToAttributeValue andThen From[AttributeValue] { __ =>
    import Rules._
    ( (__ \ "name").read[String] ~
      (__ \ "age").read[Int] ~
      (__ \ "lovesChocolate").read[Boolean])(Person.apply)
  }
}

object Rules {
  private def search(path: Path, attribute: AttributeValue): Option[AttributeValue] = path.path match {
    case KeyPathNode(k) :: _  => Option(attribute.getM).flatMap(map => Option(map.get(k)))
    case IdxPathNode(i) :: _  => Option(attribute.getL).flatMap(list => if (list.size() > i) Option(list.get(i)) else None)
    case Nil                  => Some(attribute)
  }

  implicit def pickInAttribute[II <: AttributeValue, O](p: Path)(implicit r: RuleLike[AttributeValue, O]): Rule[II, O] = Rule[II, AttributeValue] { attribute =>
    search(p, attribute) match {
      case Some(js) => Valid(js)
      case None => Invalid(Seq(Path -> Seq(ValidationError("error.required"))))
    }
  } andThen r
}
