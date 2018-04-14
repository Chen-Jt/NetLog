package edu.nudt.netlog;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public class ActivitySettings extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "NetLog.Settings";

    private boolean running = false;

    private static final int REQUEST_EXPORT = 1;
    private static final int REQUEST_IMPORT = 2;
    private static final int REQUEST_HOSTS = 3;
    private static final int REQUEST_CALL = 4;

    private AlertDialog dialogFilter = null;

    private static final Intent INTENT_VPN_SETTINGS = new Intent("android.net.vpn.SETTINGS");

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeBlue);
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new FragmentSettings()).commit();
        getSupportActionBar().setTitle(R.string.menu_settings);
        running = true;
    }

    private PreferenceScreen getPreferenceScreen() {
        return ((PreferenceFragment) getFragmentManager().findFragmentById(android.R.id.content)).getPreferenceScreen();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final PreferenceScreen screen = getPreferenceScreen();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

//        PreferenceGroup cat_options = (PreferenceGroup) ((PreferenceGroup) screen.findPreference("screen_options")).findPreference("category_options");
//        PreferenceGroup cat_advanced = (PreferenceGroup) ((PreferenceGroup) screen.findPreference("screen_advanced_options")).findPreference("category_advanced_options");
//        PreferenceGroup cat_stats = (PreferenceGroup) ((PreferenceGroup) screen.findPreference("screen_stats")).findPreference("category_stats");
//        PreferenceGroup cat_backup = (PreferenceGroup) ((PreferenceGroup) screen.findPreference("screen_backup")).findPreference("category_backup");
//
//        // Handle auto enable
//        Preference pref_auto_enable = screen.findPreference("auto_enable");
//        pref_auto_enable.setTitle(getString(R.string.setting_auto, prefs.getString("auto_enable", "0")));
//
//        // Handle screen delay
//        Preference pref_screen_delay = screen.findPreference("screen_delay");
//        pref_screen_delay.setTitle(getString(R.string.setting_delay, prefs.getString("screen_delay", "0")));
//
//        // Wi-Fi home
//        MultiSelectListPreference pref_wifi_homes = (MultiSelectListPreference) screen.findPreference("wifi_homes");
//        Set<String> ssids = prefs.getStringSet("wifi_homes", new HashSet<String>());
//        if (ssids.size() > 0)
//            pref_wifi_homes.setTitle(getString(R.string.setting_wifi_home, TextUtils.join(", ", ssids)));
//        else
//            pref_wifi_homes.setTitle(getString(R.string.setting_wifi_home, "-"));
//
//        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        List<CharSequence> listSSID = new ArrayList<>();
//        List<WifiConfiguration> configs = wm.getConfiguredNetworks();
//        if (configs != null)
//            for (WifiConfiguration config : configs)
//                listSSID.add(config.SSID == null ? "NULL" : config.SSID);
//        for (String ssid : ssids)
//            if (!listSSID.contains(ssid))
//                listSSID.add(ssid);
//        pref_wifi_homes.setEntries(listSSID.toArray(new CharSequence[0]));
//        pref_wifi_homes.setEntryValues(listSSID.toArray(new CharSequence[0]));

//        Preference pref_reset_usage = screen.findPreference("reset_usage");
//        pref_reset_usage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Util.areYouSure(ActivitySettings.this, R.string.setting_reset_usage, new Util.DoubtListener() {
//                    @Override
//                    public void onSure() {
//                        new AsyncTask<Object, Object, Throwable>() {
//                            @Override
//                            protected Throwable doInBackground(Object... objects) {
//                                try {
//                                    DatabaseHelper.getInstance(ActivitySettings.this).resetUsage(-1);
//                                    return null;
//                                } catch (Throwable ex) {
//                                    return ex;
//                                }
//                            }
//
//                            @Override
//                            protected void onPostExecute(Throwable ex) {
//                                if (ex == null)
//                                    Toast.makeText(ActivitySettings.this, R.string.msg_completed, Toast.LENGTH_LONG).show();
//                                else
//                                    Toast.makeText(ActivitySettings.this, ex.toString(), Toast.LENGTH_LONG).show();
//                            }
//                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                    }
//                });
//                return false;
//            }
//        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            TwoStatePreference pref_reload_onconnectivity =
//                    (TwoStatePreference) screen.findPreference("reload_onconnectivity");
//            pref_reload_onconnectivity.setChecked(true);
//            pref_reload_onconnectivity.setEnabled(false);
//        }
//
//        // VPN parameters
//        screen.findPreference("vpn4").setTitle(getString(R.string.setting_vpn4, prefs.getString("vpn4", "10.1.10.1")));
//        screen.findPreference("vpn6").setTitle(getString(R.string.setting_vpn6, prefs.getString("vpn6", "fd00:1:fd00:1:fd00:1:fd00:1")));
//        EditTextPreference pref_dns1 = (EditTextPreference) screen.findPreference("dns");
//        EditTextPreference pref_dns2 = (EditTextPreference) screen.findPreference("dns2");
//        List<String> def_dns = Util.getDefaultDNS(this);
//        pref_dns1.getEditText().setHint(def_dns.get(0));
//        pref_dns2.getEditText().setHint(def_dns.get(1));
//        pref_dns1.setTitle(getString(R.string.setting_dns, prefs.getString("dns", def_dns.get(0))));
//        pref_dns2.setTitle(getString(R.string.setting_dns, prefs.getString("dns2", def_dns.get(1))));
//
        // PCAP parameters
        screen.findPreference("pcap_record_size").setTitle(getString(R.string.setting_pcap_record_size, prefs.getString("pcap_record_size", "65536")));
        screen.findPreference("pcap_file_size").setTitle(getString(R.string.setting_pcap_file_size, prefs.getString("pcap_file_size", "1024")));
//
//        // Watchdog
//
//        // Show resolved
//
//        // Handle stats
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//            cat_stats.removePreference(screen.findPreference("show_top"));
//        EditTextPreference pref_stats_frequency = (EditTextPreference) screen.findPreference("stats_frequency");
//        EditTextPreference pref_stats_samples = (EditTextPreference) screen.findPreference("stats_samples");
//        pref_stats_frequency.setTitle(getString(R.string.setting_stats_frequency, prefs.getString("stats_frequency", "1000")));
//        pref_stats_samples.setTitle(getString(R.string.setting_stats_samples, prefs.getString("stats_samples", "90")));
//
//        // Handle export
//        Preference pref_export = screen.findPreference("export");
//        pref_export.setEnabled(getIntentCreateExport().resolveActivity(getPackageManager()) != null);
//        pref_export.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                startActivityForResult(getIntentCreateExport(), ActivitySettings.REQUEST_EXPORT);
//                return true;
//            }
//        });
//
//        // Handle import
//        Preference pref_import = screen.findPreference("import");
//        pref_import.setEnabled(getIntentOpenExport().resolveActivity(getPackageManager()) != null);
//        pref_import.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                startActivityForResult(getIntentOpenExport(), ActivitySettings.REQUEST_IMPORT);
//                return true;
//            }
//        });
//
//
//        // Development
//        if (!Util.isDebuggable(this))
//            screen.removePreference(screen.findPreference("screen_development"));
//
//        // Handle technical info
//        Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                updateTechnicalInfo();
//                return true;
//            }
//        };
//
//        // Technical info
//        Preference pref_technical_info = screen.findPreference("technical_info");
//        Preference pref_technical_network = screen.findPreference("technical_network");
//        pref_technical_info.setEnabled(INTENT_VPN_SETTINGS.resolveActivity(this.getPackageManager()) != null);
//        pref_technical_info.setIntent(INTENT_VPN_SETTINGS);
//        pref_technical_info.setOnPreferenceClickListener(listener);
//        pref_technical_network.setOnPreferenceClickListener(listener);
//        updateTechnicalInfo();


//        disableSetting("screen_defaults");
//        disableSetting("screen_options");
//        disableSetting("screen_network_options");
//        disableSetting("screen_advanced_options");
//        disableSetting("screen_stats");
//        disableSetting("screen_backup");
//        disableSetting("screen_development");
//        disableSetting("screen_technical");
//        disableSetting("vpn4");
//        disableSetting("vpn6");
//        disableSetting("dns");
//        disableSetting("dns2");

        screen.removePreference(screen.findPreference("screen_defaults"));
        screen.removePreference(screen.findPreference("screen_options"));
        screen.removePreference(screen.findPreference("screen_network_options"));
        screen.removePreference(screen.findPreference("screen_advanced_options"));
        screen.removePreference(screen.findPreference("screen_stats"));
        screen.removePreference(screen.findPreference("screen_backup"));
        screen.removePreference(screen.findPreference("screen_development"));
        screen.removePreference(screen.findPreference("screen_technical"));
    }

    private void disableSetting(String setting) {
        getPreferenceScreen().findPreference(setting).setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions(null);

        // Listen for preference changes
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Listen for interactive state changes
        IntentFilter ifInteractive = new IntentFilter();
        ifInteractive.addAction(Intent.ACTION_SCREEN_ON);
        ifInteractive.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(interactiveStateReceiver, ifInteractive);

        // Listen for connectivity updates
        IntentFilter ifConnectivity = new IntentFilter();
        ifConnectivity.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityChangedReceiver, ifConnectivity);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        unregisterReceiver(interactiveStateReceiver);
        unregisterReceiver(connectivityChangedReceiver);
    }

    @Override
    protected void onDestroy() {
        running = false;
        if (dialogFilter != null) {
            dialogFilter.dismiss();
            dialogFilter = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(TAG, "Up");
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onSharedPreferenceChanged(SharedPreferences prefs, String name) {
        Object value = prefs.getAll().get(name);
        if (value instanceof String && "".equals(value))
            prefs.edit().remove(name).apply();

        // Dependencies
        if ("screen_on".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("whitelist_wifi".equals(name) ||
                "screen_wifi".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("whitelist_other".equals(name) ||
                "screen_other".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("whitelist_roaming".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("auto_enable".equals(name))
            getPreferenceScreen().findPreference(name).setTitle(getString(R.string.setting_auto, prefs.getString(name, "0")));

        else if ("screen_delay".equals(name))
            getPreferenceScreen().findPreference(name).setTitle(getString(R.string.setting_delay, prefs.getString(name, "0")));

        else if ("subnet".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("tethering".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("lan".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("ip6".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("wifi_homes".equals(name)) {
            MultiSelectListPreference pref_wifi_homes = (MultiSelectListPreference) getPreferenceScreen().findPreference(name);
            Set<String> ssid = prefs.getStringSet(name, new HashSet<String>());
            if (ssid.size() > 0)
                pref_wifi_homes.setTitle(getString(R.string.setting_wifi_home, TextUtils.join(", ", ssid)));
            else
                pref_wifi_homes.setTitle(getString(R.string.setting_wifi_home, "-"));
            ServiceSinkhole.reload("changed " + name, this, false);

        } else if ("use_metered".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("unmetered_2g".equals(name) ||
                "unmetered_3g".equals(name) ||
                "unmetered_4g".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("national_roaming".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("eu_roaming".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("disable_on_call".equals(name)) {
            if (prefs.getBoolean(name, false)) {
                if (checkPermissions(name))
                    ServiceSinkhole.reload("changed " + name, this, false);
            } else
                ServiceSinkhole.reload("changed " + name, this, false);

        } else if ("lockdown_wifi".equals(name) || "lockdown_other".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);

        else if ("manage_system".equals(name)) {
            boolean manage = prefs.getBoolean(name, false);
            if (!manage)
                prefs.edit().putBoolean("show_user", true).apply();
            prefs.edit().putBoolean("show_system", manage).apply();
            ServiceSinkhole.reload("changed " + name, this, false);

        } else if ("log_app".equals(name)) {
            Intent ruleset = new Intent(ActivityMain.ACTION_RULES_CHANGED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(ruleset);

        } else if ("vpn4".equals(name)) {
            String vpn4 = prefs.getString(name, null);
            try {
                checkAddress(vpn4);
            } catch (Throwable ex) {
                prefs.edit().remove(name).apply();
                ((EditTextPreference) getPreferenceScreen().findPreference(name)).setText(null);
                if (!TextUtils.isEmpty(vpn4))
                    Toast.makeText(ActivitySettings.this, ex.toString(), Toast.LENGTH_LONG).show();
            }
            getPreferenceScreen().findPreference(name).setTitle(
                    getString(R.string.setting_vpn4, prefs.getString(name, "10.1.10.1")));
            ServiceSinkhole.reload("changed " + name, this, false);

        } else if ("vpn6".equals(name)) {
            String vpn6 = prefs.getString(name, null);
            try {
                checkAddress(vpn6);
            } catch (Throwable ex) {
                prefs.edit().remove(name).apply();
                ((EditTextPreference) getPreferenceScreen().findPreference(name)).setText(null);
                if (!TextUtils.isEmpty(vpn6))
                    Toast.makeText(ActivitySettings.this, ex.toString(), Toast.LENGTH_LONG).show();
            }
            getPreferenceScreen().findPreference(name).setTitle(
                    getString(R.string.setting_vpn6, prefs.getString(name, "fd00:1:fd00:1:fd00:1:fd00:1")));
            ServiceSinkhole.reload("changed " + name, this, false);

        } else if ("dns".equals(name) || "dns2".equals(name)) {
            String dns = prefs.getString(name, null);
            try {
                checkAddress(dns);
            } catch (Throwable ex) {
                prefs.edit().remove(name).apply();
                ((EditTextPreference) getPreferenceScreen().findPreference(name)).setText(null);
                if (!TextUtils.isEmpty(dns))
                    Toast.makeText(ActivitySettings.this, ex.toString(), Toast.LENGTH_LONG).show();
            }
            getPreferenceScreen().findPreference(name).setTitle(
                    getString(R.string.setting_dns,
                            prefs.getString(name, Util.getDefaultDNS(this).get("dns".equals(name) ? 0 : 1))));
            ServiceSinkhole.reload("changed " + name, this, false);

        } else if ("pcap_record_size".equals(name) || "pcap_file_size".equals(name)) {
            if ("pcap_record_size".equals(name))
                getPreferenceScreen().findPreference(name).setTitle(getString(R.string.setting_pcap_record_size, prefs.getString(name, "65536")));
            else
                getPreferenceScreen().findPreference(name).setTitle(getString(R.string.setting_pcap_file_size, prefs.getString(name, "1024")));

            ServiceSinkhole.setPcap(false, this);

            File pcap_file = new File(getDir("data", MODE_PRIVATE), "netguard.pcap");
            if (pcap_file.exists() && !pcap_file.delete())
                Log.w(TAG, "Delete PCAP failed");

            if (prefs.getBoolean("pcap", false))
                ServiceSinkhole.setPcap(true, this);

        } else if ("show_stats".equals(name))
            ServiceSinkhole.reloadStats("changed " + name, this);

        else if ("stats_frequency".equals(name))
            getPreferenceScreen().findPreference(name).setTitle(getString(R.string.setting_stats_frequency, prefs.getString(name, "1000")));

        else if ("stats_samples".equals(name))
            getPreferenceScreen().findPreference(name).setTitle(getString(R.string.setting_stats_samples, prefs.getString(name, "90")));

        else if ("hosts_url".equals(name))
            getPreferenceScreen().findPreference(name).setSummary(prefs.getString(name, "http://www.netguard.me/hosts"));

        else if ("loglevel".equals(name))
            ServiceSinkhole.reload("changed " + name, this, false);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermissions(String name) {
        PreferenceScreen screen = getPreferenceScreen();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if permission was revoked
        if ((name == null || "disable_on_call".equals(name)) && prefs.getBoolean("disable_on_call", false))
            if (!Util.hasPhoneStatePermission(this)) {
                prefs.edit().putBoolean("disable_on_call", false).apply();
                ((TwoStatePreference) screen.findPreference("disable_on_call")).setChecked(false);

                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CALL);

                if (name != null)
                    return false;
            }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PreferenceScreen screen = getPreferenceScreen();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean granted = (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);

        if (requestCode == REQUEST_CALL) {
            prefs.edit().putBoolean("disable_on_call", granted).apply();
            ((TwoStatePreference) screen.findPreference("disable_on_call")).setChecked(granted);
        }

        if (granted)
            ServiceSinkhole.reload("permission granted", this, false);
    }

    private void checkAddress(String address) throws IllegalArgumentException, UnknownHostException {
        if (address == null || TextUtils.isEmpty(address.trim()))
            throw new IllegalArgumentException("Bad address");
        if (!Util.isNumericAddress(address))
            throw new IllegalArgumentException("Bad address");
        InetAddress idns = InetAddress.getByName(address);
        if (idns.isLoopbackAddress() || idns.isAnyLocalAddress())
            throw new IllegalArgumentException("Bad address");
    }

    private BroadcastReceiver interactiveStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Util.logExtras(intent);
            updateTechnicalInfo();
        }
    };

    private BroadcastReceiver connectivityChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Util.logExtras(intent);
            updateTechnicalInfo();
        }
    };

    private void updateTechnicalInfo() {
//        PreferenceScreen screen = getPreferenceScreen();
//        Preference pref_technical_info = screen.findPreference("technical_info");
//        Preference pref_technical_network = screen.findPreference("technical_network");
//
//        pref_technical_info.setSummary(Util.getGeneralInfo(this));
//        pref_technical_network.setSummary(Util.getNetworkInfo(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.i(TAG, "onActivityResult request=" + requestCode + " result=" + requestCode + " ok=" + (resultCode == RESULT_OK));
        if (requestCode == REQUEST_EXPORT) {
            if (resultCode == RESULT_OK && data != null)
                handleExport(data);

        } else if (requestCode == REQUEST_IMPORT) {
            if (resultCode == RESULT_OK && data != null)
                handleImport(data);

        } else if (requestCode == REQUEST_HOSTS) {
            if (resultCode == RESULT_OK && data != null)
                handleHosts(data);

        } else {
            Log.w(TAG, "Unknown activity result request=" + requestCode);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Intent getIntentCreateExport() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (Util.isPackageInstalled("org.openintents.filemanager", this)) {
                intent = new Intent("org.openintents.action.PICK_DIRECTORY");
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=org.openintents.filemanager"));
            }
        } else {
            intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*"); // text/xml
            intent.putExtra(Intent.EXTRA_TITLE, "netguard_" + new SimpleDateFormat("yyyyMMdd").format(new Date().getTime()) + ".xml");
        }
        return intent;
    }

    private Intent getIntentOpenExport() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        else
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // text/xml
        return intent;
    }

    private Intent getIntentOpenHosts() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        else
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // text/plain
        return intent;
    }

    private void handleExport(final Intent data) {
        new AsyncTask<Object, Object, Throwable>() {
            @Override
            protected Throwable doInBackground(Object... objects) {
                OutputStream out = null;
                try {
                    Uri target = data.getData();
                    if (data.hasExtra("org.openintents.extra.DIR_PATH"))
                        target = Uri.parse(target + "/netguard_" + new SimpleDateFormat("yyyyMMdd").format(new Date().getTime()) + ".xml");
                    Log.i(TAG, "Writing URI=" + target);
                    out = getContentResolver().openOutputStream(target);
                    xmlExport(out);
                    return null;
                } catch (Throwable ex) {
                    Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                    return ex;
                } finally {
                    if (out != null)
                        try {
                            out.close();
                        } catch (IOException ex) {
                            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                        }
                }
            }

            @Override
            protected void onPostExecute(Throwable ex) {
                if (running) {
                    if (ex == null)
                        Toast.makeText(ActivitySettings.this, R.string.msg_completed, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(ActivitySettings.this, ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void handleHosts(final Intent data) {
        new AsyncTask<Object, Object, Throwable>() {
            @Override
            protected Throwable doInBackground(Object... objects) {
                File hosts = new File(getFilesDir(), "hosts.txt");

                FileOutputStream out = null;
                InputStream in = null;
                try {
                    Log.i(TAG, "Reading URI=" + data.getData());
                    ContentResolver resolver = getContentResolver();
                    String[] streamTypes = resolver.getStreamTypes(data.getData(), "*/*");
                    String streamType = (streamTypes == null || streamTypes.length == 0 ? "*/*" : streamTypes[0]);
                    AssetFileDescriptor descriptor = resolver.openTypedAssetFileDescriptor(data.getData(), streamType, null);
                    in = descriptor.createInputStream();
                    out = new FileOutputStream(hosts);

                    int len;
                    long total = 0;
                    byte[] buf = new byte[4096];
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                        total += len;
                    }
                    Log.i(TAG, "Copied bytes=" + total);

                    return null;
                } catch (Throwable ex) {
                    Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                    return ex;
                } finally {
                    if (out != null)
                        try {
                            out.close();
                        } catch (IOException ex) {
                            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                        }
                    if (in != null)
                        try {
                            in.close();
                        } catch (IOException ex) {
                            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                        }
                }
            }

            @Override
            protected void onPostExecute(Throwable ex) {
                if (running) {
                    if (ex == null) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ActivitySettings.this);
                        String last = SimpleDateFormat.getDateTimeInstance().format(new Date().getTime());
                        prefs.edit().putString("hosts_last_import", last).apply();

                        if (running) {
                            getPreferenceScreen().findPreference("hosts_import").setSummary(getString(R.string.msg_import_last, last));
                            Toast.makeText(ActivitySettings.this, R.string.msg_completed, Toast.LENGTH_LONG).show();
                        }

                        ServiceSinkhole.reload("hosts import", ActivitySettings.this, false);
                    } else
                        Toast.makeText(ActivitySettings.this, ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void handleImport(final Intent data) {
        new AsyncTask<Object, Object, Throwable>() {
            @Override
            protected Throwable doInBackground(Object... objects) {
                InputStream in = null;
                try {
                    Log.i(TAG, "Reading URI=" + data.getData());
                    ContentResolver resolver = getContentResolver();
                    String[] streamTypes = resolver.getStreamTypes(data.getData(), "*/*");
                    String streamType = (streamTypes == null || streamTypes.length == 0 ? "*/*" : streamTypes[0]);
                    AssetFileDescriptor descriptor = resolver.openTypedAssetFileDescriptor(data.getData(), streamType, null);
                    in = descriptor.createInputStream();
                    xmlImport(in);
                    return null;
                } catch (Throwable ex) {
                    Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                    return ex;
                } finally {
                    if (in != null)
                        try {
                            in.close();
                        } catch (IOException ex) {
                            Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
                        }
                }
            }

            @Override
            protected void onPostExecute(Throwable ex) {
                if (running) {
                    if (ex == null) {
                        Toast.makeText(ActivitySettings.this, R.string.msg_completed, Toast.LENGTH_LONG).show();
                        ServiceSinkhole.reloadStats("import", ActivitySettings.this);
                        // Update theme, request permissions
                        recreate();
                    } else
                        Toast.makeText(ActivitySettings.this, ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void xmlExport(OutputStream out) throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(out, "UTF-8");
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.startTag(null, "netguard");

        serializer.startTag(null, "application");
        xmlExport(PreferenceManager.getDefaultSharedPreferences(this), serializer);
        serializer.endTag(null, "application");

        serializer.startTag(null, "wifi");
        xmlExport(getSharedPreferences("wifi", Context.MODE_PRIVATE), serializer);
        serializer.endTag(null, "wifi");

        serializer.startTag(null, "mobile");
        xmlExport(getSharedPreferences("other", Context.MODE_PRIVATE), serializer);
        serializer.endTag(null, "mobile");

        serializer.startTag(null, "screen_wifi");
        xmlExport(getSharedPreferences("screen_wifi", Context.MODE_PRIVATE), serializer);
        serializer.endTag(null, "screen_wifi");

        serializer.startTag(null, "screen_other");
        xmlExport(getSharedPreferences("screen_other", Context.MODE_PRIVATE), serializer);
        serializer.endTag(null, "screen_other");

        serializer.startTag(null, "roaming");
        xmlExport(getSharedPreferences("roaming", Context.MODE_PRIVATE), serializer);
        serializer.endTag(null, "roaming");

        serializer.startTag(null, "lockdown");
        xmlExport(getSharedPreferences("lockdown", Context.MODE_PRIVATE), serializer);
        serializer.endTag(null, "lockdown");

        serializer.startTag(null, "apply");
        xmlExport(getSharedPreferences("apply", Context.MODE_PRIVATE), serializer);
        serializer.endTag(null, "apply");

        serializer.startTag(null, "notify");
        xmlExport(getSharedPreferences("notify", Context.MODE_PRIVATE), serializer);
        serializer.endTag(null, "notify");

        serializer.startTag(null, "filter");
        filterExport(serializer);
        serializer.endTag(null, "filter");

        serializer.startTag(null, "forward");
        forwardExport(serializer);
        serializer.endTag(null, "forward");

        serializer.endTag(null, "netguard");
        serializer.endDocument();
        serializer.flush();
    }

    private void xmlExport(SharedPreferences prefs, XmlSerializer serializer) throws IOException {
        Map<String, ?> settings = prefs.getAll();
        for (String key : settings.keySet()) {
            Object value = settings.get(key);

            if ("imported".equals(key))
                continue;

            if (value instanceof Boolean) {
                serializer.startTag(null, "setting");
                serializer.attribute(null, "key", key);
                serializer.attribute(null, "type", "boolean");
                serializer.attribute(null, "value", value.toString());
                serializer.endTag(null, "setting");

            } else if (value instanceof Integer) {
                serializer.startTag(null, "setting");
                serializer.attribute(null, "key", key);
                serializer.attribute(null, "type", "integer");
                serializer.attribute(null, "value", value.toString());
                serializer.endTag(null, "setting");

            } else if (value instanceof String) {
                serializer.startTag(null, "setting");
                serializer.attribute(null, "key", key);
                serializer.attribute(null, "type", "string");
                serializer.attribute(null, "value", value.toString());
                serializer.endTag(null, "setting");

            } else if (value instanceof Set) {
                Set<String> set = (Set<String>) value;
                serializer.startTag(null, "setting");
                serializer.attribute(null, "key", key);
                serializer.attribute(null, "type", "set");
                serializer.attribute(null, "value", TextUtils.join("\n", set));
                serializer.endTag(null, "setting");

            } else
                Log.e(TAG, "Unknown key=" + key);
        }
    }

    private void filterExport(XmlSerializer serializer) throws IOException {
        Cursor cursor = DatabaseHelper.getInstance(this).getAccess();
        int colUid = cursor.getColumnIndex("uid");
        int colVersion = cursor.getColumnIndex("version");
        int colProtocol = cursor.getColumnIndex("protocol");
        int colDAddr = cursor.getColumnIndex("daddr");
        int colDPort = cursor.getColumnIndex("dport");
        int colTime = cursor.getColumnIndex("time");
        int colBlock = cursor.getColumnIndex("block");
        while (cursor.moveToNext())
            for (String pkg : getPackages(cursor.getInt(colUid))) {
                serializer.startTag(null, "rule");
                serializer.attribute(null, "pkg", pkg);
                serializer.attribute(null, "version", Integer.toString(cursor.getInt(colVersion)));
                serializer.attribute(null, "protocol", Integer.toString(cursor.getInt(colProtocol)));
                serializer.attribute(null, "daddr", cursor.getString(colDAddr));
                serializer.attribute(null, "dport", Integer.toString(cursor.getInt(colDPort)));
                serializer.attribute(null, "time", Long.toString(cursor.getLong(colTime)));
                serializer.attribute(null, "block", Integer.toString(cursor.getInt(colBlock)));
                serializer.endTag(null, "rule");
            }
        cursor.close();
    }

    private void forwardExport(XmlSerializer serializer) throws IOException {
        Cursor cursor = DatabaseHelper.getInstance(this).getForwarding();
        int colProtocol = cursor.getColumnIndex("protocol");
        int colDPort = cursor.getColumnIndex("dport");
        int colRAddr = cursor.getColumnIndex("raddr");
        int colRPort = cursor.getColumnIndex("rport");
        int colRUid = cursor.getColumnIndex("ruid");
        while (cursor.moveToNext())
            for (String pkg : getPackages(cursor.getInt(colRUid))) {
                serializer.startTag(null, "port");
                serializer.attribute(null, "pkg", pkg);
                serializer.attribute(null, "protocol", Integer.toString(cursor.getInt(colProtocol)));
                serializer.attribute(null, "dport", Integer.toString(cursor.getInt(colDPort)));
                serializer.attribute(null, "raddr", cursor.getString(colRAddr));
                serializer.attribute(null, "rport", Integer.toString(cursor.getInt(colRPort)));
                serializer.endTag(null, "port");
            }
        cursor.close();
    }

    private String[] getPackages(int uid) {
        if (uid == 0)
            return new String[]{"root"};
        else if (uid == 1013)
            return new String[]{"mediaserver"};
        else if (uid == 9999)
            return new String[]{"nobody"};
        else {
            String pkgs[] = getPackageManager().getPackagesForUid(uid);
            if (pkgs == null)
                return new String[0];
            else
                return pkgs;
        }
    }

    private void xmlImport(InputStream in) throws IOException, SAXException, ParserConfigurationException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        prefs.edit().putBoolean("enabled", false).apply();
        ServiceSinkhole.stop("import", this, false);

        XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        XmlImportHandler handler = new XmlImportHandler(this);
        reader.setContentHandler(handler);
        reader.parse(new InputSource(in));

        xmlImport(handler.application, prefs);
        xmlImport(handler.wifi, getSharedPreferences("wifi", Context.MODE_PRIVATE));
        xmlImport(handler.mobile, getSharedPreferences("other", Context.MODE_PRIVATE));
        xmlImport(handler.screen_wifi, getSharedPreferences("screen_wifi", Context.MODE_PRIVATE));
        xmlImport(handler.screen_other, getSharedPreferences("screen_other", Context.MODE_PRIVATE));
        xmlImport(handler.roaming, getSharedPreferences("roaming", Context.MODE_PRIVATE));
        xmlImport(handler.lockdown, getSharedPreferences("lockdown", Context.MODE_PRIVATE));
        xmlImport(handler.apply, getSharedPreferences("apply", Context.MODE_PRIVATE));
        xmlImport(handler.notify, getSharedPreferences("notify", Context.MODE_PRIVATE));

        // Upgrade imported settings
        ReceiverAutostart.upgrade(true, this);

        DatabaseHelper.clearCache();

        // Refresh UI
        prefs.edit().putBoolean("imported", true).apply();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    private void xmlImport(Map<String, Object> settings, SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();

        // Clear existing setting
        for (String key : prefs.getAll().keySet())
            if (!"enabled".equals(key))
                editor.remove(key);

        // Apply new settings
        for (String key : settings.keySet()) {
            Object value = settings.get(key);
            if (value instanceof Boolean)
                editor.putBoolean(key, (Boolean) value);
            else if (value instanceof Integer)
                editor.putInt(key, (Integer) value);
            else if (value instanceof String)
                editor.putString(key, (String) value);
            else if (value instanceof Set)
                editor.putStringSet(key, (Set<String>) value);
            else
                Log.e(TAG, "Unknown type=" + value.getClass());
        }

        editor.apply();
    }

    private class XmlImportHandler extends DefaultHandler {
        private Context context;
        public boolean enabled = false;
        public Map<String, Object> application = new HashMap<>();
        public Map<String, Object> wifi = new HashMap<>();
        public Map<String, Object> mobile = new HashMap<>();
        public Map<String, Object> screen_wifi = new HashMap<>();
        public Map<String, Object> screen_other = new HashMap<>();
        public Map<String, Object> roaming = new HashMap<>();
        public Map<String, Object> lockdown = new HashMap<>();
        public Map<String, Object> apply = new HashMap<>();
        public Map<String, Object> notify = new HashMap<>();
        private Map<String, Object> current = null;

        public XmlImportHandler(Context context) {
            this.context = context;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equals("netguard"))
                ; // Ignore

            else if (qName.equals("application"))
                current = application;

            else if (qName.equals("wifi"))
                current = wifi;

            else if (qName.equals("mobile"))
                current = mobile;

            else if (qName.equals("screen_wifi"))
                current = screen_wifi;

            else if (qName.equals("screen_other"))
                current = screen_other;

            else if (qName.equals("roaming"))
                current = roaming;

            else if (qName.equals("lockdown"))
                current = lockdown;

            else if (qName.equals("apply"))
                current = apply;

            else if (qName.equals("notify"))
                current = notify;

            else if (qName.equals("filter")) {
                current = null;
                Log.i(TAG, "Clearing filters");
                DatabaseHelper.getInstance(context).clearAccess();

            } else if (qName.equals("forward")) {
                current = null;
                Log.i(TAG, "Clearing forwards");
                DatabaseHelper.getInstance(context).deleteForward();

            } else if (qName.equals("setting")) {
                String key = attributes.getValue("key");
                String type = attributes.getValue("type");
                String value = attributes.getValue("value");

                if (current == null)
                    Log.e(TAG, "No current key=" + key);
                else {
                    if ("enabled".equals(key))
                        enabled = Boolean.parseBoolean(value);
                    else {
                        if (current == application) {
                            if ("hosts_last_import".equals(key) || "hosts_last_download".equals(key))
                                return;
                        }

                        if ("boolean".equals(type))
                            current.put(key, Boolean.parseBoolean(value));
                        else if ("integer".equals(type))
                            current.put(key, Integer.parseInt(value));
                        else if ("string".equals(type))
                            current.put(key, value);
                        else if ("set".equals(type)) {
                            Set<String> set = new HashSet<>();
                            if (!TextUtils.isEmpty(value))
                                for (String s : value.split("\n"))
                                    set.add(s);
                            current.put(key, set);
                        } else
                            Log.e(TAG, "Unknown type key=" + key);
                    }
                }

            } else if (qName.equals("rule")) {
                String pkg = attributes.getValue("pkg");

                String version = attributes.getValue("version");
                String protocol = attributes.getValue("protocol");

                Packet packet = new Packet();
                packet.version = (version == null ? 4 : Integer.parseInt(version));
                packet.protocol = (protocol == null ? 6 /* TCP */ : Integer.parseInt(protocol));
                packet.daddr = attributes.getValue("daddr");
                packet.dport = Integer.parseInt(attributes.getValue("dport"));
                packet.time = Long.parseLong(attributes.getValue("time"));

                int block = Integer.parseInt(attributes.getValue("block"));

                try {
                    packet.uid = getUid(pkg);
                    DatabaseHelper.getInstance(context).updateAccess(packet, null, block);
                } catch (PackageManager.NameNotFoundException ex) {
                    Log.w(TAG, "Package not found pkg=" + pkg);
                }

            } else if (qName.equals("port")) {
                String pkg = attributes.getValue("pkg");
                int protocol = Integer.parseInt(attributes.getValue("protocol"));
                int dport = Integer.parseInt(attributes.getValue("dport"));
                String raddr = attributes.getValue("raddr");
                int rport = Integer.parseInt(attributes.getValue("rport"));

                try {
                    int uid = getUid(pkg);
                    DatabaseHelper.getInstance(context).addForward(protocol, dport, raddr, rport, uid);
                } catch (PackageManager.NameNotFoundException ex) {
                    Log.w(TAG, "Package not found pkg=" + pkg);
                }

            } else
                Log.e(TAG, "Unknown element qname=" + qName);
        }

        private int getUid(String pkg) throws PackageManager.NameNotFoundException {
            if ("root".equals(pkg))
                return 0;
            else if ("mediaserver".equals(pkg))
                return 1013;
            else if ("nobody".equals(pkg))
                return 9999;
            else
                return getPackageManager().getApplicationInfo(pkg, 0).uid;
        }
    }
}