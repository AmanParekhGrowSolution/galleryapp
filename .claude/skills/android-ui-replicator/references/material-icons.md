# Material Icons — Visual Description → Icon Name

Use this reference when a screenshot has icons and you need the correct Material icon name.

## Navigation & App Bar
| Visual Description | Material Icon Name |
|---|---|
| Back arrow (←) | `ic_arrow_back` |
| Forward arrow (→) | `ic_arrow_forward` |
| Hamburger menu (≡) | `ic_menu` |
| Close / X | `ic_close` |
| More options (⋮) | `ic_more_vert` |
| More options (⋯) | `ic_more_horiz` |
| Search (🔍) | `ic_search` |
| Filter / Tune | `ic_tune` |
| Sort | `ic_sort` |

## Actions
| Visual Description | Material Icon Name |
|---|---|
| Add / Plus (+) | `ic_add` |
| Edit / Pencil | `ic_edit` |
| Delete / Trash | `ic_delete` |
| Share | `ic_share` |
| Download | `ic_download` |
| Upload | `ic_upload` |
| Copy | `ic_content_copy` |
| Refresh | `ic_refresh` |
| Send | `ic_send` |
| Done / Checkmark | `ic_done` |
| Save | `ic_save` |
| Print | `ic_print` |

## Communication
| Visual Description | Material Icon Name |
|---|---|
| Phone | `ic_phone` |
| Email / Envelope | `ic_email` |
| Message / Chat bubble | `ic_chat` |
| SMS / Text bubble | `ic_sms` |
| Notifications / Bell | `ic_notifications` |
| Notifications off | `ic_notifications_off` |
| Video call | `ic_videocam` |

## Media
| Visual Description | Material Icon Name |
|---|---|
| Play button ▶ | `ic_play_arrow` |
| Pause ⏸ | `ic_pause` |
| Stop ⏹ | `ic_stop` |
| Skip next ⏭ | `ic_skip_next` |
| Skip previous ⏮ | `ic_skip_previous` |
| Volume | `ic_volume_up` |
| Volume muted | `ic_volume_off` |
| Camera | `ic_camera_alt` |
| Image / Photo | `ic_image` |
| Mic | `ic_mic` |
| Mic off | `ic_mic_off` |

## Navigation Tabs (Common BottomNav)
| Visual Description | Material Icon Name |
|---|---|
| House / Home | `ic_home` |
| Person / Profile | `ic_person` |
| Heart / Favorite | `ic_favorite` |
| Shopping cart | `ic_shopping_cart` |
| Settings / Gear | `ic_settings` |
| Explore / Compass | `ic_explore` |
| Dashboard / Grid | `ic_dashboard` |
| History / Clock | `ic_history` |
| Bookmark | `ic_bookmark` |
| Star | `ic_star` |

## Status & Indicators
| Visual Description | Material Icon Name |
|---|---|
| Warning / Triangle ! | `ic_warning` |
| Error / Circle ! | `ic_error` |
| Info / Circle i | `ic_info` |
| Check circle | `ic_check_circle` |
| Lock | `ic_lock` |
| Unlock | `ic_lock_open` |
| Visibility (eye) | `ic_visibility` |
| Visibility off | `ic_visibility_off` |
| Wi-Fi | `ic_wifi` |
| Bluetooth | `ic_bluetooth` |
| Battery | `ic_battery_full` |
| Location / Pin | `ic_location_on` |

## Content
| Visual Description | Material Icon Name |
|---|---|
| List / Lines | `ic_list` |
| Grid view | `ic_grid_view` |
| Calendar | `ic_calendar_today` |
| Clock / Time | `ic_access_time` |
| Map | `ic_map` |
| Attachment / Clip | `ic_attach_file` |
| Link | `ic_link` |
| File | `ic_insert_drive_file` |
| Folder | `ic_folder` |
| Cloud | `ic_cloud` |

## How to use in XML
```xml
<!-- Using Material icons via vector drawable -->
<ImageView
    android:layout_width="24dp"
    android:layout_height="24dp"
    android:src="@drawable/ic_search"
    android:tint="@color/icon_color"
    android:contentDescription="@string/search" />

<!-- Or with MaterialButton icon -->
<com.google.android.material.button.MaterialButton
    app:icon="@drawable/ic_add"
    ... />
```

Add to `build.gradle` to use the full Material icons vector pack:
```gradle
implementation 'androidx.vectordrawable:vectordrawable:1.2.0'
```

Or use `android:drawableLeft/Right/Start/End` for inline icon+text buttons.
