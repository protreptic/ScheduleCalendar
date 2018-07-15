package com.mumimaps.android.myapplication.calendar

import android.content.Context
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mumimaps.android.myapplication.R
import com.mumimaps.android.myapplication.calendar.ScheduleCalendar.Delegate
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

class ScheduleCalendarMonth : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private val monthAdapter = Adapter()

    var delegate: Delegate? = null

    var date: LocalDate = LocalDate.now().withDayOfMonth(1)
    var pickedDate: LocalDate? = null

    var schedule: Set<LocalDate> = setOf()

    init {
        adapter = monthAdapter
        layoutManager = GridLayoutManager(context, 7)
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    fun updateSchedule(newSchedule: Set<LocalDate>) {
        date = newSchedule.first().withDayOfMonth(1)
        schedule = newSchedule

        adapter.notifyDataSetChanged()
    }

    inner class Adapter : RecyclerView.Adapter<DateViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                DateViewHolder(
                    LayoutInflater
                        .from(context)
                        .inflate(R.layout.view_schedule_calendar__date, parent, false))

        override fun getItemCount(): Int =
                date.dayOfMonth().maximumValue + date.dayOfWeek - 1

        override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
            val date = getDate(position)

            holder.bind(date, schedule, delegate)
        }

        private fun getDate(position: Int) =
                date.plusDays(position - date.dayOfWeek + 1)

    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val format = DateTimeFormat.forPattern("dd")

        fun bind(date: LocalDate, schedule: Set<LocalDate>, delegate: Delegate?) {
            val dateText = itemView.findViewById<TextView>(R.id.monthDate).apply {
                text = if (checkIfSameMonth(date, schedule)) {
                    format.print(date)
                } else {
                    ""
                }
            }

            if (schedule.contains(date)) {
                dateText.setTextColor(getColor(itemView.context, R.color.colorPrimary))

                itemView.setOnClickListener {
                    delegate?.onDatePicked(date)
                }
            } else {
                dateText.setTextColor(getColor(itemView.context, android.R.color.darker_gray))

                itemView.setOnClickListener(null)
            }
        }

        private fun checkIfSameMonth(date: LocalDate, schedule: Set<LocalDate>) =
                date.withDayOfMonth(1) == schedule.first().withDayOfMonth(1)

    }

}