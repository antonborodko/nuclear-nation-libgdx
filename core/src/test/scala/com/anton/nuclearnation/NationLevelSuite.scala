package com.anton.nuclearnation

import com.anton.nuclearnation.nation.NationLevelTracker.{Level1, Level2, Level3}
import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NationLevelSuite extends AnyFunSuite {

  test("Check downgrade of nation level") {
    val level = Level2
    val newLevel = level.updateState(Level1.getMinimalCities, Level1.getMinimalPopulation)
    assert(newLevel.isInstanceOf[Level1.type], "Nation level was not downgraded")
  }

  test("Check upgrade of nation level") {
    val level = Level1
    val newLevel = level.updateState(Level2.getMinimalCities, Level2.getMinimalPopulation)
    assert(newLevel.isInstanceOf[Level2.type], s"Nation level was not upgraded. Returned nation level: ${newLevel}")
  }

  test("Check that minimum level doesn't get downgraded") {
    val level = Level1
    val newLevel = level.updateState(Level1.getMinimalCities-1, Level1.getMinimalPopulation)
    assert(newLevel.isInstanceOf[Level1.type], s"Nation level was changed. Returned nation level: ${newLevel}")
  }

  test("Check that level gets downgraded per city") {
    val level = Level2
    val newLevel = level.updateState(Level2.getMinimalCities-1, Level2.getMinimalPopulation)
    assert(newLevel.isInstanceOf[Level1.type], s"Nation level was not downgraded. Returned nation level: ${newLevel}")
  }

  test("Check that level gets downgraded per population") {
    val level = Level2
    val newLevel = level.updateState(Level2.getMinimalCities, Level2.getMinimalPopulation-1)
    assert(newLevel.isInstanceOf[Level1.type], s"Nation level was not downgraded. Returned nation level: ${newLevel}")
  }

  test("Check that level gets doesn't get upgraded by cities only") {
    val level = Level2
    val newLevel = level.updateState(Level3.getMinimalCities, Level2.getMinimalPopulation)
    assert(newLevel.isInstanceOf[Level2.type], s"Nation level was upgraded. Returned nation level: ${newLevel}")

  }

  test("Check that level doesn't get upgraded per population only ") {
    val level = Level2
    val newLevel = level.updateState(Level2.getMinimalCities, Level3.getMinimalPopulation)
    assert(newLevel.isInstanceOf[Level2.type], s"Nation level was upgraded. Returned nation level: ${newLevel}")
  }

  test("Check that maximum level doesn't get upgraded") {
    val maxLevel = Level1.getMaxLevel
    println(s"Max level is ${maxLevel}")
    val newLevel = maxLevel.updateState(maxLevel.getMinimalCities+10, maxLevel.getMinimalPopulation+100)
    assert(newLevel.isInstanceOf[maxLevel.type], s"Nation level was changed. Returned nation level: ${newLevel}")
  }
}