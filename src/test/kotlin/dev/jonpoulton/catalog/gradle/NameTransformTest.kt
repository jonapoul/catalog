package dev.jonpoulton.catalog.gradle

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NameTransformTest {
  @Test
  fun `No change`() = runTransformTest(
    transform = NameTransform.NoChange,
    input = "my_test_string",
    expected = "my_test_string",
  )

  @Test
  fun `Camel-casing an underscored string`() = runTransformTest(
    transform = NameTransform.CamelCase,
    input = "my_test_string",
    expected = "myTestString",
  )

  @Test
  fun `Camel-casing an already camel-cased string`() = runTransformTest(
    transform = NameTransform.CamelCase,
    input = "myTestString",
    expected = "myTestString",
  )

  @Test
  fun `Removing prefix when the prefix isn't there`() = runTransformTest(
    transform = NameTransform.removePrefix(prefix = "test"),
    input = "hello_world",
    expected = "hello_world",
  )

  @Test
  fun `Removing prefix when the prefix is there`() = runTransformTest(
    transform = NameTransform.removePrefix(prefix = "test_"),
    input = "test_string",
    expected = "string",
  )

  @Test
  fun `Removing prefix when the prefix is there but wrong case`() = runTransformTest(
    transform = NameTransform.removePrefix(prefix = "Test_"),
    input = "test_string",
    expected = "test_string",
  )

  @Test
  fun `Removing suffix when the prefix is there`() = runTransformTest(
    transform = NameTransform.removeSuffix(suffix = "_string"),
    input = "test_string",
    expected = "test",
  )

  @Test
  fun `Chaining with a single transform`() = runTransformTest(
    transform = NameTransform.chained(NameTransform.CamelCase),
    input = "test_string",
    expected = "testString",
  )

  @Test
  fun `Chaining with multiple transforms`() = runTransformTest(
    transform = NameTransform.chained(
      NameTransform.removePrefix(prefix = "my_"),
      NameTransform.CamelCase,
      NameTransform.removeSuffix(suffix = "Suffix"),
      NameTransform { name -> name + "Other" },
    ),
    input = "my_test_string_with_suffix",
    expected = "testStringWithOther",
  )

  private fun runTransformTest(transform: NameTransform, input: String, expected: String) {
    assertThat(expected).isEqualTo(transform(input))
  }
}
