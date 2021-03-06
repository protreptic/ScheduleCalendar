package com.mumimaps.android.myapplication.calendar

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.SCROLL_STATE_IDLE
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.mumimaps.android.myapplication.R
import kotlinx.android.synthetic.main.view_schedule_calendar.view.*
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

class ScheduleCalendar : FrameLayout {

    interface Delegate {

        /**
         * Событие наступает когда пользователь выбирает
         * дату в календаре.
         *
         * @param pickedDate выбранная дата
         */
        fun onDatePicked(pickedDate: LocalDate)

    }

    inner class Adapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val date = schedule.keys.toList()[position]
            val month = ScheduleCalendarMonth(context)

            month.delegate = scheduleDelegate
            month.updateSchedule(schedule[date] ?: setOf())

            container.addView(month)

            return month
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View)
        }

        override fun isViewFromObject(view: View, page: Any) =
                view == page

        override fun getCount(): Int = schedule.size

    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val monthsAdapter = Adapter()
    private val monthFormatter = DateTimeFormat.forPattern("MMMM, yyyy")

    var scheduleDelegate: Delegate? = null
    var schedule: Map<LocalDate, Set<LocalDate>> = mapOf()

    var pickedDate: LocalDate? = null

    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.view_schedule_calendar, this, true)

        scheduleCalendarPreviousMonth.setOnClickListener { toPreviousMonth() }
        scheduleCalendarNextMonth.setOnClickListener { toNextMonth() }
        scheduleCalendarMonths.apply {
            adapter = monthsAdapter
            offscreenPageLimit = 1

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                    if (SCROLL_STATE_IDLE == state) {
                        notifyMonthChanged()
                    }
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    notifyMonthChanged()
                }

            })
        }
    }

    fun updateSchedule(newSchedule: Set<LocalDate>) {
        if (newSchedule.isNotEmpty()) {
            schedule = prepareSchedule(newSchedule)

            monthsAdapter.notifyDataSetChanged()

            notifyMonthChanged()
        }
    }

    private fun prepareSchedule(schedule: Set<LocalDate>) =
            mutableMapOf<LocalDate, Set<LocalDate>>().apply {
                schedule.map { it.withDayOfMonth(1) }
                        .distinctBy { it.monthOfYear }
                        .sorted()
                        .forEach { month ->
                            put(month, schedule.filter { date ->
                                date.withDayOfMonth(1) == month }.toSet())
                            }
                        }
                        .toMap()

    private fun toPreviousMonth() {
        scheduleCalendarMonths.setCurrentItem(
            scheduleCalendarMonths.currentItem - 1, true)
    }

    private fun toNextMonth() {
        scheduleCalendarMonths.setCurrentItem(
            scheduleCalendarMonths.currentItem + 1, true)
    }

    private fun notifyMonthChanged() {
        val date = schedule.keys.toList()[scheduleCalendarMonths.currentItem]

        scheduleCalendarTitle.text = monthFormatter.print(date)
    }

}