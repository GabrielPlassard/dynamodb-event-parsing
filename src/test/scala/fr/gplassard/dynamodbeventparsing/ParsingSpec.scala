package fr.gplassard.dynamodbeventparsing

import cats.data.Validated.Valid
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import org.specs2.mutable.Specification

class ParsingSpec extends Specification with Rules {

  "The string rule" should {
    "extract from a string attribute" in {
      val attribute = new AttributeValue("a string")

      strRule.validate(attribute) must beEqualTo(Valid("a string"))
    }

    "fail if it's not a string" in {
      val attribute = new AttributeValue().withBOOL(false)

      strRule.validate(attribute).isInvalid must beTrue
    }
  }

  "The int rule" should {
    "extract from a string attribute" in {
      val attribute = new AttributeValue("12")

      intRule.validate(attribute) must beEqualTo(Valid(12))
    }

    "fail if it's not an int" in {
      val attribute = new AttributeValue("12.3")

      intRule.validate(attribute).isInvalid must beTrue
    }
  }
  "The bool rule" should {
    "extract from a bool attribute" in {
      val attribute = new AttributeValue().withBOOL(true)

      booleanRule.validate(attribute) must beEqualTo(Valid(true))
    }

    "fail if it's not a boolean" in {
      val attribute = new AttributeValue("true")

      booleanRule.validate(attribute).isInvalid must beTrue
    }
  }
}
