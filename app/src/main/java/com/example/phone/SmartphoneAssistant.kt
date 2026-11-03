package com.example.phone

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast

object SmartphoneAssistant {
    private const val TAG = "SmartphoneAssistant"

    fun openWhatsApp(context: Context, phone: String? = null, message: String? = null): Boolean {
        return try {
            val intent = if (!phone.isNullOrBlank()) {
                val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
                val url = if (!message.isNullOrBlank()) {
                    "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
                } else {
                    "https://api.whatsapp.com/send?phone=$cleanPhone"
                }
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            } else {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                launchIntent ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send"))
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open WhatsApp", e)
            Toast.makeText(context, "WhatsApp is not installed or could not be opened", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun openYouTube(context: Context, query: String? = null): Boolean {
        return try {
            val intent = if (!query.isNullOrBlank()) {
                Intent(Intent.ACTION_SEARCH).apply {
                    setPackage("com.google.android.youtube")
                    putExtra("query", query)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                launchIntent ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                // Fallback to web browser
                val webUrl = if (!query.isNullOrBlank()) {
                    "https://www.youtube.com/results?search_query=${Uri.encode(query)}"
                } else {
                    "https://www.youtube.com"
                }
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(browserIntent)
                true
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to open YouTube browser fallback", ex)
                false
            }
        }
    }

    fun openGoogleSearch(context: Context, query: String? = null): Boolean {
        return try {
            val intent = if (!query.isNullOrBlank()) {
                Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, query)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open Google search", e)
            false
        }
    }

    fun openPhoneSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open settings", e)
            false
        }
    }

    fun openWifiSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openBluetoothSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openSoundSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openCamera(context: Context): Boolean {
        return try {
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
                true
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to open camera", ex)
                Toast.makeText(context, "Camera could not be launched", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    fun openDialer(context: Context, phoneNumber: String): Boolean {
        return try {
            val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open dialer", e)
            false
        }
    }

    fun setAlarm(context: Context, hour: Int, minute: Int, message: String = "ANX AI Alarm"): Boolean {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set alarm", e)
            Toast.makeText(context, "AlarmClock feature not available on this device", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun openAppByNameOrPackage(context: Context, query: String): Boolean {
        val pm = context.packageManager
        val cleanQuery = query.lowercase().trim()

        try {
            // Direct launch by exact package name
            val launchIntent = pm.getLaunchIntentForPackage(cleanQuery)
            if (launchIntent != null) {
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(launchIntent)
                return true
            }

            // Search installed applications for matching label
            val apps = pm.getInstalledApplications(0)
            for (app in apps) {
                val appLabel = pm.getApplicationLabel(app).toString().lowercase()
                if (appLabel == cleanQuery || appLabel.contains(cleanQuery)) {
                    val intent = pm.getLaunchIntentForPackage(app.packageName)
                    if (intent != null) {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error looking for app: $query", e)
        }
        return false
    }

    fun toggleTorch(context: Context, enable: Boolean): Boolean {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull() ?: return false
            cameraManager.setTorchMode(cameraId, enable)
            true
        } catch (e: Exception) {
            Log.w(TAG, "Torch toggle error: ${e.message}")
            false
        }
    }

    fun openNotificationSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            openPhoneSettings(context)
        }
    }
}
