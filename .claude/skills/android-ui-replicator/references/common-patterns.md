# Common Android Screen Patterns — Kotlin + XML

Quick-start code snippets for the most common screen types. Read the relevant section when the screenshot matches one of these patterns.

---

## Login / Auth Screen

```xml
<!-- activity_login.xml -->
<androidx.constraintlayout.widget.ConstraintLayout ...>

    <com.google.android.material.textfield.TextInputLayout
        android:id="@+id/tilEmail"
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
        android:hint="@string/hint_email"
        app:layout_constraintTop_toTopOf="parent"
        ...>
        <com.google.android.material.textfield.TextInputEditText
            android:inputType="textEmailAddress" />
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:id="@+id/tilPassword"
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
        android:hint="@string/hint_password"
        app:passwordToggleEnabled="true"
        ...>
        <com.google.android.material.textfield.TextInputEditText
            android:inputType="textPassword" />
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnLogin"
        android:text="@string/btn_login"
        ... />

</androidx.constraintlayout.widget.ConstraintLayout>
```

```kotlin
// LoginActivity.kt
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()
            if (validateInputs(email, password)) {
                // proceed with login
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true
        if (email.isBlank()) {
            binding.tilEmail.error = getString(R.string.error_email_required)
            isValid = false
        } else binding.tilEmail.error = null
        if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_password_short)
            isValid = false
        } else binding.tilPassword.error = null
        return isValid
    }
}
```

---

## List / Feed Screen (RecyclerView)

```xml
<!-- activity_list.xml -->
<androidx.coordinatorlayout.widget.CoordinatorLayout ...>

    <com.google.android.material.appbar.AppBarLayout ...>
        <com.google.android.material.appbar.MaterialToolbar
            android:id="@+id/toolbar"
            android:title="@string/screen_title"
            app:navigationIcon="@drawable/ic_menu" />
    </com.google.android.material.appbar.AppBarLayout>

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        app:layout_behavior="@string/appbar_scrolling_view_behavior"
        android:clipToPadding="false"
        android:paddingBottom="16dp" />

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fab"
        app:srcCompat="@drawable/ic_add"
        app:layout_anchor="@id/recyclerView"
        app:layout_anchorGravity="bottom|end"
        android:layout_margin="16dp" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

```kotlin
// ListActivity.kt
class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    private val adapter = ItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            adapter = this@ListActivity.adapter
        }
        adapter.submitList(getSampleData())
    }
}

// ItemAdapter.kt
data class Item(val id: String, val title: String, val subtitle: String)

class ItemAdapter : ListAdapter<Item, ItemAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.tvTitle.text = item.title
            binding.tvSubtitle.text = item.subtitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(old: Item, new: Item) = old.id == new.id
        override fun areContentsTheSame(old: Item, new: Item) = old == new
    }
}
```

---

## Profile Screen

```xml
<!-- fragment_profile.xml -->
<androidx.core.widget.NestedScrollView ...>
    <androidx.constraintlayout.widget.ConstraintLayout ...>

        <!-- Avatar -->
        <de.hdodenhof.circleimageview.CircleImageView
            android:id="@+id/ivAvatar"
            android:layout_width="96dp"
            android:layout_height="96dp"
            android:src="@drawable/ic_person"
            ... />

        <!-- Name -->
        <TextView
            android:id="@+id/tvName"
            android:textSize="20sp"
            android:textStyle="bold"
            ... />

        <!-- Stats row -->
        <LinearLayout
            android:orientation="horizontal"
            android:weightSum="3"
            ...>
            <!-- repeat stat block 3x -->
            <LinearLayout android:layout_weight="1" android:orientation="vertical">
                <TextView android:id="@+id/tvStatValue" android:textStyle="bold" />
                <TextView android:id="@+id/tvStatLabel" android:textSize="12sp" />
            </LinearLayout>
        </LinearLayout>

    </androidx.constraintlayout.widget.ConstraintLayout>
</androidx.core.widget.NestedScrollView>
```

---

## Settings Screen

```xml
<!-- Use PreferenceFragmentCompat for settings screens -->
<!-- res/xml/preferences.xml -->
<PreferenceScreen xmlns:app="http://schemas.android.com/apk/res-auto">

    <PreferenceCategory app:title="@string/pref_category_account">
        <Preference
            app:key="pref_profile"
            app:title="@string/pref_profile"
            app:summary="@string/pref_profile_summary"
            app:icon="@drawable/ic_person" />
    </PreferenceCategory>

    <PreferenceCategory app:title="@string/pref_category_notifications">
        <SwitchPreferenceCompat
            app:key="pref_notifications"
            app:title="@string/pref_notifications"
            app:defaultValue="true" />
    </PreferenceCategory>

</PreferenceScreen>
```

```kotlin
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
```

---

## Bottom Navigation Screen

```xml
<!-- activity_main.xml -->
<androidx.constraintlayout.widget.ConstraintLayout ...>

    <fragment
        android:id="@+id/navHostFragment"
        android:name="androidx.navigation.fragment.NavHostFragment"
        app:defaultNavHost="true"
        app:navGraph="@navigation/nav_graph"
        app:layout_constraintBottom_toTopOf="@id/bottomNav" ... />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNav"
        app:menu="@menu/bottom_nav_menu"
        app:layout_constraintBottom_toBottomOf="parent" ... />

</androidx.constraintlayout.widget.ConstraintLayout>
```

```xml
<!-- res/menu/bottom_nav_menu.xml -->
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:id="@+id/homeFragment"
          android:icon="@drawable/ic_home"
          android:title="@string/nav_home" />
    <item android:id="@+id/searchFragment"
          android:icon="@drawable/ic_search"
          android:title="@string/nav_search" />
    <item android:id="@+id/profileFragment"
          android:icon="@drawable/ic_person"
          android:title="@string/nav_profile" />
</menu>
```

```kotlin
// MainActivity.kt
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = (supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment).navController
        binding.bottomNav.setupWithNavController(navController)
    }
}
```

---

## build.gradle Dependencies (Material + Navigation)

```gradle
dependencies {
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.7'
    implementation 'androidx.navigation:navigation-ui-ktx:2.7.7'
    // Optional: circle image for avatar
    implementation 'de.hdodenhof:circleimageview:3.1.0'
}

android {
    buildFeatures {
        viewBinding true
    }
}
```
