package com.inlacou.inksliderlibraryproject

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.inlacou.inksliderlibraryproject.ui.fragments.temperature.TemperatureFragment
import com.inlacou.inksliderlibraryproject.ui.fragments.sound.SoundFragment

class MainActivity : AppCompatActivity() {
	
	var currentFragment: Fragment? = null
	private lateinit var appBarConfiguration: AppBarConfiguration
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		val toolbar: Toolbar = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar)
		
		val fab: FloatingActionButton = findViewById(R.id.fab)
		fab.setOnClickListener { view ->
			currentFragment?.let {
				if(it is SoundFragment) {
					it.setValue(it.values?.get((0 until (it.values?.size ?: 2)).shuffled().first()), false)
				} else if(it is TemperatureFragment) {
					it.setValue(it.values?.get((0 until (it.values?.size ?: 2)).shuffled().first()), false)
				}
			}
		}
		val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
		val navView: NavigationView = findViewById(R.id.nav_view)
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_sound, R.id.nav_temperature), drawerLayout)
		
		//Wtf listener not working without this line
		navView.bringToFront()
		
		navView.setNavigationItemSelectedListener {
			loadFragment(it.itemId)
			drawerLayout.closeDrawer(GravityCompat.START)
			true
		}
		
		loadFragment(R.id.nav_temperature)
	}
	
	private fun loadFragment(id: Int) {
		var fragment: Fragment? = null
		when(id){
			R.id.nav_sound -> fragment = SoundFragment()
			R.id.nav_temperature -> fragment = TemperatureFragment()
		}
		currentFragment = fragment
		loadFragment(fragment)
	}
	
	private fun loadFragment(fragment: Fragment?){
		fragment?.let {
			supportFragmentManager.beginTransaction().apply {
				replace(R.id.container_frame_layout, fragment)
				commit()
			}
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.main, menu)
		return true
	}
	
}
