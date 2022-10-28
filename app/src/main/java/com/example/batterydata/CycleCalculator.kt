package com.example.batterydata

import android.app.Application
import android.content.Context
import androidx.room.Room
import java.time.LocalDateTime
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.format.DateTimeFormatter


class CycleCalculator {
    private lateinit var db:AppDatabase
    private var list_data:MutableList<Discharge>  = mutableListOf()
    private val hour:Long=3600000
    private val half_hour:Long=1800000
    private val Minutes:Long=60000
    private var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    private fun StartandEnd(date:Long):Pair<Long,Long>
    {
        //Time Start in Indian Time 1970 Jan 1 05:30
        var start=((date/hour)*hour)+half_hour
        var end=date+hour
        return Pair( start,end)
    }

    public fun CycleCal(context:Context):MutableList<Discharge>
    {
        list_data.clear()
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()

        val users: List<User> = db.userDao().getAll()
        var dateTime= users[0].timestamp;

        var temp_Datetime=dateTime
        var temp_charge=users[0].batterylevel

        var(starting_date,ending_date)=StartandEnd(dateTime)
        dateTime=starting_date

        while(true)
        {
            var(starting_date,ending_date)=StartandEnd(dateTime)
            val userDao = db.userDao().getDateResult(starting_date ,ending_date)
            var count=userDao.count();
            Log.i("helo",count.toString())

            if(count>0)
            {
                //Tim Fix
                var initial_charge:Float=temp_charge
                var initial_time=temp_Datetime
                var charge = 0.00
                var time :Long= 0
                var timediff:Long=0
                var i=1

                while(i<count)
                {
                    var final_charge = userDao[i].batterylevel
                    var final_time = userDao[i].timestamp
                    var plugged_state = userDao[i].pluggedstate


                    if (plugged_state == 0 && initial_charge!=final_charge)
                    {
                        charge += initial_charge - final_charge;
                        timediff += final_time-initial_time
                        initial_time = final_time;
                    }

                    if (plugged_state == 1)
                    {
                        initial_time = final_time;
                    }
                    initial_charge = final_charge;
                    i++
                }
                temp_Datetime= userDao[i-1].timestamp
                temp_charge=userDao[i-1].batterylevel

                time = timediff/Minutes;
                charge = charge;
                if(time>60)
                    time=60

                var dateString = simpleDateFormat.format(starting_date)

                val list = Discharge(dateString,charge,time);
                list_data.add(list)
            }
            else
            {
                // Shutdown Case
                var dateString = simpleDateFormat.format(starting_date)
                val list = Discharge(dateString,0.00,0);
                list_data.add(list);

            }
            if(dateTime+hour>=System.currentTimeMillis())
            {

                break;
            }
            dateTime=dateTime+hour


        }
        return list_data
    }
}