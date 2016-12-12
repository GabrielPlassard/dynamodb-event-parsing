package fr.gplassard.dynamodbeventparsing

import cats.data.Validated.{Invalid, Valid}
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import jto.validation.{GenericRules, ParsingRules, Rule, ValidationError}

trait Rules extends GenericRules with ParsingRules {

  val strRule: Rule[AttributeValue, String] = Rule.fromMapping { attribute =>
    if (attribute.getS != null) Valid(attribute.getS)
    else Invalid(Seq(ValidationError(s"Expected String, got $attribute")))
  }

  val intRule: Rule[AttributeValue, Int] = strRule.andThen(intR)

  val booleanRule: Rule[AttributeValue, Boolean] = Rule.fromMapping { attribute =>
    if (attribute.getBOOL != null) Valid(attribute.getBOOL)
    else Invalid(Seq(ValidationError(s"Expected Boolean, got $attribute")))
  }
}
