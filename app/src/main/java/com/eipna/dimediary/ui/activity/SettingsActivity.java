package com.eipna.dimediary.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.eipna.dimediary.R;
import com.eipna.dimediary.databinding.SettingsActivityBinding;
import com.eipna.dimediary.receiver.ReminderReceiver;
import com.eipna.dimediary.util.DateUtil;
import com.eipna.dimediary.util.PreferenceUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private SettingsActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private Preference dailyReminderTime;
        private Preference removeDailyReminder;
        private PreferenceUtil preferenceUtil;
        private SwitchPreferenceCompat darkMode;

        private AlarmManager alarmManager;

        @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            darkMode = findPreference("dark_mode");
            dailyReminderTime = findPreference("daily_reminder_time");
            removeDailyReminder = findPreference("clear_daily_reminder");
            alarmManager = (AlarmManager) requireContext().getSystemService(ALARM_SERVICE);

            preferenceUtil = new PreferenceUtil(requireContext());

            assert dailyReminderTime != null;

            if (preferenceUtil.getAlarm() == -1) {
                dailyReminderTime.setSummary("Not set");
            } else {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                dailyReminderTime.setSummary(dateFormat.format(new Date(preferenceUtil.getAlarm())));
            }

            dailyReminderTime.setOnPreferenceClickListener(preference -> {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    showTimePickerDialog();
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.POST_NOTIFICATIONS)) {
                    Toast.makeText(requireContext(), "This applications needs notification permission to send notifications", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
                return true;
            });

            removeDailyReminder.setOnPreferenceClickListener(preference -> {
                Toast.makeText(requireContext(), "Removed daily reminder", Toast.LENGTH_SHORT).show();
                clearAlarm();
                return true;
            });

            darkMode.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isDarkMode = (boolean) newValue;
                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            });
        }

        private final ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        showTimePickerDialog();
                    } else {
                        Toast.makeText(requireContext(), "This applications needs notification permission to send notifications", Toast.LENGTH_SHORT).show();
                    }
                });

        @SuppressLint("ShortAlarm")
        private void showTimePickerDialog() {
            MaterialTimePicker builder = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setTitleText("Select time")
                    .build();

            builder.show(requireActivity().getSupportFragmentManager(), "TIME_PICKER");
            builder.addOnPositiveButtonClickListener(view -> {
                int hour = builder.getHour();
                int minute = builder.getMinute();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                preferenceUtil.setAlarm(calendar.getTimeInMillis());
                removeDailyReminder.setEnabled(true);

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String reminderTime = dateFormat.format(new Date(preferenceUtil.getAlarm()));

                Toast.makeText(requireContext(), "Daily reminder time set at " + reminderTime, Toast.LENGTH_SHORT).show();
                dailyReminderTime.setSummary(reminderTime);
                setAlarm(calendar);
            });
        }

        private void setAlarm(Calendar calendar) {
            Intent intent = new Intent(requireContext(), ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60, pendingIntent);
        }

        private void clearAlarm() {
            Intent intent = new Intent(requireContext(), ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
            alarmManager.cancel(pendingIntent);
            removeDailyReminder.setEnabled(false);
            preferenceUtil.setAlarm(0);
            dailyReminderTime.setSummary("Not set");
        }
    }
}