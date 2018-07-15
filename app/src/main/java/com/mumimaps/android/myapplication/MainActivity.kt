package com.mumimaps.android.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mumimaps.android.myapplication.calendar.ScheduleCalendar
import org.joda.time.LocalDate

class MainActivity : AppCompatActivity(), ScheduleCalendar.Delegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val schedule = setOf(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3),

                LocalDate.now().plusMonths(1).plusDays(1),
                LocalDate.now().plusMonths(1).plusDays(2),
                LocalDate.now().plusMonths(1).plusDays(3))

        findViewById<ScheduleCalendar>(R.id.scheduleCalendar).apply {
            scheduleDelegate = this@MainActivity

            setUpSchedule(schedule)
        }
    }

    override fun onDatePicked(pickedDate: LocalDate) {
        Toast.makeText(applicationContext, pickedDate.toString(), Toast.LENGTH_LONG).show()
    }

}
