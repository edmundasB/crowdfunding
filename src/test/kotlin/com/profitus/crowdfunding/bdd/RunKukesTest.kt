package com.profitus.crowdfunding.bdd

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith


@RunWith(Cucumber::class)
@CucumberOptions(features = ["src/test/resources/features"], tags = ["not @ignored"], plugin = ["pretty"])
class RunKukesTest