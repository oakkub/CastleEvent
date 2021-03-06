package com.ipati.dev.castleevent.service.FirebaseService

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.ipati.dev.castleevent.adapter.ListCategoryMenuAdapter
import com.ipati.dev.castleevent.adapter.ListEventAdapter
import com.ipati.dev.castleevent.model.category.ALL
import com.ipati.dev.castleevent.model.category.Education
import com.ipati.dev.castleevent.model.category.Sport
import com.ipati.dev.castleevent.model.category.Technology
import com.ipati.dev.castleevent.model.modelListEvent.ItemListEvent

class RealTimeDatabaseManager(context: Context, lifeCycle: Lifecycle) : LifecycleObserver {
    var mCategory: String = "ALL"
    private var tagChild = "eventItem/eventContinue"
    private var refDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var refDatabaseChild: DatabaseReference? = refDatabase.child(tagChild)
    var listItem: ItemListEvent? = null
    var hasMapData: HashMap<*, *>? = null
    var mContext: Context? = null
    var mLifeCycle: Lifecycle? = null
    var arrayItemList: ArrayList<ItemListEvent> = ArrayList()
    var adapterListEvent: ListEventAdapter? = ListEventAdapter(listItem = arrayItemList)
        get() = field

    lateinit var listItemEvent: List<ItemListEvent>

    //Todo:init Class Constructor
    init {
        this.mContext = context
        this.mLifeCycle = lifeCycle
        mLifeCycle!!.addObserver(this)
    }

    //Todo:Handling Life Cycle
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        registerChildEvent()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        removeChildEvent()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        mLifeCycle?.removeObserver(this)
    }

    private fun registerChildEvent() {
        refDatabaseChild?.addChildEventListener(childEventListener())
    }

    private fun removeChildEvent() {
        refDatabaseChild?.removeEventListener(childEventListener())
        arrayItemList.clear()
    }

    fun onChangeCategory() {
        arrayItemList.clear()
        refDatabaseChild?.let {
            refDatabaseChild?.removeEventListener(childEventListener())
        }

        refDatabaseChild?.addChildEventListener(childEventListener())
    }

    private fun childEventListener(): ChildEventListener {
        return object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.d("onCancelled", p0?.message.toString())
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                hasMapData = p0?.value as HashMap<*, *>
                val objectItem: List<ItemListEvent> = arrayItemList.filter { it.eventId == hasMapData!!["eventId"] as Long }
                if (objectItem.count() != 0) {
                    listItem = ItemListEvent(
                            p0.key.toString(),
                            hasMapData!!["eventId"] as Long,
                            hasMapData!!["eventName"].toString(),
                            hasMapData!!["eventCover"].toString(),
                            hasMapData!!["eventAdvertise"].toString(),
                            hasMapData!!["categoryName"].toString(),
                            hasMapData!!["accountBank"].toString(),
                            hasMapData!!["eventDescription"].toString(),
                            hasMapData!!["eventLocation"].toString(),
                            hasMapData!!["eventLogoCredit"].toString(),
                            hasMapData!!["eventLatitude"] as Double,
                            hasMapData!!["eventLongitude"] as Double,
                            hasMapData!!["eventMax"] as Long,
                            hasMapData!!["eventRest"] as Long,
                            hasMapData!!["eventStatus"] as Boolean,
                            hasMapData!!["eventTime"].toString(),
                            hasMapData!!["eventCalendarStart"].toString(),
                            hasMapData!!["eventCalendarEnd"].toString(),
                            hasMapData!!["eventPrice"].toString()
                    )
                    arrayItemList.remove(objectItem[0])
                    adapterListEvent?.notifyDataSetChanged()
                    arrayItemList.add(0, listItem!!)
                }
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                hasMapData = p0?.value as HashMap<*, *>
                listItem = ItemListEvent(
                        p0.key.toString(),
                        hasMapData!!["eventId"] as Long,
                        hasMapData!!["eventName"].toString(),
                        hasMapData!!["eventCover"].toString(),
                        hasMapData!!["eventAdvertise"].toString(),
                        hasMapData!!["categoryName"].toString(),
                        hasMapData!!["accountBank"].toString(),
                        hasMapData!!["eventDescription"].toString(),
                        hasMapData!!["eventLocation"].toString(),
                        hasMapData!!["eventLogoCredit"].toString(),
                        hasMapData!!["eventLatitude"] as Double,
                        hasMapData!!["eventLongitude"] as Double,
                        hasMapData!!["eventMax"] as Long,
                        hasMapData!!["eventRest"] as Long,
                        hasMapData!!["eventStatus"] as Boolean,
                        hasMapData!!["eventTime"].toString(),
                        hasMapData!!["eventCalendarStart"].toString(),
                        hasMapData!!["eventCalendarEnd"].toString(),
                        hasMapData!!["eventPrice"].toString()

                )
                onFilter(hasMapData!!["categoryName"].toString(), listItem!!)
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                adapterListEvent?.notifyDataSetChanged()
                hasMapData = p0?.value as HashMap<*, *>
                listItemEvent = arrayItemList.filter { it.eventId == hasMapData!!["eventId"] as Long }

                if (listItemEvent.count() != 0) {
                    arrayItemList.remove(listItemEvent[0])
                }
            }
        }
    }

    private fun onFilter(category: String, listItem: ItemListEvent) {
        if (mCategory == category) {
            arrayItemList.add(listItem)
            adapterListEvent?.notifyDataSetChanged()
        } else if (mCategory == "ALL") {
            arrayItemList.add(listItem)
            adapterListEvent?.notifyDataSetChanged()
        }
    }
}
