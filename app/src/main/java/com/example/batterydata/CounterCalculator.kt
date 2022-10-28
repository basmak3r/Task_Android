package com.example.batterydata

import android.content.Context
import androidx.room.Room
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CounterCalculator {

    private lateinit var db:AppDatabase
    private val hour:Long=3600000
    private val half_hour:Long=1800000
    private val Minutes:Long=60000
    private var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")


    private var counter_list:MutableList<Counter>  = mutableListOf()


    public fun CounterCal(context: Context):MutableList<Counter>
    {
        var count_init=Counter(0,0,0)
        counter_list.clear()
        counter_list.add(count_init)
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()

        var time_final: Long=0
        var time_initial: Long=0
        var charge:Int
        val batterydata: List<User> = db.userDao().getAll()
        val count=batterydata.count()
        var i=0
        var flag=0

        while(i<count)
        {
            var pluggedstate=batterydata[i].pluggedstate
            flag=0
            if(pluggedstate==1)
            {
                flag=1
                if( batterydata[i].batterylevel.toInt()==100)
                {
                    time_initial= batterydata[i].timestamp
                    time_final=time_initial
                    flag=2
                }
                i++
                while(i<count)
                {
                    pluggedstate=batterydata[i].pluggedstate
                    charge=batterydata[i].batterylevel.toInt()
                    if(pluggedstate==1 && flag==1 && charge==100)
                    {
                        time_initial=batterydata[i].timestamp
                        time_final=time_initial
                        flag=2
                    }
                    else if(flag==2 && pluggedstate==1)
                    {
                        time_final=batterydata[i].timestamp
                    }
                    if(pluggedstate==0)
                        break

                    i++
                }
            }
            if(time_final-time_initial/Minutes>=30 && flag==2)
                counter_list[0].BadCount++
            else if(time_final-time_initial/Minutes<30 && flag==2 )
                counter_list[0].OptimalCount++
            else if(flag==1)
                counter_list[0].SpotCount++

            i++
        }


        return counter_list

    }
}