package com.dicoding.habitapp.ui.list

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.dicoding.habitapp.R
import org.junit.Rule
import org.junit.Test

//TODO 16 : Write UI test to validate when user tap Add Habit (+), the AddHabitActivity displayed
class HabitActivityTest {
    @Rule
    @JvmField
    var activityRule = ActivityTestRule(HabitListActivity::class.java)

    @Test
    fun testAddHabitButton() {
        onView(withId(R.id.fab)).perform(click())

        onView(withId(R.id.add_habit_layout)).check(matches(isDisplayed()))
    }
}