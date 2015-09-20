package com.cheekybastards.ftw.utils;

public interface Constants {
    
    // CPU constants
    public static final String CPU = "/sys/devices/system/cpu/cpu";
    public static final String CPU_FREQ = "/sys/devices/system/cpu/cpufreq/";
    public static final String CUR_FREQ = "/cpufreq/scaling_cur_freq";
    public static final String MAX_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    public static final String MIN_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
    public static final String STEPS_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    public static final String GOVERNORS_LIST = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    public static final String GOVERNOR_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
    public static final String IO_SCHED = "/sys/block/mmcblk0/queue/scheduler";
    public static final String CPU_TOTAL = "/sys/devices/system/cpu/present";
    public static final String PREF_MAX_CPU = "pref_max_cpu";
    public static final String PREF_MIN_CPU = "pref_min_cpu";
    public static final String PREF_GOV = "pref_gov";
    public static final String PREF_IO = "pref_io";
    public static final String GOV_SETTINGS = "gov_settings";
    public static final String GOV_NAME = "gov_name";
    public static final String TIME_IN_STATE = "/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state";
    public static final String PREF_OFFSETS = "pref_offsets";
    public static final String CPU_SCALING0 = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
	public static final String CPU_SCALING1 = "/sys/devices/system/cpu/cpu1/cpufreq/scaling_governor";
	public static final String CPU_SCALING2 = "/sys/devices/system/cpu/cpu2/cpufreq/scaling_governor";
	public static final String CPU_SCALING3 = "/sys/devices/system/cpu/cpu3/cpufreq/scaling_governor";
	public static final String CPU_VOLTAGE = "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table";
	public static final String SD_SCHED = "/sys/block/mmcblk0/queue/scheduler";
	public static final String SD_READAHEAD = "/sys/block/mmcblk0/queue/read_ahead_kb";
	public static final String SD_READAHEAD2 = "/sys/devices/virtual/bdi/179:0/read_ahead_kb";
    // Charger and Battery Constants
    public static final String BAT_VOLTAGE = "/sys/class/power_supply/battery/voltage_now";
    public static final String FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
    
    // Proc Constants
    public static String CPUINFO = "/proc/cpuinfo";
    public static String MEMINFO = "/proc/meminfo";
    public static String DSA = "/proc/default_smp_affinity";
    public static String KALLSYMS = "/proc/kallsyms";
    public static String KMSG = "/proc/kmsg";
    public static String MISC = "/proc/misc";
    public static String MODULES = "/proc/modules";
    public static String MOUNTS = "/proc/mounts";
    public static String PTI = "/proc/pagetypeinfo";
    public static String PARTITIONS = "/proc/partitions";
    public static String SCHED_DEBUG = "/proc/sched_debug";
    public static String DEV_INFO = "/proc/device_info";
    public static String SLAB_INFO = "/proc/slabinfo";
    public static String SOFT_IRQS = "/proc/softirqs";
    public static String STAT = "/proc/stat";
    public static String SWAPS = "/proc/swaps";
    public static String SYSRQ_TRIG = "/proc/sysrq-trigger";
    public static String TIMER_LIST = "/proc/timer_list";
    public static String TIMER_STATS = "/proc/timer_stats";
    public static String UPTIME = "/proc/uptime";
    public static String KERN_VERSION = "/proc/version";
    public static String VMALLOC_INFO = "/proc/vmallocinfo";
    public static String VMSTAT = "/proc/vmstat";
    public static String ZONE_INFO = "/proc/zoneinfo";
}


