package com.enihsyou.map.baidu

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BitmapDescriptor
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MapPoi
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.map.MyLocationConfiguration
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tile_info.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.toast
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity(), AddLoggerMixin {
    private var lastLocation: LatLng? = null
    private lateinit var locationClient: LocationClient
    private lateinit var navigationIcon: BitmapDescriptor
    private lateinit var markerIcon: BitmapDescriptor

    private var locationMode = MyLocationConfiguration.LocationMode.NORMAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(applicationContext)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        registerForContextMenu(toolbar)

        fab_get_location.setOnClickListener(fabOnClickListener)
        orientationListener.sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        navigationIcon = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_compass)
        markerIcon = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_myplaces)
    }

    override fun onStart() {
        super.onStart()
        initMapView()
        initMapLocation()
        initMarker()
    }

    override fun onResume() {
        super.onResume()
        baiduMapView?.onResume()
        val map = baiduMapView?.map ?: return
        map.isMyLocationEnabled = true
        if (!locationClient.isStarted)
            locationClient.start()
        orientationListener.start()
    }

    override fun onPause() {
        super.onPause()
        baiduMapView?.onPause()
        val map = baiduMapView?.map ?: return
        map.isMyLocationEnabled = false
        if (locationClient.isStarted)
            locationClient.stop()
        orientationListener.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        baiduMapView?.onDestroy()
    }

    /**初始化地图视图*/
    private fun initMapView() {
        val map = baiduMapView?.map ?: return
        map.animateMapStatus(MapStatusUpdateFactory.zoomTo(16.0f))
    }

    /**初始化定位组件*/
    private fun initMapLocation() {
        // http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/get-location/latlng
        locationClient = LocationClient(applicationContext).apply {
            locOption = LocationClientOption().apply {
                setIsNeedAddress(true)
                locationMode = LocationClientOption.LocationMode.Hight_Accuracy
                coorType = "bd09ll"
                openGps = true
                scanSpan = LocationClientOption.MIN_SCAN_SPAN
            }
            registerLocationListener(bdAbstractLocationListener)
        }
    }

    /**初始化锚点*/
    private fun initMarker() {
        val map = baiduMapView?.map ?: return
        map.setOnMarkerClickListener(onMarkerClickListener)
        map.setOnMapLongClickListener(onMapLongClickListener)
        map.setOnMapClickListener(onMapClickListener)
    }

    private val fabOnClickListener = View.OnClickListener {
        // 检查null
        val latLng = lastLocation ?: return@OnClickListener
        val map = baiduMapView?.map ?: return@OnClickListener

        // 将获得的位置居中
        toast("latitude = ${latLng.latitude} longitude = ${latLng.longitude}")
        val mapUpdate = MapStatusUpdateFactory.newLatLng(latLng)
        map.animateMapStatus(mapUpdate)
    }

    private val bdAbstractLocationListener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
            // 检查null
            location ?: return
            val map = baiduMapView?.map ?: return

            // 获取经纬度和定位精度
            val latitude = location.latitude
            val longitude = location.longitude
            val radius = location.radius

            // 构建数据类
            val locationData = MyLocationData.Builder()
                .accuracy(radius)
                .latitude(latitude)
                .longitude(longitude)
                .direction(orientationListener.lastX)
                .build()
            val latLng = LatLng(latitude, longitude)
            val locationConfiguration =
                MyLocationConfiguration(locationMode, true, navigationIcon)

            // 如果是首次定位，将视图居中
            if (lastLocation == null) {
                toast("latitude = $latitude longitude = $longitude")
                val mapUpdate = MapStatusUpdateFactory.newLatLng(latLng)
                map.animateMapStatus(mapUpdate)
            }
            // 最后 设置位置
            map.setMyLocationData(locationData)
            map.setMyLocationConfiguration(locationConfiguration)
            lastLocation = latLng
        }
    }

    private val onMarkerClickListener = BaiduMap.OnMarkerClickListener {
        val extraInfo = it.extraInfo ?: return@OnMarkerClickListener false
        val info = extraInfo.getSerializable("info") as Info

        info_image?.imageResource = info.imageId
        info_text?.text = info.name
        info_distance?.text = info.distance

        info_card?.visibility = View.VISIBLE
        fab_get_location?.visibility = View.INVISIBLE
        true
    }
    private val onMapLongClickListener = BaiduMap.OnMapLongClickListener {
        toast("latitude = ${it.latitude} longitude = ${it.longitude}")
    }
    private val onMapClickListener = object : BaiduMap.OnMapClickListener {
        override fun onMapClick(p0: LatLng?) {
            info_card?.visibility = View.GONE
            fab_get_location?.visibility = View.VISIBLE
        }

        override fun onMapPoiClick(p0: MapPoi?): Boolean {
            return false
        }
    }
    @Suppress("DEPRECATION")
    private val orientationListener = object : SensorEventListener {
        lateinit var sensorManager: SensorManager
        //        val rotationMatrix = FloatArray(9)
        //        val orientationAngles = FloatArray(3)
        lateinit var orientation: FloatArray

        var lastX: Float = 0.0f
        fun start() {
            val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) ?: return

            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }

        fun stop() {
            sensorManager.unregisterListener(this)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            event ?: return
            when (event.sensor.type) {
                Sensor.TYPE_ORIENTATION -> {
                    val x = event.values[SensorManager.DATA_X]
                    if ((x - lastX).absoluteValue > 1.0) {
                        lastX = x
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_map_normal    -> optionToNormalMap(item)
            R.id.action_map_satellite -> optionToSatelliteMap(item)
            R.id.action_map_traffic   -> optionToTrafficMap(item)
            R.id.action_map_heat      -> optionToHeatMap(item)
            else                      -> super.onOptionsItemSelected(item)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_map_mode_normal    -> optionToNormalMode(item)
            R.id.action_map_mode_following -> optionToFollowingMode(item)
            R.id.action_map_mode_compass   -> optionToCompassMode(item)
            R.id.action_map_overlay        -> optionDisplayOverlay(item)
            else                           -> super.onContextItemSelected(item)
        }
    }

    private fun optionToNormalMode(item: MenuItem): Boolean {
        locationMode = MyLocationConfiguration.LocationMode.NORMAL
        return true
    }

    private fun optionToFollowingMode(item: MenuItem): Boolean {
        locationMode = MyLocationConfiguration.LocationMode.FOLLOWING
        return true
    }

    private fun optionToCompassMode(item: MenuItem): Boolean {
        locationMode = MyLocationConfiguration.LocationMode.COMPASS
        return true
    }

    private fun optionDisplayOverlay(item: MenuItem): Boolean {
        val map = baiduMapView?.map ?: return false
        map.clear()

        Info.infos.forEach {
            val latLng = LatLng(it.latitude, it.longitude)
            val icon = MarkerOptions()
                .position(latLng)
                .icon(markerIcon)
                .zIndex(4)

            val marker = map.addOverlay(icon) as Marker

            val bundle = Bundle().apply {
                putSerializable("info", it)
            }
            marker.extraInfo = bundle
        }
        val it = Info.infos.first()
        val latLng = LatLng(it.latitude, it.longitude)
        map.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng))
        return true
    }

    private fun optionToNormalMap(item: MenuItem): Boolean {
        val map = baiduMapView?.map ?: return false
        map.mapType = BaiduMap.MAP_TYPE_NORMAL
        return true
    }

    private fun optionToSatelliteMap(item: MenuItem): Boolean {
        val map = baiduMapView?.map ?: return false
        map.mapType = BaiduMap.MAP_TYPE_SATELLITE
        return true
    }

    private fun optionToTrafficMap(item: MenuItem): Boolean {
        val map = baiduMapView?.map ?: return false
        item.isChecked = !item.isChecked
        map.isTrafficEnabled = item.isChecked

        debug { "交通地图: ${map.isTrafficEnabled}" }
        return true
    }

    private fun optionToHeatMap(item: MenuItem): Boolean {
        val map = baiduMapView?.map ?: return false
        item.isChecked = !item.isChecked
        map.isBaiduHeatMapEnabled = item.isChecked

        debug { "热力图: ${map.isBaiduHeatMapEnabled}" }
        return true
    }
}
